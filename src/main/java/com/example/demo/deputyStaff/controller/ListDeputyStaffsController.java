package com.example.demo.deputyStaff.controller;

import com.example.demo.deputyStaff.model.DeputyStaffs;
import com.example.demo.deputyStaff.service.DeputyStaffsService;
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
public class ListDeputyStaffsController {

    private final DeputyStaffsService deputyStaffsService;

    public ListDeputyStaffsController(DeputyStaffsService deputyStaffsService) {
        this.deputyStaffsService = deputyStaffsService;
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
                    pageSize = 5;
                }
            }
            if (pageSize < 1 || pageSize > 100) {
                pageSize = 5;
            }
            session.setAttribute("deputyStaffPageSize", pageSize);

            Long totalDeputyStaffs = deputyStaffsService.numberOfDeputyStaffs();

            if (totalDeputyStaffs == 0) {
                model.addAttribute("deputyStaffs", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
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
            return "DeputyStaffsList";
        } catch (Exception e) {
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            return "error";
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
}