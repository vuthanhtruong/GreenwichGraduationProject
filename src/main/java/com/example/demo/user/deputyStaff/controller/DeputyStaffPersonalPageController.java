package com.example.demo.user.deputyStaff.controller;

import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import jakarta.servlet.http.HttpSession;
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

import java.util.List;

@Controller
@RequestMapping("/deputy-staff-home/personal-page")
@PreAuthorize("hasRole('DEPUTY_STAFF')")
public class DeputyStaffPersonalPageController {
    private static final Logger logger = LoggerFactory.getLogger(DeputyStaffPersonalPageController.class);
    private final DeputyStaffsService deputyStaffsService;

    public DeputyStaffPersonalPageController(DeputyStaffsService deputyStaffsService) {
        this.deputyStaffsService = deputyStaffsService;
    }

    @GetMapping
    public String showDeputyStaffPersonalPage(Model model, HttpSession session) {
        try {
            DeputyStaffs deputyStaff = deputyStaffsService.getDeputyStaff();
            if (deputyStaff == null) {
                logger.warn("No authenticated deputy staff found");
                model.addAttribute("errors", List.of("No authenticated deputy staff found."));
                model.addAttribute("deputyStaff", new DeputyStaffs());
                model.addAttribute("avatarUrl", "/DefaultAvatar/DeputyStaff_Male.png");
                return "DeputyStaffPersonalPage";
            }

            model.addAttribute("deputyStaff", deputyStaff);
            model.addAttribute("avatarUrl", deputyStaff.getAvatar() != null ? "/deputy-staff-home/personal-page/avatar/" + deputyStaff.getId() : deputyStaff.getDefaultAvatarPath());
            return "DeputyStaffPersonalPage";
        } catch (Exception e) {
            logger.error("Error loading personal page: {}", e.getMessage(), e);
            model.addAttribute("errors", List.of("Error loading personal page: " + e.getMessage()));
            model.addAttribute("deputyStaff", new DeputyStaffs());
            model.addAttribute("avatarUrl", "/DefaultAvatar/DeputyStaff_Male.png");
            return "DeputyStaffPersonalPage";
        }
    }

    @GetMapping("/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getDeputyStaffAvatar(@PathVariable String id) {
        DeputyStaffs deputyStaff = deputyStaffsService.getDeputyStaffById(id);
        if (deputyStaff != null && deputyStaff.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(deputyStaff.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}