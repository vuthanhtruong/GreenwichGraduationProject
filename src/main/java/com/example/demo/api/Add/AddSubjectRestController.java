package com.example.demo.api.Add;

import com.example.demo.entity.Subjects;
import com.example.demo.service.StaffsService;
import com.example.demo.service.SubjectsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/staff-home")
@PreAuthorize("hasRole('STAFF')")
public class AddSubjectRestController {

    private final SubjectsService subjectsService;
    private final StaffsService staffsService;

    @Autowired
    public AddSubjectRestController(SubjectsService subjectsService, StaffsService staffsService) {
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;
    }

    @PostMapping("/major-subjects-list/add-subject")
    public ResponseEntity<?> addSubject(
            @Valid @ModelAttribute("newSubject") Subjects newSubject) {

        List<String> errors = new ArrayList<>();

        if (subjectsService.checkNameSubject(newSubject) != null) {
            errors.add("Subject name already taken.");
        }

        if (newSubject.getSubjectName() == null || newSubject.getSubjectName().trim().isEmpty()) {
            errors.add("Subject name cannot be blank.");
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            // Set creator and major
            newSubject.setCreator(staffsService.getStaffs());
            newSubject.setMajor(staffsService.getMajors());

            // Generate unique ID
            String subjectId = generateUniqueSubjectId(staffsService.getMajors().getMajorId(), LocalDate.now());
            newSubject.setSubjectId(subjectId);

            subjectsService.addSubject(newSubject);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Subject added successfully with ID: " + subjectId);
        } catch (Exception e) {
            errors.add("Failed to add subject: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
        }
    }

    private String generateUniqueSubjectId(String majorId, LocalDate createdDate) {
        String prefix;
        switch (majorId) {
            case "major001":
                prefix = "SUBGBH";
                break;
            case "major002":
                prefix = "SUBGCH";
                break;
            case "major003":
                prefix = "SUBGDH";
                break;
            case "major004":
                prefix = "SUBGKH";
                break;
            default:
                prefix = "SUBGEN";
                break;
        }

        String year = String.format("%02d", createdDate.getYear() % 100);
        String date = String.format("%02d%02d", createdDate.getMonthValue(), createdDate.getDayOfMonth());

        String subjectId;
        SecureRandom random = new SecureRandom();
        do {
            String randomDigit = String.valueOf(random.nextInt(10));
            subjectId = prefix + year + date + randomDigit;
        } while (subjectsService.getSubjectById(subjectId) != null);
        return subjectId;
    }
}