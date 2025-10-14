package com.example.demo.passwordResetToken.dao;

import com.example.demo.passwordResetToken.model.PasswordResetToken;
import java.util.Optional;

public interface PasswordResetTokenDAO {
    Optional<PasswordResetToken> findByAuthenticatorId(String authenticatorId);
    void save(PasswordResetToken resetToken);
    void delete(PasswordResetToken resetToken);
    Optional<PasswordResetToken> findByToken(String token);
}