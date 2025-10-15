package com.example.demo.majorSubject.model;

import com.example.demo.curriculum.model.Curriculum;
import com.example.demo.major.model.Majors;
import com.example.demo.staff.model.Staffs;
import com.example.demo.subject.model.Subjects;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@DiscriminatorValue("MAJOR")
@Getter
@Setter
public class MajorSubjects extends Subjects {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MajorID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Majors major;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CurriculumID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Curriculum curriculum;
}