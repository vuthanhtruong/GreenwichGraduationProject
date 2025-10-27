package com.example.demo.user.deputyStaff.controller;

import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.authenticator.service.AuthenticatorsService;
import com.example.demo.campus.service.CampusesService;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import com.example.demo.user.person.service.PersonsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin-home")
public class AddDeputyStaffsController {
    private final DeputyStaffsService deputyStaffsService;
    private final CampusesService campusesService;
    private final AuthenticatorsService authenticatorsService;
    private final PersonsService personsService;

    public AddDeputyStaffsController(DeputyStaffsService deputyStaffsService, CampusesService campusesService, AuthenticatorsService authenticatorsService, PersonsService personsService) {
        this.deputyStaffsService = deputyStaffsService;
        this.campusesService = campusesService;
        this.authenticatorsService = authenticatorsService;
        this.personsService = personsService;
    }

    @PostMapping("/deputy-staffs-list/add-deputy-staff")
    public String addDeputyStaff(
            @ModelAttribute("newDeputyStaff") DeputyStaffs deputyStaff,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam("campusId") String campusId,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Map<String, String> errors = new HashMap<>();

        try {
            errors = deputyStaffsService.validateDeputyStaff(deputyStaff, avatarFile, campusId);
            if (!errors.isEmpty()) {
                model.addAttribute("openAddOverlay", true);
                model.addAttribute("errors", errors);
                model.addAttribute("newDeputyStaff", deputyStaff);
                model.addAttribute("campuses", campusesService.getCampuses());
                model.addAttribute("deputyStaffs", deputyStaffsService.getPaginatedDeputyStaffs(0, (Integer) session.getAttribute("deputyStaffPageSize") != null ? (Integer) session.getAttribute("deputyStaffPageSize") : 5));
                model.addAttribute("currentPage", session.getAttribute("deputyStaffPage") != null ? session.getAttribute("deputyStaffPage") : 1);
                model.addAttribute("totalPages", session.getAttribute("deputyStaffTotalPages") != null ? session.getAttribute("deputyStaffTotalPages") : 1);
                model.addAttribute("pageSize", session.getAttribute("deputyStaffPageSize") != null ? session.getAttribute("deputyStaffPageSize") : 5);
                model.addAttribute("totalDeputyStaffs", deputyStaffsService.numberOfDeputyStaffs());
                return "DeputyStaffsList";
            }

            deputyStaff.setId(deputyStaffsService.generateUniqueDeputyStaffId(LocalDate.now()));
            deputyStaff.setCampus(campusesService.getCampusById(campusId));
            if (avatarFile != null && !avatarFile.isEmpty()) {
                deputyStaff.setAvatar(avatarFile.getBytes());
            }
            String randomPassword=deputyStaffsService.generateRandomPassword(12);
            deputyStaffsService.addDeputyStaff(deputyStaff, randomPassword);

            Authenticators authenticators = new Authenticators();
            authenticators.setPersonId(deputyStaff.getId());
            authenticators.setPerson(personsService.getPersonById(deputyStaff.getId()));
            authenticators.setPassword(randomPassword);
            authenticatorsService.createAuthenticator(authenticators);

            redirectAttributes.addFlashAttribute("message", "Deputy staff added successfully!");
            return "redirect:/admin-home/deputy-staffs-list";
        } catch (Exception e) {
            errors.put("general", "An error occurred while adding deputy staff: " + e.getMessage());
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("newDeputyStaff", deputyStaff);
            model.addAttribute("campuses", campusesService.getCampuses());
            model.addAttribute("deputyStaffs", deputyStaffsService.getPaginatedDeputyStaffs(0, (Integer) session.getAttribute("deputyStaffPageSize") != null ? (Integer) session.getAttribute("deputyStaffPageSize") : 5));
            model.addAttribute("currentPage", session.getAttribute("deputyStaffPage") != null ? session.getAttribute("deputyStaffPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("deputyStaffTotalPages") != null ? session.getAttribute("deputyStaffTotalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("deputyStaffPageSize") != null ? session.getAttribute("deputyStaffPageSize") : 5);
            model.addAttribute("totalDeputyStaffs", deputyStaffsService.numberOfDeputyStaffs());
            return "DeputyStaffsList";
        }
    }
}
