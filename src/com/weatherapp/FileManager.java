package com.weatherapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class FileManager {
    private static final String FILE_PATH = "weather_data.csv";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    public List<WeatherEntry> loadEntries() {
        List<WeatherEntry> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 4) {
                    try {
                        Date date = DATE_FORMAT.parse(data[0]);
                        double temperature = Double.parseDouble(data[1]);
                        int humidity = Integer.parseInt(data[2]);
                        String condition = data[3];
                        entries.add(new WeatherEntry(date, temperature, humidity, condition));
                    } catch (ParseException | NumberFormatException e) {
                        // Ignore malformed lines
                    }
                }
            }
        } catch (IOException e) {
            // File might not exist yet, which is fine
        }
        return entries;
    }

    public void saveEntries(List<WeatherEntry> entries) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (WeatherEntry entry : entries) {
                String line = String.format("%s,%.1f,%d,%s",
                        DATE_FORMAT.format(entry.getDate()),
                        entry.getTemperature(),
                        entry.getHumidity(),
                        entry.getCondition());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
