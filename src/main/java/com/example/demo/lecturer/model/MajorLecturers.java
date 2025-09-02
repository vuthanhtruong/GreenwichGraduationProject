package com.example.demo.lecturer.model;

import com.example.demo.employe.model.MajorEmployes;
import com.example.demo.entity.Enums.EmploymentTypes;
import com.example.demo.entity.Enums.Gender;
import com.example.demo.staff.model.Staffs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "Lecturers")
@PrimaryKeyJoinColumn(name = "ID")
@Getter
@Setter
public class MajorLecturers extends MajorEmployes implements MajorLecturersInterface {

    @Column(name = "Type", nullable = true, length = 50)
    @Enumerated(EnumType.STRING)
    private EmploymentTypes employmentTypes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AddedBy", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

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
        return "LECTURER";
    }

}