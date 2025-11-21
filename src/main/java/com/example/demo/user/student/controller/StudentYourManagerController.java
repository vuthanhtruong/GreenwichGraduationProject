package com.example.demo.user.student.controller;

import com.example.demo.user.majorLecturer.service.MajorLecturersService;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/student-home/your-manager")
public class StudentYourManagerController {

    private final StudentsService studentsService;
    private final StaffsService staffsService;

    public StudentYourManagerController(StudentsService studentsService,
                                        StaffsService staffsService) {
        this.studentsService = studentsService;
        this.staffsService = staffsService;
    }

    @GetMapping
    public String listManagers(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {

        // === PAGE SIZE ===
        if (pageSize == null || pageSize <= 0) {
            pageSize = (Integer) session.getAttribute("studentManagerPageSize");
            if (pageSize == null || pageSize <= 0) pageSize = 10;
        }
        pageSize = Math.min(pageSize, 100);
        session.setAttribute("studentManagerPageSize", pageSize);

        // === LẤY SINH VIÊN HIỆN TẠI ===
        Students currentStudent = studentsService.getStudent();
        if (currentStudent == null) {
            return "redirect:/login";
        }

        String campusId = currentStudent.getCampus().getCampusId();
        String majorId = currentStudent.getSpecialization().getMajor().getMajorId(); // Sinh viên có Major

        // === TÌM KIẾM TỪ SESSION ===
        String keyword = (String) session.getAttribute("studentManagerKeyword");
        String searchType = (String) session.getAttribute("studentManagerSearchType");

        List<Staffs> managers;
        long totalManagers;
        int totalPages;
        int firstResult = (page - 1) * pageSize;

        if (keyword != null && !keyword.trim().isEmpty() && searchType != null) {
            // Tìm kiếm trong toàn campus (vì manager có thể không cùng major chính xác 100%)
            managers = staffsService.searchStaffsByCampus(campusId, searchType, keyword.trim(), firstResult, pageSize);
            totalManagers = staffsService.countSearchResultsByCampus(campusId, searchType, keyword.trim());
        } else {
            // LẤY DANH SÁCH MANAGER CỦA CHUYÊN NGÀNH SINH VIÊN (đã fix phân trang ở DAO)
            managers = staffsService.getYourManagersPaginated(campusId, majorId, firstResult, pageSize);
            totalManagers = staffsService.countYourManagers(campusId, majorId);
        }

        totalPages = Math.max(1, (int) Math.ceil((double) totalManagers / pageSize));
        page = Math.max(1, Math.min(page, totalPages));

        session.setAttribute("studentManagerPage", page);
        session.setAttribute("studentManagerTotalPages", totalPages);

        // === MODEL ===
        model.addAttribute("managers", managers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalManagers", totalManagers);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("currentStudent", currentStudent);
        model.addAttribute("majorName", currentStudent.getSpecialization().getMajor().getMajorName());

        return "StudentYourManagerList"; // tạo thư mục student/ để phân biệt
    }

    // === SEARCH ===
    @PostMapping("/search")
    public String search(
            @RequestParam String searchType,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session) {

        session.setAttribute("studentManagerKeyword", keyword.trim().isEmpty() ? null : keyword.trim());
        session.setAttribute("studentManagerSearchType", searchType);
        session.setAttribute("studentManagerPageSize", Math.min(pageSize, 100));
        session.setAttribute("studentManagerPage", 1);

        return "redirect:/student-home/your-manager";
    }

    // === CLEAR SEARCH ===
    @GetMapping("/clear-search")
    public String clearSearch(HttpSession session) {
        session.removeAttribute("studentManagerKeyword");
        session.removeAttribute("studentManagerSearchType");
        session.setAttribute("studentManagerPage", 1);
        return "redirect:/student-home/your-manager";
    }

    // === CHANGE PAGE SIZE ===
    @PostMapping("/change-page-size")
    public String changePageSize(@RequestParam int pageSize, HttpSession session) {
        session.setAttribute("studentManagerPageSize", Math.max(1, Math.min(pageSize, 100)));
        session.setAttribute("studentManagerPage", 1);
        return "redirect:/student-home/your-manager";
    }

    // === AVATAR ===
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