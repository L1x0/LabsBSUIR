set -eu -o pipefail

PROXY_HOST="127.0.0.1"
PROXY_PORT="8888"
PROXY="${PROXY_HOST}:${PROXY_PORT}"

UP_A_HOST="127.0.0.1"
UP_A_PORT="${3:-9000}"
UP_G_HOST="127.0.0.1"
UP_G_PORT="${4:-9001}"

OUT_A="amazon.out"
OUT_G="google.out"
SLEEP_SECONDS=5

cleanup() {
  echo "Cleaning up..."
  for p in "${server_pids[@]:-}"; do
    kill "$p" 2>/dev/null || true
  done
  for p in "${curl_pids[@]:-}"; do
    kill "$p" 2>/dev/null || true
  done
}
trap cleanup EXIT INT TERM

server_pids=()
start_slow_server() {
  local host="$1"; local port="$2"; local sleep_sec="$3"
  python3 - <<PYTHON &>/dev/null &
import http.server, socketserver, time, sys
class H(http.server.BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path.startswith("/slow"):
            time.sleep(${sleep_sec})
            body = f"OK from upstream {sys.argv[1]}:{sys.argv[2]}\\n".encode()
            self.send_response(200)
            self.send_header("Content-Type","text/plain")
            self.send_header("Content-Length", str(len(body)))
            self.end_headers()
            self.wfile.write(body)
        else:
            self.send_response(404); self.end_headers()
    def log_message(self, fmt, *args): pass

with socketserver.TCPServer(("${host}", int("${port}")), H) as httpd:
    httpd.serve_forever()
PYTHON
  server_pids+=("$!")
  echo "Started slow upstream ${host}:${port} pid=$!"
}

start_slow_server "${UP_A_HOST}" "${UP_A_PORT}" "${SLEEP_SECONDS}"
start_slow_server "${UP_G_HOST}" "${UP_G_PORT}" "${SLEEP_SECONDS}"
sleep 0.2

curl_pids=()
run_curl_prefixed() {
  local name="$1"; local url="$2"; local out_file="$3"

  ( curl -v -x "http://${PROXY}" --max-time 30 "${url}" 2>&1 \
      | awk -v P="[$name] " '{ print P $0; fflush(); }' \
      | tee "${out_file}" ) &
  curl_pids+=("$!")
  echo "${name} started pid=${!}"
}

echo "Proxy: ${PROXY}"
echo "Upstream A: http://${UP_A_HOST}:${UP_A_PORT}/slow  (will sleep ${SLEEP_SECONDS}s)"
echo "Upstream G: http://${UP_G_HOST}:${UP_G_PORT}/slow  (will sleep ${SLEEP_SECONDS}s)"
echo

run_curl_prefixed "amazon" "http://${UP_A_HOST}:${UP_A_PORT}/slow" "${OUT_A}"
run_curl_prefixed "google"  "http://${UP_G_HOST}:${UP_G_PORT}/slow" "${OUT_G}"


RC_A=0; RC_G=0
wait "${curl_pids[0]}" || RC_A=$?
wait "${curl_pids[1]}" || RC_G=$?

echo
echo "Done. Exit codes: amazon=${RC_A}, google=${RC_G}"
echo "Outputs: ${OUT_A}, ${OUT_G}"
