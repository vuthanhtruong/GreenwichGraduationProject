package com.example.demo.classes.specializedClasses.controller;

import com.example.demo.Specialization.service.SpecializationService;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.classes.specializedClasses.service.SpecializedClassesService;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/staff-home/specialized-classes-list")
public class EditSpecializedClassController {

    private final SpecializedClassesService classesService;
    private final StaffsService staffsService;
    private final SpecializationService specializationService;

    @Autowired
    public EditSpecializedClassController(SpecializedClassesService classesService, StaffsService staffsService, SpecializationService specializationService) {
        this.classesService = classesService;
        this.staffsService = staffsService;
        this.specializationService = specializationService;
    }

    @PostMapping("/edit-class-form")
    public String showEditClassForm(
            @RequestParam("id") String classId,
            @RequestParam(value = "source", required = false, defaultValue = "list") String source,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            Model model,
            RedirectAttributes redirectAttributes) {
        Staffs user = staffsService.getStaff();
        if (!(user instanceof Staffs)) {
            redirectAttributes.addFlashAttribute("errors", List.of("Only staff members can edit classes."));
            if (source.equals("search")) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize != null ? pageSize : 5);
                return "redirect:/staff-home/specialized-classes-list/search-classes";
            }
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize != null ? pageSize : 5);
            return "redirect:/staff-home/specialized-classes-list";
        }

        SpecializedClasses editClass = classesService.getClassById(classId);
        if (editClass == null) {
            redirectAttributes.addFlashAttribute("errors", List.of("Class not found."));
            if (source.equals("search")) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize != null ? pageSize : 5);
                return "redirect:/staff-home/specialized-classes-list/search-classes";
            }
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize != null ? pageSize : 5);
            return "redirect:/staff-home/specialized-classes-list";
        }

        model.addAttribute("class", editClass);
        model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaff().getMajorManagement()));
        model.addAttribute("source", source);
        model.addAttribute("searchType", searchType != null ? searchType : "name");
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
        return "EditFormSpecializedClass";
    }

    @PutMapping("/edit-class")
    public String editClass(
            @Valid @ModelAttribute("class") SpecializedClasses classObj,
            BindingResult bindingResult,
            @RequestParam(value = "source", required = false, defaultValue = "list") String source,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            RedirectAttributes redirectAttributes,
            Model model) {
        List<String> errors = classesService.validateClass(classObj, classObj.getClassId());

        if (!errors.isEmpty() || bindingResult.hasErrors()) {
            errors.addAll(bindingResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList());
            model.addAttribute("editErrors", errors);
            model.addAttribute("class", classObj);
            model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaff().getMajorManagement()));
            model.addAttribute("source", source);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
            return "EditFormSpecializedClass";
        }

        try {
            classesService.editClass(classObj.getClassId(), classObj);
            redirectAttributes.addFlashAttribute("successMessage", "Class edited successfully!");
            if (source.equals("search") && searchType != null && keyword != null) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize != null ? pageSize : 5);
                return "redirect:/staff-home/specialized-classes-list/search-classes";
            }
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize != null ? pageSize : 5);
            return "redirect:/staff-home/specialized-classes-list";
        } catch (Exception e) {
            model.addAttribute("editErrors", List.of("Error updating class: " + e.getMessage()));
            model.addAttribute("class", classObj);
            model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaff().getMajorManagement()));
            model.addAttribute("source", source);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
            return "EditFormSpecializedClass";
        }
    }
}