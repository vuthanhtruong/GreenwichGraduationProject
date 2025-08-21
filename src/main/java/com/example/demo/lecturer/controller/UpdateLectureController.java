package com.example.demo.lecturer.controller;

import com.example.demo.entity.Enums.Gender;
import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.lecturer.service.LecturesService;
import com.example.demo.person.service.PersonsService;
import com.example.demo.majorStaff.service.StaffsService;
import com.example.demo.student.service.StudentsService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/staff-home/lectures-list")
public class UpdateLectureController {
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final LecturesService lecturesService;
    private final PersonsService personsService;

    public UpdateLectureController(StaffsService staffsService, LecturesService lecturesService, StudentsService studentsService, PersonsService personsService) {
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.lecturesService = lecturesService;
        this.personsService = personsService;
    }

    @PostMapping("/edit-lecture-form")
    public String handleEditLecturePost(@RequestParam String id, Model model) {
        MajorLecturers lecture = lecturesService.getLecturerById(id);
        model.addAttribute("lecture", lecture);
        model.addAttribute("genders", Arrays.asList(Gender.values()));
        return "EditLectureForm";
    }

    @PutMapping("/edit-lecture-form")
    public String updateLecture(
            @Valid @ModelAttribute("lecture") MajorLecturers lecture,
            BindingResult bindingResult,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            RedirectAttributes redirectAttributes,
            ModelMap modelMap) {
        List<String> errors = lecturesService.lectureValidation(lecture, avatarFile);
        if (bindingResult.hasErrors()) {
            errors.addAll(bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList()));
        }

        if (!errors.isEmpty()) {
            modelMap.addAttribute("errors", errors);
            modelMap.addAttribute("genders", Arrays.asList(Gender.values()));
            return "EditLectureForm";
        }

        try {
            if (!personsService.existsPersonById(lecture.getId())) {
                redirectAttributes.addFlashAttribute("error", "Lecture with ID " + lecture.getId() + " not found.");
                return "redirect:/staff-home/lectures-list";
            }
            lecturesService.updateLecturer(lecture.getId(), lecture, avatarFile);
            redirectAttributes.addFlashAttribute("successMessage", "Lecture updated successfully!");
        } catch (IOException | MessagingException e) {
            redirectAttributes.addFlashAttribute("error", "Error updating lecture: " + e.getMessage());
            modelMap.addAttribute("errors", List.of("Error updating lecture: " + e.getMessage()));
            modelMap.addAttribute("genders", Arrays.asList(Gender.values()));
            return "EditLectureForm";
        }
        return "redirect:/staff-home/lectures-list";
    }
}