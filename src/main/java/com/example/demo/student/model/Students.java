package com.example.demo.student.model;

import com.example.demo.person.model.Persons;
import com.example.demo.campus.model.Campuses;
import com.example.demo.entity.Enums.Gender;
import com.example.demo.entity.Enums.LearningProgramTypes;
import com.example.demo.major.model.Majors;
import com.example.demo.Staff.model.Staffs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "Students")
@PrimaryKeyJoinColumn(name = "ID")
@Getter
@Setter
@OnDelete(action = OnDeleteAction.CASCADE)
public class Students extends Persons {

    @Column(nullable = false, updatable = false)
    private LocalDate createdDate = LocalDate.now();

    @Column(name = "MIS_ID", length = 50)
    private String misId;

    @Column(name = "Admission_Year", nullable = false)
    private LocalDate admissionYear;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CreatorID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CampusID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Campuses campus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MajorID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Majors major;

    @Enumerated(EnumType.STRING)
    @Column(name = "LearningProgramType", nullable = true)
    private LearningProgramTypes learningProgramType;

    public String getDefaultAvatarPath() {
        if (getAvatar() != null) {
            return null;
        }
        return getGender() == Gender.MALE ? "/DefaultAvatar/Student_Boy.png" : "/DefaultAvatar/Student_Girl.png";
    }

    @Override
    public String getRoleType() {
        return "STUDENT";
    }

}