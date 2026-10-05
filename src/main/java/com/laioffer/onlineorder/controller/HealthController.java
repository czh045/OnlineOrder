package com.laioffer.onlineorder.controller;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController

public class HealthController {

    // GET /health：
    @GetMapping("/health")

    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
