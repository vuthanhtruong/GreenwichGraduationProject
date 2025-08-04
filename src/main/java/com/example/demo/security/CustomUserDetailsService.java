package com.example.demo.security;

import com.example.demo.entity.Persons;
import com.example.demo.entity.Students;
import com.example.demo.entity.Staffs;
import com.example.demo.entity.Lecturers;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    @Cacheable(value = "users", key = "#id")
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        long startTime = System.currentTimeMillis();
        try {
            Persons person = entityManager.createQuery(
                            "SELECT p FROM Persons p WHERE p.id = :id or p.email=:id", Persons.class)
                    .setParameter("id", id)
                    .getSingleResult();

            String role = switch (person) {
                case Students s -> "ROLE_STUDENT";
                case Staffs s -> "ROLE_STAFF";
                case Lecturers l -> "ROLE_LECTURER";
                default -> throw new IllegalStateException("Unknown person type: " + person.getClass());
            };

            logger.info("Load user {} took {} ms", id, System.currentTimeMillis() - startTime);
            return new CustomUserDetails(person, role);
        } catch (NoResultException e) {
            logger.warn("User not found with id: {}", id);
            throw new UsernameNotFoundException("User not found with id: " + id);
        }
    }
}