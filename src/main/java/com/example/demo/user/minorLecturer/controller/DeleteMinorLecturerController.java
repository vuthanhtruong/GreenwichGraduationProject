package com.example.demo.user.minorLecturer.controller;

import com.example.demo.user.minorLecturer.service.MinorLecturersService;
import com.example.demo.user.person.service.PersonsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/deputy-staff-home/minor-lecturers-list")
public class DeleteMinorLecturerController {

    private final MinorLecturersService minorLecturersService;
    private final PersonsService personsService;

    public DeleteMinorLecturerController(MinorLecturersService minorLecturersService, PersonsService personsService) {
        this.minorLecturersService = minorLecturersService;
        this.personsService = personsService;
    }

    @DeleteMapping("/delete-minor-lecturer/{id}")
    public String deleteMinorLecturer(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "list") String source,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            RedirectAttributes redirectAttributes) {
        try {
            if (!personsService.existsPersonById(id)) {
                redirectAttributes.addFlashAttribute("error", "Minor Lecturer with ID " + id + " not found.");
                if (source.equals("search")) {
                    redirectAttributes.addFlashAttribute("searchType", searchType);
                    redirectAttributes.addFlashAttribute("keyword", keyword);
                    redirectAttributes.addFlashAttribute("page", page);
                    redirectAttributes.addFlashAttribute("pageSize", pageSize);
                    return "redirect:/deputy-staff-home/minor-lecturers-list/search-minor-lecturers";
                }
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/deputy-staff-home/minor-lecturers-list";
            }

            minorLecturersService.deleteMinorLecturer(id);
            redirectAttributes.addFlashAttribute("message", "Deleted minor lecturer ID: " + id);
            if (source.equals("search")) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/deputy-staff-home/minor-lecturers-list/search-minor-lecturers";
            }
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize);
            return "redirect:/deputy-staff-home/minor-lecturers-list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting minor lecturer: " + e.getMessage());
            if (source.equals("search")) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/deputy-staff-home/minor-lecturers-list/search-minor-lecturers";
            }
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize);
            return "redirect:/deputy-staff-home/minor-lecturers-list";
        }
    }
}