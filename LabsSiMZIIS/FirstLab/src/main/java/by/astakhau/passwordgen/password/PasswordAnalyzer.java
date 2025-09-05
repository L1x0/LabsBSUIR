package by.astakhau.passwordgen.password;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.paukov.combinatorics3.Generator;


import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class PasswordAnalyzer {
    private final int ASCII_a = 97;
    private final int ASCII_z = 122;
    PasswordGeneration pwg;

    public PasswordAnalyzer(PasswordGeneration pwg) {
        this.pwg = pwg;
    }

    public void fullAnalyze() {

    }

    public String symbolDistribution() {
        int size = 1000;
        String password = pwg.generatePassword(size);

        Map<Character, Integer> map = new HashMap<>();

        for (int i = 0; i < size; i++) {
            if (map.containsKey(password.charAt(i))) {
                map.put(password.charAt(i), map.get(password.charAt(i)) + 1);
            } else {
                map.put(password.charAt(i), 1);
            }
        }

        if (map.isEmpty()) {
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(
                            null,
                            "Данные пусты — нечего отображать.",
                            "Info", JOptionPane.INFORMATION_MESSAGE)
            );
        }

        DefaultCategoryDataset dataset = createDataset(map);
        JFreeChart chart = createChart(dataset, "Symbol Distribution");
        showChartInWindow(chart, "Bar chart - HashMap<Character,Integer>", 800, 600);

        return map.toString();
    }

    public void averageBrutTime() {
        String password = pwg.generatePassword(7);
        StringBuilder sb = new StringBuilder();

        List<String> alphabet = new ArrayList<>();

        for (int i = ASCII_a; i <= ASCII_z; i++) {
            alphabet.add(String.valueOf((char)i));
        }

        long start = System.nanoTime();

        Optional<String> found = Generator
                .cartesianProduct(alphabet, alphabet, alphabet, alphabet, alphabet, alphabet, alphabet)
                .stream()
                .map(tuple -> String.join("", tuple))
                .filter(password::equals)
                .findFirst();

        if (found.isPresent()) {
            sb.append(found.get());

            long duration = System.nanoTime() - start;

            printElapsed(duration);
        }

    }

    private static void printElapsed(long nanos) {
        long ms = nanos / 1_000_000;
        long s = ms / 1000;
        long minutes = s / 60;
        long hours = minutes / 60;
        long remHours = hours;
        long remMinutes = minutes % 60;
        long remSeconds = s % 60;
        long remMillis = ms % 1000;

        System.out.printf("Затраченное время: %02d:%02d:%02d.%03d",
                remHours, remMinutes, remSeconds, remMillis);
    }

    private static DefaultCategoryDataset createDataset(Map<Character, Integer> map) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (map == null) return dataset;

        List<Character> keys = map.keySet().stream()
                .sorted(Comparator.naturalOrder())
                .toList();

        for (Character ch : keys) {
            Integer v = map.get(ch);

            dataset.addValue(v == null ? 0 : v, "Count", ch.toString());
        }
        return dataset;
    }

    private static JFreeChart createChart(DefaultCategoryDataset dataset, String title) {
        JFreeChart chart = ChartFactory.createBarChart(
                title,                 // chart title
                "Character",           // domain axis label (X)
                "Count",               // range axis label (Y)
                dataset,               // data
                PlotOrientation.VERTICAL,
                true,                  // include legend
                true,                  // tooltips
                false                  // urls
        );

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);

        return chart;
    }

    private static void showChartInWindow(JFreeChart chart, String windowTitle, int width, int height) {
        SwingUtilities.invokeLater(() -> {
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(width, height));

            JFrame frame = new JFrame(windowTitle);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(chartPanel, BorderLayout.CENTER);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.setTitle("diagram");
        });
    }


}
