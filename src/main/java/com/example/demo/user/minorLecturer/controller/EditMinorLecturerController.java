package com.example.demo.user.minorLecturer.controller;

import com.example.demo.entity.Enums.Gender;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.user.minorLecturer.service.MinorLecturersService;
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
@RequestMapping("/deputy-staff-home/minor-lecturers-list")
public class EditMinorLecturerController {

    private final MinorLecturersService minorLecturersService;
    private final PersonsService personsService;

    public EditMinorLecturerController(MinorLecturersService minorLecturersService, PersonsService personsService) {
        this.minorLecturersService = minorLecturersService;
        this.personsService = personsService;
    }

    @PostMapping("/edit-minor-lecturer-form")
    public String handleEditMinorLecturerPost(
            @RequestParam String id,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false, defaultValue = "list") String source,
            Model model) {
        MinorLecturers minorLecturer = minorLecturersService.getMinorLecturerById(id);
        if (minorLecturer == null) {
            if (source.equals("search")) {
                return "redirect:/deputy-staff-home/minor-lecturers-list/search-minor-lecturers?error=Minor+Lecturer+not+found&searchType=" +
                        (searchType != null ? searchType : "") + "&keyword=" + (keyword != null ? keyword : "") +
                        "&page=" + page + "&pageSize=" + (pageSize != null ? pageSize : 5);
            }
            return "redirect:/deputy-staff-home/minor-lecturers-list?error=Minor+Lecturer+not+found&page=" +
                    page + "&pageSize=" + (pageSize != null ? pageSize : 5);
        }
        model.addAttribute("minorLecturer", minorLecturer);
        model.addAttribute("genders", Arrays.asList(Gender.values()));
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
        model.addAttribute("source", source);
        return "EditMinorLecturerForm";
    }

    @PutMapping("/edit-minor-lecturer-form")
    public String updateMinorLecturer(
            @Valid @ModelAttribute("minorLecturer") MinorLecturers minorLecturer,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "source", required = false, defaultValue = "list") String source,
            RedirectAttributes redirectAttributes,
            ModelMap modelMap,
            HttpSession session) {
        Map<String, String> errors = minorLecturersService.minorLecturerValidation(minorLecturer, avatarFile);
        if (!errors.isEmpty()) {
            modelMap.addAttribute("errors", errors);
            modelMap.addAttribute("genders", Arrays.asList(Gender.values()));
            modelMap.addAttribute("searchType", searchType);
            modelMap.addAttribute("keyword", keyword);
            modelMap.addAttribute("page", page);
            modelMap.addAttribute("pageSize", pageSize != null ? pageSize : 5);
            modelMap.addAttribute("source", source);
            session.setAttribute("avatarMinorLecturer", "/deputy-staff-home/minor-lecturers-list/avatar/" + minorLecturer.getId());
            return "EditMinorLecturerForm";
        }

        try {
            if (!personsService.existsPersonById(minorLecturer.getId())) {
                redirectAttributes.addFlashAttribute("error", "Minor Lecturer with ID " + minorLecturer.getId() + " not found.");
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

            minorLecturersService.updateMinorLecturer(minorLecturer.getId(), minorLecturer, avatarFile);
            redirectAttributes.addFlashAttribute("successMessage", "Minor Lecturer updated successfully!");
            session.removeAttribute("avatarMinorLecturer");
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
        } catch (IOException | MessagingException e) {
            Map<String, String> errorsCatch = new HashMap<>();
            errorsCatch.put("general", "Error updating minor lecturer: " + e.getMessage());
            modelMap.addAttribute("errors", errorsCatch);
            modelMap.addAttribute("genders", Arrays.asList(Gender.values()));
            modelMap.addAttribute("searchType", searchType);
            modelMap.addAttribute("keyword", keyword);
            modelMap.addAttribute("page", page);
            modelMap.addAttribute("pageSize", pageSize != null ? pageSize : 5);
            modelMap.addAttribute("source", source);
            session.setAttribute("avatarMinorLecturer", "/deputy-staff-home/minor-lecturers-list/avatar/" + minorLecturer.getId());
            return "EditMinorLecturerForm";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}