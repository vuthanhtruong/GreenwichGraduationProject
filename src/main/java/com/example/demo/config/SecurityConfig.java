package com.example.demo.config;

import com.example.demo.security.model.CustomOidcUserPrincipal;
import com.example.demo.security.service.CustomOAuth2UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.util.Collection;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/student-home/**", "/api/student-home/**").hasRole("STUDENT")
                        .requestMatchers("/staff-home/**", "/api/staff-home/**").hasRole("STAFF")
                        .requestMatchers("/lecturer-home/**", "/api/lecturer-home/**").hasRole("LECTURER")
                        .requestMatchers("/admin-home/**", "/api/admin-home/**").hasRole("ADMIN")
                        .requestMatchers(
                                "/login",
                                "/resources/**",
                                "/css/**",
                                "/js/**",
                                "/*.css",
                                "/oauth2/**",
                                "/home",
                                "/auth/reset-password/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler((req, res, auth) -> {
                            String redirectUrl = getRedirectUrlByRole(auth.getAuthorities());
                            res.sendRedirect(redirectUrl != null ? redirectUrl : "/login");
                        })
                        .failureHandler(flashErrorHandler("Invalid username or password."))
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(customOAuth2UserService))
                        .successHandler((req, res, auth) -> {
                            var principal = (CustomOidcUserPrincipal) auth.getPrincipal();
                            String redirectUrl = getRedirectUrlByRole(principal.getAuthorities());
                            res.sendRedirect(redirectUrl != null ? redirectUrl : "/login");
                        })
                        .failureHandler(flashErrorHandler("OAuth2 login failed. Please try again."))
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((req, res, auth) -> {
                            req.getSession().setAttribute("message", "You have been logged out successfully.");
                            res.sendRedirect("/login");
                        })
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );
        return http.build();
    }

    private AuthenticationFailureHandler flashErrorHandler(String defaultMessage) {
        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request,
                                                HttpServletResponse response,
                                                org.springframework.security.core.AuthenticationException exception)
                    throws IOException, ServletException {
                logger.warn("Authentication failed: {}", exception.getMessage());
                request.getSession().setAttribute("error", defaultMessage);
                response.sendRedirect("/login");
            }
        };
    }

    private String getRedirectUrlByRole(Collection<?> authorities) {
        if (authorities.contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) return "/student-home";
        if (authorities.contains(new SimpleGrantedAuthority("ROLE_STAFF"))) return "/staff-home";
        if (authorities.contains(new SimpleGrantedAuthority("ROLE_LECTURER"))) return "/major-lecturer-home";
        if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) return "/admin-home";
        return null;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}