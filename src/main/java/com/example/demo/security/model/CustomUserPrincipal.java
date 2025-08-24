package com.example.demo.security.model;

import com.example.demo.person.model.Persons;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserPrincipal extends User {

    private final Persons person; // Thông tin chung (Persons)

    public CustomUserPrincipal(
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            Persons person
    ) {
        super(username, password, authorities);
        this.person = person;
    }

    public Persons getPerson() {
        return person;
    }
}
