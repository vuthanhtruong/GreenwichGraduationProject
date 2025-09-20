package com.example.demo.login.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage(HttpServletRequest request, Model model) {
        // Lấy error/message từ session (Flash attribute)
        Object errorMsg = request.getSession().getAttribute("error");
        Object successMsg = request.getSession().getAttribute("message");

        if (errorMsg != null) {
            model.addAttribute("error", errorMsg);
            request.getSession().removeAttribute("error"); // Xóa sau khi dùng
        }
        if (successMsg != null) {
            model.addAttribute("message", successMsg);
            request.getSession().removeAttribute("message");
        }

        return "login"; // login.html
    }
}
