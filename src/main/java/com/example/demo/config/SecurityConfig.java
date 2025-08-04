package com.example.demo.config;

import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.security.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          CustomOAuth2UserService customOAuth2UserService) {
        this.userDetailsService = userDetailsService;
        this.customOAuth2UserService = customOAuth2UserService;
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
                        .requestMatchers("/login", "/resources/**", "/css/**", "/js/**", "/*.css", "/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(customAuthenticationSuccessHandler())
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(customOAuth2UserService)
                        )
                        .successHandler(customAuthenticationSuccessHandler())
                        .failureHandler((request, response, exception) -> {
                            response.sendRedirect("/login?error");
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
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
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}