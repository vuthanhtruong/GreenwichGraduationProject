package com.example.demo.user.majorLecturer.controller;

import com.example.demo.entity.Enums.Gender;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
import com.example.demo.user.person.service.PersonsService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/staff-home/lecturers-list")
public class EditLecturerController {
    private final MajorLecturersService lecturesService;
    private final PersonsService personsService;

    public EditLecturerController(MajorLecturersService lecturesService, PersonsService personsService) {
        this.lecturesService = lecturesService;
        this.personsService = personsService;
    }

    @PostMapping("/edit-lecturer-form")
    public String handleEditLecturePost(
            @RequestParam String id,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false, defaultValue = "list") String source,
            Model model) {
        MajorLecturers lecture = lecturesService.getLecturerById(id);
        if (lecture == null) {
            if (source.equals("search")) {
                return "redirect:/staff-home/lecturers-list/search-lecturers?error=Lecturer+not+found&searchType=" + (searchType != null ? searchType : "") + "&keyword=" + (keyword != null ? keyword : "") + "&page=" + page + "&pageSize=" + (pageSize != null ? pageSize : 5);
            }
            return "redirect:/staff-home/lecturers-list?error=Lecturer+not+found&page=" + page + "&pageSize=" + (pageSize != null ? pageSize : 5);
        }
        model.addAttribute("lecture", lecture);
        model.addAttribute("genders", Arrays.asList(Gender.values()));
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
        model.addAttribute("source", source);
        return "EditLecturerForm";
    }

    @PutMapping("/edit-lecturer-form")
    public String updateLecture(
            @Valid @ModelAttribute("lecture") MajorLecturers lecture,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "source", required = false, defaultValue = "list") String source,
            RedirectAttributes redirectAttributes,
            ModelMap modelMap,
            HttpSession session) {
        Map<String, String> errors = lecturesService.lectureValidation(lecture, avatarFile);
        if (!errors.isEmpty()) {
            modelMap.addAttribute("errors", errors);
            modelMap.addAttribute("genders", Arrays.asList(Gender.values()));
            modelMap.addAttribute("searchType", searchType);
            modelMap.addAttribute("keyword", keyword);
            modelMap.addAttribute("page", page);
            modelMap.addAttribute("pageSize", pageSize != null ? pageSize : 5);
            modelMap.addAttribute("source", source);
            session.setAttribute("avatarLecture", "/staff-home/lecturers-list/avatar/" + lecture.getId());
            return "EditLecturerForm";
        }

        try {
            if (!personsService.existsPersonById(lecture.getId())) {
                redirectAttributes.addFlashAttribute("error", "Lecturer with ID " + lecture.getId() + " not found.");
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

            lecturesService.updateLecturer(lecture.getId(), lecture, avatarFile);
            redirectAttributes.addFlashAttribute("successMessage", "Lecturer updated successfully!");
            session.removeAttribute("avatarLecture");
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
        } catch (IOException | MessagingException e) {
            Map<String, String> errorsCatch = new HashMap<>();
            errorsCatch.put("general", "Error updating lecturer: " + e.getMessage());
            modelMap.addAttribute("errors", errorsCatch);
            modelMap.addAttribute("genders", Arrays.asList(Gender.values()));
            modelMap.addAttribute("searchType", searchType);
            modelMap.addAttribute("keyword", keyword);
            modelMap.addAttribute("page", page);
            modelMap.addAttribute("pageSize", pageSize != null ? pageSize : 5);
            modelMap.addAttribute("source", source);
            session.setAttribute("avatarLecture", "/staff-home/lecturers-list/avatar/" + lecture.getId());
            return "EditLecturerForm";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}