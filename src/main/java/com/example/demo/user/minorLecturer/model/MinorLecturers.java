package com.example.demo.user.minorLecturer.model;

import com.example.demo.user.employe.model.MinorEmployes;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.entity.Enums.EmploymentTypes;
import com.example.demo.entity.Enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "MinorLecturers")
@PrimaryKeyJoinColumn(name = "ID")
@Getter
@Setter
public class MinorLecturers extends MinorEmployes implements MinorLecturersInterface {

    @Column(name = "Type", nullable = true, length = 50)
    @Enumerated(EnumType.STRING)
    private EmploymentTypes employmentTypes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AddedBy", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DeputyStaffs creator;

    @Override
    public String getDefaultAvatarPath() {
        if (getAvatar() != null) {
            return null; // Avatar exists, no default needed
        }
        return getGender() == Gender.MALE ? "/DefaultAvatar/Teacher_Boy.png" : "/DefaultAvatar/Teacher_Girl.png";
    }

    @Override
    public String getEmploymentInfo() {
        StringBuilder sb = new StringBuilder(super.getEmploymentInfo());
        sb.append("\nEmployment Type: ").append(employmentTypes != null ? employmentTypes.toString() : "N/A");
        sb.append("\nAdded By: ").append(creator != null ? creator.getFullName() : "N/A");
        return sb.toString();
    }

    @Override
    public String getLecturerInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Employment Type: ").append(employmentTypes != null ? employmentTypes.toString() : "N/A").append("\n");
        sb.append("Added By: ").append(creator != null ? creator.getFullName() : "N/A");
        return sb.toString();
    }

    @Override
    public String getRoleType() {
        return "MINOR_LECTURER";
    }
}