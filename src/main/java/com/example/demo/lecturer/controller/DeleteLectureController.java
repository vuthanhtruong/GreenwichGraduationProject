package com.example.demo.lecturer.controller;

import com.example.demo.lecturer.service.LecturesService;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.person.service.PersonsService;
import com.example.demo.student.service.StudentsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff-home/lecturers-list")
public class DeleteLectureController {
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final LecturesService lecturesService;
    private final PersonsService personsService;

    public DeleteLectureController(StaffsService staffsService, LecturesService lecturesService, StudentsService studentsService, PersonsService personsService) {
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.lecturesService = lecturesService;
        this.personsService = personsService;
    }

    @DeleteMapping("/delete-lecturer/{id}")
    public String deleteLecture(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "list") String source,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            RedirectAttributes redirectAttributes) {
        try {
            if (!personsService.existsPersonById(id)) {
                redirectAttributes.addFlashAttribute("error", "Lecturer with ID " + id + " not found.");
                if (source.equals("search")) {
                    redirectAttributes.addFlashAttribute("searchType", searchType);
                    redirectAttributes.addFlashAttribute("keyword", keyword);
                    redirectAttributes.addFlashAttribute("page", page);
                    redirectAttributes.addFlashAttribute("pageSize", pageSize);
                    return "redirect:/staff-home/lecturers-list/search-lecturers";
                }
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/staff-home/lecturers-list";
            }

            lecturesService.deleteLecturer(id);
            redirectAttributes.addFlashAttribute("message", "Deleted lecturer ID: " + id);
            if (source.equals("search")) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/staff-home/lecturers-list/search-lecturers";
            }
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize);
            return "redirect:/staff-home/lecturers-list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting lecturer: " + e.getMessage());
            if (source.equals("search")) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/staff-home/lecturers-list/search-lecturers";
            }
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize);
            return "redirect:/staff-home/lecturers-list";
        }
    }
}