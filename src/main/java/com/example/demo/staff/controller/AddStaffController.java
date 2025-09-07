package com.example.demo.staff.controller;

import com.example.demo.staff.model.Staffs;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.authenticator.service.AuthenticatorsService;
import com.example.demo.campus.model.Campuses;
import com.example.demo.campus.service.CampusesService;
import com.example.demo.major.model.Majors;
import com.example.demo.major.service.MajorsService;
import com.example.demo.person.service.PersonsService;
import jakarta.servlet.http.HttpSession;
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
@RequestMapping("/admin-home/staffs-list/")
public class AddStaffController {

    private final StaffsService staffsService;
    private final PersonsService personsService;
    private final AuthenticatorsService authenticatorsService;
    private final MajorsService majorsService;
    private final CampusesService  campusesService;


    public AddStaffController(StaffsService staffsService, PersonsService personsService,
                              AuthenticatorsService authenticatorsService, MajorsService majorsService, CampusesService campusesService) {
        this.staffsService = staffsService;
        this.personsService = personsService;
        this.authenticatorsService = authenticatorsService;
        this.majorsService = majorsService;
        this.campusesService = campusesService;
    }

    @PostMapping("/add-staff")
    public String addStaff(
            @Valid @ModelAttribute("staff") Staffs staff,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam("majorId") String majorId,
            @RequestParam("campusId") String campusId,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        List<String> errors = new ArrayList<>();
        errors.addAll(staffsService.validateStaff(staff, avatarFile, majorId, campusId));

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("staff", staff);
            model.addAttribute("majors", majorsService.getMajors());
            model.addAttribute("campuses", campusesService.getCampuses());
            model.addAttribute("staffs", staffsService.getPaginatedStaffs(0, (Integer) session.getAttribute("staffPageSize") != null ? (Integer) session.getAttribute("staffPageSize") : 5));
            model.addAttribute("currentPage", session.getAttribute("staffPage") != null ? session.getAttribute("staffPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("staffTotalPages") != null ? session.getAttribute("staffTotalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("staffPageSize") != null ? session.getAttribute("staffPageSize") : 5);
            model.addAttribute("totalStaffs", staffsService.numberOfStaffs());
            if (avatarFile != null && !avatarFile.isEmpty()) {
                try {
                    session.setAttribute("tempAvatar", avatarFile.getBytes());
                    session.setAttribute("tempAvatarName", avatarFile.getOriginalFilename());
                } catch (IOException e) {
                    errors.add("Failed to store avatar temporarily: " + e.getMessage());
                }
            }
            return "StaffsList";
        }

        try {
            Majors major = majorsService.getByMajorId(majorId);
            Campuses campus = campusesService.getCampusById(campusId);
            staff.setMajorManagement(major);
            staff.setCampus(campus);
            String staffId = staffsService.generateUniqueStaffId(
                    major != null ? major.getMajorId() : "STF",
                    staff.getCreatedDate() != null ? staff.getCreatedDate() : LocalDate.now());
            staff.setId(staffId);

            if (avatarFile != null && !avatarFile.isEmpty()) {
                staff.setAvatar(avatarFile.getBytes());
            } else if (session.getAttribute("tempAvatar") != null) {
                staff.setAvatar((byte[]) session.getAttribute("tempAvatar"));
            }

            String randomPassword = staffsService.generateRandomPassword(12);
            staffsService.addStaff(staff, randomPassword);

            Authenticators authenticators = new Authenticators();
            authenticators.setPersonId(staffId);
            authenticators.setPerson(personsService.getPersonById(staffId));
            authenticators.setPassword(randomPassword);
            authenticatorsService.createAuthenticator(authenticators);

            session.removeAttribute("tempAvatar");
            session.removeAttribute("tempAvatarName");

            redirectAttributes.addFlashAttribute("message", "Staff added successfully!");
            return "redirect:/admin-home/staffs-list";
        } catch (IOException e) {
            errors.add("Failed to process avatar: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("staff", staff);
            model.addAttribute("majors", majorsService.getMajors());
            model.addAttribute("campuses", campusesService.getCampuses());
            model.addAttribute("staffs", staffsService.getPaginatedStaffs(0, (Integer) session.getAttribute("staffPageSize") != null ? (Integer) session.getAttribute("staffPageSize") : 5));
            model.addAttribute("currentPage", session.getAttribute("staffPage") != null ? session.getAttribute("staffPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("staffTotalPages") != null ? session.getAttribute("staffTotalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("staffPageSize") != null ? session.getAttribute("staffPageSize") : 5);
            model.addAttribute("totalStaffs", staffsService.numberOfStaffs());
            return "StaffsList";
        } catch (Exception e) {
            errors.add("An error occurred while adding the staff: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("staff", staff);
            model.addAttribute("majors", majorsService.getMajors());
            model.addAttribute("campuses", campusesService.getCampuses());
            model.addAttribute("staffs", staffsService.getPaginatedStaffs(0, (Integer) session.getAttribute("staffPageSize") != null ? (Integer) session.getAttribute("staffPageSize") : 5));
            model.addAttribute("currentPage", session.getAttribute("staffPage") != null ? session.getAttribute("staffPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("staffTotalPages") != null ? session.getAttribute("staffTotalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("staffPageSize") != null ? session.getAttribute("staffPageSize") : 5);
            model.addAttribute("totalStaffs", staffsService.numberOfStaffs());
            return "StaffsList";
        }
    }

}