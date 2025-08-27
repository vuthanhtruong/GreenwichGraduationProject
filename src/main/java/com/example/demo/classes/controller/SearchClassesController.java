package com.example.demo.classes.controller;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.classes.service.ClassesService;
import com.example.demo.subject.service.MajorSubjectsService;
import com.example.demo.Staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home/classes-list")
public class SearchClassesController {

    private final ClassesService classesService;
    private final StaffsService staffsService;
    private final MajorSubjectsService subjectsService;

    public SearchClassesController(ClassesService classesService, StaffsService staffsService, MajorSubjectsService subjectsService) {
        this.classesService = classesService;
        this.staffsService = staffsService;
        this.subjectsService = subjectsService;
    }

    @PostMapping("/search-classes")
    public String searchClasses(
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            Model model,
            HttpSession session) {
        try {
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("pageSize");
                if (pageSize == null) {
                    pageSize = 5; // Mặc định 5 lớp học mỗi trang
                }
            }
            session.setAttribute("pageSize", pageSize);

            List<MajorClasses> classes;
            long totalClasses;

            // Nếu không có keyword hoặc keyword rỗng, trả về danh sách đầy đủ
            if (keyword == null || keyword.trim().isEmpty()) {
                totalClasses = classesService.numberOfClasses(staffsService.getStaffMajor());
                classes = classesService.getPaginatedClasses((page - 1) * pageSize, pageSize, staffsService.getStaffMajor());
            } else {
                // Tìm kiếm lớp học theo searchType (name hoặc id)
                classes = classesService.searchClasses(searchType, keyword, (page - 1) * pageSize, pageSize, staffsService.getStaffMajor());
                totalClasses = classesService.countSearchResults(searchType, keyword, staffsService.getStaffMajor());
            }

            if (totalClasses == 0) {
                model.addAttribute("classes", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("searchType", searchType);
                model.addAttribute("keyword", keyword);
                model.addAttribute("subjects", subjectsService.AcceptedSubjectsByMajor(staffsService.getStaffMajor()));
                model.addAttribute("message", "No classes found matching the search criteria.");
                return "ClassesList";
            }

            int totalPages = (int) Math.ceil((double) totalClasses / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;

            model.addAttribute("classes", classes);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("searchType", searchType);
            model.addAttribute("keyword", keyword);
            model.addAttribute("subjects", subjectsService.AcceptedSubjectsByMajor(staffsService.getStaffMajor()));

            return "ClassesList";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while searching for classes: " + e.getMessage());
            return "error";
        }
    }
}