package com.example.demo.passwordResetToken.service;

import com.example.demo.passwordResetToken.dao.PasswordResetTokenDAO;
import com.example.demo.passwordResetToken.model.PasswordResetToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {
    @Override
    public String generateVerificationCode() {
        return passwordResetTokenDAO.generateVerificationCode();
    }

    private final PasswordResetTokenDAO passwordResetTokenDAO;

    @Autowired
    public PasswordResetTokenServiceImpl(PasswordResetTokenDAO passwordResetTokenDAO) {
        this.passwordResetTokenDAO = passwordResetTokenDAO;
    }

    @Override
    public Optional<PasswordResetToken> findByAuthenticatorId(String authenticatorId) {
        return passwordResetTokenDAO.findByAuthenticatorId(authenticatorId);
    }

    @Override
    public void save(PasswordResetToken resetToken) {
        passwordResetTokenDAO.save(resetToken);
    }

    @Override
    public void delete(PasswordResetToken resetToken) {
        passwordResetTokenDAO.delete(resetToken);
    }

    @Override
    public void deleteByAuthenticatorId(String authenticatorId) {
        passwordResetTokenDAO.deleteByAuthenticatorId(authenticatorId);
    }

    @Override
    public Map<String, String> validateResetPasswordRequest(String email) {
        return passwordResetTokenDAO.validateResetPasswordRequest(email);
    }

    @Override
    public Map<String, String> validateVerificationCode(String email, String verificationCode) {
        return passwordResetTokenDAO.validateVerificationCode(email, verificationCode);
    }

    @Override
    public Map<String, String> validateSession(String authenticatorId) {
        return passwordResetTokenDAO.validateSession(authenticatorId);
    }

    @Override
    public Map<String, String> validateNewPassword(String authenticatorId, String newPassword, String confirmPassword) {
        return passwordResetTokenDAO.validateNewPassword(authenticatorId, newPassword, confirmPassword);
    }

}