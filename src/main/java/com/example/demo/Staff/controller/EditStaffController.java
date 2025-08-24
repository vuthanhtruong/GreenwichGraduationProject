package com.example.demo.Staff.controller;

import com.example.demo.Staff.model.Staffs;
import com.example.demo.Staff.service.StaffsService;
import com.example.demo.campus.service.CampusesService;
import com.example.demo.entity.Enums.Gender;
import com.example.demo.major.service.MajorsService;
import com.example.demo.person.service.PersonsService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
@RequestMapping("/admin-home/staffs-list")
public class EditStaffController {

    private final StaffsService staffsService;
    private final PersonsService personsService;
    private final MajorsService majorsService;
    private final CampusesService campusesService;

    public EditStaffController(StaffsService staffsService, PersonsService personsService, MajorsService majorsService, CampusesService campusesService) {
        this.staffsService = staffsService;
        this.personsService = personsService;
        this.majorsService = majorsService;
        this.campusesService = campusesService;
    }

    @PostMapping("/edit-staff-form")
    public String handleEditStaffFormPost(@RequestParam String id, Model model) {
        Staffs staff = staffsService.getStaffById(id);
        model.addAttribute("staff", staff);
        model.addAttribute("genders", Arrays.asList(Gender.values()));
        model.addAttribute("majors", majorsService.getMajors());
        model.addAttribute("campuses", campusesService.getCampuses());
        return "EditStaffForm";
    }

    @PutMapping("/edit-staff-form")
    public String editStaff(
            @Valid @ModelAttribute("staff") Staffs staff,
            BindingResult bindingResult,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam(value = "majorId", required = false) String majorId,
            @RequestParam(value = "campusId", required = false) String campusId,
            RedirectAttributes redirectAttributes,
            Model model) {
        List<String> errors = staffsService.validateStaff(staff, avatarFile, majorId, campusId);
        if (bindingResult.hasErrors()) {
            errors.addAll(bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList()));
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("genders", Arrays.asList(Gender.values()));
            model.addAttribute("majors", majorsService.getMajors());
            model.addAttribute("campuses", campusesService.getCampuses());
            return "EditStaffForm";
        }

        try {
            if (!personsService.existsPersonById(staff.getId())) {
                redirectAttributes.addFlashAttribute("error", "Staff with ID " + staff.getId() + " not found.");
                return "redirect:/admin-home/staffs-list";
            }
            staffsService.editStaff(staff, avatarFile);
            redirectAttributes.addFlashAttribute("successMessage", "Staff editd successfully!");
        } catch (IOException | MessagingException e) {
            redirectAttributes.addFlashAttribute("error", "Error updating staff: " + e.getMessage());
            model.addAttribute("errors", List.of("Error updating staff: " + e.getMessage()));
            model.addAttribute("genders", Arrays.asList(Gender.values()));
            model.addAttribute("majors", majorsService.getMajors());
            model.addAttribute("campuses", campusesService.getCampuses());
            return "EditStaffForm";
        }
        return "redirect:/admin-home/staffs-list";
    }
}