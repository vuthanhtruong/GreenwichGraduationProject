package com.example.demo.major.controller;

import com.example.demo.admin.service.AdminsService;
import com.example.demo.major.model.Majors;
import com.example.demo.major.service.MajorsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/admin-home/majors-list")
public class ListMajorsController {
    private static final Logger logger = LoggerFactory.getLogger(ListMajorsController.class);
    private final MajorsService majorsService;
    private final AdminsService adminsService;

    public ListMajorsController(MajorsService majorsService, AdminsService adminsService) {
        this.majorsService = majorsService;
        this.adminsService = adminsService;
    }

    @GetMapping("")
    public String listMajors(Model model) {
        try {
            List<Majors> majors = majorsService.getMajors();
            model.addAttribute("major", new Majors());
            model.addAttribute("majors", majors);
            return "ListMajors";
        } catch (Exception e) {
            logger.error("Error listing majors: {}", e.getMessage());
            model.addAttribute("error", "Error listing majors: " + e.getMessage());
            model.addAttribute("major", new Majors());
            model.addAttribute("majors", List.of());
            return "ListMajors";
        }
    }

    @GetMapping("/avatar/{majorId}")
    @ResponseBody
    public ResponseEntity<byte[]> getMajorAvatar(@PathVariable String majorId) {
        Majors major = majorsService.getMajorById(majorId);
        if (major != null && major.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(major.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}