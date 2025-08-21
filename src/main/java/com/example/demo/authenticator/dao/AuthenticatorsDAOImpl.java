package com.example.demo.authenticator.dao;

import com.example.demo.authenticator.model.Authenticators;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class AuthenticatorsDAOImpl implements AuthenticatorsDAO {
    @Override
    public void deleteAuthenticatorByPersonId(String personId) {
        Authenticators authenticator = entityManager.createQuery(
                        "SELECT a FROM Authenticators a WHERE a.personId = :personId",
                        Authenticators.class)
                .setParameter("personId", personId)
                .setMaxResults(1)
                .getSingleResult();
        entityManager.remove(authenticator);
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void createAuthenticator(Authenticators authenticator) {
        entityManager.persist(authenticator);
    }
}
