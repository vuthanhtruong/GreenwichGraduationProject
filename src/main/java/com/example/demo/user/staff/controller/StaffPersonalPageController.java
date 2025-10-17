package com.example.demo.user.staff.controller;

import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/staff-home/personal-page")
@PreAuthorize("hasRole('STAFF')")
public class StaffPersonalPageController {
    private static final Logger logger = LoggerFactory.getLogger(StaffPersonalPageController.class);
    private final StaffsService staffsService;

    public StaffPersonalPageController(StaffsService staffsService) {
        this.staffsService = staffsService;
    }

    @GetMapping
    public String showStaffPersonalPage(Model model, HttpSession session) {
        try {
            Staffs staff = staffsService.getStaff();
            if (staff == null) {
                logger.warn("No authenticated staff found");
                model.addAttribute("errors", List.of("No authenticated staff found."));
                model.addAttribute("staff", new Staffs());
                model.addAttribute("staffForm", new Staffs());
                model.addAttribute("avatarUrl", "/DefaultAvatar/Staff_Male.png");
                return "StaffPersonalPage";
            }

            model.addAttribute("staff", staff);
            model.addAttribute("staffForm", staff);
            model.addAttribute("avatarUrl", staff.getAvatar() != null ? "/staff-home/personal-page/avatar/" + staff.getId() : staff.getDefaultAvatarPath());
            return "StaffPersonalPage";
        } catch (Exception e) {
            logger.error("Error loading personal page: {}", e.getMessage(), e);
            model.addAttribute("errors", List.of("Error loading personal page: " + e.getMessage()));
            model.addAttribute("staff", new Staffs());
            model.addAttribute("staffForm", new Staffs());
            model.addAttribute("avatarUrl", "/DefaultAvatar/Staff_Male.png");
            return "StaffPersonalPage";
        }
    }

    @PostMapping("/edit")
    public String editStaffPersonalPage(
            @ModelAttribute("staffForm") Staffs staffForm,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {
            Staffs staff = staffsService.getStaff();
            if (staff == null) {
                logger.warn("No authenticated staff found for edit");
                redirectAttributes.addFlashAttribute("errors", List.of("No authenticated staff found."));
                return "redirect:/staff-home/personal-page";
            }

            if (!staff.getId().equals(staffForm.getId())) {
                logger.warn("Authenticated staff ID {} attempted to edit staff ID {}", staff.getId(), staffForm.getId());
                redirectAttributes.addFlashAttribute("errors", List.of("Not authorized to edit this profile."));
                return "redirect:/staff-home/personal-page";
            }

            // Get majorId and campusId from existing staff
            String majorId = staff.getMajorManagement() != null ? staff.getMajorManagement().getMajorId() : null;
            String campusId = staff.getCampus() != null ? staff.getCampus().getCampusId() : null;

            Map<String, String> errors = staffsService.validateStaff(staffForm, avatarFile, majorId, campusId);
            if (!errors.isEmpty()) {
                List<String> errorList = new ArrayList<>();
                errors.forEach((field, msg) -> errorList.add(field + ": " + msg));
                model.addAttribute("errors", errorList);
                model.addAttribute("staff", staff);
                model.addAttribute("staffForm", staffForm);
                model.addAttribute("avatarUrl", staff.getAvatar() != null ? "/staff-home/personal-page/avatar/" + staff.getId() : staff.getDefaultAvatarPath());
                return "StaffPersonalPage";
            }

            staffsService.editStaff(staffForm, avatarFile);
            logger.info("Staff ID {} updated successfully", staff.getId());
            redirectAttributes.addFlashAttribute("message", "Profile updated successfully.");
            return "redirect:/staff-home/personal-page";
        } catch (IOException e) {
            logger.error("IO error updating staff profile: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errors", List.of("Error uploading avatar: " + e.getMessage()));
            return "redirect:/staff-home/personal-page";
        } catch (Exception e) {
            logger.error("Error updating staff profile: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errors", List.of("Error updating profile: " + e.getMessage()));
            return "redirect:/staff-home/personal-page";
        }
    }

    @GetMapping("/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getStaffAvatar(@PathVariable String id) {
        Staffs staff = staffsService.getStaffById(id);
        if (staff != null && staff.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(staff.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}