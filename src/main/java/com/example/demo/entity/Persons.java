package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class Persons {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "FirstName", nullable = true, length = 100)
    private String firstName;

    @Column(name = "LastName", nullable = true, length = 100)
    private String lastName;

    @Column(name = "Email", nullable = false, unique = true, length = 255)
    @Email(message = "Invalid email")
    private String email;

    @Column(name = "PhoneNumber", nullable = false, unique = true, length = 20)
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
    private String phoneNumber;

    @Column(name = "BirthDate", nullable = true)
    @Past(message = "Birth date must be a past date")
    private LocalDate birthDate;

    @Column(name = "Gender", nullable = true)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Lob
    @Column(name = "FaceData", columnDefinition = "LONGTEXT", nullable = true)
    private String faceData;

    @Lob
    @Column(name = "VoiceData", columnDefinition = "LONGTEXT", nullable = true)
    private String voiceData;

    @Column(name = "Country", nullable = true, length = 100)
    private String country; // Country

    @Column(name = "Province", nullable = true, length = 100)
    private String province; // Tỉnh/Bang

    @Column(name = "City", nullable = true, length = 100)
    private String city; // Thành phố

    @Column(name = "District", nullable = true, length = 100)
    private String district; // Quận/Huyện

    @Column(name = "Ward", nullable = true, length = 100)
    private String ward; // Xã/Phường

    @Column(name = "Street", nullable = true, length = 255)
    private String street; // Đường, số nhà

    @Column(name = "PostalCode", nullable = true, length = 20)
    private String postalCode; // Mã bưu điện

    public String getFullName() {
        return firstName + " " + lastName;
    }
}