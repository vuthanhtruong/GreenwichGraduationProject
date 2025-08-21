package com.example.demo.person.model;

import com.example.demo.entity.Enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class Persons {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "FirstName", nullable = true, length = 100)
    private String firstName;

    @Column(name = "LastName", nullable = true, length = 100)
    private String lastName;

    @Column(name = "Email", nullable = true, unique = true, length = 255)
    private String email;

    @Column(name = "PhoneNumber", nullable = true, unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "BirthDate", nullable = true)
    @Past(message = "Birth date must be a past date")
    private LocalDate birthDate;

    @Column(name = "Gender", nullable = true)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "Country", nullable = true, length = 100)
    private String country;

    @Column(name = "Province", nullable = true, length = 100)
    private String province;

    @Column(name = "City", nullable = true, length = 100)
    private String city;

    @Column(name = "District", nullable = true, length = 100)
    private String district;

    @Column(name = "Ward", nullable = true, length = 100)
    private String ward;

    @Column(name = "Street", nullable = true, length = 255)
    private String street;

    @Column(name = "PostalCode", nullable = true, length = 20)
    private String postalCode;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "Avatar", nullable = true, columnDefinition = "LONGBLOB")
    private byte[] avatar;

    public String getFullName() {
        String fn = firstName == null ? "" : firstName.trim();
        String ln = lastName == null ? "" : lastName.trim();
        return (fn + " " + ln).trim();
    }

    public abstract String getRoleType();
}
