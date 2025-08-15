package com.example.demo.security;

import com.example.demo.entity.AbstractClasses.Persons;
import com.example.demo.entity.Staffs;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Campuses;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserPrincipal extends User {
    private final Persons person;   // Thông tin chung (Persons)
    private final Staffs staff;     // null nếu không phải Staff
    private final Majors major;     // null nếu không có major
    private final Campuses campus;  // null nếu không có campus

    public CustomUserPrincipal(
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            Persons person,
            Staffs staff,
            Majors major,
            Campuses campus
    ) {
        super(username, password, authorities);
        this.person = person;
        this.staff = staff;
        this.major = major;
        this.campus = campus;
    }

    public Persons getPerson() {
        return person;
    }

    public Staffs getStaff() {
        return staff;
    }

    public Majors getMajor() {
        return major;
    }

    public Campuses getCampus() {
        return campus;
    }
}


