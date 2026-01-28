#!/usr/bin/env python3
from __future__ import annotations
import argparse
import errno
import logging
import socket
import sys
import threading
import time
import traceback
import uuid
from logging.handlers import RotatingFileHandler
from urllib.parse import urlsplit

LISTEN_BACKLOG = 50
BUFFER_SIZE = 4096

logger = logging.getLogger("proxy")
logger.setLevel(logging.INFO)
ch = logging.StreamHandler(sys.stdout)
ch.setFormatter(logging.Formatter("%(asctime)s [%(levelname)s] %(message)s"))
logger.addHandler(ch)
fh = RotatingFileHandler("proxy.log", maxBytes=5 * 1024 * 1024, backupCount=3)
fh.setFormatter(logging.Formatter("%(asctime)s [%(levelname)s] %(message)s"))
logger.addHandler(fh)

def log(*parts, level="info"):
    msg = " ".join(str(p) for p in parts)
    if level == "debug":
        logger.debug(msg)
    elif level == "warning":
        logger.warning(msg)
    elif level == "error":
        logger.error(msg)
    else:
        logger.info(msg)

def make_error_response(code: int, reason: str, body: str | None = None) -> bytes:
    if body is None:
        body = f"<html><body><h1>{code} {reason}</h1></body></html>"
    body_bytes = body.encode("utf-8", errors="replace")
    headers = [
        f"HTTP/1.0 {code} {reason}",
        "Content-Type: text/html; charset=utf-8",
        f"Content-Length: {len(body_bytes)}",
        "Connection: close",
        "",
        "",
    ]
    header_bytes = "\r\n".join(headers).encode("utf-8")
    return header_bytes + body_bytes

def pretty_error_body(code: int, reason: str, details: str | None = None) -> str:
    details_html = ""
    if details:
        safe = str(details).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
        details_html = f"<pre style='white-space:pre-wrap'>{safe}</pre>"
    return f"""<html>
<head><title>{code} {reason}</title></head>
<body style="font-family: sans-serif; max-width:900px; margin:30px;">
  <h1>{code} {reason}</h1>
  <p>Прокси обнаружил проблему при обращении к upstream.</p>
  {details_html}
  <hr>
  <small>HTTP proxy</small>
</body>
</html>"""

def recv_until_double_crlf(conn: socket.socket, timeout: float) -> tuple[bytes, bytes]:
    data = b""
    conn.settimeout(timeout)
    try:
        while b"\r\n\r\n" not in data:
            chunk = conn.recv(BUFFER_SIZE)
            if not chunk:
                break
            data += chunk
            if len(data) > 65536:
                break
    except socket.timeout:
        raise TimeoutError("Timeout while reading headers")
    if b"\r\n\r\n" in data:
        headers, rest = data.split(b"\r\n\r\n", 1)
        return headers + b"\r\n\r\n", rest
    else:
        return data, b""

def parse_headers_bytes(headers_bytes: bytes) -> tuple[str, dict, bytes]:
    try:
        text = headers_bytes.decode("iso-8859-1")
    except Exception:
        text = headers_bytes.decode("utf-8", errors="replace")
    lines = text.split("\r\n")
    if not lines:
        return "", {}, headers_bytes
    status_line = lines[0]
    hdrs = {}
    for line in lines[1:]:
        if not line:
            continue
        if ":" in line:
            k, v = line.split(":", 1)
            hdrs[k.strip().lower()] = v.strip()
    return status_line, hdrs, headers_bytes

def map_exception_to_response(exc: Exception) -> tuple[int, str, str]:
    if isinstance(exc, socket.timeout):
        code, reason = 504, "Gateway Timeout"
        body = pretty_error_body(code, reason, details=str(exc))
        return code, reason, body
    if isinstance(exc, socket.gaierror):
        code, reason = 502, "Bad Gateway"
        body = pretty_error_body(code, reason, details=f"DNS error: {exc}")
        return code, reason, body
    if isinstance(exc, (ConnectionRefusedError,)):
        code, reason = 502, "Bad Gateway"
        body = pretty_error_body(code, reason, details=str(exc))
        return code, reason, body
    if isinstance(exc, OSError):
        if getattr(exc, "errno", None) in (errno.ECONNREFUSED, errno.EHOSTUNREACH, errno.ENETUNREACH):
            code, reason = 502, "Bad Gateway"
            body = pretty_error_body(code, reason, details=f"OSError: {exc}")
            return code, reason, body
        code, reason = 502, "Bad Gateway"
        body = pretty_error_body(code, reason, details=f"OSError: {exc}")
        return code, reason, body
    code, reason = 500, "Internal Server Error"
    body = pretty_error_body(code, reason, details=str(exc))
    return code, reason, body

