package com.example.demo.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
