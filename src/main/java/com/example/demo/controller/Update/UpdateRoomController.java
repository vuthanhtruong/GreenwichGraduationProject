package com.example.demo.controller.Update;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff-home/lectures-list")
public class UpdateRoomController {
    @GetMapping("/edit-room")
    public String UpdateRoom(){
        return "EditRoomForm";
    }
}
