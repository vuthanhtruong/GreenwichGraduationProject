package com.example.demo.controller.AddByMajorStaff;

import com.example.demo.entity.MajorClasses;
import com.example.demo.service.ClassesService;
import com.example.demo.service.StaffsService;
import com.example.demo.service.MajorSubjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/staff-home/classes-list")
public class AddClassController {

    private final ClassesService classesService;
    private final StaffsService staffsService;
    private final MajorSubjectsService subjectsService;

    @Autowired
    public AddClassController(ClassesService classesService, StaffsService staffsService, MajorSubjectsService subjectsService) {
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
            RedirectAttributes redirectAttributes) {

        MajorClasses newClass = new MajorClasses();
        newClass.setNameClass(nameClass);
        newClass.setSlotQuantity(slotQuantity);
        newClass.setSubject(subjectsService.getSubjectById(subjectId));

        List<String> errors = classesService.validateClass(newClass, null);

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("classes", classesService.ClassesByMajor(staffsService.getStaffMajor()));
            model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
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
            model.addAttribute("classes", classesService.ClassesByMajor(staffsService.getStaffMajor()));
            model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
            return "ClassesList";
        }
    }
}