package com.weatherapp;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Dimension;
import java.awt.geom.Arc2D;
import java.awt.Font;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChartPanel extends JPanel {
    private List<WeatherEntry> entries;
    private String chartType = "Line Chart";

    public ChartPanel(List<WeatherEntry> entries) {
        this.entries = entries;
        setPreferredSize(new Dimension(400, 200));
    }

    public void setEntries(List<WeatherEntry> entries) {
        this.entries = entries;
        repaint();
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (entries == null || entries.isEmpty()) {
            g.drawString("No data to display.", 10, 20);
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (chartType) {
            case "Line Chart":
                drawLineChart(g2d);
                break;
            case "Bar Chart":
                drawBarChart(g2d);
                break;
            case "Area Chart":
                drawAreaChart(g2d);
                break;
            case "Scatter Plot":
                drawScatterPlot(g2d);
                break;
            case "Pie Chart (Conditions)":
                drawPieChart(g2d);
                break;
        }
    }

    private void drawLineChart(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();
        int padding = 25;
        int labelPadding = 25;

        double maxTemp = entries.stream().mapToDouble(WeatherEntry::getTemperature).max().orElse(0);
        double minTemp = entries.stream().mapToDouble(WeatherEntry::getTemperature).min().orElse(0);

        g2d.setColor(Color.WHITE);
        g2d.fillRect(padding + labelPadding, padding, width - 2 * padding - labelPadding, height - 2 * padding - labelPadding);
        g2d.setColor(Color.BLACK);

        // Draw Y-axis
        g2d.drawLine(padding + labelPadding, height - padding - labelPadding, padding + labelPadding, padding);
        // Draw X-axis
        g2d.drawLine(padding + labelPadding, height - padding - labelPadding, width - padding, height - padding - labelPadding);

        g2d.setColor(Color.BLUE);

        if (entries.size() < 2) {
            // Not enough data to draw a line, maybe draw a point?
            if (entries.size() == 1) {
                int x = padding + labelPadding + (width - 2 * padding - labelPadding) / 2;
                int y = (int) (height - padding - labelPadding - ((entries.get(0).getTemperature() - minTemp) / (maxTemp - minTemp == 0 ? 1 : maxTemp - minTemp)) * (height - 2 * padding - labelPadding));
                g2d.fillOval(x - 3, y - 3, 6, 6);
            }
            return;
        }

        int pointWidth = (width - 2 * padding - labelPadding) / (entries.size() - 1);
        for (int i = 0; i < entries.size() - 1; i++) {
            int x1 = padding + labelPadding + i * pointWidth;
            int y1 = (int) (height - padding - labelPadding - ((entries.get(i).getTemperature() - minTemp) / (maxTemp - minTemp == 0 ? 1 : maxTemp - minTemp)) * (height - 2 * padding - labelPadding));
            int x2 = padding + labelPadding + (i + 1) * pointWidth;
            int y2 = (int) (height - padding - labelPadding - ((entries.get(i + 1).getTemperature() - minTemp) / (maxTemp - minTemp == 0 ? 1 : maxTemp - minTemp)) * (height - 2 * padding - labelPadding));
            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    private void drawBarChart(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();
        int padding = 25;
        int labelPadding = 25;

        double maxTemp = entries.stream().mapToDouble(WeatherEntry::getTemperature).max().orElse(0);

        int barWidth = (width - 2 * padding - labelPadding) / entries.size();
        for (int i = 0; i < entries.size(); i++) {
            int barHeight = (int) ((entries.get(i).getTemperature() / maxTemp) * (height - 2 * padding - labelPadding));
            int x = padding + labelPadding + i * barWidth;
            int y = height - padding - labelPadding - barHeight;
            g2d.setColor(Color.CYAN);
            g2d.fillRect(x, y, barWidth - 2, barHeight);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, barWidth - 2, barHeight);
        }
    }

    private void drawAreaChart(Graphics2D g2d) {
        if (entries.size() < 2) {
            g2d.drawString("Area chart requires at least 2 data points.", 10, 20);
            return;
        }
        int width = getWidth();
        int height = getHeight();
        int padding = 25;
        int labelPadding = 25;

        double maxTemp = entries.stream().mapToDouble(WeatherEntry::getTemperature).max().orElse(0);
        double minTemp = entries.stream().mapToDouble(WeatherEntry::getTemperature).min().orElse(0);

        int[] xPoints = new int[entries.size() + 2];
        int[] yPoints = new int[entries.size() + 2];

        for (int i = 0; i < entries.size(); i++) {
            xPoints[i] = padding + labelPadding + i * (width - 2 * padding - labelPadding) / (entries.size() - 1);
            yPoints[i] = (int) (height - padding - labelPadding - ((entries.get(i).getTemperature() - minTemp) / (maxTemp - minTemp == 0 ? 1 : maxTemp - minTemp)) * (height - 2 * padding - labelPadding));
        }
        xPoints[entries.size()] = padding + labelPadding + (width - 2 * padding - labelPadding);
        yPoints[entries.size()] = height - padding - labelPadding;
        xPoints[entries.size() + 1] = padding + labelPadding;
        yPoints[entries.size() + 1] = height - padding - labelPadding;

        g2d.setColor(new Color(0, 150, 255, 100));
        g2d.fillPolygon(xPoints, yPoints, entries.size() + 2);

        g2d.setColor(Color.BLUE);
        for (int i = 0; i < entries.size() - 1; i++) {
            g2d.drawLine(xPoints[i], yPoints[i], xPoints[i+1], yPoints[i+1]);
        }
    }

    private void drawScatterPlot(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();
        int padding = 25;
        int labelPadding = 25;

        double maxTemp = entries.stream().mapToDouble(WeatherEntry::getTemperature).max().orElse(0);
        double minTemp = entries.stream().mapToDouble(WeatherEntry::getTemperature).min().orElse(0);

        g2d.setColor(Color.RED);

        for (int i = 0; i < entries.size(); i++) {
            int x = padding + labelPadding;
            if (entries.size() > 1) {
                x += i * (width - 2 * padding - labelPadding) / (entries.size() - 1);
            } else {
                x += (width - 2 * padding - labelPadding) / 2;
            }
            int y = (int) (height - padding - labelPadding - ((entries.get(i).getTemperature() - minTemp) / (maxTemp - minTemp == 0 ? 1 : maxTemp - minTemp)) * (height - 2 * padding - labelPadding));
            g2d.fillOval(x - 3, y - 3, 6, 6);
        }
    }

    private void drawPieChart(Graphics2D g2d) {
        Map<String, Long> conditionCounts = entries.stream()
                .collect(Collectors.groupingBy(WeatherEntry::getCondition, Collectors.counting()));

        int width = getWidth();
        int height = getHeight();
        int pieSize = Math.min(width, height) - 50;
        int x = (width - pieSize) / 2;
        int y = (height - pieSize) / 2;

        double total = entries.size();
        double currentAngle = 0.0;
        int i = 0;
        for (Map.Entry<String, Long> entry : conditionCounts.entrySet()) {
            double sweepAngle = (entry.getValue() / total) * 360.0;
            g2d.setColor(getPieColor(i++));
            g2d.fill(new Arc2D.Double(x, y, pieSize, pieSize, currentAngle, sweepAngle, Arc2D.PIE));
            currentAngle += sweepAngle;
        }
        
        // Draw legend
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        i = 0;
        int legendY = y;
        for (Map.Entry<String, Long> entry : conditionCounts.entrySet()) {
            g2d.setColor(getPieColor(i++));
            g2d.fillRect(x + pieSize + 10, legendY, 10, 10);
            g2d.setColor(Color.BLACK);
            g2d.drawString(entry.getKey(), x + pieSize + 25, legendY + 10);
            legendY += 15;
        }
    }

    private Color getPieColor(int i) {
        Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.ORANGE, Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.PINK};
        return colors[i % colors.length];
    }
}
