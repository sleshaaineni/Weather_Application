package com.weatherapp;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsPanel extends JPanel {
    private final JLabel avgTempLabel = createLabel("~ Avg Temp", new Font("Arial", Font.PLAIN, 18));
    private final JLabel maxTempLabel = createLabel("↑ Max Temp", new Font("Arial", Font.BOLD, 18), Color.RED);
    private final JLabel minTempLabel = createLabel("↓ Min Temp", new Font("Arial", Font.BOLD, 18), Color.BLUE);
    private final JLabel commonConditionLabel = createLabel("* Most Common", new Font("Arial", Font.PLAIN, 18));

    public StatisticsPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Statistics"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(avgTempLabel, gbc);

        gbc.gridx = 1;
        add(maxTempLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(minTempLabel, gbc);

        gbc.gridx = 1;
        add(commonConditionLabel, gbc);
    }

    private JLabel createLabel(String text, Font font) {
        return createLabel(text, font, Color.BLACK);
    }

    private JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    public void updateStatistics(List<WeatherEntry> entries) {
        if (entries.isEmpty()) {
            avgTempLabel.setText("~ Avg Temp: N/A");
            maxTempLabel.setText("↑ Max Temp: N/A");
            minTempLabel.setText("↓ Min Temp: N/A");
            commonConditionLabel.setText("* Most Common: N/A");
            return;
        }

        double totalTemp = 0;
        double maxTemp = Double.MIN_VALUE;
        double minTemp = Double.MAX_VALUE;
        for (WeatherEntry entry : entries) {
            totalTemp += entry.getTemperature();
            if (entry.getTemperature() > maxTemp) {
                maxTemp = entry.getTemperature();
            }
            if (entry.getTemperature() < minTemp) {
                minTemp = entry.getTemperature();
            }
        }
        double avgTemp = totalTemp / entries.size();

        avgTempLabel.setText(String.format("~ Avg Temp: %.1f°C", avgTemp));
        maxTempLabel.setText(String.format("↑ Max Temp: %.1f°C", maxTemp));
        minTempLabel.setText(String.format("↓ Min Temp: %.1f°C", minTemp));
        commonConditionLabel.setText("* Most Common: " + getMostCommonCondition(entries));
    }

    private String getMostCommonCondition(List<WeatherEntry> entries) {
        if (entries.isEmpty()) {
            return "N/A";
        }
        return entries.stream()
                .collect(Collectors.groupingBy(WeatherEntry::getCondition, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }
}