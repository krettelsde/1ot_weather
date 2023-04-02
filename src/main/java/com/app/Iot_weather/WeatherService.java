package com.app.Iot_weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;


@Service

public class WeatherService {
    private static final String WEATHER_API_URL = "https://www.ilmateenistus.ee/ilma_andmed/xml/forecast.php?lang=eng";

    private final RestTemplate restTemplate;

    public WeatherService() {

        this.restTemplate = new RestTemplate();
    }

    public String getWeatherForecastXml() {
        ResponseEntity<String> response = restTemplate.getForEntity(WEATHER_API_URL, String.class);
        return response.getBody();
    }


    @Scheduled(cron = "0 0 * * * *") // runs every hour at minute 0
    public void hourlyTemperatureAverages() throws IOException {
        saveTemperatureAverages();
    }
    public JsonNode getForecast() throws IOException {

        String xmlString = getWeatherForecastXml();

        // Create an instance of XmlMapper
        XmlMapper xmlMapper = new XmlMapper();

        // Convert XML string to Java object
        Object object = xmlMapper.readValue(xmlString, Object.class);

        // Create an instance of ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Convert Java object to JSON string
        String jsonString = objectMapper.writeValueAsString(object);


        JsonNode root = objectMapper.readTree(jsonString);


        return root;


    }

    public void saveTemperatureAverages() throws IOException {
        JsonNode forecasts = getForecast();

        JsonNode forecast = forecasts.get("forecast").get(0);
        JsonNode night = forecast.get("night");
        int sumMin = 0;
        JsonNode places = night.get("place");
        for (JsonNode place : places) {
            sumMin+= place.get("tempmin").asInt();

        }

        int minAvg = sumMin / places.size();


        JsonNode day = forecast.get("day");
        int sumMax = 0;
        JsonNode places2 = day.get("place");
        for (JsonNode place : places2) {
            sumMax+= place.get("tempmax").asInt();

        }


        int maxAvg = sumMax / places.size();

        String filename = "temperature-averages.txt";
        File file = new File(filename);
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.printf("Average minimum temperature: %d°C%n", minAvg);
            writer.printf("Average maximum temperature: %d°C%n", maxAvg);
        }catch (IOException e) {
            System.err.println("Error saving temperature averages to file: " + e.getMessage());
            return;
        }
        System.out.printf("Temperature averages saved to file: %s%n", filename);

    }
}