def connect_with_retries(host: str, port: int, timeout: float, max_retries: int, backoff_base: float) -> socket.socket:
    last_exc: Exception | None = None
    for attempt in range(1, max_retries + 1):
        try:
            log(f"Attempt {attempt}/{max_retries}: connecting to {host}:{port}")
            s = socket.create_connection((host, port), timeout=timeout)
            return s
        except Exception as e:
            last_exc = e
            log(f"Connect attempt {attempt} failed: {e}", level="warning")
            if attempt < max_retries:
                sleep_time = backoff_base * (2 ** (attempt - 1))
                log(f"Backoff {sleep_time:.1f}s before next attempt")
                time.sleep(sleep_time)
    if last_exc:
        raise last_exc
    else:
        raise ConnectionError("Unknown error connecting to upstream")

POPULAR_ERROR_CODES = {
    400, 401, 403, 404, 405, 408, 410, 415, 429, 431, 444, 499,
    500, 501, 502, 503, 504, 505, 507, 508, 510, 522, 523, 524
}

def safe_send(sock: socket.socket, data: bytes) -> None:
    try:
        if not data:
            return
        sock.sendall(data)
    except Exception as e:
        log(f"safe_send failed: {e}", level="debug")
        raise

def tunnel_tcp(client_sock: socket.socket, upstream_sock: socket.socket) -> None:
    def forward(src: socket.socket, dst: socket.socket, name: str):
        try:
            while True:
                data = src.recv(BUFFER_SIZE)
                if not data:
                    break
                dst.sendall(data)
        except Exception as e:
            log(f"Tunnel forward {name} ended: {e}", level="debug")
        finally:
            try:
                dst.shutdown(socket.SHUT_WR)
            except Exception:
                pass
    t1 = threading.Thread(target=forward, args=(client_sock, upstream_sock, "c2u"), daemon=True)
    t2 = threading.Thread(target=forward, args=(upstream_sock, client_sock, "u2c"), daemon=True)
    t1.start()
    t2.start()
    t1.join()
    t2.join()

