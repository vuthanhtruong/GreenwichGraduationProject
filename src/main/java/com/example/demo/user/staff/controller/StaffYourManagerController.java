package com.example.demo.user.staff.controller;

import com.example.demo.user.admin.model.Admins;
import com.example.demo.user.admin.service.AdminsService;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/staff-home/your-manager")
public class StaffYourManagerController {

    private final StaffsService staffsService;
    private final AdminsService adminsService;

    public StaffYourManagerController(StaffsService staffsService, AdminsService adminsService) {
        this.staffsService = staffsService;
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
            pageSize = (Integer) session.getAttribute("staffManagerPageSize");
            if (pageSize == null || pageSize <= 0) pageSize = 10;
        }
        pageSize = Math.min(pageSize, 100);
        session.setAttribute("staffManagerPageSize", pageSize);

        // --- LẤY STAFF HIỆN TẠI ---
        Staffs currentStaff = staffsService.getStaff();
        if (currentStaff == null || currentStaff.getCampus() == null) {
            return "redirect:/login";
        }
        String campusId = currentStaff.getCampus().getCampusId();

        // --- TÌM KIẾM TỪ SESSION ---
        String keyword = (String) session.getAttribute("staffManagerKeyword");
        String searchType = (String) session.getAttribute("staffManagerSearchType");

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

        session.setAttribute("staffManagerPage", page);
        session.setAttribute("staffManagerTotalPages", totalPages);

        model.addAttribute("managers", managers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalManagers", totalManagers);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("currentStaff", currentStaff);

        return "StaffYourManagerList"; // → StaffYourManagerList.html
    }

    @PostMapping("/search")
    public String search(
            @RequestParam String searchType,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session) {

        session.setAttribute("staffManagerKeyword", keyword.trim().isEmpty() ? null : keyword.trim());
        session.setAttribute("staffManagerSearchType", searchType);
        session.setAttribute("staffManagerPageSize", Math.min(pageSize, 100));
        session.setAttribute("staffManagerPage", 1);

        return "redirect:/staff-home/your-manager";
    }

    @GetMapping("/clear-search")
    public String clearSearch(HttpSession session) {
        session.removeAttribute("staffManagerKeyword");
        session.removeAttribute("staffManagerSearchType");
        session.setAttribute("staffManagerPage", 1);
        return "redirect:/staff-home/your-manager";
    }

    @PostMapping("/change-page-size")
    public String changePageSize(@RequestParam int pageSize, HttpSession session) {
        session.setAttribute("staffManagerPageSize", Math.max(1, Math.min(pageSize, 100)));
        session.setAttribute("staffManagerPage", 1);
        return "redirect:/staff-home/your-manager";
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