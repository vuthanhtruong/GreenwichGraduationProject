package com.example.demo.security.model;

import com.example.demo.user.person.model.Persons;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class DatabaseUserPrincipal extends User {

    private final Persons person;

    public DatabaseUserPrincipal(
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
