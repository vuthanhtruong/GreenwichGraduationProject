package com.example.demo.deputyStaff.controller;

import com.example.demo.deputyStaff.model.DeputyStaffs;
import com.example.demo.deputyStaff.service.DeputyStaffsService;
import com.example.demo.campus.service.CampusesService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin-home")
public class ListDeputyStaffsController {

    private final DeputyStaffsService deputyStaffsService;
    private final CampusesService campusesService;

    @Autowired
    public ListDeputyStaffsController(DeputyStaffsService deputyStaffsService, CampusesService campusesService) {
        this.deputyStaffsService = deputyStaffsService;
        this.campusesService = campusesService;
    }

    @GetMapping("/deputy-staffs-list")
    public String listDeputyStaffs(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("deputyStaffPageSize");
                if (pageSize == null) {
                    pageSize = 20;
                }
            }
            if (pageSize < 1 || pageSize > 100) {
                pageSize = 5;
            }
            session.setAttribute("deputyStaffPageSize", pageSize);

            Long totalDeputyStaffs = deputyStaffsService.numberOfDeputyStaffs();

            if (totalDeputyStaffs == 0) {
                model.addAttribute("deputyStaffs", List.of());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("totalDeputyStaffs", 0);
                model.addAttribute("campuses", campusesService.getCampuses());
                model.addAttribute("newDeputyStaff", new DeputyStaffs());
                return "DeputyStaffsList";
            }

            int totalPages = (int) Math.ceil((double) totalDeputyStaffs / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;

            int firstResult = (page - 1) * pageSize;

            List<DeputyStaffs> deputyStaffs = deputyStaffsService.getPaginatedDeputyStaffs(firstResult, pageSize);

            model.addAttribute("deputyStaffs", deputyStaffs);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalDeputyStaffs", totalDeputyStaffs);
            model.addAttribute("campuses", campusesService.getCampuses());
            model.addAttribute("newDeputyStaff", new DeputyStaffs());
            return "DeputyStaffsList";
        } catch (Exception e) {
            Map<String, String> errors = new HashMap<>();
            errors.put("general", "An unexpected error occurred: " + e.getMessage());
            model.addAttribute("errors", errors);
            return "DeputyStaffsList";
        }
    }

    @GetMapping("/deputy-staffs-list/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getDeputyStaffAvatar(@PathVariable String id) {
        DeputyStaffs deputyStaff = deputyStaffsService.getDeputyStaffById(id);
        if (deputyStaff != null && deputyStaff.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(deputyStaff.getAvatar());
        }
        return ResponseEntity.notFound().build();
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
            deputyStaffsService.addDeputyStaff(deputyStaff, deputyStaffsService.generateRandomPassword(12));

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