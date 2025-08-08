package com.example.demo.controller.AddByStaff;

import com.example.demo.entity.Syllabuses;
import com.example.demo.entity.MajorSubjects;
import com.example.demo.service.MajorSubjectsService;
import com.example.demo.service.SyllabusesService;
import com.example.demo.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/staff-home/major-subjects-list/")
public class AddSyllabusController {

    private final SyllabusesService syllabusesService;
    private final StaffsService staffsService;
    private final MajorSubjectsService subjectsService;

    @Autowired
    public AddSyllabusController(SyllabusesService syllabusesService, StaffsService staffsService, MajorSubjectsService subjectsService) {
        this.syllabusesService = syllabusesService;
        this.staffsService = staffsService;
        this.subjectsService = subjectsService;
    }

    @PostMapping("/syllabuses-list/add-syllabus")
    public String addSyllabus(
            @Valid @ModelAttribute("newSyllabus") Syllabuses syllabus,
            BindingResult result,
            @RequestParam("subjectId") String subjectId,
            @RequestParam("uploadFile") MultipartFile file,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) throws IOException {
        MajorSubjects subject = subjectsService.getSubjectById(subjectId);
        List<String> errors = new ArrayList<>();

        // Perform all validations
        validateSyllabus(syllabus, result, file, subject, errors);

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("subject", subject != null ? subject : new MajorSubjects());
            model.addAttribute("newSyllabus", syllabus);
            model.addAttribute("syllabuses", subject != null ? syllabusesService.getSyllabusesBySubject(subject) : null);
            return "SyllabusesList";
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
            redirectAttributes.addFlashAttribute("successMessage", "Syllabus added successfully!");
            session.setAttribute("currentSubjectId", subjectId);
            return "redirect:/staff-home/major-subjects-list/syllabuses-list";
        } catch (Exception e) {
            errors.add("Failed to add syllabus: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("subject", subject != null ? subject : new MajorSubjects());
            model.addAttribute("newSyllabus", syllabus);
            model.addAttribute("syllabuses", subject != null ? syllabusesService.getSyllabusesBySubject(subject) : null);
            return "SyllabusesList";
        }
    }

    private void validateSyllabus(Syllabuses syllabus, BindingResult result, MultipartFile file, MajorSubjects subject, List<String> errors) {
        // Annotation-based validation errors
        if (result.hasErrors()) {
            result.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

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

    private boolean isValidStatus(String status) {
        String statusRegex = "^(Active|Inactive|Draft)$";
        return status == null || status.matches(statusRegex);
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

        // Extract year (last two digits) and date (MMdd) from createdDate
        String year = String.format("%02d", createdDate.getYear() % 100); // e.g., 2025 -> 25
        String date = String.format("%02d%02d", createdDate.getMonthValue(), createdDate.getDayOfMonth()); // e.g., July 26 -> 0726

        String syllabusId;
        SecureRandom random = new SecureRandom();
        do {
            // Generate 1 random digit to make total length 10 (6 prefix + 2 year + 4 date + 1 random)
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
            case "application/octet-stream": // Fallback for ZIP
                return ".zip";
            default:
                return "";
        }
    }
}