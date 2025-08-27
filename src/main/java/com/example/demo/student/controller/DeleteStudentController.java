package com.example.demo.student.controller;

import com.example.demo.lecturer.service.LecturesService;
import com.example.demo.Staff.service.StaffsService;
import com.example.demo.person.service.PersonsService;
import com.example.demo.student.service.StudentsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff-home/students-list")
public class DeleteStudentController {
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final LecturesService lecturesService;
    private final PersonsService personsService;

    public DeleteStudentController(StaffsService staffsService, LecturesService lecturesService, StudentsService studentsService, PersonsService personsService) {
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.lecturesService = lecturesService;
        this.personsService = personsService;
    }

    @DeleteMapping("/delete-student/{id}")
    public String deleteStudent(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "list") String source,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            RedirectAttributes redirectAttributes) {
        try {
            if (!personsService.existsPersonById(id)) {
                redirectAttributes.addFlashAttribute("error", "Student with ID " + id + " not found.");
                if (source.equals("search")) {
                    redirectAttributes.addFlashAttribute("searchType", searchType);
                    redirectAttributes.addFlashAttribute("keyword", keyword);
                    redirectAttributes.addFlashAttribute("page", page);
                    redirectAttributes.addFlashAttribute("pageSize", pageSize);
                    return "redirect:/staff-home/search-students";
                }
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/staff-home/students-list";
            }

            studentsService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("message", "Deleted student ID: " + id);
            if (source.equals("search")) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/staff-home/search-students";
            }
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize);
            return "redirect:/staff-home/students-list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting student: " + e.getMessage());
            if (source.equals("search")) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/staff-home/search-students";
            }
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize);
            return "redirect:/staff-home/students-list";
        }
    }
}