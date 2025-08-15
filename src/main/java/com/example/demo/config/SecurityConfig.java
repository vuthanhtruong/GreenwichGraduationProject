package com.example.demo.config;

import com.example.demo.entity.MajorLecturers;
import com.example.demo.entity.Students;
import com.example.demo.entity.Staffs;
import com.example.demo.entity.AbstractClasses.Persons;
import com.example.demo.entity.Enums.AccountStatus;
import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.security.CustomOAuth2UserService;
import com.example.demo.security.CustomUserPrincipal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final DataSource dataSource;

    @PersistenceContext
    private EntityManager entityManager;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          CustomOAuth2UserService customOAuth2UserService,
                          DataSource dataSource) {
        this.userDetailsService = userDetailsService;
        this.customOAuth2UserService = customOAuth2UserService;
        this.dataSource = dataSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/student-home/**").hasRole("STUDENT")
                        .requestMatchers("/staff-home/**").hasRole("STAFF")
                        .requestMatchers("/teacher-home/**").hasRole("LECTURER")

                        .requestMatchers("/api/student-home/**").hasRole("STUDENT")
                        .requestMatchers("/api/staff-home/**").hasRole("STAFF")
                        .requestMatchers("/api/teacher-home/**").hasRole("LECTURER")
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
                        .failureHandler((req, res, ex) -> res.sendRedirect("/login?error"))
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .tokenRepository(persistentTokenRepository())
                        .tokenValiditySeconds(7 * 24 * 60 * 60)
                        .key("your-secret-key-1234567890") // TODO: đưa vào ENV/Secrets
                        .userDetailsService(userDetailsService)
                        .useSecureCookie(true)
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler formSuccessHandler() {
        return (request, response, authentication) -> {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
                response.sendRedirect("/student-home");
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STAFF"))) {
                response.sendRedirect("/staff-home");
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_LECTURER"))) {
                response.sendRedirect("/teacher-home");
            } else {
                response.sendRedirect("/login?error=no_role");
            }
        };
    }

    @Bean
    public AuthenticationSuccessHandler oauth2SuccessHandler() {
        return (request, response, authentication) -> {
            // Lấy email từ principal OAuth2/OIDC
            String email = null;
            Object p = authentication.getPrincipal();
            if (p instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser oidc) {
                email = oidc.getAttribute("email");
            } else if (p instanceof org.springframework.security.oauth2.core.user.DefaultOAuth2User ou) {
                Object e = ou.getAttributes().get("email");
                if (e != null) email = e.toString();
            }
            if (email == null || email.isBlank()) {
                email = authentication.getName();
            }

            // Tra DB -> xác định role + lấy Persons (nếu có)
            Persons person = null;
            String role = "ROLE_STUDENT"; // mặc định nếu chưa map được
            try {
                person = entityManager.createQuery(
                                "SELECT p FROM Persons p JOIN Authenticators a ON p.id = a.personId " +
                                        "WHERE p.email = :email AND a.accountStatus = :st",
                                Persons.class)
                        .setParameter("email", email)
                        .setParameter("st", AccountStatus.ACTIVE)
                        .getSingleResult();

                if (person instanceof Staffs) {
                    role = "ROLE_STAFF";
                } else if (person instanceof MajorLecturers) {
                    role = "ROLE_LECTURER";
                } else if (person instanceof Students) {
                    role = "ROLE_STUDENT";
                } else {
                    // Giữ mặc định
                }
            } catch (NoResultException ignore) {
                // Không tìm thấy -> giữ mặc định ROLE_STUDENT, person = null
            }

            var authorities = List.of(new SimpleGrantedAuthority(role));
            var principal = new CustomUserPrincipal(
                    email,
                    "N/A", // OAuth2 không có password
                    authorities,
                    person
            );

            // Ghi lại Authentication với principal đã chuẩn hoá
            var newAuth = new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
            newAuth.setDetails(authentication.getDetails());
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            // Redirect theo role
            if (authorities.contains(new SimpleGrantedAuthority("ROLE_STAFF"))) {
                response.sendRedirect("/staff-home");
            } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_LECTURER"))) {
                response.sendRedirect("/teacher-home");
            } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
                response.sendRedirect("/student-home");
            } else {
                response.sendRedirect("/login?error=no_role");
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        // tokenRepository.setCreateTableOnStartup(true);
        return tokenRepository;
    }
}
