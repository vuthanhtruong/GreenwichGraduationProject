package com.example.demo.EmailTemplates.controller;

import com.example.demo.EmailTemplates.model.EmailTemplates;
import com.example.demo.EmailTemplates.service.EmailTemplatesService;
import com.example.demo.entity.Enums.EmailTemplateTypes;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/admin-home/email-templates")
@PreAuthorize("hasRole('ADMIN')")
public class EmailTemplatesController {

    private final EmailTemplatesService emailTemplatesService;

    public EmailTemplatesController(EmailTemplatesService emailTemplatesService) {
        this.emailTemplatesService = emailTemplatesService;
    }

    // 📌 List all email templates with pagination
    @GetMapping("")
    public String listEmailTemplates(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {

        if (pageSize == null) {
            pageSize = (Integer) session.getAttribute("templatePageSize");
            if (pageSize == null) {
                pageSize = 20;
            }
        }
        session.setAttribute("templatePageSize", pageSize);

        long totalTemplates = emailTemplatesService.numberOfTemplates();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalTemplates / pageSize));
        page = Math.max(1, Math.min(page, totalPages));

        int firstResult = (page - 1) * pageSize;
        List<EmailTemplates> templates = Optional.ofNullable(
                emailTemplatesService.getPaginatedTemplates(firstResult, pageSize)
        ).orElse(Collections.emptyList());

        model.addAttribute("templates", templates);
        model.addAttribute("emailTemplate", new EmailTemplates());
        model.addAttribute("editEmailTemplate", new EmailTemplates());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalTemplates", totalTemplates);
        model.addAttribute("emailTemplateTypes", EmailTemplateTypes.values());

        return "ListEmailTemplates";
    }

    // 📌 Add new template
    @PostMapping("/add-template")
    public String addEmailTemplate(
            @Valid @ModelAttribute("emailTemplate") EmailTemplates emailTemplate,
            @RequestParam(value = "headerImageFile", required = false) MultipartFile headerImageFile,
            @RequestParam(value = "bannerImageFile", required = false) MultipartFile bannerImageFile,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        Map<String, String> errors = emailTemplatesService.templateValidation(emailTemplate, headerImageFile, bannerImageFile);

        if (bindingResult.hasErrors() || !errors.isEmpty()) {
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            // luôn add templates để tránh null
            model.addAttribute("templates", emailTemplatesService.getPaginatedTemplates(0, pageSizeFromSession(session)));
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSizeFromSession(session));
            model.addAttribute("totalTemplates", emailTemplatesService.numberOfTemplates());
            model.addAttribute("emailTemplateTypes", EmailTemplateTypes.values());
            return "ListEmailTemplates";
        }

        try {
            emailTemplatesService.addTemplate(emailTemplate, headerImageFile, bannerImageFile);
            redirectAttributes.addFlashAttribute("message", "Email template added successfully!");
            return "redirect:/admin-home/email-templates";
        } catch (Exception e) {
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", Map.of("general", "Failed to save template: " + e.getMessage()));
            // luôn add templates khi exception
            model.addAttribute("templates", emailTemplatesService.getPaginatedTemplates(0, pageSizeFromSession(session)));
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSizeFromSession(session));
            model.addAttribute("totalTemplates", emailTemplatesService.numberOfTemplates());
            model.addAttribute("emailTemplateTypes", EmailTemplateTypes.values());
            return "ListEmailTemplates";
        }
    }

    // 📌 Show edit form
    @PostMapping("/edit-template-form")
    public String showEditTemplateForm(@RequestParam("id") Integer id, Model model) {
        Optional<EmailTemplates> templateOpt = emailTemplatesService.findById(id);
        if (templateOpt.isEmpty()) {
            return "redirect:/admin-home/email-templates?errorMessage=Email template not found.";
        }
        model.addAttribute("emailTemplate", templateOpt.get());
        model.addAttribute("emailTemplateTypes", EmailTemplateTypes.values());
        return "EditEmailTemplate";
    }

    // 📌 Update template
    @PutMapping("/edit-template-form")
    public String updateEmailTemplate(
            @Valid @ModelAttribute("emailTemplate") EmailTemplates emailTemplate,
            @RequestParam(value = "headerImageFile", required = false) MultipartFile headerImageFile,
            @RequestParam(value = "bannerImageFile", required = false) MultipartFile bannerImageFile,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        Map<String, String> errors = emailTemplatesService.templateValidation(emailTemplate, headerImageFile, bannerImageFile);

        if (bindingResult.hasErrors() || !errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("emailTemplate", emailTemplate);
            model.addAttribute("emailTemplateTypes", EmailTemplateTypes.values());
            return "EditEmailTemplate";
        }

        try {
            emailTemplatesService.updateTemplate(emailTemplate.getId(), emailTemplate, headerImageFile, bannerImageFile);
            redirectAttributes.addFlashAttribute("message", "Email template updated successfully!");
            return "redirect:/admin-home/email-templates";
        } catch (Exception e) {
            model.addAttribute("errors", Map.of("general", "Failed to update template: " + e.getMessage()));
            model.addAttribute("emailTemplate", emailTemplate);
            model.addAttribute("emailTemplateTypes", EmailTemplateTypes.values());
            return "EditEmailTemplate";
        }
    }

    // 📌 Delete template
    @DeleteMapping("/delete-template/{id}")
    public String deleteEmailTemplate(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Optional<EmailTemplates> templateOpt = emailTemplatesService.findById(id);
        if (templateOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email template not found.");
            return "redirect:/admin-home/email-templates";
        }

        emailTemplatesService.deleteTemplate(id);
        redirectAttributes.addFlashAttribute("message", "Email template deleted successfully!");
        return "redirect:/admin-home/email-templates";
    }

    // 📌 Get template image
    @GetMapping("/image/{id}/{type}")
    @ResponseBody
    public ResponseEntity<byte[]> getTemplateImage(@PathVariable Integer id, @PathVariable String type) {
        Optional<EmailTemplates> templateOpt = emailTemplatesService.findById(id);
        if (templateOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        EmailTemplates template = templateOpt.get();
        byte[] image = null;

        if ("header".equalsIgnoreCase(type)) {
            image = template.getHeaderImage();
        } else if ("banner".equalsIgnoreCase(type)) {
            image = template.getBannerImage();
        }

        if (image != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(image);
        }
        return ResponseEntity.notFound().build();
    }

    // 📌 Get template by type
    @GetMapping("/type/{type}")
    @ResponseBody
    public ResponseEntity<EmailTemplates> getTemplateByType(@PathVariable String type) {
        try {
            EmailTemplateTypes templateType = EmailTemplateTypes.valueOf(type.toUpperCase());
            Optional<EmailTemplates> templateOpt = emailTemplatesService.findByType(templateType);
            return templateOpt.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 📌 Helper
    private int pageSizeFromSession(HttpSession session) {
        return (Integer) Optional.ofNullable(session.getAttribute("templatePageSize")).orElse(5);
    }
}
