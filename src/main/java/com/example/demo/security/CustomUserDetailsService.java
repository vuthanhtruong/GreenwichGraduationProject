package com.example.demo.security;

import com.example.demo.entity.*;
import com.example.demo.entity.AbstractClasses.Persons;
import com.example.demo.entity.Enums.AccountStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
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
        long startTime = System.currentTimeMillis();
        try {
            // 1) Lấy Persons đang ACTIVE
            Persons person = entityManager.createQuery(
                            "SELECT p FROM Persons p JOIN Authenticators a ON p.id = a.personId " +
                                    "WHERE (p.id = :username OR p.email = :username) AND a.accountStatus = :status",
                            Persons.class)
                    .setParameter("username", username)
                    .setParameter("status", AccountStatus.ACTIVE)
                    .getSingleResult();

            // 2) Lấy password từ Authenticators
            Authenticators authenticators = entityManager.createQuery(
                            "SELECT a FROM Authenticators a WHERE a.personId = :personId",
                            Authenticators.class)
                    .setParameter("personId", person.getId())
                    .getSingleResult();

            // 3) Xác định role + entity tham chiếu đưa vào principal
            String role;
            Staffs staff = null;
            Majors major = null;
            Campuses campus = null;

            if (person instanceof Staffs s) {
                role = "ROLE_STAFF";
                staff = s;
                major = s.getMajorManagement();   // có thể null
                campus = s.getCampus();           // có thể null
            } else if (person instanceof MajorLecturers l) {
                role = "ROLE_LECTURER";
                // Nếu MajorLecturers có major/campus, lấy tương tự:
                // major = l.getMajorManagement() / campus = l.getCampus() (tuỳ model)
            } else if (person instanceof Students st) {
                role = "ROLE_STUDENT";
                // Nếu Students có major/campus, lấy tương tự:
                // major = st.getMajor(); campus = st.getCampus();
            } else {
                throw new IllegalStateException("Unknown person type: " + person.getClass());
            }

            var authorities = List.of(new SimpleGrantedAuthority(role));

            logger.info("Loaded user {} in {} ms", username, System.currentTimeMillis() - startTime);
            return new CustomUserPrincipal(
                    person.getEmail() != null ? person.getEmail() : person.getId(), // username hiển thị
                    authenticators.getPassword(),
                    authorities,
                    person,   // Persons entity
                    staff,    // Staffs entity (hoặc null)
                    major,    // Majors entity (hoặc null)
                    campus    // Campuses entity (hoặc null)
            );
        } catch (NoResultException e) {
            logger.warn("User not found with username: {}", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
