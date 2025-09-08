package com.example.demo.Other;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff-home")
public class Classroom {
    @GetMapping("/classroom")
    public String classroom(){
        return "classroom";
    }
}
