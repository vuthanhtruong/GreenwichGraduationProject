package com.example.demo.Staff.controller;

import com.example.demo.Staff.model.Staffs;
import com.example.demo.Staff.service.StaffsService;
import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.authenticator.service.AuthenticatorsService;
import com.example.demo.campus.model.Campuses;
import com.example.demo.campus.service.CampusesService;
import com.example.demo.major.model.Majors;
import com.example.demo.major.service.MajorsService;
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

    @GetMapping("/add-staff")
    public String showAddStaffPage(Model model) {
        model.addAttribute("staff", new Staffs());
        model.addAttribute("majors", majorsService.getMajors());
        model.addAttribute("campuses",campusesService.getCampuses());
        return "AddStaff";
    }

    @PostMapping("/add-staff")
    public String addStaff(
            @Valid @ModelAttribute("staff") Staffs staff,
            BindingResult bindingResult,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam("majorId") String majorId,
            @RequestParam("campusId") String campusId,
            Model model,
            RedirectAttributes redirectAttributes) {
        List<String> errors = new ArrayList<>();

        // Gán major và campus từ request param
        Majors major = majorsService.getByMajorId(majorId);
        Campuses campus = campusesService.getCampusById(campusId);
        if (major == null) errors.add("Invalid major selected.");
        if (campus == null) errors.add("Invalid campus selected.");
        staff.setMajorManagement(major);
        staff.setCampus(campus);
        errors.addAll(staffsService.validateStaff(staff, avatarFile));
        if (bindingResult.hasErrors()) {
            errors.addAll(bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList()));
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("majors", majorsService.getMajors());
            model.addAttribute("campuses", campusesService.getCampuses());
            return "AddStaff";
        }

        try {
            String randomPassword = staffsService.generateRandomPassword(12);
            String staffId = staffsService.generateUniqueStaffId(
                    major != null ? major.getMajorId() : "STF",
                    staff.getCreatedDate() != null ? staff.getCreatedDate() : LocalDate.now());
            staff.setId(staffId);
            if (avatarFile != null && !avatarFile.isEmpty()) {
                staff.setAvatar(avatarFile.getBytes());
            }
            staffsService.addStaff(staff, randomPassword);

            Authenticators authenticators = new Authenticators();
            authenticators.setPersonId(staffId);
            authenticators.setPerson(personsService.getPersonById(staffId));
            authenticators.setPassword(randomPassword);
            authenticatorsService.createAuthenticator(authenticators);

            redirectAttributes.addFlashAttribute("successMessage", "Staff added successfully!");
            return "redirect:/admin-home/staffs-list";
        } catch (IOException e) {
            errors.add("Failed to process avatar: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("majors", majorsService.getMajors());
            model.addAttribute("campuses", campusesService.getCampuses());
            return "AddStaff";
        } catch (Exception e) {
            errors.add("An error occurred while adding the staff: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("majors", majorsService.getMajors());
            model.addAttribute("campuses", campusesService.getCampuses());
            return "AddStaff";
        }
    }

}