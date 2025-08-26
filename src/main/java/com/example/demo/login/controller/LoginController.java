package com.example.demo.login.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {
        if (error != null) {
            if (error.equals("no_email")) {
                model.addAttribute("error", "No email found for OAuth2 login. Please use a valid account.");
            } else if (error.equals("no_role")) {
                model.addAttribute("error", "No role assigned to this account. Please contact the administrator.");
            } else {
                model.addAttribute("error", "Invalid email or password.");
            }
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        return "login";
    }
}