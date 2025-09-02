package com.example.demo.subject.model;

import com.example.demo.deputyStaff.model.DeputyStaffs;
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

    public MinorSubjects(String subjectId, String subjectName, Integer semester, DeputyStaffs creator) {
        setSubjectId(subjectId);
        setSubjectName(subjectName);
        setSemester(semester);
        this.creator = creator;
    }
}