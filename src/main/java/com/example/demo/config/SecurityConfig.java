package com.example.demo.config;

import com.example.demo.admin.model.Admins;
import com.example.demo.entity.Enums.AccountStatus;
import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.person.model.Persons;
import com.example.demo.security.model.CustomUserPrincipal;
import com.example.demo.security.service.CustomOAuth2UserService;
import com.example.demo.security.service.CustomUserDetailsService;
import com.example.demo.staff.model.Staffs;
import com.example.demo.student.model.Students;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final CustomUserDetailsService userDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final DataSource dataSource;
    private final Environment environment;

    @PersistenceContext
    private EntityManager entityManager;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          CustomOAuth2UserService customOAuth2UserService,
                          DataSource dataSource,
                          Environment environment) {
        this.userDetailsService = userDetailsService;
        this.customOAuth2UserService = customOAuth2UserService;
        this.dataSource = dataSource;
        this.environment = environment;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/student-home/**", "/api/student-home/**").hasRole("STUDENT")
                        .requestMatchers("/staff-home/**", "/api/staff-home/**").hasRole("STAFF")
                        .requestMatchers("/lecturer-home/**", "/api/lecturer-home/**").hasRole("LECTURER")
                        .requestMatchers("/admin-home/**", "/api/admin-home/**").hasRole("ADMIN")
                        .requestMatchers("/login", "/resources/**", "/css/**", "/js/**", "/*.css", "/oauth2/**", "/home", "/auth/reset-password").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(formSuccessHandler())
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(customOAuth2UserService))
                        .successHandler(oauth2SuccessHandler())
                        .failureHandler((req, res, ex) -> {
                            logger.error("OAuth2 authentication failed", ex);
                            res.sendRedirect("/login?error=oauth2_failed");
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .addLogoutHandler((request, response, authentication) -> {
                            try {
                                PersistentTokenRepository tokenRepository = persistentTokenRepository();
                                if (authentication != null) {
                                    tokenRepository.removeUserTokens(authentication.getName());
                                }
                            } catch (Exception e) {
                                logger.error("Error during remember-me token cleanup", e);
                            }
                        })
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .tokenRepository(persistentTokenRepository())
                        .tokenValiditySeconds(7 * 24 * 60 * 60)
                        .key(environment.getProperty("security.remember-me.key", "your-secret-key-1234567890"))
                        .userDetailsService(userDetailsService)
                        .useSecureCookie(true)
                );
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        // Ngăn không cho BCrypt xử lý OAuth2 user (password chứa "OAUTH2")
        provider.setPreAuthenticationChecks(user -> {
            if (user.getPassword() != null && user.getPassword().contains("OAUTH2")) {
                throw new IllegalStateException("OAuth2 user must login via OAuth2");
            }
        });

        return provider;
    }

    @Bean
    public AuthenticationSuccessHandler formSuccessHandler() {
        return (request, response, authentication) -> {
            String redirectUrl = getRedirectUrlByRole(authentication.getAuthorities());
            if (redirectUrl != null) {
                response.sendRedirect(redirectUrl);
            } else {
                response.sendRedirect("/login?error=no_role");
            }
        };
    }

    @Bean
    public AuthenticationSuccessHandler oauth2SuccessHandler() {
        return (request, response, authentication) -> {
            try {
                String email = extractEmailFromOAuth2Principal(authentication.getPrincipal());
                if (email == null) {
                    logger.warn("OAuth2 authentication succeeded but no email found");
                    response.sendRedirect("/login?error=no_email");
                    return;
                }

                Persons person = findActivePersonByEmail(email);
                if (person == null) {
                    logger.warn("OAuth2 user {} not found in database", email);
                    response.sendRedirect("/login?error=user_not_found");
                    return;
                }

                String role = determineRole(person);
                var authorities = List.of(new SimpleGrantedAuthority(role));
                var principal = new CustomUserPrincipal(
                        email,
                        "{noop}OAUTH2_USER", // Không dùng BCrypt cho OAuth2
                        authorities,
                        person
                );

                var newAuth = new UsernamePasswordAuthenticationToken(principal, null, authorities);
                newAuth.setDetails(authentication.getDetails());
                SecurityContextHolder.getContext().setAuthentication(newAuth);

                String redirectUrl = getRedirectUrlByRole(authorities);
                if (redirectUrl != null) {
                    response.sendRedirect(redirectUrl);
                } else {
                    response.sendRedirect("/login?error=no_role");
                }
            } catch (Exception e) {
                logger.error("Error during OAuth2 success handling", e);
                response.sendRedirect("/login?error=oauth2_processing");
            }
        };
    }

    private String extractEmailFromOAuth2Principal(Object principal) {
        if (principal instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser oidcUser) {
            return oidcUser.getAttribute("email");
        } else if (principal instanceof org.springframework.security.oauth2.core.user.DefaultOAuth2User oauth2User) {
            return (String) oauth2User.getAttributes().get("email");
        }
        return null;
    }

    private Persons findActivePersonByEmail(String email) {
        try {
            return entityManager.createQuery(
                            "SELECT p FROM Persons p JOIN Authenticators a ON p.id = a.personId " +
                                    "WHERE p.email = :email AND a.accountStatus = :st",
                            Persons.class)
                    .setParameter("email", email)
                    .setParameter("st", AccountStatus.ACTIVE)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private String determineRole(Persons person) {
        if (person instanceof Staffs) return "ROLE_STAFF";
        if (person instanceof MajorLecturers) return "ROLE_LECTURER";
        if (person instanceof Students) return "ROLE_STUDENT";
        if (person instanceof Admins) return "ROLE_ADMIN";
        return "ROLE_STUDENT"; // Default
    }

    private String getRedirectUrlByRole(Collection<?> authorities) {
        if (authorities.contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) return "/student-home";
        if (authorities.contains(new SimpleGrantedAuthority("ROLE_STAFF"))) return "/staff-home";
        if (authorities.contains(new SimpleGrantedAuthority("ROLE_LECTURER"))) return "/lecturer-home";
        if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) return "/admin-home";
        return null;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS persistent_logins (" +
                    "username VARCHAR(64) NOT NULL, " +
                    "series VARCHAR(64) PRIMARY KEY, " +
                    "token VARCHAR(64) NOT NULL, " +
                    "last_used TIMESTAMP NOT NULL)");
        } catch (Exception e) {
            logger.error("Failed to create persistent_logins table", e);
        }
        return tokenRepository;
    }
}
