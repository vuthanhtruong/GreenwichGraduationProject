package com.example.demo.staff.controller;

import com.example.demo.staff.model.Staffs;
import com.example.demo.staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin-home")
public class ListStaffsController {

    private final StaffsService staffsService;

    public ListStaffsController(StaffsService staffsService) {
        this.staffsService = staffsService;
    }

    @GetMapping("/staffs-list")
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

            if (totalStaffs == 0) {
                model.addAttribute("staffs", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                return "StaffsList";
            }

            int totalPages = (int) Math.ceil((double) totalStaffs / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;

            int firstResult = (page - 1) * pageSize;

            List<Staffs> staffs = staffsService.getPaginatedStaffs(firstResult, pageSize);

            model.addAttribute("staffs", staffs);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            return "StaffsList";
        } catch (Exception e) {
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/staffs-list/avatar/{id}")
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