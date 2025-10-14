package com.example.demo.passwordResetToken.controller;

import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.authenticator.service.AuthenticatorsService;
import com.example.demo.email_service.dao.EmailServiceForUserDAO;
import com.example.demo.email_service.dto.UserEmailContext;
import com.example.demo.email_service.service.EmailServiceForUserService;
import com.example.demo.passwordResetToken.model.PasswordResetToken;
import com.example.demo.passwordResetToken.service.PasswordResetTokenService;
import com.example.demo.person.model.Persons;
import com.example.demo.person.service.PersonsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.mail.MessagingException;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Controller
@RequestMapping("/auth")
public class PasswordResetController {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);

    private final PasswordResetTokenService passwordResetTokenService;
    private final AuthenticatorsService authenticatorsService;
    private final PersonsService personsService;
    private final PasswordEncoder passwordEncoder;
    private final EmailServiceForUserService emailServiceForUserService;

    @Value("${app.reset-token.expiry-hours:24}")
    private int tokenExpiryHours;

    public PasswordResetController(
            PasswordResetTokenService passwordResetTokenService,
            AuthenticatorsService authenticatorsService,
            PersonsService personsService,
            PasswordEncoder passwordEncoder,
            EmailServiceForUserService emailServiceForUserService) {
        this.passwordResetTokenService = passwordResetTokenService;
        this.authenticatorsService = authenticatorsService;
        this.personsService = personsService;
        this.passwordEncoder = passwordEncoder;
        this.emailServiceForUserService = emailServiceForUserService;
    }

    // Hiển thị form yêu cầu đặt lại mật khẩu (nhập email)
    @GetMapping("/reset-password")
    public String showResetPasswordForm(Model model) {
        model.addAttribute("email", "");
        return "ResetPassword";
    }

    // Xử lý yêu cầu đặt lại mật khẩu (gửi mã xác nhận)
    @PostMapping("/reset-password")
    public String requestPasswordReset(
            @RequestParam("email") @NotBlank String email,
            Model model,
            RedirectAttributes redirectAttributes) {
        Map<String, String> errors = new HashMap<>();

        // Kiểm tra email hợp lệ
        if (email == null || !isValidEmail(email)) {
            errors.put("email", "Invalid email format.");
        } else {
            // Kiểm tra xem email có tồn tại trong hệ thống không
            Persons person = personsService.getPersonByEmail(email);
            if (person == null) {
                errors.put("email", "No account found with this email address.");
            }
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("email", email);
            return "ResetPassword";
        }

        Persons person = personsService.getPersonByEmail(email);

        // Tạo mã xác nhận (6 chữ số)
        String verificationCode = generateVerificationCode();
        Authenticators authenticator = authenticatorsService.getAuthenticatorByPersonId(person.getId());

        // Xóa token cũ nếu có
        try {
            passwordResetTokenService.findByAuthenticatorId(authenticator.getPersonId())
                    .ifPresent(passwordResetTokenService::delete);
        } catch (Exception e) {
            logger.error("Error deleting old reset token for personId {}: {}", authenticator.getPersonId(), e.getMessage());
            errors.put("general", "An error occurred while processing your request. Please try again later.");
            model.addAttribute("errors", errors);
            model.addAttribute("email", email);
            return "ResetPassword";
        }

        // Lưu mã xác nhận vào PasswordResetToken (hashed)
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(passwordEncoder.encode(verificationCode));
        resetToken.setAuthenticator(authenticator);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(tokenExpiryHours));
        passwordResetTokenService.save(resetToken);

        // Gửi email với mã xác nhận
        UserEmailContext context = new UserEmailContext(
                person.getId(),
                person.getFullName(),
                person.getEmail(),
                person.getPhoneNumber(),
                person.getBirthDate(),
                person.getGender() != null ? person.getGender().toString() : null,
                person.getFullAddress(),
                null, // campusName
                null, // majorName
                null, // createdDate
                null  // creatorName
        );

        try {
            emailServiceForUserService.sendEmailForVerificationCode(
                    email,
                    "Password Reset Verification Code",
                    context,
                    verificationCode
            );
            redirectAttributes.addFlashAttribute("message", "A verification code has been sent to your email.");
            logger.info("Verification code sent to email: {}", email);
        } catch (MessagingException e) {
            logger.error("Failed to send verification email to {}: {}", email, e.getMessage());
            errors.put("general", "Failed to send verification email. Please try again later.");
            model.addAttribute("errors", errors);
            model.addAttribute("email", email);
            return "ResetPassword";
        }

        return "redirect:/auth/reset-password/verify?email=" + email;
    }

    // Hiển thị form nhập mã xác nhận
    @GetMapping("/reset-password/verify")
    public String showVerifyForm(
            @RequestParam(value = "email", required = false) String email,
            Model model) {
        model.addAttribute("email", email != null ? email : "");
        model.addAttribute("verificationCode", "");
        return "ResetPasswordVerify";
    }

    // Xử lý xác nhận mã xác nhận
    @PostMapping("/reset-password/verify")
    public String verifyCode(
            @RequestParam("email") @NotBlank String email,
            @RequestParam("verificationCode") @NotBlank String verificationCode,
            Model model,
            RedirectAttributes redirectAttributes) {
        Map<String, String> errors = new HashMap<>();

        // Kiểm tra email
        Persons person = personsService.getPersonByEmail(email);
        if (person == null) {
            errors.put("email", "No account found with this email address.");
        } else {
            Authenticators authenticator = authenticatorsService.getAuthenticatorByPersonId(person.getId());
            Optional<PasswordResetToken> resetTokenOpt = passwordResetTokenService.findByAuthenticatorId(authenticator.getPersonId());

            if (resetTokenOpt.isEmpty()) {
                errors.put("verificationCode", "Invalid verification code.");
            } else {
                PasswordResetToken tokenEntity = resetTokenOpt.get();
                if (tokenEntity.isExpired()) {
                    errors.put("verificationCode", "This verification code has expired.");
                    passwordResetTokenService.delete(tokenEntity);
                } else if (!passwordEncoder.matches(verificationCode, tokenEntity.getToken())) {
                    errors.put("verificationCode", "Invalid verification code.");
                }
            }
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("email", email);
            model.addAttribute("verificationCode", verificationCode);
            return "ResetPasswordVerify";
        }

        // Mã xác nhận đúng, tạo reset token mới (UUID) và cập nhật
        PasswordResetToken tokenEntity = passwordResetTokenService.findByAuthenticatorId(
                authenticatorsService.getAuthenticatorByPersonId(person.getId()).getPersonId()).get();
        String resetTokenStr = UUID.randomUUID().toString();
        tokenEntity.setToken(resetTokenStr); // Lưu không hashed
        tokenEntity.setExpiryDate(LocalDateTime.now().plusHours(1)); // Thời hạn ngắn cho bước đặt mật khẩu
        passwordResetTokenService.save(tokenEntity);

        redirectAttributes.addFlashAttribute("message", "Verification successful.");
        return "redirect:/auth/reset-password/new?token=" + resetTokenStr;
    }

    // Hiển thị form nhập mật khẩu mới
    @GetMapping("/reset-password/new")
    public String showNewPasswordForm(
            @RequestParam("token") String token,
            Model model,
            RedirectAttributes redirectAttributes) {
        Optional<PasswordResetToken> resetTokenOpt = passwordResetTokenService.findByToken(token);
        if (resetTokenOpt.isEmpty() || resetTokenOpt.get().isExpired()) {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired token.");
            return "redirect:/auth/reset-password";
        }

        PasswordResetToken tokenEntity = resetTokenOpt.get();
        Authenticators authenticator = tokenEntity.getAuthenticator();
        Persons person = personsService.getPersonById(authenticator.getPersonId());

        model.addAttribute("token", token);
        model.addAttribute("email", person.getEmail());
        model.addAttribute("newPassword", "");
        model.addAttribute("confirmPassword", "");
        return "ResetPasswordNew";
    }

    // Xử lý cập nhật mật khẩu mới
    @PostMapping("/reset-password/new")
    public String setNewPassword(
            @RequestParam("token") @NotBlank String token,
            @RequestParam("newPassword") @NotBlank String newPassword,
            @RequestParam("confirmPassword") @NotBlank String confirmPassword,
            Model model,
            RedirectAttributes redirectAttributes) {
        Map<String, String> errors = new HashMap<>();

        // Kiểm tra mật khẩu
        if (!newPassword.equals(confirmPassword)) {
            errors.put("confirmPassword", "Passwords do not match.");
        }
        if (newPassword.length() < 8) {
            errors.put("newPassword", "Password must be at least 8 characters long.");
        }

        // Kiểm tra token
        Optional<PasswordResetToken> resetTokenOpt = passwordResetTokenService.findByToken(token);
        if (resetTokenOpt.isEmpty()) {
            errors.put("general", "Invalid token.");
        } else {
            PasswordResetToken tokenEntity = resetTokenOpt.get();
            if (tokenEntity.isExpired()) {
                errors.put("general", "This token has expired.");
                passwordResetTokenService.delete(tokenEntity);
            }
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            // Lấy lại email từ token để hiển thị
            if (resetTokenOpt.isPresent()) {
                Authenticators authenticator = resetTokenOpt.get().getAuthenticator();
                Persons person = personsService.getPersonById(authenticator.getPersonId());
                model.addAttribute("email", person.getEmail());
            }
            model.addAttribute("token", token);
            model.addAttribute("newPassword", newPassword);
            model.addAttribute("confirmPassword", confirmPassword);
            return "ResetPasswordNew";
        }

        // Cập nhật mật khẩu
        PasswordResetToken tokenEntity = resetTokenOpt.get();
        Authenticators authenticator = tokenEntity.getAuthenticator();
        authenticator.setPassword(newPassword);
        try {
            authenticatorsService.createAuthenticator(authenticator);
            passwordResetTokenService.delete(tokenEntity);
            redirectAttributes.addFlashAttribute("message", "Your password has been reset successfully.");
        } catch (Exception e) {
            logger.error("Error updating password: {}", e.getMessage());
            errors.put("general", "An error occurred while resetting your password. Please try again later.");
            model.addAttribute("errors", errors);
            Authenticators auth = tokenEntity.getAuthenticator();
            Persons p = personsService.getPersonById(auth.getPersonId());
            model.addAttribute("email", p.getEmail());
            model.addAttribute("token", token);
            model.addAttribute("newPassword", newPassword);
            model.addAttribute("confirmPassword", confirmPassword);
            return "ResetPasswordNew";
        }

        return "redirect:/login";
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}