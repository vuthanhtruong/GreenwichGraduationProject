package com.example.demo.controller.Read;

import com.example.demo.entity.Subjects;
import com.example.demo.service.StaffsService;
import com.example.demo.service.SubjectsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home")
public class ListSubjectController {

    private final SubjectsService subjectsService;
    private final StaffsService staffsService;

    @Autowired
    public ListSubjectController(SubjectsService subjectsService, StaffsService staffsService) {
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;

    }

    @GetMapping("/major-subjects-list")
    public String subjectsList(Model model) {
        model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getMajors()));
        model.addAttribute("newSubject", new Subjects());
        return "SubjectsList";
    }

    @PostMapping("/major-subjects-list/add-subject")
    public String addSubject(
            @Valid @ModelAttribute("newSubject") Subjects newSubject,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        List<String> errors = new ArrayList<>();

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        if (subjectsService.checkNameSubject(newSubject) != null) {
            errors.add("Subject name already taken.");
        }

        if (newSubject.getSubjectName() == null) {
            errors.add("Subject name cannot be blank. ");
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getMajors()));
            return "SubjectsList";
        }

        try {
            String subjectId = generateUniqueSubjectId(staffsService.getMajors().getMajorId(), LocalDate.now());
            newSubject.setSubjectId(subjectId);
            subjectsService.addSubject(newSubject);
            redirectAttributes.addFlashAttribute("successMessage", "Subject added successfully!");
            return "redirect:/staff-home/major-subjects-list";
        } catch (Exception e) {
            errors.add("Failed to add subject: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("subjects", subjectsService.getSubjects());
            return "SubjectsList";
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