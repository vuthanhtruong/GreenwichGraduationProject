package com.example.demo.user.deputyStaff.controller;

import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.authenticator.service.AuthenticatorsService;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import com.example.demo.campus.service.CampusesService;
import com.example.demo.user.person.service.PersonsService;
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

}