package com.example.demo.security.service;

import com.example.demo.Staff.model.Staffs;
import com.example.demo.admin.model.Admins;
import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.entity.Enums.AccountStatus;
import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.parentAccount.model.ParentAccounts;
import com.example.demo.person.model.Persons;
import com.example.demo.security.model.CustomUserPrincipal;
import com.example.demo.student.model.Students;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        long start = System.currentTimeMillis();
        try {
            Persons person = entityManager.createQuery(
                            "SELECT p FROM Persons p JOIN Authenticators a ON p.id = a.personId " +
                                    "WHERE (p.id = :u OR p.email = :u) AND a.accountStatus = :st",
                            Persons.class)
                    .setParameter("u", username)
                    .setParameter("st", AccountStatus.ACTIVE)
                    .getSingleResult();

            Authenticators auth = entityManager.createQuery(
                            "SELECT a FROM Authenticators a WHERE a.personId = :pid",
                            Authenticators.class)
                    .setParameter("pid", person.getId())
                    .getSingleResult();

            String role = determineRole(person);
            var authorities = List.of(new SimpleGrantedAuthority(role));
            String effectiveUsername = person.getEmail() != null ? person.getEmail() : person.getId();

            logger.info("Loaded user {} in {} ms", username, System.currentTimeMillis() - start);

            return new CustomUserPrincipal(
                    effectiveUsername,
                    auth.getPassword(),
                    authorities,
                    person
            );
        } catch (NoResultException e) {
            logger.warn("User not found: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }
    }

    private String determineRole(Persons person) {
        if (person instanceof Staffs) return "ROLE_STAFF";
        if (person instanceof MajorLecturers) return "ROLE_LECTURER";
        if (person instanceof Students) return "ROLE_STUDENT";
        if (person instanceof Admins) return "ROLE_ADMIN";
        if (person instanceof ParentAccounts) return "ROLE_PARENT";
        throw new IllegalStateException("Unknown person type: " + person.getClass());
    }
}