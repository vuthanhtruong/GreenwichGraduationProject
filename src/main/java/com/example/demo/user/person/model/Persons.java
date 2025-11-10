package com.example.demo.user.person.model;

import com.example.demo.entity.Enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class Persons implements PersonsInterface {

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

    @Override
    public String getFullName() {
        String fn = firstName == null ? "" : firstName.trim();
        String ln = lastName == null ? "" : lastName.trim();
        return (fn + " " + ln).trim();
    }

    public abstract String getDefaultAvatarPath();

    @Override
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (street != null && !street.trim().isEmpty()) {
            sb.append(street.trim()).append(", ");
        }
        if (ward != null && !ward.trim().isEmpty()) {
            sb.append(ward.trim()).append(", ");
        }
        if (district != null && !district.trim().isEmpty()) {
            sb.append(district.trim()).append(", ");
        }
        if (city != null && !city.trim().isEmpty()) {
            sb.append(city.trim()).append(", ");
        }
        if (province != null && !province.trim().isEmpty()) {
            sb.append(province.trim()).append(", ");
        }
        if (country != null && !country.trim().isEmpty()) {
            sb.append(country.trim()).append(" ");
        }
        if (postalCode != null && !postalCode.trim().isEmpty()) {
            sb.append(postalCode.trim());
        }
        String address = sb.toString().trim();
        if (address.endsWith(",")) {
            address = address.substring(0, address.length() - 1).trim();
        }
        return address;
    }

    @Override
    public String getPersonalInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id != null ? id : "N/A").append("\n");
        sb.append("Full Name: ").append(getFullName()).append("\n");
        sb.append("Email: ").append(email != null ? email : "N/A").append("\n");
        sb.append("Phone Number: ").append(phoneNumber != null ? phoneNumber : "N/A").append("\n");
        sb.append("Birth Date: ").append(birthDate != null ? birthDate.toString() : "N/A").append("\n");
        sb.append("Gender: ").append(gender != null ? gender.toString() : "N/A");
        return sb.toString();
    }

    @Override
    public abstract String getRoleType();
}