package com.example.demo.user.majorLecturer.controller;

import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.authenticator.service.AuthenticatorsService;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.user.person.service.PersonsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/staff-home/lecturers-list")
public class AddLecturerController {
    private final StaffsService staffsService;
    private final MajorLecturersService lecturesService;
    private final PersonsService personsService;
    private final AuthenticatorsService authenticatorsService;

    public AddLecturerController(StaffsService staffsService, MajorLecturersService lecturesService,
                                 PersonsService personsService, AuthenticatorsService authenticatorsService) {
        this.staffsService = staffsService;
        this.lecturesService = lecturesService;
        this.personsService = personsService;
        this.authenticatorsService = authenticatorsService;
    }

    @PostMapping("/add-lecturer")
    public String addLecturer(
            @Valid @ModelAttribute("lecturer") MajorLecturers lecturer,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        Map<String, String> errors = lecturesService.lectureValidation(lecturer, avatarFile);

        if (!errors.isEmpty()) {
            model.addAttribute("openAddOverlay", true); // 👈 thêm cờ này
            model.addAttribute("errors", errors);
            model.addAttribute("lecturer", lecturer);
            model.addAttribute("teachers", lecturesService.getPaginatedLecturers(0, (Integer) session.getAttribute("lecturerPageSize") != null ? (Integer) session.getAttribute("lecturerPageSize") : 5));
            model.addAttribute("currentPage", session.getAttribute("lecturerPage") != null ? session.getAttribute("lecturerPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("lecturerTotalPages") != null ? session.getAttribute("lecturerTotalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("lecturerPageSize") != null ? session.getAttribute("lecturerPageSize") : 5);
            model.addAttribute("totalLecturers", lecturesService.numberOfLecturers());
            if (avatarFile != null && !avatarFile.isEmpty()) {
                try {
                    session.setAttribute("tempAvatar", avatarFile.getBytes());
                    session.setAttribute("tempAvatarName", avatarFile.getOriginalFilename());
                } catch (IOException e) {
                    errors.put("avatarFile", "Failed to store avatar temporarily: " + e.getMessage());
                }
            }
            return "LecturersList";
        }

        try {
            String lectureId = lecturesService.generateUniqueLectureId(
                    staffsService.getStaffMajor().getMajorId(),
                    lecturer.getCreatedDate() != null ? lecturer.getCreatedDate() : LocalDate.now());
            lecturer.setId(lectureId);

            if (avatarFile != null && !avatarFile.isEmpty()) {
                lecturer.setAvatar(avatarFile.getBytes());
            } else if (session.getAttribute("tempAvatar") != null) {
                lecturer.setAvatar((byte[]) session.getAttribute("tempAvatar"));
            }

            String randomPassword = lecturesService.generateRandomPassword(12);
            lecturesService.addLecturers(lecturer, randomPassword);

            Authenticators authenticators = new Authenticators();
            authenticators.setPersonId(lectureId);
            authenticators.setPerson(personsService.getPersonById(lectureId));
            authenticators.setPassword(randomPassword);
            authenticatorsService.createAuthenticator(authenticators);

            session.removeAttribute("tempAvatar");
            session.removeAttribute("tempAvatarName");

            redirectAttributes.addFlashAttribute("message", "Lecturer added successfully!");
            return "redirect:/staff-home/lecturers-list";
        } catch (IOException e) {
            model.addAttribute("errors", errors);
            model.addAttribute("lecturer", lecturer);
            model.addAttribute("teachers", lecturesService.getPaginatedLecturers(0, (Integer) session.getAttribute("lecturerPageSize") != null ? (Integer) session.getAttribute("lecturerPageSize") : 5));
            model.addAttribute("currentPage", session.getAttribute("lecturerPage") != null ? session.getAttribute("lecturerPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("lecturerTotalPages") != null ? session.getAttribute("lecturerTotalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("lecturerPageSize") != null ? session.getAttribute("lecturerPageSize") : 5);
            model.addAttribute("totalLecturers", lecturesService.numberOfLecturers());
            return "LecturersList";
        } catch (Exception e) {
            model.addAttribute("errors", errors);
            model.addAttribute("lecturer", lecturer);
            model.addAttribute("teachers", lecturesService.getPaginatedLecturers(0, (Integer) session.getAttribute("lecturerPageSize") != null ? (Integer) session.getAttribute("lecturerPageSize") : 5));
            model.addAttribute("currentPage", session.getAttribute("lecturerPage") != null ? session.getAttribute("lecturerPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("lecturerTotalPages") != null ? session.getAttribute("lecturerTotalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("lecturerPageSize") != null ? session.getAttribute("lecturerPageSize") : 5);
            model.addAttribute("totalLecturers", lecturesService.numberOfLecturers());
            return "LecturersList";
        }
    }
}