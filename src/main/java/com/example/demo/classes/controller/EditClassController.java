package com.example.demo.classes.controller;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.staff.model.Staffs;
import com.example.demo.classes.service.MajorClassesService;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.majorSubject.service.MajorSubjectsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/staff-home/classes-list")
public class EditClassController {

    private final MajorClassesService classesService;
    private final StaffsService staffsService;
    private final MajorSubjectsService subjectsService;

    @Autowired
    public EditClassController(MajorClassesService classesService, StaffsService staffsService, MajorSubjectsService subjectsService) {
        this.classesService = classesService;
        this.staffsService = staffsService;
        this.subjectsService = subjectsService;
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
                return "redirect:/staff-home/classes-list/search-classes";
            }
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize != null ? pageSize : 5);
            return "redirect:/staff-home/classes-list";
        }

        MajorClasses editClass = classesService.getClassById(classId);
        if (editClass == null) {
            redirectAttributes.addFlashAttribute("errors", List.of("Class not found."));
            if (source.equals("search")) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize != null ? pageSize : 5);
                return "redirect:/staff-home/classes-list/search-classes";
            }
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize != null ? pageSize : 5);
            return "redirect:/staff-home/classes-list";
        }

        model.addAttribute("class", editClass);
        model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
        model.addAttribute("source", source);
        model.addAttribute("searchType", searchType != null ? searchType : "name");
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
        return "EditFormClass";
    }

    @PutMapping("/edit-class")
    public String editClass(
            @Valid @ModelAttribute("class") MajorClasses classObj,
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
            model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
            model.addAttribute("source", source);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
            return "EditFormClass";
        }

        try {
            classesService.editClass(classObj.getClassId(), classObj);
            redirectAttributes.addFlashAttribute("successMessage", "Class edited successfully!");
            if (source.equals("search") && searchType != null && keyword != null) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize != null ? pageSize : 5);
                return "redirect:/staff-home/classes-list/search-classes";
            }
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize != null ? pageSize : 5);
            return "redirect:/staff-home/classes-list";
        } catch (Exception e) {
            model.addAttribute("editErrors", List.of("Error updating class: " + e.getMessage()));
            model.addAttribute("class", classObj);
            model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
            model.addAttribute("source", source);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
            return "EditFormClass";
        }
    }
}