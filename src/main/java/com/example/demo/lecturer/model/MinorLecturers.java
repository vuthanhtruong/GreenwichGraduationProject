package com.example.demo.lecturer.model;

import com.example.demo.employe.model.MinorEmployes;
import com.example.demo.entity.DeputyStaffs;
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
public class MinorLecturers extends MinorEmployes {

    @Column(name = "Type", nullable = true, length = 50)
    @Enumerated(EnumType.STRING)
    private EmploymentTypes employmentTypes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AddedBy", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DeputyStaffs creator;

    public String getDefaultAvatarPath() {
        if (getAvatar() != null) {
            return null;
        }
        return getGender() == Gender.MALE ? "/DefaultAvatar/MinorLecturer_Boy.png" : "/DefaultAvatar/MinorLecturer_Girl.png";
    }

    @Override
    public String getRoleType() {
        return "MINOR_LECTURER";
    }
}