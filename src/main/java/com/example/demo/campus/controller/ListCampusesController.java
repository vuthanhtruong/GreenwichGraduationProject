package com.example.demo.campus.controller;

import com.example.demo.admin.model.Admins;
import com.example.demo.admin.service.AdminsService;
import com.example.demo.campus.model.Campuses;
import com.example.demo.campus.service.CampusesService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        List<Campuses> campuses = campusesService.getCampuses();
        Map<String, Map<String, Long>> campusCounts = campusesService.getCampusCounts();
        model.addAttribute("campus", new Campuses());
        model.addAttribute("campuses", campuses);
        model.addAttribute("campusCounts", campusCounts);
        return "ListCampuses";
    }

    @GetMapping("/avatar/{campusId}")
    @ResponseBody
    public ResponseEntity<byte[]> getCampusAvatar(@PathVariable String campusId) {
        Campuses campus = campusesService.getCampusById(campusId);
        if (campus != null && campus.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // Adjust based on your avatar format
                    .body(campus.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}