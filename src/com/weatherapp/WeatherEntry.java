package com.weatherapp;

import java.util.Date;

public class WeatherEntry {
    private Date date;
    private double temperature;
    private int humidity;
    private String condition;

    public WeatherEntry(Date date, double temperature, int humidity, String condition) {
        this.date = date;
        this.temperature = temperature;
        this.humidity = humidity;
        this.condition = condition;
    }

    // Getters and setters
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
