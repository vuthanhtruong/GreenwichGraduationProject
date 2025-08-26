package com.example.demo.campus.controller;

import com.example.demo.admin.model.Admins;
import com.example.demo.admin.service.AdminsService;
import com.example.demo.campus.model.Campuses;
import com.example.demo.campus.service.CampusesService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin-home/campuses-list")
public class ListCampusesController {
    private static final Logger logger = LoggerFactory.getLogger(ListCampusesController.class);
    private final CampusesService campusesService;
    private final AdminsService adminsService;

    public ListCampusesController(CampusesService campusesService, AdminsService adminsService) {
        this.campusesService = campusesService;
        this.adminsService = adminsService;
    }

    @GetMapping("")
    public String listCampuses(Model model) {
        model.addAttribute("campus", new Campuses());
        model.addAttribute("campuses", campusesService.getCampuses());
        return "ListCampuses";
    }

}