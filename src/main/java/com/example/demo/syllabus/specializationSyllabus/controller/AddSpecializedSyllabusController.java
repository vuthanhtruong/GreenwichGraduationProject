package com.example.demo.syllabus.specializationSyllabus.controller;

import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import com.example.demo.syllabus.specializationSyllabus.model.SpecializationSyllabuses;
import com.example.demo.syllabus.specializationSyllabus.service.SpecializationSyllabusesService;
import com.example.demo.subject.specializedSubject.service.SpecializedSubjectsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/staff-home/specialized-subjects-list/syllabuses-list")
public class AddSpecializedSyllabusController {

    private final SpecializationSyllabusesService syllabusesService;
    private final SpecializedSubjectsService subjectsService;

    @Autowired
    public AddSpecializedSyllabusController(SpecializationSyllabusesService syllabusesService, SpecializedSubjectsService subjectsService) {
        this.syllabusesService = syllabusesService;
        this.subjectsService = subjectsService;
    }

    @PostMapping("/add-syllabus")
    public String addSyllabus(
            @Valid @ModelAttribute("newSyllabus") SpecializationSyllabuses syllabus,
            @RequestParam("subjectId") String subjectId,
            @RequestParam(value = "uploadFile", required = false) MultipartFile file,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) throws IOException {
        SpecializedSubject subject = subjectsService.getSubjectById(subjectId);
        List<String> errors = syllabusesService.validateSyllabus(syllabus, file,subjectId);

        if (!errors.isEmpty()) {
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("newSyllabus", syllabus);
            model.addAttribute("subject", subject != null ? subject : new SpecializedSubject());
            model.addAttribute("syllabuses", syllabusesService.getPaginatedSyllabuses(subjectId, 0, (Integer) session.getAttribute("syllabusPageSize") != null ? (Integer) session.getAttribute("syllabusPageSize") : 5));
            model.addAttribute("currentPage", session.getAttribute("syllabusPage") != null ? session.getAttribute("syllabusPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("syllabusTotalPages") != null ? session.getAttribute("syllabusTotalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("syllabusPageSize") != null ? session.getAttribute("syllabusPageSize") : 5);
            model.addAttribute("totalSyllabuses", syllabusesService.numberOfSyllabuses(subjectId));
            if (file != null && !file.isEmpty()) {
                try {
                    session.setAttribute("tempFile", file.getBytes());
                    session.setAttribute("tempFileName", file.getOriginalFilename());
                    session.setAttribute("tempFileType", file.getContentType());
                } catch (IOException e) {
                    errors.add("Failed to store file temporarily: " + e.getMessage());
                }
            }
            return "SpecializedSyllabusesList";
        }

        try {
            String syllabusId = generateUniqueSyllabusId(subject.getSpecialization().getSpecializationId(), LocalDate.now());
            syllabus.setSyllabusId(syllabusId);
            syllabus.setSpecializedSubject(subject);
            if (file != null && !file.isEmpty()) {
                syllabus.setFileData(file.getBytes());
                syllabus.setFileType(file.getContentType());
            } else if (session.getAttribute("tempFile") != null) {
                syllabus.setFileData((byte[]) session.getAttribute("tempFile"));
                syllabus.setFileType((String) session.getAttribute("tempFileType"));
            }

            syllabusesService.addSyllabus(syllabus);
            session.removeAttribute("tempFile");
            session.removeAttribute("tempFileName");
            session.removeAttribute("tempFileType");

            redirectAttributes.addFlashAttribute("message", "Syllabus added successfully!");
            session.setAttribute("currentSubjectId", subjectId);
            return "redirect:/staff-home/specialized-subjects-list/syllabuses-list";
        } catch (IOException e) {
            errors.add("Failed to process file: " + e.getMessage());
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("newSyllabus", syllabus);
            model.addAttribute("subject", subject != null ? subject : new SpecializedSubject());
            model.addAttribute("syllabuses", syllabusesService.getPaginatedSyllabuses(subjectId, 0, (Integer) session.getAttribute("syllabusPageSize") != null ? (Integer) session.getAttribute("syllabusPageSize") : 5));
            model.addAttribute("currentPage", session.getAttribute("syllabusPage") != null ? session.getAttribute("syllabusPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("syllabusTotalPages") != null ? session.getAttribute("syllabusTotalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("syllabusPageSize") != null ? session.getAttribute("syllabusPageSize") : 5);
            model.addAttribute("totalSyllabuses", syllabusesService.numberOfSyllabuses(subjectId));
            return "SpecializedSyllabusesList";
        } catch (Exception e) {
            errors.add("An error occurred while adding the syllabus: " + e.getMessage());
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("newSyllabus", syllabus);
            model.addAttribute("subject", subject != null ? subject : new SpecializedSubject());
            model.addAttribute("syllabuses", syllabusesService.getPaginatedSyllabuses(subjectId, 0, (Integer) session.getAttribute("syllabusPageSize") != null ? (Integer) session.getAttribute("syllabusPageSize") : 5));
            model.addAttribute("currentPage", session.getAttribute("syllabusPage") != null ? session.getAttribute("syllabusPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("syllabusTotalPages") != null ? session.getAttribute("syllabusTotalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("syllabusPageSize") != null ? session.getAttribute("syllabusPageSize") : 5);
            model.addAttribute("totalSyllabuses", syllabusesService.numberOfSyllabuses(subjectId));
            return "SpecializedSyllabusesList";
        }
    }

    @PostMapping("/view-file")
    public ResponseEntity<byte[]> viewFile(@RequestParam("syllabusId") String syllabusId, HttpSession session) {
        SpecializationSyllabuses syllabus = syllabusesService.getSyllabusById(syllabusId);
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

    private String generateUniqueSyllabusId(String specializationId, LocalDate createdDate) {
        String prefix;
        switch (specializationId) {
            case "spec001":
                prefix = "SYLSBH";
                break;
            case "spec002":
                prefix = "SYLSCH";
                break;
            case "spec003":
                prefix = "SYLSDH";
                break;
            case "spec004":
                prefix = "SYLSKH";
                break;
            default:
                prefix = "SYLSGEN";
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
    @DeleteMapping("/delete-syllabus")
    public String deleteSyllabus(
            @RequestParam("syllabusId") String syllabusId,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        String subjectId = (String) session.getAttribute("currentSubjectId");
        if (subjectId == null) {
            redirectAttributes.addFlashAttribute("errors", List.of("No subject selected. Please select a subject."));
            return "redirect:/staff-home/specialized-subjects-list";
        }

        SpecializationSyllabuses syllabus = syllabusesService.getSyllabusById(syllabusId);
        if (syllabus == null) {
            redirectAttributes.addFlashAttribute("errors", List.of("Syllabus not found."));
            return "redirect:/staff-home/specialized-subjects-list/syllabuses-list";
        }

        try {
            syllabusesService.deleteSyllabus(syllabusesService.getSyllabusById(syllabusId));
            redirectAttributes.addFlashAttribute("message", "Syllabus deleted successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errors", List.of("Failed to delete syllabus: " + e.getMessage()));
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }

        return "redirect:/staff-home/specialized-subjects-list/syllabuses-list";
    }
}