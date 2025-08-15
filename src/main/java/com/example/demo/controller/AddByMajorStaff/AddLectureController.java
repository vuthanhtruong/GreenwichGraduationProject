package com.example.demo.controller.AddByMajorStaff;

import com.example.demo.entity.Authenticators;
import com.example.demo.entity.MajorLecturers;
import com.example.demo.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        List<String> errors = new ArrayList<>();

        // Handle annotation-based validation
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        // Perform custom validations using LecturesService
        errors.addAll(lecturesService.lectureValidation(lecture, avatarFile));

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("majors", staffsService.getStaffMajor());
            if (avatarFile != null && !avatarFile.isEmpty()) {
                try {
                    session.setAttribute("tempAvatar", avatarFile.getBytes());
                    session.setAttribute("tempAvatarName", avatarFile.getOriginalFilename());
                } catch (IOException e) {
                    errors.add("Failed to store avatar temporarily: " + e.getMessage());
                }
            }
            return "AddLecture";
        }

        try {
            // Generate random password
            String randomPassword = lecturesService.generateRandomPassword(12);

            // Generate unique lecture ID
            String lectureId = lecturesService.generateUniqueLectureId(
                    staffsService.getStaffMajor().getMajorId(),
                    lecture.getCreatedDate() != null ? lecture.getCreatedDate() : LocalDate.now());
            lecture.setId(lectureId);

            // Handle avatar upload
            if (avatarFile != null && !avatarFile.isEmpty()) {
                byte[] avatarBytes = avatarFile.getBytes();
                lecture.setAvatar(avatarBytes);
            } else if (session.getAttribute("tempAvatar") != null) {
                lecture.setAvatar((byte[]) session.getAttribute("tempAvatar"));
            }

            // Add lecturer using service
            lecturesService.addLecturers(lecture, randomPassword);

            // Create and save Authenticators entity
            Authenticators authenticators = new Authenticators();
            authenticators.setPersonId(lectureId);
            authenticators.setPerson(personsService.getPersonById(lectureId));
            authenticators.setPassword(randomPassword);
            authenticatorsService.createAuthenticator(authenticators);

            // Clear session data
            session.removeAttribute("tempAvatar");
            session.removeAttribute("tempAvatarName");

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