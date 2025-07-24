package com.example.demo.controller.Add;

import com.example.demo.entity.Classes;
import com.example.demo.entity.Subjects;
import com.example.demo.service.ClassesService;
import com.example.demo.service.StaffsService;
import com.example.demo.service.SubjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home/classes-list")
public class AddClassController {

    private final ClassesService classesService;
    private final StaffsService staffsService;
    private final SubjectsService subjectsService;
    @Autowired
    public AddClassController(ClassesService classesService, StaffsService staffsService, SubjectsService subjectsService) {
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

        List<String> errors = new ArrayList<>();

        // Validate input
        if (nameClass == null || nameClass.trim().isEmpty()) {
            errors.add("Class name cannot be blank.");
        }

        if (slotQuantity == null || slotQuantity <= 0) {
            errors.add("Total slots must be greater than 0.");
        }

        if (subjectId == null || subjectId.isEmpty()) {
            errors.add("Subject is required.");
        }

        // Check for duplicate class name
        if (nameClass != null && !nameClass.trim().isEmpty() && classesService.getClassByName(nameClass) != null) {
            errors.add("Class name is already in use.");
        }

        // Fetch Subjects by subjectId
        Subjects subject = null;
        if (subjectId != null && !subjectId.isEmpty()) {
            subject = subjectsService.getSubjectById(subjectId);
            if (subject == null) {
                errors.add("Invalid subject selected.");
            }
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("classes", classesService.ClassesByMajor(staffsService.getMajors()));
            model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getMajors()));
            return "ClassesList";
        }

        try {
            // Create new Classes object
            Classes newClass = new Classes();
            newClass.setNameClass(nameClass);
            newClass.setSlotQuantity(slotQuantity);
            newClass.setSubject(subject);

            // Generate class ID and set other fields
            String majorId = staffsService.getMajors() != null ? staffsService.getMajors().getMajorId() : "default";
            String classId = generateUniqueClassId(majorId, LocalDateTime.now());
            newClass.setClassId(classId);
            newClass.setCreatedAt(LocalDateTime.now());

            // Save the class
            classesService.addClass(newClass);
            redirectAttributes.addFlashAttribute("successMessage", "Class added successfully!");
            return "redirect:/staff-home/classes-list";
        } catch (Exception e) {
            errors.add("Failed to add class: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("classes", classesService.ClassesByMajor(staffsService.getMajors()));
            model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getMajors()));
            return "ClassesList";
        }
    }

    private String generateUniqueClassId(String majorId, LocalDateTime createdDate) {
        String prefix;
        switch (majorId) {
            case "major001":
                prefix = "CLSGBH";
                break;
            case "major002":
                prefix = "CLSGCH";
                break;
            case "major003":
                prefix = "CLSGDH";
                break;
            case "major004":
                prefix = "CLSGKH";
                break;
            default:
                prefix = "CLSGEN";
                break;
        }

        String year = String.format("%02d", createdDate.getYear() % 100);
        String date = String.format("%02d%02d", createdDate.getMonthValue(), createdDate.getDayOfMonth());

        String classId;
        SecureRandom random = new SecureRandom();
        do {
            String randomDigit = String.valueOf(random.nextInt(10));
            classId = prefix + year + date + randomDigit;
        } while (classesService.getClassById(classId) != null);
        return classId;
    }
}