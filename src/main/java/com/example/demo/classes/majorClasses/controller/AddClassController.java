package com.example.demo.classes.majorClasses.controller;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.majorClasses.service.MajorClassesService;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.subject.majorSubject.service.MajorSubjectsService;
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
@RequestMapping("/staff-home/classes-list")
public class AddClassController {

    private final MajorClassesService classesService;
    private final StaffsService staffsService;
    private final MajorSubjectsService subjectsService;

    @Autowired
    public AddClassController(MajorClassesService classesService, StaffsService staffsService, MajorSubjectsService subjectsService) {
        this.classesService = classesService;
        this.staffsService = staffsService;
        this.subjectsService = subjectsService;
    }

    @PostMapping("/add-class")
    public String addClass(
            @RequestParam("nameClass") String nameClass,
            @RequestParam("slotQuantity") Integer slotQuantity,
            @RequestParam("subjectId") String subjectId,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        List<String> errors = new ArrayList<>();
        MajorClasses newClass = new MajorClasses();
        newClass.setNameClass(nameClass);
        newClass.setSlotQuantity(slotQuantity);
        newClass.setSubject(subjectsService.getSubjectById(subjectId));

        errors.addAll(classesService.validateClass(newClass, newClass.getClassId()));

        if (!errors.isEmpty()) {
            model.addAttribute("openAddOverlay", true); // 👈 thêm cờ này
            model.addAttribute("errors", errors);
            model.addAttribute("newClass", newClass);
            model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
            model.addAttribute("classes", classesService.getPaginatedClasses(0, (Integer) session.getAttribute("classPageSize") != null ? (Integer) session.getAttribute("classPageSize") : 5, staffsService.getStaffMajor()));
            model.addAttribute("currentPageClasses", session.getAttribute("currentPageClasses") != null ? session.getAttribute("currentPageClasses") : 1);
            model.addAttribute("totalPagesClasses", session.getAttribute("totalPagesClasses") != null ? session.getAttribute("totalPagesClasses") : 1);
            model.addAttribute("pageSize", session.getAttribute("classPageSize") != null ? session.getAttribute("classPageSize") : 5);
            model.addAttribute("totalClasses", classesService.numberOfClasses(staffsService.getStaffMajor()));
            return "ClassesList";
        }

        try {
            String majorId = staffsService.getStaffMajor() != null ? staffsService.getStaffMajor().getMajorId() : "default";
            String classId = classesService.generateUniqueClassId(majorId, LocalDateTime.now());
            newClass.setClassId(classId);
            newClass.setCreatedAt(LocalDateTime.now());

            classesService.addClass(newClass);
            redirectAttributes.addFlashAttribute("successMessage", "Class added successfully!");
            return "redirect:/staff-home/classes-list";
        } catch (Exception e) {
            errors.add("Failed to add class: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("newClass", newClass);
            model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
            model.addAttribute("classes", classesService.getPaginatedClasses(0, (Integer) session.getAttribute("classPageSize") != null ? (Integer) session.getAttribute("classPageSize") : 5, staffsService.getStaffMajor()));
            model.addAttribute("currentPageClasses", session.getAttribute("currentPageClasses") != null ? session.getAttribute("currentPageClasses") : 1);
            model.addAttribute("totalPagesClasses", session.getAttribute("totalPagesClasses") != null ? session.getAttribute("totalPagesClasses") : 1);
            model.addAttribute("pageSize", session.getAttribute("classPageSize") != null ? session.getAttribute("classPageSize") : 5);
            model.addAttribute("totalClasses", classesService.numberOfClasses(staffsService.getStaffMajor()));
            return "ClassesList";
        }
    }
}