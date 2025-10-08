package com.example.demo.majorSyllabus.controller;

import com.example.demo.majorSubject.model.MajorSubjects;
import com.example.demo.majorSyllabus.model.MajorSyllabuses;
import com.example.demo.majorSyllabus.service.SyllabusesService;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.majorSubject.service.MajorSubjectsService;
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
@RequestMapping("/staff-home/major-subjects-list/syllabuses-list")
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

    @PostMapping("/add-syllabus")
    public String addSyllabus(
            @Valid @ModelAttribute("newSyllabus") MajorSyllabuses syllabus,
            @RequestParam("subjectId") String subjectId,
            @RequestParam(value = "uploadFile", required = false) MultipartFile file,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) throws IOException {
        MajorSubjects subject = subjectsService.getSubjectById(subjectId);
        List<String> errors = syllabusesService.syllabusValidation(syllabus, file);

        if (!errors.isEmpty()) {
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("newSyllabus", syllabus);
            model.addAttribute("subject", subject != null ? subject : new MajorSubjects());
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
            return "SyllabusesList";
        }

        try {
            String syllabusId = generateUniqueSyllabusId(subject.getMajor().getMajorId(), LocalDate.now());
            syllabus.setSyllabusId(syllabusId);
            syllabus.setSubject(subject);
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
            return "redirect:/staff-home/major-subjects-list/syllabuses-list";
        } catch (IOException e) {
            errors.add("Failed to process file: " + e.getMessage());
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("newSyllabus", syllabus);
            model.addAttribute("subject", subject != null ? subject : new MajorSubjects());
            model.addAttribute("syllabuses", syllabusesService.getPaginatedSyllabuses(subjectId, 0, (Integer) session.getAttribute("syllabusPageSize") != null ? (Integer) session.getAttribute("syllabusPageSize") : 5));
            model.addAttribute("currentPage", session.getAttribute("syllabusPage") != null ? session.getAttribute("syllabusPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("syllabusTotalPages") != null ? session.getAttribute("syllabusTotalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("syllabusPageSize") != null ? session.getAttribute("syllabusPageSize") : 5);
            model.addAttribute("totalSyllabuses", syllabusesService.numberOfSyllabuses(subjectId));
            return "SyllabusesList";
        } catch (Exception e) {
            errors.add("An error occurred while adding the syllabus: " + e.getMessage());
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("newSyllabus", syllabus);
            model.addAttribute("subject", subject != null ? subject : new MajorSubjects());
            model.addAttribute("syllabuses", syllabusesService.getPaginatedSyllabuses(subjectId, 0, (Integer) session.getAttribute("syllabusPageSize") != null ? (Integer) session.getAttribute("syllabusPageSize") : 5));
            model.addAttribute("currentPage", session.getAttribute("syllabusPage") != null ? session.getAttribute("syllabusPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("syllabusTotalPages") != null ? session.getAttribute("syllabusTotalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("syllabusPageSize") != null ? session.getAttribute("syllabusPageSize") : 5);
            model.addAttribute("totalSyllabuses", syllabusesService.numberOfSyllabuses(subjectId));
            return "SyllabusesList";
        }
    }

    @PostMapping("/view-file")
    public ResponseEntity<byte[]> viewFile(@RequestParam("syllabusId") String syllabusId, HttpSession session) {
        MajorSyllabuses syllabus = syllabusesService.getSyllabusById(syllabusId);
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