package com.example.demo.classes.minorClasses.controller;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.minorClasses.service.MinorClassesService;
import com.example.demo.entity.Enums.Sessions;
import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import com.example.demo.subject.minorSubject.service.MinorSubjectsService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/deputy-staff-home/minor-classes-list")
@PreAuthorize("hasRole('DEPUTY_STAFF')")
public class EditMinorClassController {

    private final MinorClassesService classesService;
    private final DeputyStaffsService deputyStaffsService;
    private final MinorSubjectsService subjectsService;

    public EditMinorClassController(MinorClassesService classesService,
                                    DeputyStaffsService deputyStaffsService,
                                    MinorSubjectsService subjectsService) {
        this.classesService = classesService;
        this.deputyStaffsService = deputyStaffsService;
        this.subjectsService = subjectsService;
    }

    // ====================== HIỂN THỊ FORM ======================
    @PostMapping("/edit-class-form")
    public String showEditClassForm(
            @RequestParam String classId,
            @RequestParam(defaultValue = "list") String source,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            Model model) {

        MinorClasses editClass = classesService.getClassById(classId);
        if (editClass == null) {
            return redirectBack(source, searchType, keyword, page, pageSize != null ? pageSize : 5, "Class not found.");
        }

        model.addAttribute("class", editClass);
        model.addAttribute("subjects", subjectsService.getAllSubjects());
        model.addAttribute("source", source);
        model.addAttribute("searchType", searchType != null ? searchType : "name");
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize != null ? pageSize : 5);

        return "EditMinorClassForm";
    }

    // ====================== LƯU CHỈNH SỬA ======================
    @PutMapping("/edit-class")
    public String editClass(
            @Valid @ModelAttribute("class") MinorClasses classObj,
            BindingResult bindingResult,
            @RequestParam("subjectId") String subjectId,                      // <-- lấy từ form
            @RequestParam(defaultValue = "list") String source,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            Model model,
            RedirectAttributes redirectAttributes) {

        // ĐIỂM QUAN TRỌNG NHẤT: Set MinorSubject NGAY TRƯỚC khi validate
        if (subjectId != null && !subjectId.trim().isEmpty()) {
            MinorSubjects subject = subjectsService.getSubjectById(subjectId);
            classObj.setMinorSubject(subject);               // <-- Đặt vào object trước
        }

        // Bây giờ mới validate (lúc này subject đã có)
        Map<String, String> serviceErrors = classesService.validateClass(classObj, classObj.getClassId());

        // Nếu có lỗi → trả lại form
        if (bindingResult.hasErrors() || !serviceErrors.isEmpty()) {
            model.addAttribute("serviceErrors", serviceErrors);
            model.addAttribute("subjects", subjectsService.getAllSubjects());
            model.addAttribute("source", source);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
            return "EditMinorClassForm";
        }

        // Thành công → lưu vào DB
        try {
            classesService.editClass(classObj.getClassId(), classObj);
            redirectAttributes.addFlashAttribute("successMessage", "Minor class updated successfully!");
            return redirectBack(source, searchType, keyword, page, pageSize != null ? pageSize : 5, null);
        } catch (Exception e) {
            model.addAttribute("serviceErrors", Map.of("general", "Update failed: " + e.getMessage()));
            model.addAttribute("subjects", subjectsService.getAllSubjects());
            model.addAttribute("source", source);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
            return "EditMinorClassForm";
        }
    }

    // ====================== HELPER REDIRECT ======================
    private String redirectBack(String source,
                                String searchType,
                                String keyword,
                                int page,
                                int pageSize,
                                String error) {

        StringBuilder redirect = new StringBuilder();

        if ("search".equals(source) && searchType != null && !searchType.isBlank()
                && keyword != null && !keyword.isBlank()) {
            redirect.append("redirect:/deputy-staff-home/minor-classes-list/search-classes")
                    .append("?page=").append(page)
                    .append("&pageSize=").append(pageSize)
                    .append("&searchType=").append(searchType)
                    .append("&keyword=").append(keyword);
        } else {
            redirect.append("redirect:/deputy-staff-home/minor-classes-list")
                    .append("?pageClasses=").append(page)
                    .append("&pageSize=").append(pageSize);
        }

        return redirect.toString();
    }
}