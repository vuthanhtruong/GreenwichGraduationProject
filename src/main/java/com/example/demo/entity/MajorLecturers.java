package com.example.demo.entity;

import com.example.demo.entity.AbstractClasses.MajorEmployes;
import com.example.demo.entity.Enums.EmploymentTypes;
import com.example.demo.entity.Enums.Gender;
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
public class MajorLecturers extends MajorEmployes {

    @Column(name = "Type", nullable = true, length = 50)
    @Enumerated(EnumType.STRING)
    private EmploymentTypes employmentTypes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AddedBy", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    public String getDefaultAvatarPath() {
        if (getAvatar() != null) {
            return null; // Avatar exists, no default needed
        }
        return getGender() == Gender.MALE ? "/DefaultAvatar/Teacher_Boy.png" : "/DefaultAvatar/Teacher_Girl.png";
    }

    @Override
    public String getRoleType() {
        return "LECTURER";
    }

}