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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

            // Mặc định nếu không tìm thấy trong DB
            String role = "ROLE_STUDENT";
            Persons person = null;
            Staffs staff = null;
            Majors major = null;
            Campuses campus = null;

            try {
                person = entityManager.createQuery(
                                "SELECT p FROM Persons p JOIN Authenticators a ON p.id = a.personId " +
                                        "WHERE p.email = :email AND a.accountStatus = :status",
                                Persons.class)
                        .setParameter("email", email)
                        .setParameter("status", AccountStatus.ACTIVE)
                        .setHint("jakarta.persistence.cache.storeMode", "REFRESH")
                        .getSingleResult();

                if (person instanceof Staffs s) {
                    role = "ROLE_STAFF";
                    staff = s;
                    major = s.getMajorManagement();     // có thể null
                    campus = s.getCampus();             // có thể null
                } else if (person instanceof MajorLecturers l) {
                    role = "ROLE_LECTURER";
                    // Nếu MajorLecturers có major/campus trong model của bạn, có thể gán:
                    // major = l.getMajorManagement();
                    // campus = l.getCampus();
                } else if (person instanceof Students st) {
                    role = "ROLE_STUDENT";
                    // Nếu Students có major/campus trong model, có thể gán:
                    // major = st.getMajor();
                    // campus = st.getCampus();
                } else {
                    throw new IllegalStateException("Unknown person type: " + person.getClass());
                }
            } catch (NoResultException e) {
                // Không tìm thấy người dùng -> giữ mặc định ROLE_STUDENT, person/staff/major/campus = null
                logger.warn("User not found with email: {}, defaulting to ROLE_STUDENT", email);
            }

            var authorities = List.of(new SimpleGrantedAuthority(role));

            // Principal thống nhất: giữ entity tham chiếu (có thể null)
            var principal = new CustomUserPrincipal(
                    email,        // username hiển thị cho OAuth2
                    "N/A",        // không có password từ OIDC
                    authorities,
                    person,       // Persons (có thể null)
                    staff,        // Staffs (có thể null)
                    major,        // Majors (có thể null)
                    campus        // Campuses (có thể null)
            );

            // Ghi principal vào SecurityContext để downstream dùng nhất quán
            var auth = new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);

            logger.info("Loaded OAuth2 user {} in {} ms", email, System.currentTimeMillis() - startTime);

            // Vẫn trả về DefaultOidcUser cho Spring Security; Authentication đã mang principal tuỳ biến
            return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo(), "email");
        } catch (Exception e) {
            logger.error("Failed to load OIDC user: {}", e.getMessage());
            throw new OAuth2AuthenticationException("Failed to load OIDC user: " + e.getMessage());
        }
    }
}
