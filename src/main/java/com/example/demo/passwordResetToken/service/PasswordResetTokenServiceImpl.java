package com.example.demo.passwordResetToken.service;

import com.example.demo.passwordResetToken.dao.PasswordResetTokenDAO;
import com.example.demo.passwordResetToken.model.PasswordResetToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService{
    @Override
    public Optional<PasswordResetToken> findByAuthenticatorId(String authenticatorId) {
        return passwordResetTokenDAO.findByAuthenticatorId(authenticatorId);
    }

    @Override
    public void save(PasswordResetToken resetToken) {
        passwordResetTokenDAO.save(resetToken);
    }

    private final PasswordResetTokenDAO passwordResetTokenDAO;

    public PasswordResetTokenServiceImpl(PasswordResetTokenDAO passwordResetTokenDAO) {
        this.passwordResetTokenDAO = passwordResetTokenDAO;
    }


    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return passwordResetTokenDAO.findByToken(token);
    }

    @Override
    public void delete(PasswordResetToken token) {
        passwordResetTokenDAO.delete(token);
    }
}
