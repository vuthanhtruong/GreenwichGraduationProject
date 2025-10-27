package com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.controller;

import com.example.demo.lecturers_Classes.abstractLecturers_Classes.model.Lecturers_Classes;
import com.example.demo.lecturers_Classes.abstractLecturers_Classes.service.Lecturers_ClassesService;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
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
@RequestMapping("/major-lecturer-home/classes-list")
public class MajorLecturerClassesListController {

    private static final Logger logger = LoggerFactory.getLogger(MajorLecturerClassesListController.class);

    private final MajorLecturersService majorLecturersService;
    private final Lecturers_ClassesService lecturersClassesService;

    public MajorLecturerClassesListController(
            MajorLecturersService majorLecturersService,
            Lecturers_ClassesService lecturersClassesService) {
        this.majorLecturersService = majorLecturersService;
        this.lecturersClassesService = lecturersClassesService;
    }

    @GetMapping("")
    public String listLecturerClasses(Model model,
                                      HttpSession session,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(required = false) Integer pageSize) {
        try {
            MajorLecturers lecturer = majorLecturersService.getMajorLecturer();
            if (lecturer == null) {
                return error(model, "No authenticated lecturer", pageSize);
            }

            // Page size setup
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("classesPageSize");
                if (pageSize == null) pageSize = 10;
            }
            session.setAttribute("classesPageSize", pageSize);

            // 🔹 LẤY DUY NHẤT List<Lecturers_Classes>
            List<Lecturers_Classes> allRelations = lecturersClassesService.getClassesByLecturer(lecturer);
            long total = allRelations.size();

            // 🔹 Sắp xếp theo createdAt (được định nghĩa trong lớp cha)
            allRelations.sort(Comparator.comparing(Lecturers_Classes::getCreatedAt).reversed());

            // 🔹 Phân trang
            int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));
            page = Math.max(1, Math.min(page, totalPages));
            int first = (page - 1) * pageSize;

            List<Lecturers_Classes> pageData = allRelations.stream()
                    .skip(first)
                    .limit(pageSize)
                    .collect(Collectors.toList());

            // 🔹 Add to model
            model.addAttribute("lecturer", lecturer);
            model.addAttribute("lecturerClasses", pageData);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalClasses", total);

            session.setAttribute("classesPage", page);
            session.setAttribute("classesTotalPages", totalPages);

            return "MajorLecturerClassesList";

        } catch (Exception e) {
            logger.error("Error loading classes", e);
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
        return "MajorLecturerClassesList";
    }
}
