package com.example.demo.staff.controller;

import com.example.demo.staff.model.Staffs;
import com.example.demo.staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin-home/staffs-list")
public class SearchStaffsController {

    private final StaffsService staffsService;

    public SearchStaffsController(StaffsService staffsService) {
        this.staffsService = staffsService;
    }

    @GetMapping("/search-staffs")
    public String showSearchPage(
            Model model,
            HttpSession session,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(value = "successMessage", required = false) String successMessage,
            @RequestParam(value = "error", required = false) String error) {
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

            List<Staffs> staffs;
            long totalStaffs;

            if (keyword == null || keyword.trim().isEmpty()) {
                totalStaffs = staffsService.numberOfStaffs();
                staffs = staffsService.getPaginatedStaffs((page - 1) * pageSize, pageSize);
            } else {
                staffs = staffsService.searchStaffs(searchType, keyword, (page - 1) * pageSize, pageSize);
                totalStaffs = staffsService.countSearchResults(searchType, keyword);
            }

            if (totalStaffs == 0) {
                model.addAttribute("staffs", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("searchType", searchType != null ? searchType : "name");
                model.addAttribute("keyword", keyword != null ? keyword : "");
                model.addAttribute("message", successMessage != null ? successMessage : (error != null ? error : "No staff found matching the search criteria."));
                return "SearchStaffs";
            }

            int totalPages = (int) Math.ceil((double) totalStaffs / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;

            model.addAttribute("staffs", staffs);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            if (successMessage != null) {
                model.addAttribute("message", successMessage);
            } else if (error != null) {
                model.addAttribute("error", error);
            }

            return "SearchStaffs";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while searching for staff: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/search-staffs")
    public String searchStaffs(
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(value = "successMessage", required = false) String successMessage,
            @RequestParam(value = "error", required = false) String error,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
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

            List<Staffs> staffs;
            long totalStaffs;

            if (keyword == null || keyword.trim().isEmpty()) {
                totalStaffs = staffsService.numberOfStaffs();
                staffs = staffsService.getPaginatedStaffs((page - 1) * pageSize, pageSize);
            } else {
                staffs = staffsService.searchStaffs(searchType, keyword, (page - 1) * pageSize, pageSize);
                totalStaffs = staffsService.countSearchResults(searchType, keyword);
            }

            if (totalStaffs == 0) {
                model.addAttribute("staffs", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("searchType", searchType != null ? searchType : "name");
                model.addAttribute("keyword", keyword != null ? keyword : "");
                model.addAttribute("message", successMessage != null ? successMessage : (error != null ? error : "No staff found matching the search criteria."));
                return "SearchStaffs";
            }

            int totalPages = (int) Math.ceil((double) totalStaffs / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;

            model.addAttribute("staffs", staffs);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            if (successMessage != null) {
                model.addAttribute("message", successMessage);
            } else if (error != null) {
                model.addAttribute("error", error);
            }

            return "SearchStaffs";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while searching for staff: " + e.getMessage());
            redirectAttributes.addFlashAttribute("searchType", searchType);
            redirectAttributes.addFlashAttribute("keyword", keyword);
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize);
            return "redirect:/admin-home/staffs-list/search-staffs";
        }
    }
}