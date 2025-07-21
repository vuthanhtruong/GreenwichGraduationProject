package com.example.demo.controller;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
@Transactional
public class LoginController {
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Returns the Thymeleaf template 'login.html'
    }
}