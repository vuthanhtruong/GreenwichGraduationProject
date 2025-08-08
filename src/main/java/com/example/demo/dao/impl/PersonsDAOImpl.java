package com.example.demo.dao.impl;

import com.example.demo.dao.PersonsDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional

public class PersonsDAOImpl implements PersonsDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean existsByPhoneNumberExcludingId(String phoneNumber, String id) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(s) > 0 FROM Persons s WHERE s.phoneNumber = :phoneNumber AND s.id != :id"
        );
        query.setParameter("phoneNumber", phoneNumber);
        query.setParameter("id", id);
        return (boolean) query.getSingleResult();
    }

    @Override
    public boolean existsByEmailExcludingId(String email, String id) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(s) > 0 FROM Persons s WHERE s.email = :email AND s.id != :id"
        );
        query.setParameter("email", email);
        query.setParameter("id", id);
        return (boolean) query.getSingleResult();
    }

    @Override
    public boolean existsPersonById(String id) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(p) FROM Persons p WHERE p.id = :id");
        query.setParameter("id", id);
        return (Long) query.getSingleResult() > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(p) FROM Persons p WHERE p.email = :email");
        query.setParameter("email", email);
        return (Long) query.getSingleResult() > 0;
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(p) FROM Persons p WHERE p.phoneNumber = :phoneNumber");
        query.setParameter("phoneNumber", phoneNumber);
        return (Long) query.getSingleResult() > 0;
    }

}
