package com.example.demo.subject.controller;

import com.example.demo.entity.Enums.LearningProgramTypes;
import com.example.demo.subject.model.MajorSubjects;
import com.example.demo.Staff.service.StaffsService;
import com.example.demo.subject.service.MajorSubjectsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/staff-home/major-subjects-list")
@PreAuthorize("hasRole('STAFF')")
public class SearchMajorSubjectsController {

    private final MajorSubjectsService subjectsService;
    private final StaffsService staffsService;

    @Autowired
    public SearchMajorSubjectsController(MajorSubjectsService subjectsService, StaffsService staffsService) {
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;
    }

    @GetMapping("/search-subjects")
    public String showSearchResults(
            @RequestParam(value = "searchType", required = false, defaultValue = "name") String searchType,
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(value = "successMessage", required = false) String successMessage,
            @RequestParam(value = "errorMessage", required = false) String errorMessage,
            Model model,
            HttpSession session) {
        try {
            // Xử lý pageSize từ session hoặc mặc định
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("subjectsPageSize");
                if (pageSize == null) {
                    pageSize = 5;
                }
            }
            session.setAttribute("subjectsPageSize", pageSize);

            // Kiểm tra chuyên ngành của nhân viên
            if (staffsService.getStaffMajor() == null) {
                model.addAttribute("errorMessage", "No major assigned to the current staff.");
                model.addAttribute("subjects", List.of());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("searchType", searchType);
                model.addAttribute("keyword", keyword);
                model.addAttribute("newSubject", new MajorSubjects());
                model.addAttribute("learningProgramTypes", LearningProgramTypes.values());
                return "SearchMajorSubjects";
            }

            // Kiểm tra searchType hợp lệ
            if (!"name".equals(searchType) && !"id".equals(searchType)) {
                model.addAttribute("errorMessage", "Invalid search type.");
                model.addAttribute("subjects", List.of());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("searchType", searchType != null ? searchType : "name");
                model.addAttribute("keyword", keyword);
                model.addAttribute("newSubject", new MajorSubjects());
                model.addAttribute("learningProgramTypes", LearningProgramTypes.values());
                return "SearchMajorSubjects";
            }

            // Kiểm tra từ khóa tìm kiếm
            if (keyword == null || keyword.trim().isEmpty()) {
                model.addAttribute("errorMessage", "Search keyword cannot be empty.");
                model.addAttribute("subjects", List.of());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("searchType", searchType);
                model.addAttribute("keyword", "");
                model.addAttribute("newSubject", new MajorSubjects());
                model.addAttribute("learningProgramTypes", LearningProgramTypes.values());
                return "SearchMajorSubjects";
            }

            // Thực hiện tìm kiếm
            List<MajorSubjects> subjects = subjectsService.searchSubjects(searchType, keyword, (page - 1) * pageSize, pageSize, staffsService.getStaffMajor());
            long totalSubjects = subjectsService.countSearchResults(searchType, keyword, staffsService.getStaffMajor());

            // Xử lý trường hợp không tìm thấy kết quả
            if (totalSubjects == 0) {
                model.addAttribute("subjects", List.of());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("searchType", searchType);
                model.addAttribute("keyword", keyword);
                model.addAttribute("newSubject", new MajorSubjects());
                model.addAttribute("learningProgramTypes", LearningProgramTypes.values());
                model.addAttribute("message", "No subjects found matching the search criteria.");
                return "SearchMajorSubjects";
            }

            // Tính toán phân trang
            int totalPages = (int) Math.ceil((double) totalSubjects / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;

            // Thêm dữ liệu vào model
            model.addAttribute("subjects", subjects);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("searchType", searchType);
            model.addAttribute("keyword", keyword);
            model.addAttribute("newSubject", new MajorSubjects());
            model.addAttribute("learningProgramTypes", LearningProgramTypes.values());
            if (successMessage != null) {
                model.addAttribute("successMessage", successMessage);
            } else if (errorMessage != null) {
                model.addAttribute("errorMessage", errorMessage);
            }

            return "SearchMajorSubjects";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while searching for subjects: " + e.getMessage());
            model.addAttribute("subjects", List.of());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("newSubject", new MajorSubjects());
            model.addAttribute("learningProgramTypes", LearningProgramTypes.values());
            return "SearchMajorSubjects";
        }
    }

    @PostMapping("/search-subjects")
    public String searchSubjects(
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(value = "source", required = false, defaultValue = "search") String source,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        // Lưu pageSize vào session
        if (pageSize == null) {
            pageSize = (Integer) session.getAttribute("subjectsPageSize");
            if (pageSize == null) {
                pageSize = 5;
            }
        }
        session.setAttribute("subjectsPageSize", pageSize);

        // Thêm các tham số vào redirectAttributes để duy trì trạng thái
        redirectAttributes.addFlashAttribute("searchType", searchType != null ? searchType : "name");
        redirectAttributes.addFlashAttribute("keyword", keyword != null ? keyword : "");
        redirectAttributes.addFlashAttribute("page", page);
        redirectAttributes.addFlashAttribute("pageSize", pageSize);

        // Chuyển hướng về GET /search-subjects
        return "redirect:/staff-home/major-subjects-list/search-subjects";
    }
}