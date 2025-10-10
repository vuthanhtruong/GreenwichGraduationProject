package com.example.demo.specializedClasses.controller;

import com.example.demo.specializedClasses.model.SpecializedClasses;
import com.example.demo.specializedClasses.service.SpecializedClassesService;
import com.example.demo.Specialization.service.SpecializationService;
import com.example.demo.staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home/specialized-classes-list")
public class AddSpecializedClassController {

    private final SpecializedClassesService classesService;
    private final StaffsService staffsService;
    private final SpecializationService specializationService;

    @Autowired
    public AddSpecializedClassController(SpecializedClassesService classesService, StaffsService staffsService, SpecializationService specializationService) {
        this.classesService = classesService;
        this.staffsService = staffsService;
        this.specializationService = specializationService;
    }

    @PostMapping("/add-class")
    public String addClass(
            @RequestParam("nameClass") String nameClass,
            @RequestParam("slotQuantity") Integer slotQuantity,
            @RequestParam("specializationId") String specializationId,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        List<String> errors = new ArrayList<>();
        SpecializedClasses newClass = new SpecializedClasses();
        newClass.setNameClass(nameClass);
        newClass.setSlotQuantity(slotQuantity);
        newClass.setSpecialization(specializationService.getSpecializationById(specializationId));

        errors.addAll(classesService.validateClass(newClass, newClass.getClassId()));

        if (!errors.isEmpty()) {
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("newClass", newClass);
            model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaff().getMajorManagement()));
            model.addAttribute("classes", classesService.getPaginatedClasses(0, (Integer) session.getAttribute("classPageSize") != null ? (Integer) session.getAttribute("classPageSize") : 5, staffsService.getStaff().getMajorManagement()));
            model.addAttribute("currentPageClasses", session.getAttribute("currentPageClasses") != null ? session.getAttribute("currentPageClasses") : 1);
            model.addAttribute("totalPagesClasses", session.getAttribute("totalPagesClasses") != null ? session.getAttribute("totalPagesClasses") : 1);
            model.addAttribute("pageSize", session.getAttribute("classPageSize") != null ? session.getAttribute("classPageSize") : 5);
            model.addAttribute("totalClasses", classesService.numberOfClasses(staffsService.getStaff().getMajorManagement()));
            return "SpecializedClassesList";
        }

        try {
            String specializationIdSafe = specializationId != null ? specializationId : "default";
            String classId = classesService.generateUniqueClassId(specializationIdSafe, LocalDateTime.now());
            newClass.setClassId(classId);
            newClass.setCreatedAt(LocalDateTime.now());

            classesService.addClass(newClass);
            redirectAttributes.addFlashAttribute("successMessage", "Class added successfully!");
            return "redirect:/staff-home/specialized-classes-list";
        } catch (Exception e) {
            errors.add("Failed to add class: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("newClass", newClass);
            model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaff().getMajorManagement()));
            model.addAttribute("classes", classesService.getPaginatedClasses(0, (Integer) session.getAttribute("classPageSize") != null ? (Integer) session.getAttribute("classPageSize") : 5, staffsService.getStaff().getMajorManagement()));
            model.addAttribute("currentPageClasses", session.getAttribute("currentPageClasses") != null ? session.getAttribute("currentPageClasses") : 1);
            model.addAttribute("totalPagesClasses", session.getAttribute("totalPagesClasses") != null ? session.getAttribute("totalPagesClasses") : 1);
            model.addAttribute("pageSize", session.getAttribute("classPageSize") != null ? session.getAttribute("classPageSize") : 5);
            model.addAttribute("totalClasses", classesService.numberOfClasses(staffsService.getStaff().getMajorManagement()));
            return "SpecializedClassesList";
        }
    }
}