package com.example.demo.passwordResetToken.dao;

import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.authenticator.service.AuthenticatorsService;
import com.example.demo.passwordResetToken.model.PasswordResetToken;
import com.example.demo.person.model.Persons;
import com.example.demo.person.service.PersonsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Repository
public class PasswordResetTokenDAOImpl implements PasswordResetTokenDAO {

    @PersistenceContext
    private EntityManager entityManager;

    private final PersonsService personsService;
    private final AuthenticatorsService authenticatorsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordResetTokenDAOImpl(PersonsService personsService, AuthenticatorsService authenticatorsService, PasswordEncoder passwordEncoder) {
        this.personsService = personsService;
        this.authenticatorsService = authenticatorsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<PasswordResetToken> findByAuthenticatorId(String authenticatorId) {
        try {
            TypedQuery<PasswordResetToken> query = entityManager.createQuery(
                    "SELECT t FROM PasswordResetToken t WHERE t.authenticator.personId = :personId",
                    PasswordResetToken.class
            );
            query.setParameter("personId", authenticatorId);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void save(PasswordResetToken resetToken) {
        if (resetToken.getId() == null) {
            entityManager.persist(resetToken);
        } else {
            entityManager.merge(resetToken);
        }
    }

    @Override
    @Transactional
    public void delete(PasswordResetToken resetToken) {
        if (entityManager.contains(resetToken)) {
            entityManager.remove(resetToken);
        } else {
            entityManager.remove(entityManager.merge(resetToken));
        }
    }

    @Override
    @Transactional
    public void deleteByAuthenticatorId(String authenticatorId) {
        findByAuthenticatorId(authenticatorId).ifPresent(this::delete);
    }

    @Override
    public Map<String, String> validateResetPasswordRequest(String email) {
        Map<String, String> errors = new HashMap<>();

        if (email == null || !isValidEmail(email)) {
            errors.put("email", "Invalid email format.");
            return errors;
        }

        Persons person = personsService.getPersonByEmail(email);
        if (person == null) {
            errors.put("email", "No account found with this email address.");
        }

        return errors;
    }

    @Override
    public Map<String, String> validateVerificationCode(String email, String verificationCode) {
        Map<String, String> errors = new HashMap<>();

        Persons person = personsService.getPersonByEmail(email);
        if (person == null) {
            errors.put("email", "No account found with this email address.");
            return errors;
        }

        Authenticators authenticator = authenticatorsService.getAuthenticatorByPersonId(person.getId());
        if (authenticator == null) {
            errors.put("email", "No authentication data found for this account.");
            return errors;
        }

        Optional<PasswordResetToken> resetTokenOpt = findByAuthenticatorId(authenticator.getPersonId());
        if (resetTokenOpt.isEmpty()) {
            errors.put("verificationCode", "Invalid verification code.");
            return errors;
        }

        PasswordResetToken tokenEntity = resetTokenOpt.get();
        if (tokenEntity.isExpired()) {
            errors.put("verificationCode", "This verification code has expired.");
            delete(tokenEntity);
            return errors;
        }

        if (!passwordEncoder.matches(verificationCode, tokenEntity.getToken())) {
            errors.put("verificationCode", "Invalid verification code.");
        }

        return errors;
    }

    @Override
    public Map<String, String> validateSession(String authenticatorId) {
        Map<String, String> errors = new HashMap<>();

        Optional<PasswordResetToken> resetTokenOpt = findByAuthenticatorId(authenticatorId);
        if (resetTokenOpt.isEmpty() || resetTokenOpt.get().isExpired()) {
            errors.put("general", "Invalid or expired session.");
        }

        return errors;
    }

    @Override
    public Map<String, String> validateNewPassword(String authenticatorId, String newPassword, String confirmPassword) {
        Map<String, String> errors = new HashMap<>();

        if (!newPassword.equals(confirmPassword)) {
            errors.put("confirmPassword", "Passwords do not match.");
        }
        if (newPassword.length() < 8) {
            errors.put("newPassword", "Password must be at least 8 characters long.");
        }

        Optional<PasswordResetToken> resetTokenOpt = findByAuthenticatorId(authenticatorId);
        if (resetTokenOpt.isEmpty() || resetTokenOpt.get().isExpired()) {
            errors.put("general", "Invalid or expired session.");
        }

        return errors;
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
    @Override
    public String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}