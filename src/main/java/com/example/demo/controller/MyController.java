package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/")
public class MyController {
    @GetMapping("")
    public String home() {
        return "login";
    }
    @GetMapping("/home")
    public String home2() {
        return "login";
    }
}
