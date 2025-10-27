package com.example.demo.user.deputyStaff.controller;

import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/deputy-staff-home")
public class DeputyStaffController {
    private final DeputyStaffsService deputyStaffsService;

    public DeputyStaffController(DeputyStaffsService deputyStaffsService) {
        this.deputyStaffsService = deputyStaffsService;
    }

    @GetMapping
    public String getDeputyStaff(Model model) {
        model.addAttribute("deputyStaffs", deputyStaffsService.getDeputyStaff());
        return "DeputyStaffHome";
    }
}
