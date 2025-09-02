package com.example.demo.staff.controller;

import com.example.demo.staff.model.Staffs;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.campus.model.Campuses;
import com.example.demo.campus.service.CampusesService;
import com.example.demo.entity.Enums.Gender;
import com.example.demo.major.model.Majors;
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
    public String handleEditStaffFormPost(
            @RequestParam String id,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false) Integer pageSize,
            Model model) {
        Staffs staff = staffsService.getStaffById(id);
        model.addAttribute("staff", staff);
        model.addAttribute("genders", Arrays.asList(Gender.values()));
        model.addAttribute("majors", majorsService.getMajors());
        model.addAttribute("campuses", campusesService.getCampuses());
        model.addAttribute("source", source);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
        return "EditStaffForm";
    }

    @PutMapping("/edit-staff-form")
    public String editStaff(
            @Valid @ModelAttribute("staff") Staffs staff,
            BindingResult bindingResult,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam(value = "majorId", required = false) String majorId,
            @RequestParam(value = "campusId", required = false) String campusId,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
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
            model.addAttribute("source", source);
            model.addAttribute("searchType", searchType);
            model.addAttribute("keyword", keyword);
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
            return "EditStaffForm";
        }

        try {
            if (!personsService.existsPersonById(staff.getId())) {
                redirectAttributes.addFlashAttribute("error", "Staff with ID " + staff.getId() + " not found.");
                if ("search".equals(source)) {
                    redirectAttributes.addFlashAttribute("searchType", searchType);
                    redirectAttributes.addFlashAttribute("keyword", keyword);
                    redirectAttributes.addFlashAttribute("page", page);
                    redirectAttributes.addFlashAttribute("pageSize", pageSize);
                    return "redirect:/admin-home/staffs-list/search-staffs";
                }
                return "redirect:/admin-home/staffs-list";
            }
            Majors majors = majorsService.getByMajorId(majorId);
            Campuses campuses = campusesService.getCampusById(campusId);
            staff.setCampus(campuses);
            staff.setMajorManagement(majors);
            staffsService.editStaff(staff, avatarFile);
            redirectAttributes.addFlashAttribute("successMessage", "Staff edited successfully!");
            if ("search".equals(source)) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/admin-home/staffs-list/search-staffs";
            }
            return "redirect:/admin-home/staffs-list?page=" + page + "&pageSize=" + (pageSize != null ? pageSize : 5);
        } catch (IOException | MessagingException e) {
            redirectAttributes.addFlashAttribute("error", "Error updating staff: " + e.getMessage());
            if ("search".equals(source)) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/admin-home/staffs-list/search-staffs";
            }
            return "redirect:/admin-home/staffs-list?page=" + page + "&pageSize=" + (pageSize != null ? pageSize : 5);
        }
    }
}