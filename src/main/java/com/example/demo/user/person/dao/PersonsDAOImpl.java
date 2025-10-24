package com.example.demo.user.person.dao;

import com.example.demo.security.model.CustomOidcUserPrincipal;
import com.example.demo.security.model.DatabaseUserPrincipal;
import com.example.demo.user.admin.model.Admins;
import com.example.demo.user.person.model.Persons;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

@Repository
@Transactional

public class PersonsDAOImpl implements PersonsDAO {
    @Override
    public Persons getPerson() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Object principal = auth.getPrincipal();
        Persons person = switch (principal) {
            case DatabaseUserPrincipal dbPrincipal -> dbPrincipal.getPerson();
            case CustomOidcUserPrincipal oidcPrincipal -> oidcPrincipal.getPerson();
            default -> throw new IllegalStateException("Unknown principal type: " + principal.getClass());
        };

        return person;
    }

    @Override
    public Persons getPersonByEmail(String email) {
        try {
            return entityManager.createQuery(
                            "SELECT p FROM Persons p WHERE p.email = :email", Persons.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Persons getPersonById(String id) {
        Persons person = entityManager.find(Persons.class, id);
        return person;
    }

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
