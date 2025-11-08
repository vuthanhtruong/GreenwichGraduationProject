package com.example.demo.RetakeSubjects.controller;

import com.example.demo.RetakeSubjects.service.RetakeSubjectsService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/student-home")
@PreAuthorize("hasRole('STUDENT')")
public class RetakeSubjectsController {
    private final StudentsService studentsService;;
    private final RetakeSubjectsService reStudyPaymentService;

    public RetakeSubjectsController(StudentsService studentsService, RetakeSubjectsService reStudyPaymentService) {
        this.studentsService = studentsService;
        this.reStudyPaymentService = reStudyPaymentService;
    }

    @PostMapping("/re-study-request/pay")
    public String processPayment(
            @RequestParam(name = "selectedSubjects", required = false) List<String> selectedSubjectIds,
            RedirectAttributes redirectAttributes) {

        Students student = studentsService.getStudent();

        // 1. Chỉ kiểm tra số dư
        Map<String, Object> validation = reStudyPaymentService.validateBalance(student, selectedSubjectIds);

        if (!(Boolean) validation.get("valid")) {
            redirectAttributes.addFlashAttribute("errorMessage", validation.get("message"));
            return "redirect:/student-home/re-study-request";
        }

        // 2. Thanh toán
        reStudyPaymentService.processReStudyPayment(student, selectedSubjectIds);

        double total = (double) validation.get("totalCost");
        redirectAttributes.addFlashAttribute("successMessage",
                "Re-study registration successful! Total paid: $" + String.format("%.2f", total));

        return "redirect:/student-home/re-study-request";
    }
}
