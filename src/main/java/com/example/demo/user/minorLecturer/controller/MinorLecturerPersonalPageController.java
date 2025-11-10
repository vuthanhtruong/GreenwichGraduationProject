// File: MinorLecturerPersonalPageController.java
package com.example.demo.user.minorLecturer.controller;

import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.user.minorLecturer.service.MinorLecturersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/minor-lecturer-home/personal-page")
@PreAuthorize("hasRole('LECTURER')")
public class MinorLecturerPersonalPageController {

    private static final Logger logger = LoggerFactory.getLogger(MinorLecturerPersonalPageController.class);
    private final MinorLecturersService minorLecturersService;

    public MinorLecturerPersonalPageController(MinorLecturersService minorLecturersService) {
        this.minorLecturersService = minorLecturersService;
    }

    @GetMapping
    public String showLecturerPersonalPage(Model model) {
        try {
            MinorLecturers minorLecturer = minorLecturersService.getMinorLecturer();
            model.addAttribute("lecturer", minorLecturer);
            return "MinorLecturerPersonalPage";
        } catch (Exception e) {
            logger.error("Error loading minor lecturer personal page", e);
            return "MinorLecturerPersonalPage";
        }
    }

    @GetMapping("/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getLecturerAvatar(@PathVariable String id) {
        MinorLecturers lecturer = minorLecturersService.getMinorLecturerById(id);
        if (lecturer != null && lecturer.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(lecturer.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}