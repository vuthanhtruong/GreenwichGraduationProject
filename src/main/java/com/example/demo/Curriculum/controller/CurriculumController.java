package com.example.demo.Curriculum.controller;

import com.example.demo.Curriculum.model.Curriculum;
import com.example.demo.Curriculum.service.CurriculumService;
import com.example.demo.admin.service.AdminsService;
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
    private final AdminsService adminsService;

    public CurriculumController(CurriculumService curriculumService, AdminsService adminsService) {
        this.curriculumService = curriculumService;
        this.adminsService = adminsService;
    }

    @GetMapping("/curriculums-list")
    public String listCurriculums(Model model) {
        model.addAttribute("curriculums", curriculumService.getCurriculums());
        model.addAttribute("curriculum", new Curriculum());
        return "ListCurriculums";
    }

    @PostMapping("/curriculums-list/add")
    public String addCurriculum(
            @ModelAttribute("curriculum") Curriculum curriculum,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (curriculum.getCurriculumId() == null || curriculum.getCurriculumId().isBlank()) {
            curriculum.setCurriculumId(curriculumService.generateUniqueCurriculumId());
        }

        Map<String, String> errors = curriculumService.validateCurriculum(curriculum);
        if (!errors.isEmpty()) {
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
            errors.put("general", "An error occurred while adding the curriculum: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("curriculum", curriculum);
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            return "ListCurriculums";
        }
    }

    @PostMapping("/curriculums-list/edit-form")
    public String showEditCurriculumForm(@RequestParam String id, Model model) {
        try {
            Curriculum curriculum = curriculumService.getCurriculumById(id);
            if (curriculum == null) {
                model.addAttribute("error", "Curriculum with ID " + id + " not found");
                model.addAttribute("curriculum", new Curriculum());
                model.addAttribute("curriculums", curriculumService.getCurriculums());
                return "ListCurriculums";
            }
            model.addAttribute("curriculum", curriculum);
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            return "ListCurriculums";
        } catch (Exception e) {
            logger.error("Error retrieving curriculum with ID {}: {}", id, e.getMessage());
            model.addAttribute("error", "Error retrieving curriculum: " + e.getMessage());
            model.addAttribute("curriculum", new Curriculum());
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            return "ListCurriculums";
        }
    }

    @PostMapping("/curriculums-list/edit")
    public String editCurriculum(
            @ModelAttribute("curriculum") Curriculum curriculum,
            Model model,
            RedirectAttributes redirectAttributes) {
        Map<String, String> errors = curriculumService.validateCurriculum(curriculum);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("curriculum", curriculum);
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            return "ListCurriculums";
        }

        try {
            curriculumService.updateCurriculum(curriculum);
            redirectAttributes.addFlashAttribute("message", "Curriculum updated successfully!");
            return "redirect:/admin-home/curriculums-list";
        } catch (Exception e) {
            logger.error("Error updating curriculum: {}", e.getMessage());
            errors.put("general", "An error occurred while updating the curriculum: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("curriculum", curriculum);
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            return "ListCurriculums";
        }
    }

    @PostMapping("/curriculums-list/delete")
    public String deleteCurriculum(@RequestParam String id, RedirectAttributes redirectAttributes) {
        try {
            curriculumService.deleteCurriculum(id);
            redirectAttributes.addFlashAttribute("message", "Curriculum deleted successfully!");
        } catch (Exception e) {
            logger.error("Error deleting curriculum with ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error deleting curriculum: " + e.getMessage());
        }
        return "redirect:/admin-home/curriculums-list";
    }
}