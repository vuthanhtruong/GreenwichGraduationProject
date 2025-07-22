package com.example.demo.security;

import com.example.demo.entity.Persons;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private final Persons person;
    private final String role;

    public CustomUserDetails(Persons person, String role) {
        this.person = person;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        // Assuming password is stored in the respective subclass (Students, Staffs, Lecturers)
        if (person instanceof com.example.demo.entity.Students) {
            return ((com.example.demo.entity.Students) person).getPassword();
        } else if (person instanceof com.example.demo.entity.Staffs) {
            return ((com.example.demo.entity.Staffs) person).getPassword();
        } else if (person instanceof com.example.demo.entity.Lecturers) {
            return ((com.example.demo.entity.Lecturers) person).getPassword();
        }
        return null;
    }

    @Override
    public String getUsername() {
        return person.getId(); // Using email as username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Persons getPerson() {
        return person;
    }

    public String getRole() {
        return role;
    }
}