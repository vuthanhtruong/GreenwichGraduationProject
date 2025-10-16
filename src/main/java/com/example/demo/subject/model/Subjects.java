package com.example.demo.subject.model;

import com.example.demo.admin.model.Admins;
import com.example.demo.classes.model.Classes;
import com.example.demo.classes.model.MajorClasses;
import com.example.demo.classes.model.MinorClasses;
import com.example.demo.entity.Enums.SubjectTypes;
import com.example.demo.majorSubject.model.MajorSubjects;
import com.example.demo.specializedClasses.model.SpecializedClasses;
import com.example.demo.specializedSubject.model.SpecializedSubject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.Hibernate;
@Entity
@Table(name = "Subjects")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter

public abstract class Subjects {

    @Id
    @Column(name = "SubjectID")
    private String subjectId;

    @Column(name = "SubjectName", nullable = false, length = 255)
    private String subjectName;

    @Column(name = "Semester", nullable = true)
    private Integer semester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AcceptorID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Admins acceptor;

    @Enumerated(EnumType.STRING)
    @Column(name = "RequirementType", nullable = true)
    private SubjectTypes requirementType;

    public String getSubjectMajor(Subjects subject) {
        // Khởi tạo proxy để đảm bảo lấy đúng lớp con
        Hibernate.initialize(subject);

        if (subject instanceof MajorSubjects majorSubject) {
            return majorSubject.getMajor().getMajorName();
        }
        if (subject instanceof MinorSubjects minorSubject) {
            return "General";
        }
        if (subject instanceof SpecializedSubject specializedSubject) {
            return specializedSubject.getSpecialization().getSpecializationName();
        }
        return "Unknown: " + subject.getSubjectName();
    }
    public String getSubjectType(Subjects subject) {
        // Khởi tạo proxy để đảm bảo lấy đúng lớp con
        Hibernate.initialize(subject);

        if (subject instanceof MajorSubjects majorSubject) {
            return "Major Subject";
        }
        if (subject instanceof MinorSubjects minorSubject) {
            return "Minor Subject";
        }
        if (subject instanceof SpecializedSubject specializedSubject) {
            return "Specialized Subject";
        }
        return "Unknown: " + subject.getSubjectName();
    }

}