package com.example.demo.admin.dao;

import com.example.demo.admin.model.Admins;
import com.example.demo.campus.model.Campuses;
import com.example.demo.security.model.CustomUserPrincipal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class AdminDAOImpl implements AdminsDAO {
    @Override
    public Campuses getAdminCampus() {
        return getAdmin().getCampus();
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Admins getAdminByName(String name) {
        try {
            TypedQuery<Admins> query = entityManager.createQuery(
                    "SELECT a FROM Admins a WHERE LOWER(CONCAT(a.firstName, ' ', a.lastName)) LIKE LOWER(:name)",
                    Admins.class
            );
            query.setParameter("name", "%" + name + "%");
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving admin by name: " + e.getMessage(), e);
        }
    }

    @Override
    public Admins getAdminById(String id) {
        try {
            return entityManager.find(Admins.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving admin by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Admins getAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserPrincipal principal)) {
            throw new IllegalStateException("No authenticated principal");
        }
        return (Admins) principal.getPerson();
    }

    @Override
    public List<Admins> getAdmins() {
        try {
            TypedQuery<Admins> query = entityManager.createQuery(
                    "SELECT a FROM Admins a",
                    Admins.class
            );
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving list of admins: " + e.getMessage(), e);
        }
    }
}