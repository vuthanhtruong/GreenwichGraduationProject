package com.example.demo.login.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Returns the Thymeleaf template 'login.html'
    }
}