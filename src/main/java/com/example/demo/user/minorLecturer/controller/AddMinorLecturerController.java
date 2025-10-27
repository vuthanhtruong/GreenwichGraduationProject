package com.example.demo.user.minorLecturer.controller;

import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.authenticator.service.AuthenticatorsService;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.user.minorLecturer.service.MinorLecturersService;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import com.example.demo.user.person.service.PersonsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/deputy-staff-home/minor-lecturers-list")
public class AddMinorLecturerController {

    private final MinorLecturersService minorLecturersService;
    private final PersonsService personsService;
    private final AuthenticatorsService authenticatorsService;

    public AddMinorLecturerController(MinorLecturersService minorLecturersService,
                                      PersonsService personsService, AuthenticatorsService authenticatorsService) {
        this.minorLecturersService = minorLecturersService;
        this.personsService = personsService;
        this.authenticatorsService = authenticatorsService;
    }

    @PostMapping("/add-minor-lecturer")
    public String addMinorLecturer(
            @Valid @ModelAttribute("minorLecturer") MinorLecturers minorLecturer,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        Map<String, String> errors = minorLecturersService.minorLecturerValidation(minorLecturer, avatarFile);

        if (!errors.isEmpty()) {
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("minorLecturer", minorLecturer);
            model.addAttribute("minorLecturers", minorLecturersService.getPaginatedMinorLecturers(0, (Integer) session.getAttribute("minorLecturerPageSize") != null ? (Integer) session.getAttribute("minorLecturerPageSize") : 5));
            model.addAttribute("currentPage", session.getAttribute("minorLecturerPage") != null ? session.getAttribute("minorLecturerPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("minorLecturerTotalPages") != null ? session.getAttribute("minorLecturerTotalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("minorLecturerPageSize") != null ? session.getAttribute("minorLecturerPageSize") : 5);
            model.addAttribute("totalMinorLecturers", minorLecturersService.numberOfMinorLecturers());
            if (avatarFile != null && !avatarFile.isEmpty()) {
                try {
                    session.setAttribute("tempAvatar", avatarFile.getBytes());
                    session.setAttribute("tempAvatarName", avatarFile.getOriginalFilename());
                } catch (IOException e) {
                    errors.put("avatarFile", "Failed to store avatar temporarily: " + e.getMessage());
                }
            }
            return "MinorLecturersList";
        }

        try {
            String minorLectureId = minorLecturersService.generateUniqueMinorLectureId(
                    minorLecturer.getCreatedDate() != null ? minorLecturer.getCreatedDate() : LocalDate.now());
            minorLecturer.setId(minorLectureId);

            if (avatarFile != null && !avatarFile.isEmpty()) {
                minorLecturer.setAvatar(avatarFile.getBytes());
            } else if (session.getAttribute("tempAvatar") != null) {
                minorLecturer.setAvatar((byte[]) session.getAttribute("tempAvatar"));
            }

            String randomPassword = minorLecturersService.generateRandomPassword(12);
            minorLecturersService.addMinorLecturers(minorLecturer, randomPassword);

            Authenticators authenticators = new Authenticators();
            authenticators.setPersonId(minorLectureId);
            authenticators.setPerson(personsService.getPersonById(minorLectureId));
            authenticators.setPassword(randomPassword);
            authenticatorsService.createAuthenticator(authenticators);

            session.removeAttribute("tempAvatar");
            session.removeAttribute("tempAvatarName");

            redirectAttributes.addFlashAttribute("message", "Minor Lecturer added successfully!");
            return "redirect:/deputy-staff-home/minor-lecturers-list";
        } catch (IOException e) {
            model.addAttribute("errors", errors);
            model.addAttribute("minorLecturer", minorLecturer);
            model.addAttribute("minorLecturers", minorLecturersService.getPaginatedMinorLecturers(0, (Integer) session.getAttribute("minorLecturerPageSize") != null ? (Integer) session.getAttribute("minorLecturerPageSize") : 5));
            model.addAttribute("currentPage", session.getAttribute("minorLecturerPage") != null ? session.getAttribute("minorLecturerPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("minorLecturerTotalPages") != null ? session.getAttribute("minorLecturerTotalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("minorLecturerPageSize") != null ? session.getAttribute("minorLecturerPageSize") : 5);
            model.addAttribute("totalMinorLecturers", minorLecturersService.numberOfMinorLecturers());
            return "MinorLecturersList";
        } catch (Exception e) {
            model.addAttribute("errors", errors);
            model.addAttribute("minorLecturer", minorLecturer);
            model.addAttribute("minorLecturers", minorLecturersService.getPaginatedMinorLecturers(0, (Integer) session.getAttribute("minorLecturerPageSize") != null ? (Integer) session.getAttribute("minorLecturerPageSize") : 5));
            model.addAttribute("currentPage", session.getAttribute("minorLecturerPage") != null ? session.getAttribute("minorLecturerPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("minorLecturerTotalPages") != null ? session.getAttribute("minorLecturerTotalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("minorLecturerPageSize") != null ? session.getAttribute("minorLecturerPageSize") : 5);
            model.addAttribute("totalMinorLecturers", minorLecturersService.numberOfMinorLecturers());
            return "MinorLecturersList";
        }
    }
}