package com.example.demo.curriculum.controller;

import com.example.demo.curriculum.model.Curriculum;
import com.example.demo.curriculum.service.CurriculumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/admin-home")
public class CurriculumController {

    private static final Logger logger = LoggerFactory.getLogger(CurriculumController.class);
    private final CurriculumService curriculumService;

    public CurriculumController(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    @GetMapping("/curriculums-list")
    public String listCurriculums(Model model) {
        model.addAttribute("curriculums", curriculumService.getCurriculums());
        // Dùng chung một object để tránh null khi add
        if (!model.containsAttribute("curriculum")) {
            model.addAttribute("curriculum", new Curriculum());
        }
        if (!model.containsAttribute("editCurriculum")) {
            model.addAttribute("editCurriculum", new Curriculum());
        }
        return "ListCurriculums";
    }

    // ===================== ADD =====================
    @PostMapping("/curriculums-list/add-curriculum")
    public String addCurriculum(
            @ModelAttribute("curriculum") Curriculum curriculum,
            Model model,
            RedirectAttributes redirectAttributes) {

        Map<String, String> errors = curriculumService.validateCurriculum(curriculum);

        if (!errors.isEmpty()) {
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("curriculum", curriculum);
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            return "ListCurriculums";
        }

        try {
            curriculumService.addCurriculum(curriculum);
            redirectAttributes.addFlashAttribute("message", "Curriculum added successfully!");
            return "redirect:/admin-home/curriculums-list";
        } catch (Exception e) {
            logger.error("Error adding curriculum: {}", e.getMessage());
            errors.put("general", "Unexpected error: " + e.getMessage());
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("curriculum", curriculum);
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            return "ListCurriculums";
        }
    }

    // ===================== SHOW EDIT FORM (POST → nên chuyển GET sau, nhưng giữ tạm) =====================
    @PostMapping("/curriculums-list/edit-form-curriculum")
    public String showEditForm(@RequestParam String id, Model model, RedirectAttributes ra) {
        Curriculum curriculum = curriculumService.getCurriculumById(id);
        if (curriculum == null) {
            ra.addFlashAttribute("error", "Curriculum not found with ID: " + id);
            return "redirect:/admin-home/curriculums-list";
        }

        model.addAttribute("openEditOverlay", true);
        model.addAttribute("editCurriculum", curriculum);
        model.addAttribute("curriculums", curriculumService.getCurriculums());
        model.addAttribute("curriculum", new Curriculum()); // cho add overlay
        return "ListCurriculums";
    }

    // ===================== EDIT =====================
    @PostMapping("/curriculums-list/edit-curriculum")
    public String editCurriculum(
            @ModelAttribute("editCurriculum") Curriculum curriculum,
            Model model,
            RedirectAttributes redirectAttributes) {

        Map<String, String> errors = curriculumService.validateCurriculum(curriculum);

        if (!errors.isEmpty()) {
            model.addAttribute("openEditOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("editCurriculum", curriculum);
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            model.addAttribute("curriculum", new Curriculum());
            return "ListCurriculums"; // trả về cùng trang, overlay edit sẽ mở
        }

        try {
            curriculumService.updateCurriculum(curriculum);
            redirectAttributes.addFlashAttribute("message", "Curriculum updated successfully!");
            return "redirect:/admin-home/curriculums-list";
        } catch (Exception e) {
            logger.error("Error updating curriculum: {}", e.getMessage());
            errors.put("general", "Unexpected error: " + e.getMessage());
            model.addAttribute("openEditOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("editCurriculum", curriculum);
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            return "ListCurriculums";
        }
    }

    // ===================== DELETE =====================
    @PostMapping("/curriculums-list/delete-curriculum")
    public String deleteCurriculum(@RequestParam String id, RedirectAttributes ra) {
        try {
            curriculumService.deleteCurriculum(id);
            ra.addFlashAttribute("message", "Curriculum deleted successfully!");
        } catch (Exception e) {
            logger.error("Error deleting curriculum: {}", e.getMessage());
            ra.addFlashAttribute("error", "Cannot delete curriculum: " + e.getMessage());
        }
        return "redirect:/admin-home/curriculums-list";
    }
}