// File: MinorSyllabusesListController.java
package com.example.demo.syllabus.minorSyllabuses.controller;

import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.subject.minorSubject.service.MinorSubjectsService;
import com.example.demo.syllabus.minorSyllabuses.model.MinorSyllabuses;
import com.example.demo.syllabus.minorSyllabuses.service.MinorSyllabusesService;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.*;
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
@RequestMapping("/deputy-staff-home/minor-subjects-list/syllabuses-list")
public class MinorSyllabusesListController {

    private final MinorSyllabusesService syllabusesService;
    private final MinorSubjectsService subjectsService;
    private final DeputyStaffsService deputyStaffsService;

    public MinorSyllabusesListController(MinorSyllabusesService syllabusesService, MinorSubjectsService subjectsService, DeputyStaffsService deputyStaffsService) {
        this.syllabusesService = syllabusesService;
        this.subjectsService = subjectsService;
        this.deputyStaffsService = deputyStaffsService;
    }

    @GetMapping
    public String list(Model model, HttpSession session,
                       @RequestParam(defaultValue = "1") int page,
                       @RequestParam(required = false) Integer pageSize) {
        String subjectId = (String) session.getAttribute("currentMinorSubjectId");
        if (subjectId == null) {
            model.addAttribute("errors", List.of("Please select a subject first."));
            return "MinorSyllabusesList";
        }

        if (pageSize == null || pageSize <= 0) pageSize = (Integer) session.getAttribute("minorSyllabusPageSize");
        if (pageSize == null || pageSize <= 0) pageSize = 10;
        pageSize = Math.min(pageSize, 100);
        session.setAttribute("minorSyllabusPageSize", pageSize);

        Long total = syllabusesService.numberOfSyllabuses(subjectId);
        int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));
        page = Math.max(1, Math.min(page, totalPages));
        int first = (page - 1) * pageSize;

        List<MinorSyllabuses> list = syllabusesService.getPaginatedSyllabuses(subjectId, first, pageSize);
        MinorSubjects subject = subjectsService.getSubjectById(subjectId);

        model.addAttribute("syllabuses", list);
        model.addAttribute("subject", subject);
        model.addAttribute("newSyllabus", new MinorSyllabuses());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalSyllabuses", total);
        session.setAttribute("minorSyllabusPage", page);
        session.setAttribute("minorSyllabusTotalPages", totalPages);

        return "MinorSyllabusesList";
    }

    @PostMapping("/add-syllabus")
    public String add(@Valid @ModelAttribute("newSyllabus") MinorSyllabuses syllabus,
                      @RequestParam("subjectId") String subjectId,
                      @RequestParam(value = "uploadFile", required = false) MultipartFile file,
                      Model model, RedirectAttributes ra, HttpSession session) throws IOException {

        MinorSubjects subject = subjectsService.getSubjectById(subjectId);
        List<String> errors = syllabusesService.syllabusValidation(syllabus, file);

        if (!errors.isEmpty()) {
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("newSyllabus", syllabus);
            model.addAttribute("subject", subject);
            return prepareList(model, session, subjectId);
        }

        String syllabusId = generateId("minor", LocalDate.now());
        syllabus.setSyllabusId(syllabusId);
        syllabus.setSubject(subject);
        syllabus.setCreator(deputyStaffsService.getDeputyStaff());
        if (file != null && !file.isEmpty()) {
            syllabus.setFileData(file.getBytes());
            syllabus.setFileType(file.getContentType());
        } else if (session.getAttribute("tempFile") != null) {
            syllabus.setFileData((byte[]) session.getAttribute("tempFile"));
            syllabus.setFileType((String) session.getAttribute("tempFileType"));
        }

        syllabusesService.addSyllabus(syllabus);
        session.removeAttribute("tempFile"); session.removeAttribute("tempFileType");
        ra.addFlashAttribute("message", "Syllabus added!");
        session.setAttribute("currentMinorSubjectId", subjectId);
        return "redirect:/deputy-staff-home/minor-subjects-list/syllabuses-list";
    }

    @PostMapping("/delete-syllabus")
    public String delete(@RequestParam("syllabusId") String syllabusId, RedirectAttributes ra, HttpSession session) {
        String subjectId = (String) session.getAttribute("currentMinorSubjectId");
        MinorSyllabuses s = syllabusesService.getSyllabusById(syllabusId);
        if (s == null || !s.getSubject().getSubjectId().equals(subjectId)) {
            ra.addFlashAttribute("message", "Access denied."); ra.addFlashAttribute("alertClass", "danger");
            return "redirect:/deputy-staff-home/minor-subjects-list/syllabuses-list";
        }
        syllabusesService.deleteSyllabus(s);
        ra.addFlashAttribute("message", "Deleted!"); ra.addFlashAttribute("alertClass", "success");
        return "redirect:/deputy-staff-home/minor-subjects-list/syllabuses-list";
    }

    @PostMapping("/view-file")
    public ResponseEntity<byte[]> view(@RequestParam("syllabusId") String id, HttpSession session) {
        MinorSyllabuses s = syllabusesService.getSyllabusById(id);
        if (s == null || s.getFileData() == null) return ResponseEntity.notFound().build();
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.parseMediaType(s.getFileType()));
        h.setContentDisposition(ContentDisposition.inline()
                .filename(s.getSyllabusName().replaceAll("[^a-zA-Z0-9.-]", "_") + ext(s.getFileType()))
                .build());
        return ResponseEntity.ok().headers(h).body(s.getFileData());
    }

    @PostMapping("/download-file")
    public ResponseEntity<byte[]> download(@RequestParam("syllabusId") String id) {
        MinorSyllabuses s = syllabusesService.getSyllabusById(id);
        if (s == null || s.getFileData() == null) return ResponseEntity.notFound().build();
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.parseMediaType(s.getFileType()));
        h.setContentDisposition(ContentDisposition.attachment()
                .filename(s.getSyllabusName().replaceAll("[^a-zA-Z0-9.-]", "_") + ext(s.getFileType()))
                .build());
        return ResponseEntity.ok().headers(h).body(s.getFileData());
    }

    private String generateId(String minorId, LocalDate date) {
        String prefix = switch (minorId) {
            case "minor001" -> "SYLMNH";
            case "minor002" -> "SYLMPH";
            default -> "SYLMGEN";
        };
        String year = String.format("%02d", date.getYear() % 100);
        String dateStr = String.format("%02d%02d", date.getMonthValue(), date.getDayOfMonth());
        String id;
        SecureRandom r = new SecureRandom();
        do {
            id = prefix + year + dateStr + r.nextInt(10);
        } while (syllabusesService.getSyllabusById(id) != null);
        return id;
    }

    private String ext(String type) {
        return switch (type) {
            case "application/pdf" -> ".pdf";
            case "application/msword" -> ".doc";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> ".docx";
            case "text/plain" -> ".txt";
            case "application/vnd.ms-powerpoint" -> ".ppt";
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> ".pptx";
            case "application/zip", "application/x-zip-compressed", "application/octet-stream" -> ".zip";
            default -> "";
        };
    }

    private String prepareList(Model model, HttpSession session, String subjectId) {
        int page = (Integer) session.getAttribute("minorSyllabusPage") != null ? (Integer) session.getAttribute("minorSyllabusPage") : 1;
        int pageSize = (Integer) session.getAttribute("minorSyllabusPageSize") != null ? (Integer) session.getAttribute("minorSyllabusPageSize") : 10;
        Long total = syllabusesService.numberOfSyllabuses(subjectId);
        int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));
        int first = (page - 1) * pageSize;
        List<MinorSyllabuses> list = syllabusesService.getPaginatedSyllabuses(subjectId, first, pageSize);
        model.addAttribute("syllabuses", list);
        model.addAttribute("subject", subjectsService.getSubjectById(subjectId));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalSyllabuses", total);
        return "MinorSyllabusesList";
    }
}