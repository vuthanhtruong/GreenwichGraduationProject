package com.example.demo.authenticator.dao;

import com.example.demo.authenticator.model.Authenticators;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@Transactional
public class AuthenticatorsDAOImpl implements AuthenticatorsDAO {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticatorsDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void createAuthenticator(Authenticators authenticator) {
        try {
            if (authenticator.getPersonId() != null && getAuthenticatorByPersonId(authenticator.getPersonId()) != null) {
                entityManager.merge(authenticator); // Update existing authenticator
                logger.info("Updated authenticator for person ID {}", authenticator.getPersonId());
            } else {
                entityManager.persist(authenticator); // Create new authenticator
                logger.info("Created new authenticator for person ID {}", authenticator.getPersonId());
            }
        } catch (Exception e) {
            logger.error("Error saving authenticator for person ID {}: {}", authenticator.getPersonId(), e.getMessage(), e);
            throw new RuntimeException("Error saving authenticator: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteAuthenticatorByPersonId(String personId) {
        try {
            Authenticators authenticator = entityManager.createQuery(
                            "SELECT a FROM Authenticators a WHERE a.personId = :personId",
                            Authenticators.class)
                    .setParameter("personId", personId)
                    .setMaxResults(1)
                    .getSingleResult();
            entityManager.remove(authenticator);
            logger.info("Deleted authenticator for person ID {}", personId);
        } catch (NoResultException e) {
            logger.warn("No authenticator found for person ID {}", personId);
        } catch (Exception e) {
            logger.error("Error deleting authenticator for person ID {}: {}", personId, e.getMessage(), e);
            throw new RuntimeException("Error deleting authenticator: " + e.getMessage(), e);
        }
    }

    @Override
    public Authenticators getAuthenticatorByPersonId(String personId) {
        try {
            return entityManager.createQuery(
                            "SELECT a FROM Authenticators a WHERE a.personId = :personId",
                            Authenticators.class)
                    .setParameter("personId", personId)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            logger.info("No authenticator found for person ID {}", personId);
            return null;
        } catch (Exception e) {
            logger.error("Error retrieving authenticator for person ID {}: {}", personId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving authenticator: " + e.getMessage(), e);
        }
    }
    @Override
    public Map<String, String> validatePasswordChange(String personId, String currentPassword, String newPassword, String confirmNewPassword) {
        Map<String, String> errors = new HashMap<>();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Check if current password is provided and valid
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            errors.put("currentPassword", "Current password is required.");
        } else {
            Authenticators authenticator = getAuthenticatorByPersonId(personId);
            if (authenticator == null || !passwordEncoder.matches(currentPassword, authenticator.getPassword())) {
                errors.put("currentPassword", "Current password is incorrect.");
            }
        }

        // Validate new password
        if (newPassword == null || newPassword.trim().isEmpty()) {
            errors.put("newPassword", "New password is required.");
        } else if (newPassword.length() < 8) {
            errors.put("newPassword", "New password must be at least 8 characters long.");
        } else if (!newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")) {
            errors.put("newPassword", "New password must contain at least one uppercase letter, one lowercase letter, and one digit.");
        }

        // Validate confirm new password
        if (confirmNewPassword == null || confirmNewPassword.trim().isEmpty()) {
            errors.put("confirmNewPassword", "Confirm new password is required.");
        } else if (!newPassword.equals(confirmNewPassword)) {
            errors.put("confirmNewPassword", "Passwords do not match.");
        }

        return errors;
    }
    @Override
    public void changePassword(String personId, String newPassword) {
        try {
            Authenticators authenticator = getAuthenticatorByPersonId(personId);
            if (authenticator == null) {
                logger.error("No authenticator found for person ID {}", personId);
                throw new IllegalStateException("No authenticator found for person ID " + personId);
            }
            authenticator.setPassword(newPassword); // Automatically hashes the password
            createAuthenticator(authenticator); // Persist the update
            logger.info("Password updated successfully for person ID {}", personId);
        } catch (Exception e) {
            logger.error("Error updating password for person ID {}: {}", personId, e.getMessage(), e);
            throw new RuntimeException("Error updating password: " + e.getMessage(), e);
        }
    }
}