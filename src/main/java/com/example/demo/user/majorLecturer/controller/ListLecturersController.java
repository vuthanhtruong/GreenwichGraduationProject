// src/main/java/com/example/demo/user/majorLecturer/controller/ListLecturersController.java
package com.example.demo.user.majorLecturer.controller;

import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home/lecturers-list")
public class ListLecturersController {

    private final MajorLecturersService lecturesService;
    private final StaffsService staffsService;

    public ListLecturersController(MajorLecturersService lecturesService, StaffsService staffsService) {
        this.lecturesService = lecturesService;
        this.staffsService = staffsService;
    }

    @GetMapping("")
    public String listLecturers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            Model model,
            HttpSession session) {

        try {
            String campusId = staffsService.getCampusOfStaff().getCampusId();
            String majorId = staffsService.getStaffMajor().getMajorId(); // Fixed: Use staff's major
            String campusName = staffsService.getCampusOfStaff().getCampusName();
            String majorName = staffsService.getStaffMajor().getMajorName();

            // === Page Size ===
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("lecturerPageSize");
                if (pageSize == null || pageSize <= 0) pageSize = 20;
            }
            session.setAttribute("lecturerPageSize", pageSize);

            // === Search or Pagination ===
            long totalLecturers;
            List<MajorLecturers> teachers;
            int firstResult = (page - 1) * pageSize;

            if (keyword != null && !keyword.trim().isBlank() && searchType != null) {
                totalLecturers = lecturesService.countSearchLecturersByCampusAndMajor(
                        campusId, majorId, searchType, keyword.trim());
                teachers = lecturesService.searchLecturersByCampusAndMajor(
                        campusId, majorId, searchType, keyword.trim(), firstResult, pageSize);
            } else {
                totalLecturers = lecturesService.totalLecturersByCampusAndMajor(campusId, majorId);
                teachers = lecturesService.getPaginatedLecturersByCampusAndMajor(
                        campusId, majorId, firstResult, pageSize);
            }

            // === Pagination Logic ===
            int totalPages = totalLecturers == 0 ? 1 : (int) Math.ceil((double) totalLecturers / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages && totalPages > 0) page = totalPages;

            session.setAttribute("lecturerPage", page);
            session.setAttribute("lecturerTotalPages", totalPages);

            // === Model Attributes ===
            model.addAttribute("teachers", teachers);
            model.addAttribute("lecturer", new MajorLecturers());
            model.addAttribute("editLecturer", new MajorLecturers());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalLecturers", totalLecturers);
            model.addAttribute("currentCampusName", campusName);
            model.addAttribute("currentMajorName", majorName);

            if (totalLecturers == 0) {
                model.addAttribute("alertClass", "alert-info");
                model.addAttribute("message", "No lecturers found in " + majorName + ".");
            }

            return "LecturersList";

        } catch (SecurityException e) {
            model.addAttribute("errors", List.of("Access denied: " + e.getMessage()));
            model.addAttribute("lecturer", new MajorLecturers());
            model.addAttribute("editLecturer", new MajorLecturers());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSize != null ? pageSize : 20);
            model.addAttribute("totalLecturers", 0);
            model.addAttribute("currentCampusName", "Unknown");
            model.addAttribute("currentMajorName", "Unknown");
            return "LecturersList";
        }
    }

    @GetMapping("/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getLecturerAvatar(@PathVariable String id) {
        MajorLecturers lecturer = lecturesService.getLecturerById(id);
        if (lecturer != null && lecturer.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(lecturer.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}