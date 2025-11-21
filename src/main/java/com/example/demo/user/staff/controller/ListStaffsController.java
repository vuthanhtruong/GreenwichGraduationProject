package com.example.demo.user.staff.controller;

import com.example.demo.user.admin.service.AdminsService;
import com.example.demo.campus.service.CampusesService;
import com.example.demo.major.service.MajorsService;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin-home/staffs-list")
public class ListStaffsController {

    private final StaffsService staffsService;
    private final MajorsService majorsService;
    private final CampusesService campusesService;
    private final AdminsService adminsService;

    public ListStaffsController(StaffsService staffsService, MajorsService majorsService,
                                CampusesService campusesService, AdminsService adminsService) {
        this.staffsService = staffsService;
        this.adminsService = adminsService;
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
                    pageSize = 20;
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
                model.addAttribute("admin", adminsService.getAdmin());
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
            model.addAttribute("admin", adminsService.getAdmin());
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
            model.addAttribute("admin", adminsService.getAdmin());
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
    @DeleteMapping("/delete-staff/{id}")
    public String deleteStaff(
            @PathVariable String id,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        try {
            Staffs staff = staffsService.getStaffById(id);
            if (staff == null) {
                redirectAttributes.addFlashAttribute("error", "Staff not found!");
            } else {
                staffsService.deleteStaff(id);
                redirectAttributes.addFlashAttribute("message",
                        "Staff '" + staff.getFirstName() + " " + staff.getLastName() + "' (ID: " + id + ") has been deleted successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete staff: " + e.getMessage());
        }

        // Giữ lại page hiện tại + pageSize + search (nếu có)
        Integer pageSize = (Integer) session.getAttribute("staffPageSize");
        if (pageSize == null) pageSize = 20;

        return "redirect:/admin-home/staffs-list?pageSize=" + pageSize;
    }
}