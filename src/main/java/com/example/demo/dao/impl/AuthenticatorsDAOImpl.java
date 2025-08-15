package com.example.demo.dao.impl;

import com.example.demo.dao.AuthenticatorsDAO;
import com.example.demo.entity.Authenticators;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class AuthenticatorsDAOImpl implements AuthenticatorsDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void createAuthenticator(Authenticators authenticator) {
        entityManager.persist(authenticator);
    }
}
