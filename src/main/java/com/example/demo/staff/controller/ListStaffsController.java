package com.example.demo.staff.controller;

import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.authenticator.service.AuthenticatorsService;
import com.example.demo.campus.model.Campuses;
import com.example.demo.campus.service.CampusesService;
import com.example.demo.major.model.Majors;
import com.example.demo.major.service.MajorsService;
import com.example.demo.person.service.PersonsService;
import com.example.demo.staff.model.Staffs;
import com.example.demo.staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/admin-home/staffs-list")
public class ListStaffsController {

    private final StaffsService staffsService;
    private final PersonsService personsService;
    private final AuthenticatorsService authenticatorsService;
    private final MajorsService majorsService;
    private final CampusesService campusesService;

    public ListStaffsController(StaffsService staffsService, PersonsService personsService,
                            AuthenticatorsService authenticatorsService, MajorsService majorsService,
                            CampusesService campusesService) {
        this.staffsService = staffsService;
        this.personsService = personsService;
        this.authenticatorsService = authenticatorsService;
        this.majorsService = majorsService;
        this.campusesService = campusesService;
    }

    @GetMapping("")
    public String listStaffs(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("staffPageSize");
                if (pageSize == null) {
                    pageSize = 5;
                }
            }
            if (pageSize < 1 || pageSize > 100) {
                pageSize = 5;
            }
            session.setAttribute("staffPageSize", pageSize);

            Long totalStaffs = staffsService.numberOfStaffs();
            int totalPages = Math.max(1, (int) Math.ceil((double) totalStaffs / pageSize));
            page = Math.max(1, Math.min(page, totalPages));
            session.setAttribute("staffPage", page);
            session.setAttribute("staffTotalPages", totalPages);

            if (totalStaffs == 0) {
                model.addAttribute("staffs", new ArrayList<>());
                model.addAttribute("staff", new Staffs());
                model.addAttribute("editStaff", new Staffs());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("totalStaffs", 0);
                model.addAttribute("message", "No staff found.");
                model.addAttribute("alertClass", "alert-warning");
                model.addAttribute("majors", majorsService.getMajors());
                model.addAttribute("campuses", campusesService.getCampuses());
                return "StaffsList";
            }

            int firstResult = (page - 1) * pageSize;
            List<Staffs> staffs = staffsService.getPaginatedStaffs(firstResult, pageSize);

            model.addAttribute("staffs", staffs);
            model.addAttribute("staff", new Staffs());
            model.addAttribute("editStaff", new Staffs());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalStaffs", totalStaffs);
            model.addAttribute("majors", majorsService.getMajors());
            model.addAttribute("campuses", campusesService.getCampuses());
            return "StaffsList";
        } catch (SecurityException e) {
            model.addAttribute("errors", List.of("Security error: " + e.getMessage()));
            model.addAttribute("staff", new Staffs());
            model.addAttribute("editStaff", new Staffs());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalStaffs", 0);
            model.addAttribute("majors", majorsService.getMajors());
            model.addAttribute("campuses", campusesService.getCampuses());
            return "StaffsList";
        }
    }

    @GetMapping("/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getStaffAvatar(@PathVariable String id) {
        Staffs staff = staffsService.getStaffById(id);
        if (staff != null && staff.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(staff.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}