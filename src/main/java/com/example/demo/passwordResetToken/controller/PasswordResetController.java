package com.example.demo.passwordResetToken.controller;

import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.authenticator.service.AuthenticatorsService;
import com.example.demo.email_service.dto.UserEmailContext;
import com.example.demo.email_service.service.EmailServiceForUserService;
import com.example.demo.passwordResetToken.model.PasswordResetToken;
import com.example.demo.passwordResetToken.service.PasswordResetTokenService;
import com.example.demo.user.person.model.Persons;
import com.example.demo.user.person.service.PersonsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequestMapping("/auth")
public class PasswordResetController {

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

    // Hiển thị form yêu cầu đặt lại mật khẩu
    @GetMapping("/reset-password")
    public String showResetPasswordForm(Model model, HttpSession session) {
        session.removeAttribute("resetEmail");
        session.removeAttribute("resetAuthenticatorId");
        model.addAttribute("email", "");
        return "ResetPassword";
    }

    // Xử lý yêu cầu đặt lại mật khẩu
    @PostMapping("/reset-password")
    public String requestPasswordReset(
            @RequestParam("email") @NotBlank String email,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        Map<String, String> errors = passwordResetTokenService.validateResetPasswordRequest(email);

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("email", email);
            return "ResetPassword";
        }

        try {
            Persons person = personsService.getPersonByEmail(email);
            Authenticators authenticator = authenticatorsService.getAuthenticatorByPersonId(person.getId());
            String verificationCode = passwordResetTokenService.generateVerificationCode();

            passwordResetTokenService.deleteByAuthenticatorId(authenticator.getPersonId());

            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(passwordEncoder.encode(verificationCode));
            resetToken.setAuthenticator(authenticator);
            resetToken.setExpiryDate(LocalDateTime.now().plusHours(tokenExpiryHours));
            passwordResetTokenService.save(resetToken);

            UserEmailContext context = new UserEmailContext(
                    person.getId(),
                    person.getFullName(),
                    person.getEmail(),
                    person.getPhoneNumber(),
                    person.getBirthDate(),
                    person.getGender() != null ? person.getGender().toString() : null,
                    person.getFullAddress(),
                    null, null, null, null
            );
            emailServiceForUserService.sendEmailForVerificationCode(
                    email, "Password Reset Verification Code", context, verificationCode);

            session.setAttribute("resetEmail", email);
            redirectAttributes.addFlashAttribute("message", "A verification code has been sent to your email.");
            return "redirect:/auth/reset-password/verify";
        } catch (MessagingException e) {
            model.addAttribute("errors", Map.of("email", "Failed to send verification email. Please try again later."));
            model.addAttribute("email", email);
            return "ResetPassword";
        } catch (Exception e) {
            model.addAttribute("errors", Map.of("email", "An error occurred while processing your request. Please try again later."));
            model.addAttribute("email", email);
            return "ResetPassword";
        }
    }

    // Hiển thị form nhập mã xác nhận
    @GetMapping("/reset-password/verify")
    public String showVerifyForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String email = (String) session.getAttribute("resetEmail");
        if (email == null) {
            redirectAttributes.addFlashAttribute("error", "Session expired or invalid. Please start over.");
            return "redirect:/auth/reset-password";
        }
        model.addAttribute("email", email);
        model.addAttribute("verificationCode", "");
        return "ResetPasswordVerify";
    }

    // Xử lý xác nhận mã xác nhận
    @PostMapping("/reset-password/verify")
    public String verifyCode(
            @RequestParam("verificationCode") @NotBlank String verificationCode,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        String email = (String) session.getAttribute("resetEmail");
        if (email == null) {
            redirectAttributes.addFlashAttribute("error", "Session expired or invalid. Please start over.");
            return "redirect:/auth/reset-password";
        }

        Map<String, String> errors = passwordResetTokenService.validateVerificationCode(email, verificationCode);

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("email", email);
            model.addAttribute("verificationCode", verificationCode);
            return "ResetPasswordVerify";
        }

        try {
            Persons person = personsService.getPersonByEmail(email);
            Authenticators authenticator = authenticatorsService.getAuthenticatorByPersonId(person.getId());
            session.setAttribute("resetAuthenticatorId", authenticator.getPersonId());
            redirectAttributes.addFlashAttribute("message", "Verification successful.");
            return "redirect:/auth/reset-password/new";
        } catch (Exception e) {
            model.addAttribute("errors", Map.of("verificationCode", "An error occurred while verifying the code. Please try again."));
            model.addAttribute("email", email);
            model.addAttribute("verificationCode", verificationCode);
            return "ResetPasswordVerify";
        }
    }

    // Hiển thị form nhập mật khẩu mới
    @GetMapping("/reset-password/new")
    public String showNewPasswordForm(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        String authenticatorId = (String) session.getAttribute("resetAuthenticatorId");
        if (authenticatorId == null) {
            redirectAttributes.addFlashAttribute("error", "Session expired or invalid. Please start over.");
            return "redirect:/auth/reset-password";
        }

        Map<String, String> errors = passwordResetTokenService.validateSession(authenticatorId);
        if (!errors.isEmpty()) {
            session.removeAttribute("resetAuthenticatorId");
            session.removeAttribute("resetEmail");
            redirectAttributes.addFlashAttribute("error", errors.getOrDefault("general", "Invalid or expired session. Please start over."));
            return "redirect:/auth/reset-password";
        }

        Persons person = personsService.getPersonById(authenticatorId);
        model.addAttribute("email", person.getEmail());
        model.addAttribute("newPassword", "");
        model.addAttribute("confirmPassword", "");
        return "ResetPasswordNew";
    }

    // Xử lý cập nhật mật khẩu mới
    @PostMapping("/reset-password/new")
    public String setNewPassword(
            @RequestParam("newPassword") @NotBlank String newPassword,
            @RequestParam("confirmPassword") @NotBlank String confirmPassword,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        String authenticatorId = (String) session.getAttribute("resetAuthenticatorId");
        if (authenticatorId == null) {
            redirectAttributes.addFlashAttribute("error", "Session expired or invalid. Please start over.");
            return "redirect:/auth/reset-password";
        }

        Map<String, String> errors = passwordResetTokenService.validateNewPassword(authenticatorId, newPassword, confirmPassword);

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            Persons person = personsService.getPersonById(authenticatorId);
            model.addAttribute("email", person.getEmail());
            model.addAttribute("newPassword", newPassword);
            model.addAttribute("confirmPassword", confirmPassword);
            return "ResetPasswordNew";
        }

        try {
            PasswordResetToken tokenEntity = passwordResetTokenService.findByAuthenticatorId(authenticatorId)
                    .orElseThrow(() -> new RuntimeException("Token not found"));
            Authenticators authenticator = tokenEntity.getAuthenticator();
            authenticator.setPassword(newPassword);
            authenticatorsService.createAuthenticator(authenticator);
            passwordResetTokenService.delete(tokenEntity);

            session.removeAttribute("resetAuthenticatorId");
            session.removeAttribute("resetEmail");

            redirectAttributes.addFlashAttribute("message", "Your password has been reset successfully.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("errors", Map.of("general", "An error occurred while resetting your password. Please try again later."));
            Persons person = personsService.getPersonById(authenticatorId);
            model.addAttribute("email", person.getEmail());
            model.addAttribute("newPassword", newPassword);
            model.addAttribute("confirmPassword", confirmPassword);
            return "ResetPasswordNew";
        }
    }
}