def handle_client(client_sock: socket.socket, client_addr, upstream_timeout: float, max_retries: int, backoff_base: float) -> None:
    rid = uuid.uuid4().hex[:8]
    start = time.perf_counter()
    def elapsed():
        return f"{(time.perf_counter()-start):.3f}s"
    log(f"[{rid}] ACCEPT from {client_addr} {elapsed()}")
    try:
        try:
            headers_bytes, rest = recv_until_double_crlf(client_sock, upstream_timeout)
            log(f"[{rid}] RECV_REQ_HEADERS {elapsed()} bytes={len(headers_bytes)}")
        except TimeoutError:
            log(f"[{rid}] TIMEOUT reading headers from client {client_addr} {elapsed()}", level="warning")
            safe_send(client_sock, make_error_response(408, "Request Timeout"))
            return
        except Exception as e:
            log(f"[{rid}] ERROR reading headers from client: {e} {elapsed()}", level="error")
            code, reason, body = map_exception_to_response(e)
            safe_send(client_sock, make_error_response(code, reason, body))
            return

        if not headers_bytes:
            log(f"[{rid}] EMPTY request from {client_addr} {elapsed()}", level="warning")
            try:
                client_sock.close()
            except Exception:
                pass
            return

        try:
            headers_text = headers_bytes.decode("iso-8859-1")
        except Exception:
            safe_send(client_sock, make_error_response(400, "Bad Request", "<h1>Bad Request (decode failed)</h1>"))
            return

        lines = headers_text.split("\r\n")
        request_line = lines[0]
        parts = request_line.split()
        if len(parts) < 3:
            safe_send(client_sock, make_error_response(400, "Bad Request", "<h1>Malformed request line</h1>"))
            return
        method, uri, http_version = parts[0], parts[1], parts[2]
        header_lines = lines[1:]
        headers = {}
        for h in header_lines:
            if not h:
                continue
            if ":" in h:
                k, v = h.split(":", 1)
                headers[k.strip().lower()] = v.strip()

        if method.upper() == "CONNECT":
            target_host = None
            target_port = 443
            if ":" in uri:
                try:
                    target_host, p = uri.split(":", 1)
                    target_port = int(p)
                except Exception:
                    target_host = uri
                    target_port = 443
            else:
                target_host = uri
                target_port = 443

            try:
                upstream = connect_with_retries(target_host, target_port, upstream_timeout, max_retries, backoff_base)
                log(f"[{rid}] CONNECT upstream connected to {target_host}:{target_port} {elapsed()}")
            except Exception as e:
                log(f"[{rid}] CONNECT error connecting to upstream after retries: {e} {elapsed()}", level="warning")
                code, reason, body_html = map_exception_to_response(e)
                safe_send(client_sock, make_error_response(code, reason, body_html))
                return

            try:
                safe_send(client_sock, b"HTTP/1.0 200 Connection established\r\n\r\n")
                log(f"[{rid}] CONNECT 200 sent to client {elapsed()}")
            except Exception as e:
                log(f"[{rid}] CONNECT failed to send 200 to client: {e} {elapsed()}", level="warning")
                try:
                    upstream.close()
                except Exception:
                    pass
                return

            try:
                tunnel_tcp(client_sock, upstream)
                log(f"[{rid}] CONNECT tunnel finished {elapsed()}")
            except Exception as e:
                log(f"[{rid}] CONNECT tunnel error: {e} {elapsed()}", level="warning")
            finally:
                try:
                    upstream.close()
                except Exception:
                    pass
                try:
                    client_sock.close()
                except Exception:
                    pass
            return

        host = None
        port = 80
        path = uri
        if uri.startswith("http://") or uri.startswith("https://"):
            parsed = urlsplit(uri)
            host = parsed.hostname
            port = parsed.port or (443 if parsed.scheme == "https" else 80)
            path = parsed.path or "/"
            if parsed.query:
                path += "?" + parsed.query
        else:
            if "host" not in headers:
                safe_send(client_sock, make_error_response(400, "Bad Request", "<h1>Host header missing</h1>"))
                return
            hostport = headers["host"]
            if ":" in hostport:
                host, port_str = hostport.split(":", 1)
                try:
                    port = int(port_str)
                except Exception:
                    port = 80
            else:
                host = hostport

        if not host:
            safe_send(client_sock, make_error_response(400, "Bad Request", "<h1>Cannot determine host</h1>"))
            return

        body = rest or b""
        try:
            content_length = int(headers.get("content-length", "0")) if headers.get("content-length") else 0
        except Exception:
            content_length = 0
        to_read = content_length - len(body)
        if to_read > 0:
            client_sock.settimeout(upstream_timeout)
            try:
                while to_read > 0:
                    chunk = client_sock.recv(min(BUFFER_SIZE, to_read))
                    if not chunk:
                        break
                    body += chunk
                    to_read -= len(chunk)
                log(f"[{rid}] RECV_REQ_BODY complete {elapsed()} bytes={len(body)}")
            except socket.timeout:
                safe_send(client_sock, make_error_response(408, "Request Timeout"))
                return
            except Exception as e:
                log(f"[{rid}] Error while reading request body: {e} {elapsed()}", level="warning")
                safe_send(client_sock, make_error_response(400, "Bad Request"))
                return

        try:
            upstream = connect_with_retries(host, port, upstream_timeout, max_retries, backoff_base)
            log(f"[{rid}] UPSTREAM_CONNECTED {host}:{port} {elapsed()}")
        except Exception as e:
            log(f"[{rid}] Error connecting to upstream after retries: {e} {elapsed()}", level="warning")
            code, reason, body_html = map_exception_to_response(e)
            safe_send(client_sock, make_error_response(code, reason, body_html))
            return

        try:
            out_lines = []
            out_lines.append(f"{method} {path} HTTP/1.0")
            for k, v in headers.items():
                if k.lower() in ("proxy-connection", "connection"):
                    continue
                out_lines.append(f"{k}: {v}")
            out_lines.append("Connection: close")
            out_lines.append("")
            request_to_upstream = "\r\n".join(out_lines).encode("iso-8859-1") + b"\r\n" + (body or b"")
            upstream.sendall(request_to_upstream)
            log(f"[{rid}] SENT_TO_UPSTREAM {len(request_to_upstream)} bytes {elapsed()}")
            try:
                resp_headers_bytes, resp_rest = recv_until_double_crlf(upstream, upstream_timeout)
                log(f"[{rid}] RECV_RESP_HEADERS {elapsed()} bytes={len(resp_headers_bytes)}")
            except TimeoutError:
                log(f"[{rid}] TIMEOUT reading response headers from upstream {host} {elapsed()}", level="warning")
                safe_send(client_sock, make_error_response(504, "Gateway Timeout"))
                try:
                    upstream.close()
                except Exception:
                    pass
                return
            except Exception as e:
                log(f"[{rid}] Error reading response headers from upstream: {e} {elapsed()}", level="warning")
                code, reason, body_html = map_exception_to_response(e)
                safe_send(client_sock, make_error_response(code, reason, body_html))
                try:
                    upstream.close()
                except Exception:
                    pass
                return

            status_line, resp_headers_dict, raw_resp_headers = parse_headers_bytes(resp_headers_bytes)
            log(f"[{rid}] UPSTREAM_STATUS {status_line} {elapsed()}")

            try:
                sl_parts = status_line.split(None, 2)
                if len(sl_parts) >= 2:
                    status_code = int(sl_parts[1])
                    reason = sl_parts[2] if len(sl_parts) >= 3 else ""
                else:
                    status_code = 0
                    reason = ""
            except Exception:
                status_code = 0
                reason = ""

            if status_code in POPULAR_ERROR_CODES:
                log(f"[{rid}] INTERCEPT_STATUS {status_code} {elapsed()}", level="warning")
                body_html = pretty_error_body(status_code, reason or "Error", details=f"Upstream: {status_line}")
                safe_send(client_sock, make_error_response(status_code, reason or "Error", body_html))
                try:
                    upstream.close()
                except Exception:
                    pass
                return

            try:
                safe_send(client_sock, raw_resp_headers)
                log(f"[{rid}] SENT_RESP_HEADERS_TO_CLIENT {elapsed()} bytes={len(raw_resp_headers)}")
            except Exception:
                log(f"[{rid}] Failed to send response headers to client {client_addr} {elapsed()}", level="warning")
                try:
                    upstream.close()
                except Exception:
                    pass
                return

            if resp_rest:
                try:
                    safe_send(client_sock, resp_rest)
                    log(f"[{rid}] SENT_INITIAL_RESP_BODY {elapsed()} bytes={len(resp_rest)}")
                except Exception:
                    log(f"[{rid}] Failed to send initial response body to client {client_addr} {elapsed()}", level="warning")
                    try:
                        upstream.close()
                    except Exception:
                        pass
                    return

            upstream.settimeout(upstream_timeout)
            total_streamed = 0
            while True:
                try:
                    chunk = upstream.recv(BUFFER_SIZE)
                except socket.timeout:
                    log(f"[{rid}] TIMEOUT while reading body from upstream {host} {elapsed()}", level="warning")
                    break
                except Exception as e:
                    log(f"[{rid}] Error while reading from upstream: {e} {elapsed()}", level="warning")
                    break
                if not chunk:
                    break
                total_streamed += len(chunk)
                try:
                    safe_send(client_sock, chunk)
                except Exception:
                    log(f"[{rid}] Client closed connection while proxying body {client_addr} {elapsed()}", level="warning")
                    break
            log(f"[{rid}] STREAM_FINISHED total_streamed={total_streamed} {elapsed()}")
        except Exception as e:
            log(f"[{rid}] Error during proxying: {traceback.format_exc()} {elapsed()}", level="error")
            code, reason, body_html = map_exception_to_response(e)
            try:
                safe_send(client_sock, make_error_response(code, reason, body_html))
            except Exception:
                pass
        finally:
            try:
                upstream.close()
            except Exception:
                pass
    except Exception as e:
        log(f"[{rid}] Unhandled exception in handler: {traceback.format_exc()} {elapsed()}", level="error")
        code, reason, body_html = map_exception_to_response(e) if isinstance(e, (socket.timeout, socket.gaierror, ConnectionRefusedError, OSError)) else (500, "Internal Server Error", pretty_error_body(500, "Internal Server Error", details=str(e)))
        try:
            safe_send(client_sock, make_error_response(code, reason, body_html))
        except Exception:
            pass
    finally:
        try:
            client_sock.close()
        except Exception:
            pass
        log(f"[{rid}] CLOSED {client_addr} {elapsed()}")

