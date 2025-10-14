package com.example.demo.passwordResetToken.dao;

import com.example.demo.passwordResetToken.model.PasswordResetToken;

import java.util.Map;
import java.util.Optional;

public interface PasswordResetTokenDAO {
    Optional<PasswordResetToken> findByAuthenticatorId(String authenticatorId);
    void save(PasswordResetToken resetToken);
    void delete(PasswordResetToken resetToken);
    void deleteByAuthenticatorId(String authenticatorId);
    Map<String, String> validateResetPasswordRequest(String email);
    Map<String, String> validateVerificationCode(String email, String verificationCode);
    Map<String, String> validateSession(String authenticatorId);
    Map<String, String> validateNewPassword(String authenticatorId, String newPassword, String confirmPassword);
    String generateVerificationCode();
}