package com.example.demo.classes.minorClasses.controller;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.minorClasses.service.MinorClassesService;
import com.example.demo.entity.Enums.Sessions;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import com.example.demo.subject.minorSubject.service.MinorSubjectsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/deputy-staff-home/minor-classes-list")
@PreAuthorize("hasRole('DEPUTY_STAFF')")
public class EditMinorClassController {

    private final MinorClassesService classesService;
    private final DeputyStaffsService deputyStaffsService;
    private final MinorSubjectsService subjectsService;

    @Autowired
    public EditMinorClassController(MinorClassesService classesService,
                                    DeputyStaffsService deputyStaffsService,
                                    MinorSubjectsService subjectsService) {
        this.classesService = classesService;
        this.deputyStaffsService = deputyStaffsService;
        this.subjectsService = subjectsService;
    }

    @PostMapping("/edit-class-form")
    public String showEditClassForm(
            @RequestParam String classId,
            @RequestParam(value = "source", required = false, defaultValue = "list") String source,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            Model model,
            RedirectAttributes redirectAttributes) {

        DeputyStaffs user = deputyStaffsService.getDeputyStaff();
        if (user == null) {
            redirectAttributes.addFlashAttribute("errors", List.of("Only deputy staff members can edit classes."));
            return redirectBack(source, searchType, keyword, page, pageSize, redirectAttributes);
        }

        MinorClasses editClass = classesService.getClassById(classId);
        if (editClass == null) {
            redirectAttributes.addFlashAttribute("errors", List.of("Class not found."));
            return redirectBack(source, searchType, keyword, page, pageSize, redirectAttributes);
        }

        model.addAttribute("class", editClass);
        model.addAttribute("subjects", subjectsService.getAllSubjects());
        model.addAttribute("sessions", Sessions.values());
        model.addAttribute("source", source);
        model.addAttribute("searchType", searchType != null ? searchType : "name");
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize != null ? pageSize : 5);

        return "EditMinorClassForm";
    }

    @PutMapping("/edit-class")
    public String editClass(
            @Valid @ModelAttribute("class") MinorClasses classObj,
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
            errors.addAll(bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage()).toList());

            model.addAttribute("editErrors", errors);
            model.addAttribute("class", classObj);
            model.addAttribute("subjects", subjectsService.getAllSubjects());
            model.addAttribute("sessions", Sessions.values());
            model.addAttribute("source", source);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
            return "EditMinorClassForm";
        }

        try {
            classesService.editClass(classObj.getClassId(), classObj);
            redirectAttributes.addFlashAttribute("successMessage", "Class edited successfully!");

            if ("search".equals(source) && searchType != null && !searchType.isEmpty() && keyword != null && !keyword.isEmpty()) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize != null ? pageSize : 5);
                return "redirect:/deputy-staff-home/minor-classes-list/search-classes";
            }

            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize != null ? pageSize : 5);
            return "redirect:/deputy-staff-home/minor-classes-list";

        } catch (Exception e) {
            model.addAttribute("editErrors", List.of("Error updating class: " + e.getMessage()));
            model.addAttribute("class", classObj);
            model.addAttribute("subjects", subjectsService.getAllSubjects());
            model.addAttribute("sessions", Sessions.values());
            model.addAttribute("source", source);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
            return "EditMinorClassForm";
        }
    }

    // Helper method
    private String redirectBack(String source, String searchType, String keyword, int page, Integer pageSize, RedirectAttributes ra) {
        if ("search".equals(source)) {
            ra.addFlashAttribute("searchType", searchType);
            ra.addFlashAttribute("keyword", keyword);
            ra.addFlashAttribute("page", page);
            ra.addFlashAttribute("pageSize", pageSize != null ? pageSize : 5);
            return "redirect:/deputy-staff-home/minor-classes-list/search-classes";
        }
        ra.addFlashAttribute("page", page);
        ra.addFlashAttribute("pageSize", pageSize != null ? pageSize : 5);
        return "redirect:/deputy-staff-home/minor-classes-list";
    }
}