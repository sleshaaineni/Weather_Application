package com.weatherapp;

import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;

public class WeatherTableModel extends AbstractTableModel {
    private List<WeatherEntry> entries;
    private final String[] columnNames = {"Date", "Temperature (°C)", "Humidity (%)", "Condition"};
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    public WeatherTableModel() {
        this.entries = new ArrayList<>();
    }

    public WeatherTableModel(List<WeatherEntry> entries) {
        this.entries = new ArrayList<>(entries);
    }

    @Override
    public int getRowCount() {
        return entries.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        WeatherEntry entry = entries.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return DATE_FORMAT.format(entry.getDate());
            case 1:
                return entry.getTemperature();
            case 2:
                return entry.getHumidity();
            case 3:
                return entry.getCondition();
            default:
                return null;
        }
    }

    public void addEntry(WeatherEntry entry) {
        entries.add(entry);
        fireTableRowsInserted(entries.size() - 1, entries.size() - 1);
    }

    public void removeEntry(int rowIndex) {
        entries.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public List<WeatherEntry> getEntries() {
        return entries;
    }
    
    public WeatherEntry getEntry(int rowIndex) {
        return entries.get(rowIndex);
    }

    public void setEntries(List<WeatherEntry> newEntries) {
        this.entries = new ArrayList<>(newEntries);
        fireTableDataChanged();
    }
}