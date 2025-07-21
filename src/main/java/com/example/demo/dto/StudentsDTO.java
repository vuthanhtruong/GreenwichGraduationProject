package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentsDTO {
    @NotBlank(message = "ID is required")
    private String id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
    private String phoneNumber;

    @Past(message = "Birth date must be a past date")
    private String birthDate;

    private String gender;

    private String country;
    private String province;
    private String city;
    private String district;
    private String ward;
    private String street;
    private String postalCode;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "MIS ID is required")
    private String misId;

    private String createdDate;

    private String campusId;
    private String campusName;

    private String majorId;
    private String majorName;
}