package com.example.demo.user.deputyStaff.controller;

import com.example.demo.user.admin.model.Admins;
import com.example.demo.user.admin.service.AdminsService;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/deputy-staff-home/your-manager")
public class DeputyYourManagerController {

    private final DeputyStaffsService deputyStaffsService;
    private final AdminsService adminsService;

    public DeputyYourManagerController(DeputyStaffsService deputyStaffsService, AdminsService adminsService) {
        this.deputyStaffsService = deputyStaffsService;
        this.adminsService = adminsService;
    }

    @GetMapping
    public String listManagers(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {

        // --- PAGE SIZE ---
        if (pageSize == null || pageSize <= 0) {
            pageSize = (Integer) session.getAttribute("dsManagerPageSize");
            if (pageSize == null || pageSize <= 0) pageSize = 10;
        }
        pageSize = Math.min(pageSize, 100);
        session.setAttribute("dsManagerPageSize", pageSize);

        // --- LẤY DEPUTY STAFF HIỆN TẠI ---
        DeputyStaffs currentDeputy = deputyStaffsService.getDeputyStaff();
        if (currentDeputy == null || currentDeputy.getCampus() == null) {
            return "redirect:/login";
        }
        String campusId = currentDeputy.getCampus().getCampusId();

        // --- TÌM KIẾM TỪ SESSION ---
        String keyword = (String) session.getAttribute("dsManagerKeyword");
        String searchType = (String) session.getAttribute("dsManagerSearchType");

        List<Admins> managers;
        long totalManagers;
        int totalPages;
        int firstResult = (page - 1) * pageSize;

        if (keyword != null && !keyword.trim().isEmpty() && searchType != null) {
            managers = adminsService.searchAdmins(searchType, keyword.trim(), firstResult, pageSize);
            totalManagers = adminsService.countSearchResults(searchType, keyword.trim());
        } else {
            managers = adminsService.yourManagerByCampusId(campusId);
            totalManagers = managers.size();

            int from = Math.min(firstResult, managers.size());
            int to = Math.min(from + pageSize, managers.size());
            managers = from < to ? managers.subList(from, to) : List.of();
        }

        totalPages = Math.max(1, (int) Math.ceil((double) totalManagers / pageSize));
        page = Math.max(1, Math.min(page, totalPages));

        session.setAttribute("dsManagerPage", page);
        session.setAttribute("dsManagerTotalPages", totalPages);

        model.addAttribute("managers", managers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalManagers", totalManagers);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("currentDeputy", currentDeputy);

        return "DeputyYourManagerList"; // → DeputyYourManagerList.html
    }

    @PostMapping("/search")
    public String search(
            @RequestParam String searchType,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session) {

        session.setAttribute("dsManagerKeyword", keyword.trim().isEmpty() ? null : keyword.trim());
        session.setAttribute("dsManagerSearchType", searchType);
        session.setAttribute("dsManagerPageSize", Math.min(pageSize, 100));
        session.setAttribute("dsManagerPage", 1);

        return "redirect:/deputy-staff-home/your-manager";
    }

    @GetMapping("/clear-search")
    public String clearSearch(HttpSession session) {
        session.removeAttribute("dsManagerKeyword");
        session.removeAttribute("dsManagerSearchType");
        session.setAttribute("dsManagerPage", 1);
        return "redirect:/deputy-staff-home/your-manager";
    }

    @PostMapping("/change-page-size")
    public String changePageSize(@RequestParam int pageSize, HttpSession session) {
        session.setAttribute("dsManagerPageSize", Math.max(1, Math.min(pageSize, 100)));
        session.setAttribute("dsManagerPage", 1);
        return "redirect:/deputy-staff-home/your-manager";
    }

    @GetMapping("/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getAvatar(@PathVariable String id) {
        Admins admin = adminsService.getAdminById(id);
        if (admin != null && admin.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(admin.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}