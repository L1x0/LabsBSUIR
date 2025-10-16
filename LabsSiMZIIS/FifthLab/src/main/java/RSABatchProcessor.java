import java.io.*;
import java.math.BigInteger;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class RSABatchProcessor {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final BigInteger PUBLIC_E = BigInteger.valueOf(65537);

    public static void main(String[] args) throws Exception {
        String initName = args.length > 0 ? args[0] : "initSet";
        Path resultsDir = Path.of(args.length > 1 ? args[1] : "results");
        int primeBits = args.length > 2 ? Integer.parseInt(args[2]) : 1024;

        if (primeBits < 1024) {
            System.err.println("primeBits must be >= 1024");
            return;
        }

        Path initDirPath = resolveInitDir(initName);
        if (initDirPath == null) {
            System.err.println("Cannot locate initDir resource: " + initName);
            return;
        }

        if (!Files.exists(resultsDir)) Files.createDirectories(resultsDir);
        System.out.println("RSABatchProcessor starting. initDir=" + initDirPath + ", resultsDir=" + resultsDir + ", primeBits=" + primeBits);

        try (Stream<Path> s = Files.list(initDirPath)) {
            s.filter(Files::isRegularFile).forEach(file -> {

                String fileName = file.getFileName().toString();
                String testName = stripExtension(fileName);

                System.out.println("\n=== Processing test: " + testName + " (" + fileName + ") ===");
                try {
                    Instant t0 = Instant.now();

                    Path keysDir = resultsDir.resolve("keys").resolve(testName);
                    Path encDir = resultsDir.resolve("encrypt").resolve(testName);
                    Path decDir = resultsDir.resolve("decrypt").resolve(testName);
                    Path signDir = resultsDir.resolve("sign").resolve(testName);
                    Path verifyDir = resultsDir.resolve("verify").resolve(testName);

                    Files.createDirectories(keysDir);
                    Files.createDirectories(encDir);
                    Files.createDirectories(decDir);
                    Files.createDirectories(signDir);
                    Files.createDirectories(verifyDir);

                    // 1) generate keys
                    System.out.println("Generating keys (" + primeBits + "-bit primes)...");
                    BigInteger p = BigInteger.probablePrime(primeBits, RANDOM);
                    BigInteger q;

                    do {
                        q = BigInteger.probablePrime(primeBits, RANDOM);
                    } while (q.equals(p));

                    BigInteger n = p.multiply(q);
                    BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
                    BigInteger e = PUBLIC_E;

                    if (!e.gcd(phi).equals(BigInteger.ONE)) {
                        e = BigInteger.valueOf(3);
                        while (!e.gcd(phi).equals(BigInteger.ONE)) e = e.add(BigInteger.TWO);
                    }

                    BigInteger d = e.modInverse(phi);

                    Path pubPath = keysDir.resolve("public.key");
                    Path privPath = keysDir.resolve("private.key");

                    writePublicKey(pubPath, n, e);
                    writePrivateKey(privPath, n, d, p, q);

                    System.out.println("Keys saved to " + keysDir);

                    byte[] message = Files.readAllBytes(file);

                    Path encFile = encDir.resolve("message.enc");
                    encrypt(n, e, message, encFile);

                    Path decFile = decDir.resolve("message.dec");
                    decrypt(n, d, p, q, encFile, decFile);

                    byte[] decData = Files.readAllBytes(decFile);
                    boolean roundtrip = Arrays.equals(message, decData);
                    System.out.println("Roundtrip equals original: " + roundtrip);

                    Path sigFile = signDir.resolve("message.sig");
                    sign(n, d, message, sigFile);

                    boolean ok = verify(n, e, message, sigFile);
                    Path verifyTxt = verifyDir.resolve("verify.txt");
                    Files.writeString(verifyTxt, ok ? "OK" : "FAILED");
                    System.out.println("Signature verification: " + (ok ? "OK" : "FAILED"));

                    Instant t1 = Instant.now();
                    System.out.println("Test " + testName + " finished in " + Duration.between(t0, t1).toMillis() + " ms\n");

                } catch (Exception ex) {
                    System.err.println("Error processing " + fileName + ": " + ex.getMessage());
                    ex.printStackTrace();
                }
            });
        }

        System.out.println("RSABatchProcessor finished.");
    }

    private static Path resolveInitDir(String initName) throws Exception {
        Path fs = Path.of(initName);
        if (Files.exists(fs) && Files.isDirectory(fs)) return fs;

        ClassLoader cl = RSABatchProcessor.class.getClassLoader();
        URL dirURL = cl.getResource(initName);
        if (dirURL == null) {
            dirURL = cl.getResource(initName + "/");
            if (dirURL == null) return null;
        }

        String protocol = dirURL.getProtocol();
        if ("file".equals(protocol)) {
            URI uri = dirURL.toURI();
            return Path.of(uri);
        } else if ("jar".equals(protocol)) {
            return extractResourceDirToTemp(initName);
        } else {
            return extractResourceDirToTemp(initName);
        }
    }

    private static Path extractResourceDirToTemp(String resourceDir) throws Exception {
        String resourceRoot = resourceDir.endsWith("/") ? resourceDir : resourceDir + "/";
        URL dirURL = RSABatchProcessor.class.getClassLoader().getResource(resourceDir);
        if (dirURL == null) throw new IOException("Resource not found: " + resourceDir);

        String urlStr = dirURL.toString();

        int bang = urlStr.indexOf("!");
        if (bang == -1) throw new IOException("Unsupported resource URL: " + urlStr);
        String jarPath = urlStr.substring(urlStr.indexOf("file:") + 5, bang);
        jarPath = URLDecoder.decode(jarPath, "UTF-8");

        Path tempRoot = Files.createTempDirectory("initSet_extracted_");
        try (JarFile jar = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (!name.startsWith(resourceRoot)) continue;
                String relPath = name.substring(resourceRoot.length());
                if (relPath.isEmpty()) continue;
                Path outPath = tempRoot.resolve(relPath);
                if (entry.isDirectory()) {
                    Files.createDirectories(outPath);
                } else {
                    Files.createDirectories(outPath.getParent());
                    try (InputStream is = RSABatchProcessor.class.getClassLoader().getResourceAsStream(name)) {
                        if (is == null) continue;
                        Files.copy(is, outPath);
                    }
                }
            }
        }
        return tempRoot;
    }

    private static String stripExtension(String name) {
        int i = name.lastIndexOf('.');
        return (i > 0) ? name.substring(0, i) : name;
    }

    private static void writePublicKey(Path path, BigInteger n, BigInteger e) throws Exception {
        try (BufferedWriter w = Files.newBufferedWriter(path)) {
            w.write(n.toString(16));
            w.newLine();
            w.write(e.toString(16));
            w.newLine();
        }
    }

    private static void writePrivateKey(Path path, BigInteger n, BigInteger d, BigInteger p, BigInteger q) throws Exception {
        try (BufferedWriter w = Files.newBufferedWriter(path)) {
            w.write(n.toString(16));
            w.newLine();
            w.write(d.toString(16));
            w.newLine();
            w.write(p.toString(16));
            w.newLine();
            w.write(q.toString(16));
            w.newLine();
        }
    }

    private static void encrypt(BigInteger n, BigInteger e, byte[] plaintext, Path out) throws Exception {
        int k = (n.bitLength() + 7) / 8;
        int blockSize = k - 1;
        try (OutputStream os = Files.newOutputStream(out)) {
            os.write(longToBytes(plaintext.length));
            int off = 0;
            while (off < plaintext.length) {
                int len = Math.min(blockSize, plaintext.length - off);
                byte[] chunk = Arrays.copyOfRange(plaintext, off, off + len);
                BigInteger m = new BigInteger(1, chunk);
                if (m.compareTo(n) >= 0) throw new IllegalStateException("Message block >= modulus");
                BigInteger c = modPow(m, e, n);
                byte[] cbytes = toFixedLength(c, k);
                os.write(cbytes);
                off += len;
            }
        }
        System.out.println("Encrypted to: " + out + " (blockSize=" + blockSize + ")");
    }

    private static void decrypt(BigInteger n, BigInteger d, BigInteger p, BigInteger q, Path in, Path out) throws Exception {
        byte[] ciphertext = Files.readAllBytes(in);
        if (ciphertext.length < 8) throw new IOException("Invalid ciphertext file");
        long origLen = bytesToLong(Arrays.copyOfRange(ciphertext, 0, 8));
        int k = (n.bitLength() + 7) / 8;
        int pos = 8;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (pos + k <= ciphertext.length) {
            byte[] cblock = Arrays.copyOfRange(ciphertext, pos, pos + k);
            BigInteger c = new BigInteger(1, cblock);
            BigInteger m = crtDecrypt(c, p, q, d);
            byte[] mbytes = m.toByteArray();
            if (mbytes.length > 0 && mbytes[0] == 0) mbytes = Arrays.copyOfRange(mbytes, 1, mbytes.length);
            baos.write(mbytes);
            pos += k;
        }
        byte[] outBytes = baos.toByteArray();
        if (origLen < outBytes.length) outBytes = Arrays.copyOf(outBytes, (int) origLen);
        Files.write(out, outBytes);
        System.out.println("Decrypted to: " + out);
    }

    private static void sign(BigInteger n, BigInteger d, byte[] data, Path sigOut) throws Exception {
        byte[] h = sha256(data);
        BigInteger hv = new BigInteger(1, h);
        BigInteger s = modPow(hv, d, n);
        int k = (n.bitLength() + 7) / 8;
        Files.write(sigOut, toFixedLength(s, k));
        System.out.println("Signed: " + sigOut);
    }

    private static boolean verify(BigInteger n, BigInteger e, byte[] data, Path sigFile) throws Exception {
        byte[] h = sha256(data);
        byte[] sbytes = Files.readAllBytes(sigFile);
        BigInteger s = new BigInteger(1, sbytes);
        BigInteger v = modPow(s, e, n);
        BigInteger hv = new BigInteger(1, h);
        return hv.equals(v);
    }

    private static byte[] longToBytes(long v) {
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.putLong(v);
        return bb.array();
    }

    private static long bytesToLong(byte[] b) {
        ByteBuffer bb = ByteBuffer.wrap(b);
        return bb.getLong();
    }

    private static byte[] toFixedLength(BigInteger v, int k) {
        byte[] a = v.toByteArray();
        if (a.length == k) return a;
        if (a.length == k + 1 && a[0] == 0) return Arrays.copyOfRange(a, 1, a.length);
        if (a.length < k) {
            byte[] r = new byte[k];
            System.arraycopy(a, 0, r, k - a.length, a.length);
            return r;
        }
        throw new IllegalStateException("Integer too large to fit in fixed length: " + a.length + " > " + k);
    }

    private static byte[] sha256(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(data);
    }

    private static BigInteger modPow(BigInteger base, BigInteger exp, BigInteger mod) {
        base = base.mod(mod);
        BigInteger result = BigInteger.ONE;
        for (int i = exp.bitLength() - 1; i >= 0; --i) {
            result = result.multiply(result).mod(mod);
            if (exp.testBit(i)) result = result.multiply(base).mod(mod);
        }
        return result;
    }

    private static BigInteger crtDecrypt(BigInteger c, BigInteger p, BigInteger q, BigInteger d) {
        try {
            BigInteger dp = d.mod(p.subtract(BigInteger.ONE));
            BigInteger dq = d.mod(q.subtract(BigInteger.ONE));
            BigInteger qInv = q.modInverse(p);
            BigInteger m1 = modPow(c.mod(p), dp, p);
            BigInteger m2 = modPow(c.mod(q), dq, q);
            BigInteger h = m1.subtract(m2).multiply(qInv).mod(p);
            BigInteger m = m2.add(h.multiply(q));
            return m.mod(p.multiply(q));
        } catch (Exception ex) {
            System.err.println("CRT failed, falling back to direct modPow: " + ex.getMessage());
            BigInteger n = p.multiply(q);
            return modPow(c, d, n);
        }
    }
}
