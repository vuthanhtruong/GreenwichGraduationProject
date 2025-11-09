package com.example.demo.user.majorLecturer.controller;

import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
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
@RequestMapping("/major-lecturer-home/your-manager")
public class YourManagerController {

    private final StaffsService staffsService;
    private final MajorLecturersService majorLecturersService;

    public YourManagerController(StaffsService staffsService, MajorLecturersService majorLecturersService) {
        this.staffsService = staffsService;
        this.majorLecturersService = majorLecturersService;
    }

    @GetMapping
    public String listManagers(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {

        // --- PAGE SIZE ---
        if (pageSize == null || pageSize <= 0) {
            pageSize = (Integer) session.getAttribute("managerPageSize");
            if (pageSize == null || pageSize <= 0) pageSize = 10;
        }
        pageSize = Math.min(pageSize, 100);
        session.setAttribute("managerPageSize", pageSize);

        // --- LẤY MajorLecturer HIỆN TẠI ---
        MajorLecturers currentLecturer = majorLecturersService.getMajorLecturer();
        if (currentLecturer == null) {
            return "redirect:/login"; // hoặc error page
        }

        String campusId = currentLecturer.getCampus().getCampusId();
        String majorId = currentLecturer.getMajorManagement().getMajorId(); // ← MajorLecturer có Major

        // --- TÌM KIẾM TỪ SESSION ---
        String keyword = (String) session.getAttribute("managerKeyword");
        String searchType = (String) session.getAttribute("managerSearchType");

        List<Staffs> managers;
        long totalManagers;
        int totalPages;
        int firstResult = (page - 1) * pageSize;

        if (keyword != null && !keyword.trim().isEmpty() && searchType != null) {
            managers = staffsService.searchStaffsByCampus(campusId, searchType, keyword.trim(), firstResult, pageSize);
            totalManagers = staffsService.countSearchResultsByCampus(campusId, searchType, keyword.trim());
        } else {
            managers = staffsService.yourManagersByCampusIdAndMajor(campusId, majorId);
            totalManagers = managers.size();

            managers = managers.stream()
                    .skip(firstResult)
                    .limit(pageSize)
                    .toList();
        }

        totalPages = Math.max(1, (int) Math.ceil((double) totalManagers / pageSize));
        page = Math.max(1, Math.min(page, totalPages));

        session.setAttribute("managerPage", page);
        session.setAttribute("managerTotalPages", totalPages);

        // --- MODEL ---
        model.addAttribute("managers", managers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalManagers", totalManagers);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("currentLecturer", currentLecturer); // ← Đổi tên cho rõ

        return "YourManagerList";
    }

    // --- SEARCH ---
    @PostMapping("/search")
    public String search(
            @RequestParam String searchType,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session) {

        session.setAttribute("managerKeyword", keyword.trim().isEmpty() ? null : keyword.trim());
        session.setAttribute("managerSearchType", searchType);
        session.setAttribute("managerPageSize", Math.min(pageSize, 100));
        session.setAttribute("managerPage", 1);

        return "redirect:/major-lecturer-home/your-manager";
    }

    // --- CLEAR SEARCH ---
    @GetMapping("/clear-search")
    public String clearSearch(HttpSession session) {
        session.removeAttribute("managerKeyword");
        session.removeAttribute("managerSearchType");
        session.setAttribute("managerPage", 1);
        return "redirect:/major-lecturer-home/your-manager";
    }

    // --- CHANGE PAGE SIZE ---
    @PostMapping("/change-page-size")
    public String changePageSize(@RequestParam int pageSize, HttpSession session) {
        session.setAttribute("managerPageSize", Math.max(1, Math.min(pageSize, 100)));
        session.setAttribute("managerPage", 1);
        return "redirect:/major-lecturer-home/your-manager";
    }

    // --- AVATAR ---
    @GetMapping("/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getAvatar(@PathVariable String id) {
        Staffs staff = staffsService.getStaffById(id);
        if (staff != null && staff.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(staff.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}