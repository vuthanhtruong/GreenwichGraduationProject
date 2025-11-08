package com.example.demo.lecturers_Classes.minorLecturers_MinorClasses.controller;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.minorClasses.service.MinorClassesService;
import com.example.demo.lecturers_Classes.minorLecturers_MinorClasses.service.MinorLecturers_MinorClassesService;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/deputy-staff-home/minor-classes-list")
@PreAuthorize("hasRole('DEPUTY_STAFF')")
public class MinorLecturerMemberController {

    private final MinorLecturers_MinorClassesService lecturersClassesService;
    private final MinorClassesService minorClassesService;

    @Autowired
    public MinorLecturerMemberController(
            MinorLecturers_MinorClassesService lecturersClassesService,
            MinorClassesService minorClassesService) {
        this.lecturersClassesService = lecturersClassesService;
        this.minorClassesService = minorClassesService;
    }

    @GetMapping("/member-arrangement/lecturers")
    public String showLecturerArrangement(Model model, HttpSession session) {
        String classId = (String) session.getAttribute("currentClassId");
        if (classId == null) return handleNoClassSelected(model);

        MinorClasses clazz = minorClassesService.getClassById(classId);
        if (clazz == null) return handleNoClassSelected(model);

        List<MinorLecturers> lecturersInClass = lecturersClassesService.listLecturersInClass(clazz);
        List<MinorLecturers> lecturersNotInClass = lecturersClassesService.listLecturersNotInClass(clazz);

        model.addAttribute("class", clazz);
        model.addAttribute("lecturersInClass", lecturersInClass);
        model.addAttribute("lecturersNotInClass", lecturersNotInClass);

        return "MinorClassMemberArrangement";
    }

    @PostMapping("/add-lecturer-to-class")
    public String addLecturers(@RequestParam("classId") String classId,
                               @RequestParam(value = "lecturerIds", required = false) List<String> lecturerIds,
                               RedirectAttributes ra, HttpSession session) {
        if (lecturerIds != null && !lecturerIds.isEmpty()) {
            MinorClasses clazz = minorClassesService.getClassById(classId);
            if (clazz != null) {
                lecturersClassesService.addLecturersToClass(clazz, lecturerIds);
                ra.addFlashAttribute("successMessage", lecturerIds.size() + " lecturer(s) added.");
            }
        }
        session.setAttribute("currentClassId", classId);
        return "redirect:/deputy-staff-home/minor-classes-list/member-arrangement/lecturers";
    }

    @PostMapping("/remove-lecturer-from-class")
    public String removeLecturers(@RequestParam("classId") String classId,
                                  @RequestParam(value = "lecturerIds", required = false) List<String> lecturerIds,
                                  RedirectAttributes ra, HttpSession session) {
        if (lecturerIds != null && !lecturerIds.isEmpty()) {
            MinorClasses clazz = minorClassesService.getClassById(classId);
            if (clazz != null) {
                lecturersClassesService.removeLecturerFromClass(clazz, lecturerIds);
                ra.addFlashAttribute("successMessage", lecturerIds.size() + " lecturer(s) removed.");
            }
        }
        session.setAttribute("currentClassId", classId);
        return "redirect:/deputy-staff-home/minor-classes-list/member-arrangement/lecturers";
    }

    private String handleNoClassSelected(Model model) {
        model.addAttribute("errorMessage", "No class selected");
        model.addAttribute("class", new MinorClasses());
        model.addAttribute("lecturersInClass", new ArrayList<>());
        model.addAttribute("lecturersNotInClass", new ArrayList<>());
        return "MinorClassMemberArrangement";
    }
}