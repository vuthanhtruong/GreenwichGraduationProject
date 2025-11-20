// src/main/java/com/example/demo/user/parentAccount/controller/ParentPersonalPageController.java
package com.example.demo.user.parentAccount.controller;

import com.example.demo.user.parentAccount.model.ParentAccounts;
import com.example.demo.user.parentAccount.service.ParentAccountsService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/parent-home/personal-page")
@PreAuthorize("hasRole('PARENT')")
public class ParentPersonalPageController {

    private static final Logger logger = LoggerFactory.getLogger(ParentPersonalPageController.class);
    private final ParentAccountsService parentAccountsService;

    public ParentPersonalPageController(ParentAccountsService parentAccountsService) {
        this.parentAccountsService = parentAccountsService;
    }

    // ===================== Show Parent Profile =====================
    @GetMapping
    public String showParentPersonalPage(Model model, HttpSession session) {
        try {
            ParentAccounts parent = parentAccountsService.getParent();
            if (parent == null) {
                logger.warn("No authenticated parent found");
                model.addAttribute("errors", List.of("No authenticated parent found."));
                model.addAttribute("parent", new ParentAccounts());
                model.addAttribute("parentForm", new ParentAccounts());
                model.addAttribute("avatarUrl", "/DefaultAvatar/Parent_Male.png");
                return "ParentPersonalPage";
            }

            model.addAttribute("parent", parent);
            model.addAttribute("parentForm", parent);
            model.addAttribute("avatarUrl", parent.getAvatar() != null
                    ? "/parent-home/personal-page/avatar/" + parent.getId()
                    : parent.getDefaultAvatarPath());
            return "ParentPersonalPage";
        } catch (Exception e) {
            logger.error("Error loading parent personal page: {}", e.getMessage(), e);
            model.addAttribute("errors", List.of("Error loading profile: " + e.getMessage()));
            model.addAttribute("parent", new ParentAccounts());
            model.addAttribute("parentForm", new ParentAccounts());
            model.addAttribute("avatarUrl", "/DefaultAvatar/Parent_Male.png");
            return "ParentPersonalPage";
        }
    }

    // ===================== Edit Profile =====================
    @PostMapping("/edit")
    public String editParentProfile(
            @ModelAttribute("parentForm") ParentAccounts parentForm,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        try {
            ParentAccounts currentParent = parentAccountsService.getParent();
            if (currentParent == null) {
                logger.warn("No authenticated parent found for edit");
                redirectAttributes.addFlashAttribute("errors", List.of("No authenticated parent found."));
                return "redirect:/parent-home/personal-page";
            }

            // Security: prevent editing someone else's profile
            if (!currentParent.getId().equals(parentForm.getId())) {
                logger.warn("Parent ID {} attempted to edit ID {}", currentParent.getId(), parentForm.getId());
                redirectAttributes.addFlashAttribute("errors", List.of("Not authorized to edit this profile."));
                return "redirect:/parent-home/personal-page";
            }

            Map<String, String> errors = parentAccountsService.validateParent(parentForm, avatarFile);
            if (!errors.isEmpty()) {
                List<String> errorList = new ArrayList<>();
                errors.forEach((field, msg) -> errorList.add(msg));
                model.addAttribute("errors", errorList);
                model.addAttribute("parent", currentParent);
                model.addAttribute("parentForm", parentForm);
                model.addAttribute("avatarUrl", currentParent.getAvatar() != null
                        ? "/parent-home/personal-page/avatar/" + currentParent.getId()
                        : currentParent.getDefaultAvatarPath());
                return "ParentPersonalPage";
            }

            parentAccountsService.editParent(parentForm, avatarFile);
            logger.info("Parent ID {} updated profile successfully", currentParent.getId());
            redirectAttributes.addFlashAttribute("message", "Profile updated successfully.");
            return "redirect:/parent-home/personal-page";

        } catch (IOException e) {
            logger.error("IO error updating parent avatar: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errors", List.of("Error uploading avatar: " + e.getMessage()));
            return "redirect:/parent-home/personal-page";
        } catch (Exception e) {
            logger.error("Error updating parent profile: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errors", List.of("Error updating profile: " + e.getMessage()));
            return "redirect:/parent-home/personal-page";
        }
    }

    // ===================== Serve Parent Avatar =====================
    @GetMapping("/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getParentAvatar(@PathVariable String id) {
        ParentAccounts parent = parentAccountsService.getParent(); // or getParentById if you have
        if (parent != null && parent.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(parent.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}