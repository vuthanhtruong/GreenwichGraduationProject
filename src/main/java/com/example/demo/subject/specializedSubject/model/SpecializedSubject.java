package com.example.demo.subject.specializedSubject.model;

import com.example.demo.curriculum.model.Curriculum;
import com.example.demo.specialization.model.Specialization;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.subject.abstractSubject.model.Subjects;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "SpecializedSubjects")
@PrimaryKeyJoinColumn(name = "SubjectID")
@Getter
@Setter
public class SpecializedSubject extends Subjects {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CurriculumID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Curriculum curriculum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SpecializationID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Specialization specialization;

    public SpecializedSubject() {}

    public SpecializedSubject(String subjectId, String subjectName, Integer semester, Staffs creator, Specialization specialization) {
        setSubjectId(subjectId);
        setSubjectName(subjectName);
        setSemester(semester);
        this.creator = creator;
        this.specialization = specialization;
    }

    @Override
    public String getSubjectType() {
        return "Specialized Subject";
    }

    @Override
    public String getSubjectMajor() {
        return specialization != null ? specialization.getSpecializationName() : "Unknown Specialization";
    }

    @Override
    public String getCreatorName() {
        return creator != null ? creator.getFullName() : "Unknown Staff";
    }
}