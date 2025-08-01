package com.example.demo.api.Add;

import com.example.demo.entity.Classes;
import com.example.demo.entity.Subjects;
import com.example.demo.service.ClassesService;
import com.example.demo.service.StaffsService;
import com.example.demo.service.SubjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/staff-home/classes-list")
public class AddClassRestController {

    private final ClassesService classesService;
    private final StaffsService staffsService;
    private final SubjectsService subjectsService;

    @Autowired
    public AddClassRestController(ClassesService classesService, StaffsService staffsService, SubjectsService subjectsService) {
        this.classesService = classesService;
        this.staffsService = staffsService;
        this.subjectsService = subjectsService;
    }

    @PostMapping("/add-class")
    public ResponseEntity<?> addClass(
            @RequestParam("nameClass") String nameClass,
            @RequestParam("slotQuantity") Integer slotQuantity,
            @RequestParam("subjectId") String subjectId) {

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
            return ResponseEntity.badRequest().body(errors);
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

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Class added successfully with ID: " + classId);
        } catch (Exception e) {
            errors.add("Failed to add class: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
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