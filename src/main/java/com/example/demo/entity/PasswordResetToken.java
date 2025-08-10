package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "PasswordResetTokens")
@Getter
@Setter
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PersonID", nullable = false)
    private Authenticators authenticator;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    public PasswordResetToken() {}

    public PasswordResetToken(String token, Authenticators authenticator) {
        this.token = token;
        this.authenticator = authenticator;
        this.expiryDate = LocalDateTime.now().plusHours(1); // Token hết hạn sau 1 giờ
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}