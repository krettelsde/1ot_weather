package com.app.Iot_weather;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class WeatherController {


    @GetMapping("/")
    public String greeting(Model model) throws IOException {

        WeatherService service = new WeatherService();

        JsonNode myJsonNode = service.getForecast().get("forecast");

        model.addAttribute("myJsonNode", myJsonNode);

        return "weather";
    }

}