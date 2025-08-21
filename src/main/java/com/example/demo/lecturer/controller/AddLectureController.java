package com.example.demo.lecturer.controller;

import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.authenticator.service.AuthenticatorsService;
import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.lecturer.service.LecturesService;
import com.example.demo.majorStaff.service.StaffsService;
import com.example.demo.person.service.PersonsService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/staff-home/lectures-list/")
public class AddLectureController {
    private final StaffsService staffsService;
    private final LecturesService lecturesService;
    private final PersonsService personsService;
    private final AuthenticatorsService authenticatorsService;

    public AddLectureController(StaffsService staffsService, LecturesService lecturesService,
                                PersonsService personsService, AuthenticatorsService authenticatorsService) {
        this.staffsService = staffsService;
        this.lecturesService = lecturesService;
        this.personsService = personsService;
        this.authenticatorsService = authenticatorsService;
    }

    @GetMapping("/add-lecture")
    public String showAddLecturePage(Model model) {
        model.addAttribute("lecture", new MajorLecturers());
        model.addAttribute("majors", staffsService.getStaffMajor());
        return "AddLecture";
    }

    @PostMapping("/add-lecture")
    public String addLecture(
            @Valid @ModelAttribute("lecture") MajorLecturers lecture,
            BindingResult bindingResult,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            Model model,
            RedirectAttributes redirectAttributes) {
        List<String> errors = new ArrayList<>();
        errors.addAll(lecturesService.lectureValidation(lecture, avatarFile));
        if (bindingResult.hasErrors()) {
            errors.addAll(bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList()));
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("majors", staffsService.getStaffMajor());
            return "AddLecture";
        }

        try {
            String randomPassword = lecturesService.generateRandomPassword(12);
            String lectureId = lecturesService.generateUniqueLectureId(
                    staffsService.getStaffMajor().getMajorId(),
                    lecture.getCreatedDate() != null ? lecture.getCreatedDate() : LocalDate.now());
            lecture.setId(lectureId);
            if (avatarFile != null && !avatarFile.isEmpty()) {
                lecture.setAvatar(avatarFile.getBytes());
            }
            lecturesService.addLecturers(lecture, randomPassword);

            Authenticators authenticators = new Authenticators();
            authenticators.setPersonId(lectureId);
            authenticators.setPerson(personsService.getPersonById(lectureId));
            authenticators.setPassword(randomPassword);
            authenticatorsService.createAuthenticator(authenticators);

            redirectAttributes.addFlashAttribute("successMessage", "Lecture added successfully!");
            return "redirect:/staff-home/lectures-list";
        } catch (IOException e) {
            errors.add("Failed to process avatar: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("majors", staffsService.getStaffMajor());
            return "AddLecture";
        } catch (Exception e) {
            errors.add("An error occurred while adding the lecture: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("majors", staffsService.getStaffMajor());
            return "AddLecture";
        }
    }
}