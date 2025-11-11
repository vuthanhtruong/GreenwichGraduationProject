package com.example.demo.user.majorLecturer.controller;

import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
import com.example.demo.user.staff.service.StaffsService;
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
@RequestMapping("/staff-home/lecturers-list")
public class SearchLecturerController {

    private final MajorLecturersService lecturesService;
    private final StaffsService staffsService;

    public SearchLecturerController(MajorLecturersService lecturesService, StaffsService staffsService) {
        this.lecturesService = lecturesService;
        this.staffsService = staffsService;
    }


    @GetMapping("/search-lecturers")
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
                pageSize = (Integer) session.getAttribute("pageSize");
                if (pageSize == null) {
                    pageSize = 20;
                }
            }
            session.setAttribute("pageSize", pageSize);

            List<MajorLecturers> lecturers;
            long totalLecturers;

            if (keyword == null || keyword.trim().isEmpty()) {
                totalLecturers = lecturesService.numberOfLecturersByCampus(staffsService.getCampusOfStaff().getCampusId());
                lecturers = lecturesService.getPaginatedLecturersByCampus(staffsService.getCampusOfStaff().getCampusId(),(page - 1) * pageSize, pageSize);
            } else {
                lecturers = lecturesService.searchLecturersByCampus(staffsService.getCampusOfStaff().getCampusId(),searchType, keyword, (page - 1) * pageSize, pageSize);
                totalLecturers = lecturesService.countSearchResultsByCampus(staffsService.getCampusOfStaff().getCampusId(),searchType, keyword);
            }

            if (totalLecturers == 0) {
                model.addAttribute("teachers", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("searchType", searchType != null ? searchType : "name");
                model.addAttribute("keyword", keyword != null ? keyword : "");
                // Inside showClassesList(), after retrieving classes
                model.addAttribute("currentCampusName", staffsService.getCampusOfStaff().getCampusName());
                model.addAttribute("message", successMessage != null ? successMessage : (error != null ? error : "No lecturers found matching the search criteria."));
                return "SearchLecturers";
            }

            int totalPages = (int) Math.ceil((double) totalLecturers / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;

            model.addAttribute("teachers", lecturers);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            // Inside showClassesList(), after retrieving classes
            model.addAttribute("currentCampusName", staffsService.getCampusOfStaff().getCampusName());
            if (successMessage != null) {
                model.addAttribute("message", successMessage);
            } else if (error != null) {
                model.addAttribute("error", error);
            }

            return "SearchLecturers";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while searching for lecturers: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/search-lecturers")
    public String searchLecturers(
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
                pageSize = (Integer) session.getAttribute("pageSize");
                if (pageSize == null) {
                    pageSize = 5;
                }
            }
            session.setAttribute("pageSize", pageSize);

            List<MajorLecturers> lecturers;
            long totalLecturers;

            if (keyword == null || keyword.trim().isEmpty()) {
                totalLecturers = lecturesService.numberOfLecturersByCampus(staffsService.getCampusOfStaff().getCampusId());
                lecturers = lecturesService.getPaginatedLecturersByCampus(staffsService.getCampusOfStaff().getCampusId(),(page - 1) * pageSize, pageSize);
            } else {
                lecturers = lecturesService.searchLecturersByCampus(staffsService.getCampusOfStaff().getCampusId(),searchType, keyword, (page - 1) * pageSize, pageSize);
                totalLecturers = lecturesService.countMinorLecturersSearchResultsByCampus(staffsService.getCampusOfStaff().getCampusId(),searchType, keyword);
            }

            if (totalLecturers == 0) {
                model.addAttribute("teachers", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                // Inside showClassesList(), after retrieving classes
                model.addAttribute("currentCampusName", staffsService.getCampusOfStaff().getCampusName());
                model.addAttribute("searchType", searchType != null ? searchType : "name");
                model.addAttribute("keyword", keyword != null ? keyword : "");
                model.addAttribute("message", successMessage != null ? successMessage : (error != null ? error : "No lecturers found matching the search criteria."));
                return "SearchLecturers";
            }

            int totalPages = (int) Math.ceil((double) totalLecturers / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;

            model.addAttribute("teachers", lecturers);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            // Inside showClassesList(), after retrieving classes
            model.addAttribute("currentCampusName", staffsService.getCampusOfStaff().getCampusName());
            if (successMessage != null) {
                model.addAttribute("message", successMessage);
            } else if (error != null) {
                model.addAttribute("error", error);
            }

            return "SearchLecturers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while searching for lecturers: " + e.getMessage());
            redirectAttributes.addFlashAttribute("searchType", searchType);
            redirectAttributes.addFlashAttribute("keyword", keyword);
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize);
            return "redirect:/staff-home/lecturers-list/search-lecturers";
        }
    }
}