package com.weatherapp;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class WeatherApplication extends JFrame {
    private final FileManager fileManager;
    private final WeatherTableModel tableModel;
    private final JTable table;
    private final ChartPanel chartPanel;
    private final StatisticsPanel statisticsPanel;
    private final WeatherApiClient weatherApiClient;

    private final JTextField cityField = new JTextField(15);
    private final JTextField manualDateField = new JTextField(10);
    private final JTextField manualTempField = new JTextField(5);
    private final JTextField manualHumidityField = new JTextField(5);
    private final JTextField manualConditionField = new JTextField(10);
    
    private final JDateChooser startDateChooser = new JDateChooser();
    private final JDateChooser endDateChooser = new JDateChooser();


    private List<WeatherEntry> allEntries;


    public WeatherApplication() {
        super("Weather Information Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        fileManager = new FileManager();
        weatherApiClient = new WeatherApiClient();
        allEntries = fileManager.loadEntries();
        tableModel = new WeatherTableModel(allEntries);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel with tabs
        JTabbedPane topTabbedPane = new JTabbedPane();

        // API Fetch Panel
        JPanel apiFetchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        apiFetchPanel.add(new JLabel("City:"));
        apiFetchPanel.add(cityField);
        JButton fetchButton = new JButton("Fetch Weather");
        fetchButton.addActionListener(new FetchWeatherListener());
        apiFetchPanel.add(fetchButton);

        // Manual Entry Panel
        JPanel manualEntryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        manualEntryPanel.add(new JLabel("Date (dd-MM-yyyy):"));
        manualEntryPanel.add(manualDateField);
        manualEntryPanel.add(new JLabel("Temp (°C):"));
        manualEntryPanel.add(manualTempField);
        manualEntryPanel.add(new JLabel("Humidity (%):"));
        manualEntryPanel.add(manualHumidityField);
        manualEntryPanel.add(new JLabel("Condition:"));
        manualEntryPanel.add(manualConditionField);
        JButton manualAddButton = new JButton("Add Manual Entry");
        manualAddButton.addActionListener(new ManualAddEntryListener());
        manualEntryPanel.add(manualAddButton);

        topTabbedPane.addTab("Fetch from API", apiFetchPanel);
        topTabbedPane.addTab("Manual Entry", manualEntryPanel);
        
        // Filter and Delete Panel
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(topTabbedPane, BorderLayout.CENTER);
        
        JPanel southControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(new DeleteEntryListener());
        southControlPanel.add(deleteButton);
        
        // Date Filter
        southControlPanel.add(new JLabel("Start Date:"));
        southControlPanel.add(startDateChooser);
        southControlPanel.add(new JLabel("End Date:"));
        southControlPanel.add(endDateChooser);
        JButton filterButton = new JButton("Filter");
        filterButton.addActionListener(new FilterListener());
        southControlPanel.add(filterButton);
        
        controlPanel.add(southControlPanel, BorderLayout.SOUTH);


        // Center panel for table
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Weather Log"));

        // Bottom panel for chart and stats
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        
        // Chart panel
        chartPanel = new ChartPanel(tableModel.getEntries());
        chartPanel.setBorder(BorderFactory.createTitledBorder("Temperature Chart"));
        
        // Chart selection
        String[] chartTypes = {"Line Chart", "Bar Chart", "Area Chart", "Scatter Plot", "Pie Chart (Conditions)"};
        JComboBox<String> chartComboBox = new JComboBox<>(chartTypes);
        chartComboBox.addActionListener(e -> chartPanel.setChartType((String) chartComboBox.getSelectedItem()));
        
        JPanel chartContainerPanel = new JPanel(new BorderLayout());
        chartContainerPanel.add(chartPanel, BorderLayout.CENTER);
        chartContainerPanel.add(chartComboBox, BorderLayout.NORTH);

        // Statistics panel
        statisticsPanel = new StatisticsPanel();
        statisticsPanel.updateStatistics(tableModel.getEntries());

        bottomPanel.add(chartContainerPanel, BorderLayout.CENTER);
        bottomPanel.add(statisticsPanel, BorderLayout.SOUTH);

        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
    
    private void updateUI(List<WeatherEntry> entries) {
        tableModel.setEntries(entries);
        chartPanel.setEntries(entries);
        statisticsPanel.updateStatistics(entries);
    }

    class FetchWeatherListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String city = cityField.getText();
            if (city.isEmpty()) {
                JOptionPane.showMessageDialog(WeatherApplication.this, "Please enter a city name.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                WeatherEntry entry = weatherApiClient.fetchWeather(city);
                allEntries.add(entry);
                fileManager.saveEntries(allEntries);
                updateUI(allEntries);
                cityField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(WeatherApplication.this, "Failed to fetch weather data: " + ex.getMessage(), "API Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    class ManualAddEntryListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String dateStr = manualDateField.getText();
                String tempStr = manualTempField.getText();
                String humidityStr = manualHumidityField.getText();
                String conditionStr = manualConditionField.getText();

                if (dateStr.isEmpty() || tempStr.isEmpty() || humidityStr.isEmpty() || conditionStr.isEmpty()) {
                    JOptionPane.showMessageDialog(WeatherApplication.this, "All fields are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date date = dateFormat.parse(dateStr);
                double temp = Double.parseDouble(tempStr);
                int humidity = Integer.parseInt(humidityStr);

                WeatherEntry entry = new WeatherEntry(date, temp, humidity, conditionStr);
                allEntries.add(entry);
                fileManager.saveEntries(allEntries);
                updateUI(allEntries);

                // Clear fields
                manualDateField.setText("");
                manualTempField.setText("");
                manualHumidityField.setText("");
                manualConditionField.setText("");

            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(WeatherApplication.this, "Invalid date format. Please use dd-MM-yyyy.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(WeatherApplication.this, "Temperature and Humidity must be numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    class DeleteEntryListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                WeatherEntry entryToRemove = tableModel.getEntry(selectedRow);
                allEntries.remove(entryToRemove);
                fileManager.saveEntries(allEntries);
                updateUI(allEntries);
            } else {
                JOptionPane.showMessageDialog(WeatherApplication.this, "Please select a row to delete.", "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    class FilterListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Date startDate = startDateChooser.getDate();
            Date endDate = endDateChooser.getDate();

            if (startDate == null || endDate == null) {
                updateUI(allEntries);
                return;
            }

            List<WeatherEntry> filtered = allEntries.stream()
                    .filter(entry -> !entry.getDate().before(startDate) && !entry.getDate().after(endDate))
                    .collect(Collectors.toList());
            updateUI(filtered);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new WeatherApplication().setVisible(true);
        });
    }
}