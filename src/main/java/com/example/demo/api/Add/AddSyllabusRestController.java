package com.example.demo.api.Add;

import com.example.demo.entity.Syllabuses;
import com.example.demo.entity.MajorSubjects;
import com.example.demo.service.SubjectsService;
import com.example.demo.service.SyllabusesService;
import com.example.demo.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/staff-home/major-subjects-list/")
public class AddSyllabusRestController {

    private final SyllabusesService syllabusesService;
    private final StaffsService staffsService;
    private final SubjectsService subjectsService;

    @Autowired
    public AddSyllabusRestController(SyllabusesService syllabusesService, StaffsService staffsService, SubjectsService subjectsService) {
        this.syllabusesService = syllabusesService;
        this.staffsService = staffsService;
        this.subjectsService = subjectsService;
    }

    @PostMapping("/syllabuses-list/add-syllabus")
    public ResponseEntity<?> addSyllabus(
            @Valid @ModelAttribute("newSyllabus") Syllabuses syllabus,
            @RequestParam("subjectId") String subjectId,
            @RequestParam("uploadFile") MultipartFile file,
            HttpSession session) throws IOException {
        MajorSubjects subject = subjectsService.getSubjectById(subjectId);
        List<String> errors = new ArrayList<>();

        // Perform all validations
        validateSyllabus(syllabus, file, subject, errors);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            // Set subject and creator
            syllabus.setSubject(subject);
            // Set other properties
            String syllabusId = generateUniqueSyllabusId(subject.getMajor().getMajorId(), LocalDate.now());
            syllabus.setSyllabusId(syllabusId);
            syllabus.setFileData(file.getBytes());
            syllabus.setFileType(file.getContentType());

            syllabusesService.addSyllabus(syllabus);
            session.setAttribute("currentSubjectId", subjectId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Syllabus added successfully with ID: " + syllabusId);
        } catch (Exception e) {
            errors.add("Failed to add syllabus: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
        }
    }

    private void validateSyllabus(Syllabuses syllabus, MultipartFile file, MajorSubjects subject, List<String> errors) {
        // Custom validations
        if (subject == null) {
            errors.add("Subject not found. Please select a subject.");
        }

        if (file == null || file.isEmpty()) {
            errors.add("Please select a file to upload.");
        } else {
            Set<String> allowedTypes = Set.of(
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "text/plain",
                    "application/vnd.ms-powerpoint",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                    "application/zip",
                    "application/x-zip-compressed",
                    "application/octet-stream" // Fallback for some ZIP files
            );
            if (!allowedTypes.contains(file.getContentType())) {
                errors.add("Only PDF, DOC, DOCX, TXT, PPT, PPTX, or ZIP files are allowed.");
            }
        }

        if (syllabus.getSyllabusName() == null || syllabus.getSyllabusName().trim().isEmpty()) {
            errors.add("Syllabus name cannot be blank.");
        } else if (!isValidSyllabusName(syllabus.getSyllabusName())) {
            errors.add("Syllabus name is not valid. Only letters, numbers, spaces, and standard punctuation are allowed.");
        }
    }

    private boolean isValidSyllabusName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String nameRegex = "^[\\p{L}\\p{N}][\\p{L}\\p{N} .'-]{1,99}$";
        return name.matches(nameRegex);
    }

    private String generateUniqueSyllabusId(String majorId, LocalDate createdDate) {
        String prefix;
        switch (majorId) {
            case "major001":
                prefix = "SYLGBH";
                break;
            case "major002":
                prefix = "SYLGCH";
                break;
            case "major003":
                prefix = "SYLGDH";
                break;
            case "major004":
                prefix = "SYLGKH";
                break;
            default:
                prefix = "SYLGEN";
                break;
        }

        String year = String.format("%02d", createdDate.getYear() % 100);
        String date = String.format("%02d%02d", createdDate.getMonthValue(), createdDate.getDayOfMonth());

        String syllabusId;
        SecureRandom random = new SecureRandom();
        do {
            String randomDigit = String.valueOf(random.nextInt(10));
            syllabusId = prefix + year + date + randomDigit;
        } while (syllabusesService.getSyllabusById(syllabusId) != null);
        return syllabusId;
    }

    @PostMapping("/syllabuses-list/view-file")
    public ResponseEntity<byte[]> viewFile(@RequestParam("syllabusId") String syllabusId, HttpSession session) {
        Syllabuses syllabus = syllabusesService.getSyllabusById(syllabusId);
        String subjectId = (String) session.getAttribute("currentSubjectId");
        if (syllabus == null || subjectId == null || syllabus.getFileData() == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(syllabus.getFileType()));
        headers.setContentDisposition(ContentDisposition.builder("inline")
                .filename(syllabus.getSyllabusName().replaceAll("[^a-zA-Z0-9.-]", "_") + getFileExtension(syllabus.getFileType()))
                .build());
        return ResponseEntity.ok()
                .headers(headers)
                .body(syllabus.getFileData());
    }

    private String getFileExtension(String contentType) {
        switch (contentType) {
            case "application/pdf":
                return ".pdf";
            case "application/msword":
                return ".doc";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                return ".docx";
            case "text/plain":
                return ".txt";
            case "application/vnd.ms-powerpoint":
                return ".ppt";
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
                return ".pptx";
            case "application/zip":
            case "application/x-zip-compressed":
            case "application/octet-stream":
                return ".zip";
            default:
                return "";
        }
    }
}