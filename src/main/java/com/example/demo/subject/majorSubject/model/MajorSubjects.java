package com.example.demo.subject.majorSubject.model;

import com.example.demo.curriculum.model.Curriculum;
import com.example.demo.major.model.Majors;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.subject.abstractSubject.model.Subjects;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "MajorSubjects")
@PrimaryKeyJoinColumn(name = "SubjectID")
@Getter
@Setter
public class MajorSubjects extends Subjects {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MajorID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Majors major;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CurriculumID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Curriculum curriculum;

    @Override
    public String getSubjectType() {
        return "Major Subject";
    }

    @Override
    public String getSubjectMajor() {
        return major != null ? major.getMajorName() : "Unknown Major";
    }

    @Override
    public String getCreatorName() {
        return creator != null ? creator.getFullName() : "Unknown Staff";
    }
}