package com.weatherapp;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class WeatherApiClient {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public WeatherEntry fetchWeather(String city) throws Exception {
        // 1. Geocode city to get latitude and longitude
        String geocodeUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" + city;
        HttpRequest geocodeRequest = HttpRequest.newBuilder()
                .uri(URI.create(geocodeUrl))
                .build();
        HttpResponse<String> geocodeResponse = httpClient.send(geocodeRequest, HttpResponse.BodyHandlers.ofString());

        if (geocodeResponse.statusCode() != 200) {
            throw new RuntimeException("Failed to geocode city");
        }

        JSONObject geocodeJson = new JSONObject(geocodeResponse.body());
        if (!geocodeJson.has("results")) {
            throw new RuntimeException("City not found");
        }
        JSONObject location = geocodeJson.getJSONArray("results").getJSONObject(0);
        double latitude = location.getDouble("latitude");
        double longitude = location.getDouble("longitude");

        // 2. Fetch weather data
        String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&current_weather=true";
        HttpRequest weatherRequest = HttpRequest.newBuilder()
                .uri(URI.create(weatherUrl))
                .build();
        HttpResponse<String> weatherResponse = httpClient.send(weatherRequest, HttpResponse.BodyHandlers.ofString());

        if (weatherResponse.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch weather data");
        }

        JSONObject weatherJson = new JSONObject(weatherResponse.body()).getJSONObject("current_weather");
        double temperature = weatherJson.getDouble("temperature");
        // The API doesn't provide humidity directly, so we'll use a placeholder
        int humidity = 50; // Placeholder
        String condition = getWeatherCondition(weatherJson.getInt("weathercode"));
        
        LocalDate localDate = LocalDate.now();
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());


        return new WeatherEntry(date, temperature, humidity, condition);
    }

    private String getWeatherCondition(int weatherCode) {
        if (weatherCode == 0) return "Clear sky";
        if (weatherCode >= 1 && weatherCode <= 3) return "Mainly clear, partly cloudy, and overcast";
        if (weatherCode >= 45 && weatherCode <= 48) return "Fog and depositing rime fog";
        if (weatherCode >= 51 && weatherCode <= 55) return "Drizzle: Light, moderate, and dense intensity";
        if (weatherCode >= 56 && weatherCode <= 57) return "Freezing Drizzle: Light and dense intensity";
        if (weatherCode >= 61 && weatherCode <= 65) return "Rain: Slight, moderate and heavy intensity";
        if (weatherCode >= 66 && weatherCode <= 67) return "Freezing Rain: Light and heavy intensity";
        if (weatherCode >= 71 && weatherCode <= 75) return "Snow fall: Slight, moderate, and heavy intensity";
        if (weatherCode == 77) return "Snow grains";
        if (weatherCode >= 80 && weatherCode <= 82) return "Rain showers: Slight, moderate, and violent";
        if (weatherCode >= 85 && weatherCode <= 86) return "Snow showers slight and heavy";
        if (weatherCode == 95) return "Thunderstorm: Slight or moderate";
        if (weatherCode >= 96 && weatherCode <= 99) return "Thunderstorm with slight and heavy hail";
        return "Unknown";
    }
}
