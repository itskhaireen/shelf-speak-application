package com.example.bookreviewapi.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Welcome to ShelfSpeak API! See /swagger-ui/index.html for the Frontend.";
    }   
}