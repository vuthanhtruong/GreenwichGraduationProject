package com.example.demo.user.deputyStaff.controller;

import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
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
@RequestMapping("/admin-home/deputy-staffs-list")
public class SearchDeputyStaffsController {

    private final DeputyStaffsService deputyStaffsService;

    public SearchDeputyStaffsController(DeputyStaffsService deputyStaffsService) {
        this.deputyStaffsService = deputyStaffsService;
    }

    @GetMapping("/search-deputy-staffs")
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
                pageSize = (Integer) session.getAttribute("deputyStaffPageSize");
                if (pageSize == null) {
                    pageSize = 20;
                }
            }
            if (pageSize < 1 || pageSize > 100) {
                pageSize = 5;
            }
            session.setAttribute("deputyStaffPageSize", pageSize);

            List<DeputyStaffs> deputyStaffs;
            long totalDeputyStaffs;

            if (keyword == null || keyword.trim().isEmpty()) {
                totalDeputyStaffs = deputyStaffsService.numberOfDeputyStaffs();
                deputyStaffs = deputyStaffsService.getPaginatedDeputyStaffs((page - 1) * pageSize, pageSize);
            } else {
                deputyStaffs = deputyStaffsService.searchDeputyStaffs(searchType, keyword, (page - 1) * pageSize, pageSize);
                totalDeputyStaffs = deputyStaffsService.countSearchResults(searchType, keyword);
            }

            if (totalDeputyStaffs == 0) {
                model.addAttribute("deputyStaffs", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("searchType", searchType != null ? searchType : "name");
                model.addAttribute("keyword", keyword != null ? keyword : "");
                model.addAttribute("message", successMessage != null ? successMessage : (error != null ? error : "No deputy staff found matching the search criteria."));
                return "SearchDeputyStaffs";
            }

            int totalPages = (int) Math.ceil((double) totalDeputyStaffs / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;

            model.addAttribute("deputyStaffs", deputyStaffs);
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

            return "SearchDeputyStaffs";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while searching for deputy staff: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/search-deputy-staffs")
    public String searchDeputyStaffs(
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
                pageSize = (Integer) session.getAttribute("deputyStaffPageSize");
                if (pageSize == null) {
                    pageSize = 5;
                }
            }
            if (pageSize < 1 || pageSize > 100) {
                pageSize = 5;
            }
            session.setAttribute("deputyStaffPageSize", pageSize);

            List<DeputyStaffs> deputyStaffs;
            long totalDeputyStaffs;

            if (keyword == null || keyword.trim().isEmpty()) {
                totalDeputyStaffs = deputyStaffsService.numberOfDeputyStaffs();
                deputyStaffs = deputyStaffsService.getPaginatedDeputyStaffs((page - 1) * pageSize, pageSize);
            } else {
                deputyStaffs = deputyStaffsService.searchDeputyStaffs(searchType, keyword, (page - 1) * pageSize, pageSize);
                totalDeputyStaffs = deputyStaffsService.countSearchResults(searchType, keyword);
            }

            if (totalDeputyStaffs == 0) {
                model.addAttribute("deputyStaffs", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("searchType", searchType != null ? searchType : "name");
                model.addAttribute("keyword", keyword != null ? keyword : "");
                model.addAttribute("message", successMessage != null ? successMessage : (error != null ? error : "No deputy staff found matching the search criteria."));
                return "SearchDeputyStaffs";
            }

            int totalPages = (int) Math.ceil((double) totalDeputyStaffs / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;

            model.addAttribute("deputyStaffs", deputyStaffs);
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

            return "SearchDeputyStaffs";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while searching for deputy staff: " + e.getMessage());
            redirectAttributes.addFlashAttribute("searchType", searchType);
            redirectAttributes.addFlashAttribute("keyword", keyword);
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize);
            return "redirect:/admin-home/deputy-staffs-list/search-deputy-staffs";
        }
    }
}