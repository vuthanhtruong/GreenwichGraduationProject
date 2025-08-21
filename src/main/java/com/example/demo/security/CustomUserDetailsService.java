package com.example.demo.security;

import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.student.model.Students;
import com.example.demo.majorstaff.model.Staffs;
import com.example.demo.person.model.Persons;
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

/** Form login -> tạo CustomUserPrincipal chứa Persons. */
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
            // Lấy Persons đang ACTIVE theo id hoặc email
            Persons person = entityManager.createQuery(
                            "SELECT p FROM Persons p JOIN Authenticators a ON p.id = a.personId " +
                                    "WHERE (p.id = :u OR p.email = :u) AND a.accountStatus = :st",
                            Persons.class)
                    .setParameter("u", username)
                    .setParameter("st", AccountStatus.ACTIVE)
                    .getSingleResult();

            // Lấy thông tin mật khẩu / trạng thái từ Authenticators
            Authenticators auth = entityManager.createQuery(
                            "SELECT a FROM Authenticators a WHERE a.personId = :pid",
                            Authenticators.class)
                    .setParameter("pid", person.getId())
                    .getSingleResult();

            // Xác định ROLE theo kiểu đối tượng
            String role;
            if (person instanceof Staffs) {
                role = "ROLE_STAFF";
            } else if (person instanceof MajorLecturers) {
                role = "ROLE_LECTURER";
            } else if (person instanceof Students) {
                role = "ROLE_STUDENT";
            } else {
                throw new IllegalStateException("Unknown person type: " + person.getClass());
            }

            var authorities = List.of(new SimpleGrantedAuthority(role));
            logger.info("Loaded user {} in {} ms", username, System.currentTimeMillis() - start);

            return new CustomUserPrincipal(
                    person.getEmail() != null ? person.getEmail() : person.getId(),
                    auth.getPassword(),
                    authorities,
                    person
            );
        } catch (NoResultException e) {
            logger.warn("User not found: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }
    }
}
