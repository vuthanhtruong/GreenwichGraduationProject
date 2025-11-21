package com.example.demo.retakeSubjects.controller;

import com.example.demo.retakeSubjects.service.RetakeSubjectsService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/student-home")
@PreAuthorize("hasRole('STUDENT')")
public class RetakeSubjectsController {

    private final StudentsService studentsService;
    private final RetakeSubjectsService reStudyPaymentService;

    public RetakeSubjectsController(StudentsService studentsService,
                                    RetakeSubjectsService reStudyPaymentService) {
        this.studentsService = studentsService;
        this.reStudyPaymentService = reStudyPaymentService;
    }

    @PostMapping("/re-study-request/pay")
    public String processPayment(
            @RequestParam(name = "selectedSubjects", required = false) List<String> selectedSubjectIds,
            RedirectAttributes redirectAttributes) {

        // 0. Lấy student hiện tại
        Students student = studentsService.getStudent();
        if (student == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Student information not found. Please login again."
            );
            return "redirect:/student-home/re-study-request";
        }

        // 1. Không chọn môn nào
        if (selectedSubjectIds == null || selectedSubjectIds.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Please select at least one subject to re-study."
            );
            return "redirect:/student-home/re-study-request";
        }

        // 2. Gọi service validate số dư
        Map<String, Object> validation =
                reStudyPaymentService.validateBalance(student, selectedSubjectIds);

        if (validation == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Unable to validate your re-study request. Please try again later."
            );
            return "redirect:/student-home/re-study-request";
        }

        // —— Đọc các giá trị từ Map một cách an toàn, đúng theo DAO —— //
        double totalRequired = 0.0;
        double currentBalance = 0.0;
        boolean canPay = false;
        List<String> missingFees = Collections.emptyList();

        Object totalObj = validation.get("totalRequired");
        if (totalObj instanceof Number) {
            totalRequired = ((Number) totalObj).doubleValue();
        }

        Object balanceObj = validation.get("currentBalance");
        if (balanceObj instanceof Number) {
            currentBalance = ((Number) balanceObj).doubleValue();
        }

        Object canPayObj = validation.get("canPay");
        if (canPayObj instanceof Boolean) {
            canPay = (Boolean) canPayObj;
        }

        Object missingObj = validation.get("missingFees");
        if (missingObj instanceof List<?>) {
            try {
                List<String> casted = (List<String>) missingObj;
                missingFees = casted;
            } catch (ClassCastException ignored) {
                missingFees = Collections.emptyList();
            }
        }

        // 3. Nếu không thể thanh toán → báo lỗi, không gọi processReStudyPayment
        if (!canPay) {
            StringBuilder msg = new StringBuilder();

            // Các môn chưa cấu hình học phí học lại
            if (missingFees != null && !missingFees.isEmpty()) {
                msg.append("Some subjects do not have re-study fee configured: ")
                        .append(String.join(", ", missingFees))
                        .append(". ");
            }

            // Số dư không đủ
            if (currentBalance < totalRequired) {
                msg.append("Insufficient balance. Required: ")
                        .append(String.format("%,.0f VND", totalRequired))
                        .append(", current balance: ")
                        .append(String.format("%,.0f VND", currentBalance))
                        .append(".");
            }

            if (msg.length() == 0) {
                msg.append("Re-study payment cannot be processed due to invalid configuration.");
            }

            redirectAttributes.addFlashAttribute("errorMessage", msg.toString());
            return "redirect:/student-home/re-study-request";
        }

        // 4. Hợp lệ → trừ tiền + ghi log
        reStudyPaymentService.processReStudyPayment(student, selectedSubjectIds);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Re-study registration successful! Total paid: " +
                        String.format("%,.0f VND", totalRequired)
        );

        return "redirect:/student-home/re-study-request";
    }
}
