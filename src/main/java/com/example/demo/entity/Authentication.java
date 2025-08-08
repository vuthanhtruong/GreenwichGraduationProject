package com.example.demo.entity;

import com.example.demo.entity.AbstractClasses.Persons;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Table(name = "Authentication")
@Getter
@Setter
public class Authentication {

    @Id
    @Column(name = "PersonID")
    private String personId; // Khóa chính, trùng ID của Persons

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PersonID", nullable = false, insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Persons person; // Liên kết tới entity Persons (hoặc subclass)

    @Column(name = "Password", nullable = false, length = 255)
    private String password;

    public void setPassword(String rawPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(rawPassword);
    }
}
