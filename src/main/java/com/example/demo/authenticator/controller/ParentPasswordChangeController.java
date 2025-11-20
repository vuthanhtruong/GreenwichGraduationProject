// src/main/java/com/example/demo/authenticator/controller/ParentPasswordChangeController.java
package com.example.demo.authenticator.controller;

import com.example.demo.user.parentAccount.model.ParentAccounts;
import com.example.demo.user.parentAccount.service.ParentAccountsService;
import com.example.demo.authenticator.service.AuthenticatorsService;
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
@RequestMapping("/parent-home/personal-page")
@PreAuthorize("hasRole('PARENT')")
public class ParentPasswordChangeController {

    private static final Logger logger = LoggerFactory.getLogger(ParentPasswordChangeController.class);

    private final ParentAccountsService parentAccountsService;
    private final AuthenticatorsService authenticatorsService;

    public ParentPasswordChangeController(ParentAccountsService parentAccountsService,
                                          AuthenticatorsService authenticatorsService) {
        this.parentAccountsService = parentAccountsService;
        this.authenticatorsService = authenticatorsService;
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmNewPassword") String confirmNewPassword,
            RedirectAttributes redirectAttributes) {

        try {
            // Lấy phụ huynh đang đăng nhập
            ParentAccounts parent = parentAccountsService.getParent();
            Map<String, String> errors = authenticatorsService.validatePasswordChange(
                    parent.getId(), currentPassword, newPassword, confirmNewPassword);

            if (!errors.isEmpty()) {
                List<String> errorList = new ArrayList<>();
                errors.forEach((field, msg) -> errorList.add(msg)); // chỉ lấy message cho đẹp
                redirectAttributes.addFlashAttribute("passwordErrors", errorList);
                return "redirect:/parent-home/personal-page";
            }

            // Đổi mật khẩu thành công
            authenticatorsService.changePassword(parent.getId(), newPassword);
            logger.info("Password changed successfully for parent ID: {}", parent.getId());

            redirectAttributes.addFlashAttribute("passwordSuccess", "Đổi mật khẩu thành công!");
            return "redirect:/parent-home/personal-page";

        } catch (Exception e) {
            logger.error("Error changing password for parent: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("passwordErrors", List.of("Đã xảy ra lỗi khi đổi mật khẩu. Vui lòng thử lại."));
            return "redirect:/parent-home/personal-page";
        }
    }
}