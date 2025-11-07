package com.example.demo.subject.minorSubject.model;

import com.example.demo.subject.abstractSubject.model.Subjects;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "MinorSubjects")
@PrimaryKeyJoinColumn(name = "SubjectID")
@Getter
@Setter
public class MinorSubjects extends Subjects {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DeputyStaffs creator;

    public MinorSubjects() {}

    public MinorSubjects(String subjectId, String subjectName, Integer semester, DeputyStaffs creator) {
        setSubjectId(subjectId);
        setSubjectName(subjectName);
        setSemester(semester);
        this.creator = creator;
    }

    @Override
    public String getSubjectType() {
        return "Minor Subject";
    }

    @Override
    public String getSubjectMajor() {
        return "General Education";
    }

    @Override
    public String getCreatorName() {
        return creator != null ? creator.getFullName() : "Unknown Deputy";
    }
}