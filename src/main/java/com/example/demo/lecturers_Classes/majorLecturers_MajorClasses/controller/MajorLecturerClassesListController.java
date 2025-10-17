package com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.controller;

import com.example.demo.classes.abstractClass.model.Classes;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.model.MajorLecturers_MajorClasses;
import com.example.demo.lecturers_Classes.majorLecturers_SpecializedClasses.model.MajorLecturers_SpecializedClasses;
import com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.service.MajorLecturers_MajorClassesService;
import com.example.demo.lecturers_Classes.majorLecturers_SpecializedClasses.service.MajorLecturers_SpecializedClassesService;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/major-lecturer-home/classes-list")
public class MajorLecturerClassesListController {

    private static final Logger logger = LoggerFactory.getLogger(MajorLecturerClassesListController.class);
    private final MajorLecturersService majorEmployeesService;
    private final MajorLecturers_MajorClassesService majorClassesService;
    private final MajorLecturers_SpecializedClassesService specializedClassesDAO;

    public MajorLecturerClassesListController(
            MajorLecturersService majorEmployeesService,
            MajorLecturers_MajorClassesService majorClassesService,
            MajorLecturers_SpecializedClassesService specializedClassesDAO) {
        this.majorEmployeesService = majorEmployeesService;
        this.majorClassesService = majorClassesService;
        this.specializedClassesDAO = specializedClassesDAO;
    }

    @GetMapping("")
    public String listLecturerClasses(Model model, HttpSession session,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(required = false) Integer pageSize) {
        try {
            // Fetch authenticated lecturer
            MajorLecturers lecturer = majorEmployeesService.getMajorLecturer();
            if (lecturer == null) {
                logger.warn("No authenticated lecturer found");
                model.addAttribute("errors", List.of("No authenticated lecturer found"));
                model.addAttribute("classes", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", 10);
                model.addAttribute("totalClasses", 0);
                return "MajorLecturerClassesList";
            }

            // Set pageSize
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("classesPageSize");
                if (pageSize == null) {
                    pageSize = 10; // Default 10 classes per page
                }
            }
            session.setAttribute("classesPageSize", pageSize);

            // Fetch classes from MajorLecturers_MajorClasses and MajorLecturers_SpecializedClasses
            List<MajorLecturers_MajorClasses> majorClassesList = majorClassesService.getClassByLecturer(lecturer);
            List<MajorLecturers_SpecializedClasses> specializedClassesList = specializedClassesDAO.getClassByLecturer(lecturer);

            // Combine and initialize classes
            List<Classes> allClasses = new ArrayList<>();
            for (MajorLecturers_MajorClasses mc : majorClassesList) {
                Classes clazz = mc.getClassEntity();
                Hibernate.initialize(clazz);
                if (clazz instanceof MajorClasses majorClass) {
                    Hibernate.initialize(majorClass.getSubject());
                }
                allClasses.add(clazz);
            }
            for (MajorLecturers_SpecializedClasses sc : specializedClassesList) {
                Classes clazz = sc.getClassEntity();
                Hibernate.initialize(clazz);
                if (clazz instanceof SpecializedClasses specializedClass) {
                    Hibernate.initialize(specializedClass.getSpecializedSubject());
                    if (specializedClass.getSpecializedSubject() != null) {
                        Hibernate.initialize(specializedClass.getSpecializedSubject().getSpecialization());
                    }
                }
                allClasses.add(clazz);
            }
            // Sort classes by createdAt (descending)
            allClasses.sort((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()));
            // Pagination
            long totalClasses = allClasses.size();
            int totalPages = Math.max(1, (int) Math.ceil((double) totalClasses / pageSize));
            page = Math.max(1, Math.min(page, totalPages));
            session.setAttribute("classesPage", page);
            session.setAttribute("classesTotalPages", totalPages);

            // Get paginated classes
            int firstResult = (page - 1) * pageSize;
            List<Classes> paginatedClasses = allClasses.stream()
                    .skip(firstResult)
                    .limit(pageSize)
                    .collect(Collectors.toList());

            // Add attributes to model
            model.addAttribute("lecturer", lecturer);
            model.addAttribute("classes", paginatedClasses);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalClasses", totalClasses);

            return "MajorLecturerClassesList";
        } catch (Exception e) {
            logger.error("Error loading classes: {}", e.getMessage(), e);
            model.addAttribute("errors", List.of("Error loading classes: " + e.getMessage()));
            model.addAttribute("classes", new ArrayList<>());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSize != null ? pageSize : 10);
            model.addAttribute("totalClasses", 0);
            return "MajorLecturerClassesList";
        }
    }
}