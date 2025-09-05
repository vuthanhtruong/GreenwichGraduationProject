package com.example.demo.lecturer.controller;

import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.authenticator.service.AuthenticatorsService;
import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.lecturer.service.LecturesService;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.person.service.PersonsService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home/lecturers-list/")
public class AddLecturerController {
    private final StaffsService staffsService;
    private final LecturesService lecturesService;
    private final PersonsService personsService;
    private final AuthenticatorsService authenticatorsService;

    public AddLecturerController(StaffsService staffsService, LecturesService lecturesService,
                                 PersonsService personsService, AuthenticatorsService authenticatorsService) {
        this.staffsService = staffsService;
        this.lecturesService = lecturesService;
        this.personsService = personsService;
        this.authenticatorsService = authenticatorsService;
    }

    @GetMapping("/add-lecturer")
    public String showAddLecturePage(Model model) {
        model.addAttribute("lecturer", new MajorLecturers());
        model.addAttribute("majors", staffsService.getStaffMajor());
        return "AddLecturer";
    }

    @PostMapping("/add-lecturer")
    public String addLecture(
            @Valid @ModelAttribute("lecturer") MajorLecturers lecturer,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            Model model,
            RedirectAttributes redirectAttributes) {
        List<String> errors = new ArrayList<>();
        errors.addAll(lecturesService.lectureValidation(lecturer, avatarFile));

        if (!errors.isEmpty()) {
            model.addAttribute("lecturer", lecturer);
            model.addAttribute("errors", errors);
            model.addAttribute("majors", staffsService.getStaffMajor());
            return "AddLecturer";
        }

        try {
            String randomPassword = lecturesService.generateRandomPassword(12);
            String lectureId = lecturesService.generateUniqueLectureId(
                    staffsService.getStaffMajor().getMajorId(),
                    lecturer.getCreatedDate() != null ? lecturer.getCreatedDate() : LocalDate.now());
            lecturer.setId(lectureId);
            if (avatarFile != null && !avatarFile.isEmpty()) {
                lecturer.setAvatar(avatarFile.getBytes());
            }
            lecturesService.addLecturers(lecturer, randomPassword);

            Authenticators authenticators = new Authenticators();
            authenticators.setPersonId(lectureId);
            authenticators.setPerson(personsService.getPersonById(lectureId));
            authenticators.setPassword(randomPassword);
            authenticatorsService.createAuthenticator(authenticators);

            redirectAttributes.addFlashAttribute("successMessage", "Lecture added successfully!");
            return "redirect:/staff-home/lecturers-list";
        } catch (IOException e) {
            errors.add("Failed to process avatar: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("majors", staffsService.getStaffMajor());
            return "AddLecturer";
        } catch (Exception e) {
            errors.add("An error occurred while adding the lecture: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("majors", staffsService.getStaffMajor());
            return "AddLecturer";
        }
    }
}