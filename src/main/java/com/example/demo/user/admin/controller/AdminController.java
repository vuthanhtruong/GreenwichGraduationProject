package com.example.demo.user.admin.controller;

import com.example.demo.user.admin.service.AdminsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin-home")
public class AdminController {
    private final AdminsService adminsService;

    public AdminController(AdminsService adminsService) {
        this.adminsService = adminsService;
    }

    @GetMapping("")
    String adminHome(Model model) {
        model.addAttribute("admin", adminsService.getAdmin());
        model.addAttribute("campus",adminsService.getAdmin().getCampus().getCampusName());
        return "AdminHome";
    }
}
