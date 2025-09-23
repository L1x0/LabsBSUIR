import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class FFTJavaDemo {


    public static class Complex {
        public double re, im;
        public Complex(double re, double im) { this.re = re; this.im = im; }
        public Complex add(Complex o) { return new Complex(re + o.re, im + o.im); }
        public Complex sub(Complex o) { return new Complex(re - o.re, im - o.im); }
        public Complex mul(Complex o) { return new Complex(re*o.re - im*o.im, re*o.im + im*o.re); }
        public Complex scale(double s) { return new Complex(re*s, im*s); }
        public Complex conj() { return new Complex(re, -im); }
        public double abs() { return Math.hypot(re, im); }
        @Override public String toString() { return String.format("(%.5f, %.5f)", re, im); }
    }


    public static void fft(Complex[] a, boolean invert) {
        int n = a.length;

        for (int i = 1, j = 0; i < n; ++i) {
            int bit = n >> 1;
            for (; (j & bit) != 0; bit >>= 1) j ^= bit;
            j ^= bit;
            if (i < j) {
                Complex tmp = a[i]; a[i] = a[j]; a[j] = tmp;
            }
        }

        for (int len = 2; len <= n; len <<= 1) {
            double ang = 2 * Math.PI / len * (invert ? 1 : -1);
            double wlenRe = Math.cos(ang);
            double wlenIm = Math.sin(ang);
            for (int i = 0; i < n; i += len) {
                double wr = 1.0;
                double wi = 0.0;
                for (int j = 0; j < len/2; ++j) {
                    int uIdx = i + j;
                    int vIdx = i + j + len/2;
                    Complex u = a[uIdx];
                    Complex v = a[vIdx];

                    double vr = v.re * wr - v.im * wi;
                    double vi = v.re * wi + v.im * wr;

                    a[uIdx] = new Complex(u.re + vr, u.im + vi);

                    a[vIdx] = new Complex(u.re - vr, u.im - vi);

                    double nw = wr * wlenRe - wi * wlenIm;
                    double ni = wr * wlenIm + wi * wlenRe;
                    wr = nw; wi = ni;
                }
            }
        }

        if (invert) {
            for (int i = 0; i < n; ++i) {
                a[i].re /= n; a[i].im /= n;
            }
        }
    }

    public static Complex[] copy(Complex[] src) {
        Complex[] dst = new Complex[src.length];
        for (int i = 0; i < src.length; ++i) dst[i] = new Complex(src[i].re, src[i].im);
        return dst;
    }

    public static Complex[] generateSignal(String type, int N, double freq) {
        Complex[] s = new Complex[N];
        for (int k = 0; k < N; ++k) {
            double t = 2 * Math.PI * freq * k / N;
            double v = "sin".equalsIgnoreCase(type) ? Math.sin(t) : Math.cos(t);
            s[k] = new Complex(v, 0.0);
        }
        return s;
    }

    public static double rmsErrorReal(Complex[] a, Complex[] b) {
        int n = a.length; double s = 0;
        for (int i = 0; i < n; ++i) {
            double d = a[i].re - b[i].re; s += d*d;
        }
        return Math.sqrt(s/n);
    }

    public static void main(String[] args) {

        String wave;
        double freq;
        int N;
        if (args.length >= 1) wave = args[0];
        else {
            wave = "sin";
        }
        if (args.length >= 2) freq = Double.parseDouble(args[1]);
        else {
            freq = 20;
        }
        if (args.length >= 3) N = Integer.parseInt(args[2]);
        else {
            N = 1024;
        }

        if ((N & (N-1)) != 0) {
            System.err.println("N must be a power of two (e.g. 256,512,1024). Current N=" + N);
            System.exit(1);
        }

        Complex[] signal = generateSignal(wave, N, freq);
        Complex[] original = copy(signal);

        fft(signal, false);
        Complex[] spectrum = copy(signal);

        Complex[] recon = copy(spectrum);
        fft(recon, true);

        double rms = rmsErrorReal(original, recon);
        System.out.printf("RMS reconstruction error (real part): %.6e\n", rms);


        XYSeries seriesOriginal = new XYSeries("Original");
        XYSeries seriesReconstructed = new XYSeries("Reconstructed");
        for (int i = 0; i < N; ++i) {
            seriesOriginal.add(i, original[i].re);
            seriesReconstructed.add(i, recon[i].re);
        }
        XYSeriesCollection sigCollection = new XYSeriesCollection();
        sigCollection.addSeries(seriesOriginal);
        sigCollection.addSeries(seriesReconstructed);


        XYSeries seriesSpectrum = new XYSeries("Magnitude");
        for (int i = 0; i < N/2; ++i) {
            seriesSpectrum.add(i, spectrum[i].abs());
        }
        XYSeriesCollection specCollection = new XYSeriesCollection();
        specCollection.addSeries(seriesSpectrum);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("FFT demo: " + wave + " freq=" + freq + " N=" + N);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new GridLayout(2, 1));

            JFreeChart sigChart = ChartFactory.createXYLineChart(
                    "Signal: original vs reconstructed",
                    "n",
                    "value",
                    sigCollection,
                    PlotOrientation.VERTICAL,
                    true, true, false
            );
            ChartPanel sigPanel = new ChartPanel(sigChart);

            JFreeChart specChart = ChartFactory.createXYLineChart(
                    "Magnitude spectrum (first N/2 bins)",
                    "bin",
                    "magnitude",
                    specCollection,
                    PlotOrientation.VERTICAL,
                    false, true, false
            );
            ChartPanel specPanel = new ChartPanel(specChart);

            frame.add(sigPanel);
            frame.add(specPanel);
            frame.pack();
            frame.setSize(900, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
