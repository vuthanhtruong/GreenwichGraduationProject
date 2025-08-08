package com.example.demo.security;

import com.example.demo.entity.Persons;
import com.example.demo.entity.Students;
import com.example.demo.entity.Staffs;
import com.example.demo.entity.MajorLecturers;
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
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        long startTime = System.currentTimeMillis();
        try {
            Persons person = entityManager.createQuery(
                            "SELECT p FROM Persons p WHERE p.id = :username OR p.email = :username", Persons.class)
                    .setParameter("username", username)
                    .setHint("jakarta.persistence.cache.storeMode", "REFRESH")
                    .getSingleResult();

            String role = switch (person) {
                case Students s -> "ROLE_STUDENT";
                case Staffs s -> "ROLE_STAFF";
                case MajorLecturers l -> "ROLE_LECTURER";
                default -> throw new IllegalStateException("Unknown person type: " + person.getClass());
            };

            logger.info("Loaded user {} in {} ms", username, System.currentTimeMillis() - startTime);
            return new CustomUserDetails(person, role);
        } catch (NoResultException e) {
            logger.warn("User not found with username: {}", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}