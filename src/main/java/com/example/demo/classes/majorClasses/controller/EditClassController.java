package com.example.demo.classes.majorClasses.controller;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.majorClasses.service.MajorClassesService;
import com.example.demo.subject.majorSubject.model.MajorSubjects;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.subject.majorSubject.service.MajorSubjectsService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/staff-home/classes-list")
public class EditClassController {

    private final MajorClassesService classesService;
    private final StaffsService staffsService;
    private final MajorSubjectsService subjectsService;

    public EditClassController(MajorClassesService classesService,
                               StaffsService staffsService,
                               MajorSubjectsService subjectsService) {
        this.classesService = classesService;
        this.staffsService = staffsService;
        this.subjectsService = subjectsService;
    }

    // Hiển thị form chỉnh sửa
    @PostMapping("/edit-class-form")
    public String showEditClassForm(
            @RequestParam("classId") String classId,
            @RequestParam(value = "source", defaultValue = "list") String source,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            Model model) {

        MajorClasses editClass = classesService.getClassById(classId);
        if (editClass == null) {
            return redirectWithParams(source, searchType, keyword, page, pageSize != null ? pageSize : 5, "Class not found.");
        }

        model.addAttribute("class", editClass);
        model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
        model.addAttribute("source", source);
        model.addAttribute("searchType", searchType != null ? searchType : "name");
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize != null ? pageSize : 5);

        return "EditFormClass";
    }

    // Xử lý lưu chỉnh sửa
    @PutMapping("/edit-class")
    public String editClass(
            @Valid @ModelAttribute("class") MajorClasses classObj,
            BindingResult bindingResult,
            @RequestParam("subjectId") String subjectId,                    // <-- Lấy subjectId
            @RequestParam(value = "source", defaultValue = "list") String source,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            Model model,
            RedirectAttributes redirectAttributes) {

        // QUAN TRỌNG NHẤT: Set Subject vào object TRƯỚC khi validate
        if (subjectId != null && !subjectId.trim().isEmpty()) {
            MajorSubjects subject = subjectsService.getSubjectById(subjectId);
            classObj.setSubject(subject); // <-- Đặt ngay từ đầu, tránh lỗi validate
        }

        // Bây giờ mới validate
        Map<String, String> serviceErrors = classesService.validateClass(classObj, classObj.getClassId());

        // Nếu có lỗi (từ @Valid hoặc service)
        if (bindingResult.hasErrors() || !serviceErrors.isEmpty()) {
            model.addAttribute("serviceErrors", serviceErrors);
            model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
            model.addAttribute("source", source);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
            return "EditFormClass";
        }

        // Thành công → lưu
        try {
            classesService.editClass(classObj.getClassId(), classObj);
            redirectAttributes.addFlashAttribute("successMessage", "Class updated successfully!");
            return redirectWithParams(source, searchType, keyword, page, pageSize != null ? pageSize : 5, null);
        } catch (Exception e) {
            model.addAttribute("serviceErrors", Map.of("general", "Update failed: " + e.getMessage()));
            model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
            model.addAttribute("source", source);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
            return "EditFormClass";
        }
    }

    // Helper để redirect đúng trang + giữ lại tham số
    private String redirectWithParams(String source, String searchType, String keyword, int page, int pageSize, String error) {
        RedirectAttributes ra = new org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap();
        if (error != null) {
            ra.addFlashAttribute("errorMessage", error);
        }

        if ("search".equals(source) && searchType != null && keyword != null && !keyword.trim().isBlank()) {
            return "redirect:/staff-home/classes-list/search-classes?page=" + page +
                    "&pageSize=" + pageSize +
                    "&searchType=" + searchType +
                    "&keyword=" + keyword;
        }
        return "redirect:/staff-home/classes-list?pageClasses=" + page + "&pageSize=" + pageSize;
    }
}