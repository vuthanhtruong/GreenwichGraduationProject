package com.example.demo.entity;

import com.example.demo.entity.AbstractClasses.Persons;
import com.example.demo.entity.Enums.AccountStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Table(name = "Authenticators")
@Getter
@Setter
public class Authenticators {

    @Id
    @Column(name = "PersonID")
    private String personId; // Khóa chính, trùng ID của Persons

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PersonID", nullable = false, insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Persons person; // Liên kết tới entity Persons (hoặc subclass)

    @Column(name = "Password", nullable = true, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "AccountStatus", nullable = true)
    private AccountStatus accountStatus = AccountStatus.ACTIVE; // Default status

    public void setPassword(String rawPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(rawPassword);
    }
}
