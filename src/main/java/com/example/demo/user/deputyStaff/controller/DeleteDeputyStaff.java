package com.example.demo.user.deputyStaff.controller;

import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin-home/deputy-staffs-list")
public class DeleteDeputyStaff {

    private final DeputyStaffsService deputyStaffsService;

    public DeleteDeputyStaff(DeputyStaffsService deputyStaffsService) {
        this.deputyStaffsService = deputyStaffsService;
    }

    @PostMapping("/delete-deputy-staff")
    public String deleteDeputyStaff(
            @RequestParam String id,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false) Integer pageSize,
            RedirectAttributes redirectAttributes) {
        try {
            deputyStaffsService.deleteDeputyStaff(id);
            redirectAttributes.addFlashAttribute("successMessage", "Deputy staff deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting deputy staff: " + e.getMessage());
        }
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
