package com.example.demo.subject.model;

import com.example.demo.Curriculum.model.Curriculum;
import com.example.demo.Specialization.model.Specialization;
import com.example.demo.staff.model.Staffs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@DiscriminatorValue("SPECIALIZED")
@Getter
@Setter
public class SpecializedSubject extends Subjects {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CurriculumID", nullable = true)
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
}