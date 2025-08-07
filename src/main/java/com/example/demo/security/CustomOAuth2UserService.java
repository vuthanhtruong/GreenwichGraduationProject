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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class CustomOAuth2UserService extends OidcUserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "oauth2Users", key = "#userRequest.clientRegistration.registrationId + ':' + #userRequest.accessToken.tokenValue")
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        long startTime = System.currentTimeMillis();
        try {
            OidcUser oidcUser = super.loadUser(userRequest);
            String email = oidcUser.getAttribute("email");

            if (email == null || email.isEmpty()) {
                throw new OAuth2AuthenticationException("Email not found from OIDC provider");
            }

            try {
                Persons person = entityManager.createQuery(
                                "SELECT p FROM Persons p WHERE p.email = :email", Persons.class)
                        .setParameter("email", email)
                        .setHint("jakarta.persistence.cache.storeMode", "REFRESH") // Sửa từ javax sang jakarta
                        .getSingleResult();

                String role = switch (person) {
                    case Students s -> "ROLE_STUDENT";
                    case Staffs s -> "ROLE_STAFF";
                    case Lecturers l -> "ROLE_LECTURER";
                    default -> throw new IllegalStateException("Unknown person type: " + person.getClass());
                };

                logger.info("Loaded OAuth2 user {} in {} ms", email, System.currentTimeMillis() - startTime);
                return new DefaultOidcUser(
                        Collections.singletonList(new SimpleGrantedAuthority(role)),
                        oidcUser.getIdToken(),
                        oidcUser.getUserInfo(),
                        "email"
                );
            } catch (NoResultException e) {
                logger.warn("User not found with email: {}, defaulting to ROLE_STUDENT", email);
                return new DefaultOidcUser(
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT")),
                        oidcUser.getIdToken(),
                        oidcUser.getUserInfo(),
                        "email"
                );
            }
        } catch (Exception e) {
            logger.error("Failed to load OIDC user: {}", e.getMessage());
            throw new OAuth2AuthenticationException("Failed to load OIDC user: " + e.getMessage());
        }
    }
}