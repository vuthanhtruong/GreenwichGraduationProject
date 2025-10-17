package com.example.demo.user.majorLecturer.controller;

import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
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
@RequestMapping("/major-lecturer-home/personal-page")
@PreAuthorize("hasRole('LECTURER')")
public class MajorLecturerPersonalPageController {
    private static final Logger logger = LoggerFactory.getLogger(MajorLecturerPersonalPageController.class);
    private final MajorLecturersService lecturesService;

    public MajorLecturerPersonalPageController(MajorLecturersService lecturesService) {
        this.lecturesService = lecturesService;
    }

    @GetMapping
    public String showLecturerPersonalPage(Model model) {
        try {
            MajorLecturers lecturer = lecturesService.getMajorLecturer();
            model.addAttribute("lecturer", lecturer);
            return "MajorLecturerPersonalPage";
        } catch (Exception e) {
            return "MajorLecturerPersonalPage";
        }
    }

    @GetMapping("/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getLecturerAvatar(@PathVariable String id) {
        MajorLecturers lecturer = lecturesService.getLecturerById(id);
        if (lecturer != null && lecturer.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(lecturer.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}