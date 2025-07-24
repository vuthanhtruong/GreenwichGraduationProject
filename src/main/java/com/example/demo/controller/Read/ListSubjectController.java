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

}