def safe_handle_client(client_sock: socket.socket, client_addr, upstream_timeout: float, max_retries: int, backoff_base: float) -> None:
    try:
        handle_client(client_sock, client_addr, upstream_timeout, max_retries, backoff_base)
    except Exception as e:
        log(f"Critical unhandled exception in client thread: {traceback.format_exc()}", level="error")
        code, reason, body_html = map_exception_to_response(e) if isinstance(e, (socket.timeout, socket.gaierror, ConnectionRefusedError, OSError)) else (500, "Internal Server Error", pretty_error_body(500, "Internal Server Error", details=str(e)))
        try:
            safe_send(client_sock, make_error_response(code, reason, body_html))
        except Exception:
            pass
        try:
            client_sock.close()
        except Exception:
            pass

def run_proxy(listen_host: str = "0.0.0.0", listen_port: int = 8888, upstream_timeout: float = 8.0, max_retries: int = 3, backoff_base: float = 1.0) -> None:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    try:
        s.bind((listen_host, listen_port))
    except Exception as e:
        log(f"Failed to bind {listen_host}:{listen_port}: {e}", level="error")
        sys.exit(1)
    s.listen(LISTEN_BACKLOG)
    log(f"Proxy listening on {listen_host}:{listen_port} ... (timeout={upstream_timeout}s, retries={max_retries}, backoff_base={backoff_base}s)")
    try:
        while True:
            try:
                client_sock, client_addr = s.accept()
            except KeyboardInterrupt:
                log("KeyboardInterrupt received, shutting down...", level="info")
                break
            except Exception as e:
                log("Accept failed: %s" % traceback.format_exc(), level="error")
                time.sleep(0.1)
                continue
            try:
                t = threading.Thread(target=safe_handle_client, args=(client_sock, client_addr, upstream_timeout, max_retries, backoff_base), daemon=True)
                t.start()
            except Exception as e:
                log(f"Failed to start thread for client {client_addr}: {e}", level="error")
                try:
                    safe_send(client_sock, make_error_response(500, "Internal Server Error"))
                except Exception:
                    pass
                try:
                    client_sock.close()
                except Exception:
                    pass
    finally:
        try:
            s.close()
        except Exception:
            pass
        log("Proxy stopped", level="info")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Robust HTTP proxy with CONNECT support and retry/backoff")
    parser.add_argument("--port", type=int, default=8888, help="порт для прослушивания (по умолчанию 8888)")
    parser.add_argument("--host", type=str, default="0.0.0.0", help="адрес для прослушивания (по умолчанию 0.0.0.0)")
    parser.add_argument("--timeout", type=float, default=8.0, help="таймаут для соединений в секундах (по умолчанию 8)")
    parser.add_argument("--retries", type=int, default=3, help="максимум попыток подключения к upstream (по умолчанию 3)")
    parser.add_argument("--retry-backoff", type=float, default=1.0, help="базовая пауза в секундах для экспоненциального backoff (по умолчанию 1.0)")
    parser.add_argument("--verbose", action="store_true", help="включить DEBUG-логи")
    args = parser.parse_args()
    if args.verbose:
        logger.setLevel(logging.DEBUG)
    run_proxy(listen_host=args.host, listen_port=args.port, upstream_timeout=args.timeout, max_retries=args.retries, backoff_base=args.retry_backoff)
