package com.example.demo.classes.specializedClasses.controller;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.classes.specializedClasses.service.SpecializedClassesService;
import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.subject.specializedSubject.service.SpecializedSubjectsService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/staff-home/specialized-classes-list")
public class EditSpecializedClassController {

    private final SpecializedClassesService classesService;
    private final StaffsService staffsService;
    private final SpecializedSubjectsService subjectsService;

    public EditSpecializedClassController(SpecializedClassesService classesService,
                                          StaffsService staffsService,
                                          SpecializedSubjectsService subjectsService) {
        this.classesService = classesService;
        this.staffsService = staffsService;
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

        SpecializedClasses editClass = classesService.getClassById(classId);
        if (editClass == null) {
            return redirectBack(source, searchType, keyword, page, pageSize != null ? pageSize : 5, "Class not found.");
        }

        model.addAttribute("class", editClass);
        model.addAttribute("specializedSubjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
        model.addAttribute("source", source);
        model.addAttribute("searchType", searchType != null ? searchType : "name");
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize != null ? pageSize : 5);

        return "EditFormSpecializedClass";
    }

    // ====================== LƯU CHỈNH SỬA ======================
    @PutMapping("/edit-class")
    public String editClass(
            @Valid @ModelAttribute("class") SpecializedClasses classObj,
            BindingResult bindingResult,
            @RequestParam("subjectId") String subjectId,                     // lấy từ form
            @RequestParam(defaultValue = "list") String source,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            Model model,
            RedirectAttributes redirectAttributes) {

        // QUAN TRỌNG NHẤT: Set subject NGAY TRƯỚC khi validate
        if (subjectId != null && !subjectId.trim().isEmpty()) {
            SpecializedSubject subject = subjectsService.getSubjectById(subjectId);
            classObj.setSpecializedSubject(subject);   // Đặt vào object trước validate
        }

        // Bây giờ mới validate (lúc này subject đã có rồi)
        Map<String, String> serviceErrors = classesService.validateClass(classObj, classObj.getClassId());

        // Nếu có lỗi → trả lại form
        if (bindingResult.hasErrors() || !serviceErrors.isEmpty()) {
            model.addAttribute("serviceErrors", serviceErrors);
            model.addAttribute("specializedSubjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
            model.addAttribute("source", source);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
            return "EditFormSpecializedClass";
        }

        // Thành công → lưu vào DB
        try {
            classesService.editClass(classObj.getClassId(), classObj);
            redirectAttributes.addFlashAttribute("successMessage", "Specialized class updated successfully!");
            return redirectBack(source, searchType, keyword, page, pageSize != null ? pageSize : 5, null);
        } catch (Exception e) {
            model.addAttribute("serviceErrors", Map.of("general", "Update failed: " + e.getMessage()));
            model.addAttribute("specializedSubjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
            model.addAttribute("source", source);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
            return "EditFormSpecializedClass";
        }
    }

    // ====================== HELPER REDIRECT ======================
    private String redirectBack(String source,
                                String searchType,
                                String keyword,
                                int page,
                                int pageSize,
                                String error) {

        StringBuilder url = new StringBuilder();

        if ("search".equals(source) && searchType != null && !searchType.isBlank()
                && keyword != null && !keyword.isBlank()) {
            url.append("redirect:/staff-home/specialized-classes-list/search-classes")
                    .append("?page=").append(page)
                    .append("&pageSize=").append(pageSize)
                    .append("&searchType=").append(searchType)
                    .append("&keyword=").append(keyword);
        } else {
            url.append("redirect:/staff-home/specialized-classes-list")
                    .append("?pageClasses=").append(page)
                    .append("&pageSize=").append(pageSize);
        }

        return url.toString();
    }
}