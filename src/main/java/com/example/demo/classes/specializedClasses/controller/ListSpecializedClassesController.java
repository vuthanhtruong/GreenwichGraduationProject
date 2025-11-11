package com.example.demo.classes.specializedClasses.controller;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.classes.specializedClasses.service.SpecializedClassesService;
import com.example.demo.subject.specializedSubject.service.SpecializedSubjectsService;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home/specialized-classes-list")
public class ListSpecializedClassesController {

    private static final Logger log = LoggerFactory.getLogger(ListSpecializedClassesController.class);

    private final SpecializedClassesService classesService;
    private final StaffsService staffsService;
    private final SpecializedSubjectsService specializedSubjectsService;

    @Autowired
    public ListSpecializedClassesController(SpecializedClassesService classesService,
                                            StaffsService staffsService,
                                            SpecializedSubjectsService specializedSubjectsService) {
        this.classesService = classesService;
        this.staffsService = staffsService;
        this.specializedSubjectsService = specializedSubjectsService;
    }

    @GetMapping("")
    public String showClassesList(
            @RequestParam(defaultValue = "1") int pageClasses,
            @RequestParam(required = false) Integer pageSize,
            Model model,
            HttpSession session,
            Authentication authentication) {
        try {
            // Xác định pageSize
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("classPageSize");
                if (pageSize == null) {
                    pageSize = 5;
                }
            }
            session.setAttribute("classPageSize", pageSize);
            log.debug("Page size set to: {}", pageSize);

            // Lấy major từ staff
            var staff = staffsService.getStaff();
            if (staff == null || staff.getMajorManagement() == null) {
                log.warn("Staff or major management not found for user: {}", authentication.getName());
                model.addAttribute("errors", List.of("Staff or major management not found"));
                model.addAttribute("classes", new ArrayList<>());
                model.addAttribute("currentPageClasses", 1);
                model.addAttribute("totalPagesClasses", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("totalClasses", 0);
                model.addAttribute("specializedSubjects", specializedSubjectsService.getSubjects());
                model.addAttribute("newClass", new SpecializedClasses());
                model.addAttribute("alertClass", "alert-warning");
                // Inside showClassesList(), after retrieving classes
                model.addAttribute("currentCampusName", staffsService.getCampusOfStaff().getCampusName());
                return "SpecializedClassesList";
            }

            // Lấy danh sách lớp và phân trang
            long totalClasses = classesService.numberOfClassesByCampus(staff.getMajorManagement(),staffsService.getCampusOfStaff().getCampusId());
            int totalPagesClasses = Math.max(1, (int) Math.ceil((double) totalClasses / pageSize));
            pageClasses = Math.max(1, Math.min(pageClasses, totalPagesClasses));
            session.setAttribute("currentPageClasses", pageClasses);
            session.setAttribute("totalPagesClasses", totalPagesClasses);
            log.debug("Total classes: {}, Total pages: {}, Current page: {}", totalClasses, totalPagesClasses, pageClasses);

            int firstResult = (pageClasses - 1) * pageSize;
            List<SpecializedClasses> classes = classesService.getPaginatedClassesByCampus(firstResult, pageSize, staff.getMajorManagement(),staffsService.getCampusOfStaff().getCampusId());
            log.debug("Found {} classes for major ID: {}", classes.size(), staff.getMajorManagement().getMajorId());

            // Kiểm tra null cho specializedSubject
            classes.forEach(c -> {
                if (c.getSpecializedSubject() == null) {
                    log.warn("Null specializedSubject for class ID: {}", c.getClassId());
                }
            });

            if (totalClasses == 0) {
                model.addAttribute("classes", new ArrayList<>());
                model.addAttribute("currentPageClasses", 1);
                model.addAttribute("totalPagesClasses", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("totalClasses", 0);
                model.addAttribute("specializedSubjects", specializedSubjectsService.getSubjects());
                model.addAttribute("newClass", new SpecializedClasses());
                model.addAttribute("message", "No specialized classes found for this major.");
                model.addAttribute("alertClass", "alert-warning");
                // Inside showClassesList(), after retrieving classes
                model.addAttribute("currentCampusName", staffsService.getCampusOfStaff().getCampusName());
                return "SpecializedClassesList";
            }

            model.addAttribute("classes", classes);
            model.addAttribute("newClass", new SpecializedClasses());
            model.addAttribute("currentPageClasses", pageClasses);
            model.addAttribute("totalPagesClasses", totalPagesClasses);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalClasses", totalClasses);
            model.addAttribute("specializedSubjects", specializedSubjectsService.getSubjects());
            // Inside showClassesList(), after retrieving classes
            model.addAttribute("currentCampusName", staffsService.getCampusOfStaff().getCampusName());
            return "SpecializedClassesList";
        } catch (Exception e) {
            log.error("Error fetching specialized classes: {}", e.getMessage(), e);
            model.addAttribute("errors", List.of("An error occurred while retrieving classes: " + e.getMessage()));
            model.addAttribute("newClass", new SpecializedClasses());
            model.addAttribute("specializedSubjects", specializedSubjectsService.getSubjects());
            model.addAttribute("currentPageClasses", 1);
            model.addAttribute("totalPagesClasses", 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalClasses", 0);
            // Inside showClassesList(), after retrieving classes
            model.addAttribute("currentCampusName", staffsService.getCampusOfStaff().getCampusName());
            return "SpecializedClassesList";
        }
    }
}