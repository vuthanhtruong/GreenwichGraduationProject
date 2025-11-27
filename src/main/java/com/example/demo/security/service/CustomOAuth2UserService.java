package com.example.demo.security.service;

import com.example.demo.user.admin.model.Admins;
import com.example.demo.entity.Enums.AccountStatus;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.user.parentAccount.model.ParentAccounts;
import com.example.demo.user.person.model.Persons;
import com.example.demo.security.model.CustomOidcUserPrincipal;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomOAuth2UserService extends OidcUserService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);
        String email = oidcUser.getAttribute("email");

        if (email == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error("no_email"), "No email in OAuth2 profile");
        }
        Persons person;
        try {
            person = entityManager.createQuery(
                            "SELECT p FROM Persons p JOIN Authenticators a ON p.id = a.personId " +
                                    "WHERE p.email = :email AND a.accountStatus = :st",
                            Persons.class)
                    .setParameter("email", email)
                    .setParameter("st", AccountStatus.ACTIVE)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new OAuth2AuthenticationException(new OAuth2Error("user_not_found"), "User not found in DB");
        }

        String role = determineRole(person);
        var authorities = List.of(new SimpleGrantedAuthority(role));

        return new CustomOidcUserPrincipal(oidcUser, person, authorities);
    }

    private String determineRole(Persons person) {
        if (person instanceof Staffs) return "ROLE_STAFF";
        if (person instanceof MajorLecturers) return "ROLE_LECTURER";
        if (person instanceof Students) return "ROLE_STUDENT";
        if (person instanceof Admins) return "ROLE_ADMIN";
        if (person instanceof DeputyStaffs) return "ROLE_DEPUTY";
        if (person instanceof MinorLecturers) return "ROLE_MINOR";
        if (person instanceof ParentAccounts) return "ROLE_PARENT";
        return "ROLE_USER";
    }
}
