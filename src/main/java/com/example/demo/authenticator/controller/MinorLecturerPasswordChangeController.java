// File: MinorLecturerPasswordChangeController.java
package com.example.demo.authenticator.controller;

import com.example.demo.authenticator.service.AuthenticatorsService;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.user.minorLecturer.service.MinorLecturersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/minor-lecturer-home/personal-page")
@PreAuthorize("hasRole('LECTURER')")
public class MinorLecturerPasswordChangeController {

    private static final Logger logger = LoggerFactory.getLogger(MinorLecturerPasswordChangeController.class);

    private final MinorLecturersService minorLecturersService;
    private final AuthenticatorsService authenticatorsService;

    public MinorLecturerPasswordChangeController(
            MinorLecturersService minorLecturersService,
            AuthenticatorsService authenticatorsService) {
        this.minorLecturersService = minorLecturersService;
        this.authenticatorsService = authenticatorsService;
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmNewPassword") String confirmNewPassword,
            RedirectAttributes redirectAttributes) {

        try {
            // Lấy Minor Lecturer đang đăng nhập
            MinorLecturers lecturer = minorLecturersService.getMinorLecturer();
            if (lecturer == null) {
                logger.warn("No authenticated minor lecturer found for password change");
                redirectAttributes.addFlashAttribute("passwordErrors", List.of("Không tìm thấy giảng viên."));
                return "redirect:/minor-lecturer-home/personal-page";
            }

            // Validate mật khẩu
            Map<String, String> errors = authenticatorsService.validatePasswordChange(
                    lecturer.getId(), currentPassword, newPassword, confirmNewPassword);

            if (!errors.isEmpty()) {
                List<String> errorList = new ArrayList<>();
                errors.forEach((field, msg) -> errorList.add(msg)); // Chỉ hiện message, không lộ field
                redirectAttributes.addFlashAttribute("passwordErrors", errorList);
                return "redirect:/minor-lecturer-home/personal-page";
            }

            // Cập nhật mật khẩu
            authenticatorsService.changePassword(lecturer.getId(), newPassword);
            logger.info("Password updated successfully for minor lecturer ID {}", lecturer.getId());

            redirectAttributes.addFlashAttribute("message", "Đổi mật khẩu thành công.");
            return "redirect:/minor-lecturer-home/personal-page";

        } catch (Exception e) {
            logger.error("Error updating password for minor lecturer: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("passwordErrors", List.of("Lỗi hệ thống. Vui lòng thử lại sau."));
            return "redirect:/minor-lecturer-home/personal-page";
        }
    }
}