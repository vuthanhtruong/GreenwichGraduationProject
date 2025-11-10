// File: MinorLecturerClassesListController.java
package com.example.demo.lecturers_Classes.minorLecturers_MinorClasses.controller;

import com.example.demo.lecturers_Classes.minorLecturers_MinorClasses.model.MinorLecturers_MinorClasses;
import com.example.demo.lecturers_Classes.minorLecturers_MinorClasses.service.MinorLecturers_MinorClassesService;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.user.minorLecturer.service.MinorLecturersService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/minor-lecturer-home/classes-list")
public class MinorLecturerClassesListController {

    private static final Logger logger = LoggerFactory.getLogger(MinorLecturerClassesListController.class);

    private final MinorLecturersService minorLecturersService;
    private final MinorLecturers_MinorClassesService minorClassesService;

    public MinorLecturerClassesListController(
            MinorLecturersService minorLecturersService,
            MinorLecturers_MinorClassesService minorClassesService) {
        this.minorLecturersService = minorLecturersService;
        this.minorClassesService = minorClassesService;
    }

    @GetMapping
    public String listLecturerClasses(Model model,
                                      HttpSession session,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(required = false) Integer pageSize) {
        try {
            MinorLecturers lecturer = minorLecturersService.getMinorLecturer();
            if (lecturer == null) {
                return error(model, "No authenticated minor lecturer", pageSize);
            }

            // Page size setup
            if (pageSize == null || pageSize <= 0) {
                pageSize = (Integer) session.getAttribute("minorClassesPageSize");
                if (pageSize == null || pageSize <= 0) pageSize = 10;
            }
            pageSize = Math.min(pageSize, 100);
            session.setAttribute("minorClassesPageSize", pageSize);

            // LẤY DANH SÁCH LỚP TỪ DAO CHÍNH XÁC
            List<MinorLecturers_MinorClasses> allRelations = minorClassesService.getClassByLecturer(lecturer);
            long total = allRelations.size();

            // SẮP XẾP: mới nhất trước
            allRelations.sort(Comparator.comparing(MinorLecturers_MinorClasses::getCreatedAt).reversed());

            // PHÂN TRANG
            int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));
            page = Math.max(1, Math.min(page, totalPages));
            int first = (page - 1) * pageSize;

            List<MinorLecturers_MinorClasses> pageData = allRelations.stream()
                    .skip(first)
                    .limit(pageSize)
                    .collect(Collectors.toList());

            // MODEL
            model.addAttribute("lecturer", lecturer);
            model.addAttribute("lecturerClasses", pageData);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalClasses", total);

            session.setAttribute("minorClassesPage", page);
            session.setAttribute("minorClassesTotalPages", totalPages);

            return "MinorLecturerClassesList";

        } catch (Exception e) {
            logger.error("Error loading classes for minor lecturer", e);
            return error(model, "Error loading classes: " + e.getMessage(), pageSize);
        }
    }

    private String error(Model model, String msg, Integer pageSize) {
        model.addAttribute("errors", List.of(msg));
        model.addAttribute("lecturerClasses", new ArrayList<>());
        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", 1);
        model.addAttribute("pageSize", pageSize != null ? pageSize : 10);
        model.addAttribute("totalClasses", 0);
        return "MinorLecturerClassesList";
    }
}