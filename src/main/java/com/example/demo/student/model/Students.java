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
public class Students extends Persons implements StudentsInterface {

    @Column(nullable = false, updatable = false)
    private LocalDate createdDate = LocalDate.now();

    @Column(name = "Admission_Year", nullable = false)
    private LocalDate admissionYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatorID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CampusID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Campuses campus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MajorID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Majors major;

    @Enumerated(EnumType.STRING)
    @Column(name = "LearningProgramType", nullable = true)
    private LearningProgramTypes learningProgramType;

    @Override
    public String getDefaultAvatarPath() {
        if (getAvatar() != null) {
            return null;
        }
        return getGender() == Gender.MALE ? "/DefaultAvatar/Student_Boy.png" : "/DefaultAvatar/Student_Girl.png";
    }

    @Override
    public String getAcademicInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Admission Year: ").append(admissionYear != null ? admissionYear.toString() : "N/A").append("\n");
        sb.append("Campus: ").append(campus != null ? campus.getCampusName() : "N/A").append("\n");
        sb.append("Major: ").append(major != null ? major.getMajorName() : "N/A").append("\n");
        sb.append("Learning Program Type: ").append(learningProgramType != null ? learningProgramType.toString() : "N/A").append("\n");
        sb.append("Created By: ").append(creator != null ? creator.getFullName() : "N/A").append("\n");
        sb.append("Created Date: ").append(createdDate != null ? createdDate.toString() : "N/A");
        return sb.toString();
    }

    @Override
    public String getRoleType() {
        return "STUDENT";
    }

}