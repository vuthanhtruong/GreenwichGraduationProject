package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@DiscriminatorValue("MINOR")
@Getter
@Setter
public class MinorSubjects extends Subjects {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DeputyStaffs creator;

    public MinorSubjects() {}

    public MinorSubjects(String subjectId, String subjectName, Double tuition, Integer semester, DeputyStaffs creator) {
        setSubjectId(subjectId);
        setSubjectName(subjectName);
        setTuition(tuition);
        setSemester(semester);
        this.creator = creator;
    }
}