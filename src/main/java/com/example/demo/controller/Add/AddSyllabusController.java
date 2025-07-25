package com.example.demo.controller.Add;

import com.example.demo.entity.Syllabuses;
import com.example.demo.entity.Staffs;
import com.example.demo.entity.Subjects;
import com.example.demo.service.SubjectsService;
import com.example.demo.service.SyllabusesService;
import com.example.demo.service.StaffsService;
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
import java.util.UUID;

@Controller
@RequestMapping("/staff-home")
public class AddSyllabusController {

    private final SyllabusesService syllabusesService;
    private final StaffsService staffsService;
    private final SubjectsService subjectsService;

    @Autowired
    public AddSyllabusController(SyllabusesService syllabusesService, StaffsService staffsService, SubjectsService subjectsService) {
        this.syllabusesService = syllabusesService;
        this.staffsService = staffsService;
        this.subjectsService = subjectsService;
    }

    @GetMapping("/syllabuses-list")
    public String showSyllabusForm(Model model, @RequestParam(value = "subjectId", required = false) String subjectId) {
        model.addAttribute("newSyllabus", new Syllabuses());
        if (subjectId != null) {
            Subjects subject = subjectsService.getSubjectById(subjectId);
            model.addAttribute("subject", subject != null ? subject : new Subjects());
            model.addAttribute("syllabuses", subject != null ? syllabusesService.getSyllabusesBySubject(subject) : null);
        } else {
            model.addAttribute("subject", new Subjects());
        }
        return "SyllabusesList";
    }

    @GetMapping("/major-subjects-list/view-syllabus/{subjectId}")
    public String viewSyllabusBySubject(@PathVariable("subjectId") String subjectId, Model model) {
        Subjects subject = subjectsService.getSubjectById(subjectId);
        if (subject == null) {
            model.addAttribute("errorMessage", "Subject not found.");
            model.addAttribute("subject", new Subjects());
            model.addAttribute("newSyllabus", new Syllabuses());
            return "SyllabusesList";
        }
        model.addAttribute("subject", subject);
        model.addAttribute("newSyllabus", new Syllabuses());
        model.addAttribute("syllabuses", syllabusesService.getSyllabusesBySubject(subject));
        return "SyllabusesList";
    }

    @PostMapping("/syllabuses-list/add-syllabus")
    public String addSyllabus(@Valid @ModelAttribute("newSyllabus") Syllabuses syllabus,
                              BindingResult result,
                              @RequestParam("id") String subjectId,
                              @RequestParam("uploadFile") MultipartFile file,
                              Model model,
                              RedirectAttributes redirectAttributes) throws IOException {
        Subjects subject = subjectsService.getSubjectById(subjectId);
        if (subject == null) {
            model.addAttribute("errorMessage", "Subject not found. Please select a subject.");
            model.addAttribute("subject", new Subjects());
            model.addAttribute("newSyllabus", syllabus);
            return "SyllabusesList";
        }

        // Log received data for debugging
        System.out.println("Received - Subject ID: " + subjectId);
        System.out.println("Syllabus name: " + syllabus.getSyllabusName());
        System.out.println("Status: " + syllabus.getStatus());
        System.out.println("File name: " + (file != null ? file.getOriginalFilename() : "null"));
        System.out.println("File size: " + (file != null ? file.getSize() : "null"));

        // Set subject and creator before validation
        syllabus.setSubject(subject);
        Staffs creator = staffsService.getStaffs();
        if (creator == null) {
            model.addAttribute("errorMessage", "Cannot determine current staff. Please log in again.");
            model.addAttribute("subject", subject);
            model.addAttribute("newSyllabus", syllabus);
            model.addAttribute("syllabuses", syllabusesService.getSyllabusesBySubject(subject));
            return "SyllabusesList";
        }
        syllabus.setCreator(creator);

        // Validate file
        if (file == null || file.isEmpty()) {
            model.addAttribute("fileError", "Please select a file to upload.");
            model.addAttribute("subject", subject);
            model.addAttribute("newSyllabus", syllabus);
            model.addAttribute("syllabuses", syllabusesService.getSyllabusesBySubject(subject));
            return "SyllabusesList";
        }

        // Restrict file type to PDF
        if (!file.getContentType().equals("application/pdf")) {
            model.addAttribute("fileError", "Only PDF files are allowed.");
            model.addAttribute("subject", subject);
            model.addAttribute("newSyllabus", syllabus);
            model.addAttribute("syllabuses", syllabusesService.getSyllabusesBySubject(subject));
            return "SyllabusesList";
        }

        // Validate syllabus fields
        if (result.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder("Please correct the following errors: ");
            result.getFieldErrors().forEach(error ->
                    errorMsg.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; "));
            model.addAttribute("errorMessage", errorMsg.toString());
            model.addAttribute("subject", subject);
            model.addAttribute("newSyllabus", syllabus);
            model.addAttribute("syllabuses", syllabusesService.getSyllabusesBySubject(subject));
            return "SyllabusesList";
        }

        // Set other properties
        String syllabusId = "SYL" + UUID.randomUUID().toString().substring(0, 8);
        syllabus.setSyllabusId(syllabusId);
        syllabus.setFileData(file.getBytes());

        try {
            syllabusesService.addSyllabus(syllabus);
            redirectAttributes.addFlashAttribute("successMessage", "Syllabus added successfully!");
            return "redirect:/staff-home/major-subjects-list/view-syllabus/" + subjectId;
        } catch (Exception e) {
            System.err.println("Error adding syllabus: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "Failed to add syllabus: " + e.getMessage());
            model.addAttribute("subject", subject);
            model.addAttribute("newSyllabus", syllabus);
            model.addAttribute("syllabuses", syllabusesService.getSyllabusesBySubject(subject));
            return "SyllabusesList";
        }
    }

    @GetMapping("/syllabuses-list/download-file/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("id") String syllabusId) {
        Syllabuses syllabus = syllabusesService.getSyllabusById(syllabusId);
        if (syllabus == null || syllabus.getFileData() == null || syllabus.getFileData().length == 0) {
            return ResponseEntity.notFound().build();
        }

        // Set headers for download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String fileName = syllabus.getSyllabusName().replaceAll("[^a-zA-Z0-9.-]", "_") + ".pdf";
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(fileName)
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(syllabus.getFileData());
    }
}