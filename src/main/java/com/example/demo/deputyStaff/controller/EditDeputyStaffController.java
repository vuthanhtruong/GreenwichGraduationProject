package com.example.demo.deputyStaff.controller;

import com.example.demo.deputyStaff.model.DeputyStaffs;
import com.example.demo.deputyStaff.service.DeputyStaffsService;
import com.example.demo.campus.model.Campuses;
import com.example.demo.campus.service.CampusesService;
import com.example.demo.entity.Enums.Gender;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin-home/deputy-staffs-list")
public class EditDeputyStaffController {

    private final DeputyStaffsService deputyStaffsService;
    private final PersonsService personsService;
    private final CampusesService campusesService;

    public EditDeputyStaffController(DeputyStaffsService deputyStaffsService, PersonsService personsService, CampusesService campusesService) {
        this.deputyStaffsService = deputyStaffsService;
        this.personsService = personsService;
        this.campusesService = campusesService;
    }

    @PostMapping("/edit-deputy-staff-form")
    public String handleEditDeputyStaffFormPost(
            @RequestParam String id,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false) Integer pageSize,
            Model model) {
        DeputyStaffs deputyStaff = deputyStaffsService.getDeputyStaffById(id);
        model.addAttribute("deputyStaff", deputyStaff);
        model.addAttribute("genders", Arrays.asList(Gender.values()));
        model.addAttribute("campuses", campusesService.getCampuses());
        model.addAttribute("source", source);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
        return "EditDeputyStaffForm";
    }

    @PutMapping("/edit-deputy-staff-form")
    public String editDeputyStaff(
            @Valid @ModelAttribute("deputyStaff") DeputyStaffs deputyStaff,
            BindingResult bindingResult,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam(value = "campusId", required = false) String campusId,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            RedirectAttributes redirectAttributes,
            Model model) {
        List<String> errors = deputyStaffsService.validateDeputyStaff(deputyStaff, avatarFile, null, campusId);
        if (bindingResult.hasErrors()) {
            errors.addAll(bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList()));
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("genders", Arrays.asList(Gender.values()));
            model.addAttribute("campuses", campusesService.getCampuses());
            model.addAttribute("source", source);
            model.addAttribute("searchType", searchType);
            model.addAttribute("keyword", keyword);
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
            return "EditDeputyStaffForm";
        }

        try {
            if (!personsService.existsPersonById(deputyStaff.getId())) {
                redirectAttributes.addFlashAttribute("error", "Deputy staff with ID " + deputyStaff.getId() + " not found.");
                if ("search".equals(source)) {
                    redirectAttributes.addFlashAttribute("searchType", searchType);
                    redirectAttributes.addFlashAttribute("keyword", keyword);
                    redirectAttributes.addFlashAttribute("page", page);
                    redirectAttributes.addFlashAttribute("pageSize", pageSize);
                    return "redirect:/admin-home/deputy-staffs-list/search-deputy-staffs";
                }
                return "redirect:/admin-home/deputy-staffs-list";
            }
            Campuses campus = campusesService.getCampusById(campusId);
            deputyStaff.setCampus(campus);
            deputyStaffsService.editDeputyStaff(deputyStaff, avatarFile);
            redirectAttributes.addFlashAttribute("successMessage", "Deputy staff edited successfully!");
            if ("search".equals(source)) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/admin-home/deputy-staffs-list/search-deputy-staffs";
            }
            return "redirect:/admin-home/deputy-staffs-list?page=" + page + "&pageSize=" + (pageSize != null ? pageSize : 5);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error updating deputy staff: " + e.getMessage());
            if ("search".equals(source)) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/admin-home/deputy-staffs-list/search-deputy-staffs";
            }
            return "redirect:/admin-home/deputy-staffs-list?page=" + page + "&pageSize=" + (pageSize != null ? pageSize : 5);
        }
    }
}