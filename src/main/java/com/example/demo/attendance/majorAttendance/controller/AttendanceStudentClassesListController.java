package com.example.demo.attendance.majorAttendance.controller;

import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import com.example.demo.students_Classes.abstractStudents_Class.model.Students_Classes;
import com.example.demo.students_Classes.abstractStudents_Class.service.StudentsClassesService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student-home/attendance")
public class AttendanceStudentClassesListController {

    private final StudentsService studentsService;
    private final StudentsClassesService studentsClassesService;

    public AttendanceStudentClassesListController(StudentsService studentsService, StudentsClassesService studentsClassesService) {
        this.studentsService = studentsService;
        this.studentsClassesService = studentsClassesService;
    }

    @GetMapping("")
    public String listStudentClasses(Model model, HttpSession session,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(required = false) Integer pageSize) {
        try {
            // Lấy thông tin học sinh hiện tại
            Students student = studentsService.getStudent();
            if (student == null) {
                model.addAttribute("errors", List.of("No authenticated student found"));
                return "StudentClassesList";
            }

            // Thiết lập pageSize
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("classesPageSize");
                if (pageSize == null) {
                    pageSize = 10; // Mặc định 10 lớp mỗi trang
                }
            }
            session.setAttribute("classesPageSize", pageSize);

            // Lấy danh sách lớp từ StudentsClassesService
            List<Students_Classes> allClasses = studentsClassesService.getClassByStudent(student.getId());

            // Tính tổng số lớp và phân trang
            long totalClasses = allClasses.size();
            int totalPages = Math.max(1, (int) Math.ceil((double) totalClasses / pageSize));
            page = Math.max(1, Math.min(page, totalPages));
            session.setAttribute("classesPage", page);
            session.setAttribute("classesTotalPages", totalPages);

            // Lấy danh sách lớp cho trang hiện tại
            int firstResult = (page - 1) * pageSize;
            List<Students_Classes> paginatedClasses = allClasses.stream()
                    .skip(firstResult)
                    .limit(pageSize)
                    .collect(Collectors.toList());

            // Thêm thông tin vào model
            model.addAttribute("student", student);
            model.addAttribute("classes", paginatedClasses);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalClasses", totalClasses);

            return "AttendanceStudentClassesList";
        } catch (Exception e) {
            model.addAttribute("errors", List.of("Error loading classes: " + e.getMessage()));
            model.addAttribute("student", studentsService.getStudent());
            model.addAttribute("classes", new ArrayList<>());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSize != null ? pageSize : 10);
            model.addAttribute("totalClasses", 0);
            return "AttendanceStudentClassesList";
        }
    }
}