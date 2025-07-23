package com.example.demo.controller.Authenticator;
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