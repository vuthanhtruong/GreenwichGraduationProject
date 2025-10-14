package com.example.demo.passwordResetToken.dao;

import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.passwordResetToken.model.PasswordResetToken;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class PasswordResetTokenDAOImpl implements PasswordResetTokenDAO {

    @PersistenceContext
    private EntityManager entityManager;

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
    public Optional<PasswordResetToken> findByToken(String token) {
        try {
            TypedQuery<PasswordResetToken> query = entityManager.createQuery(
                    "SELECT t FROM PasswordResetToken t WHERE t.token = :token",
                    PasswordResetToken.class
            );
            query.setParameter("token", token);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}