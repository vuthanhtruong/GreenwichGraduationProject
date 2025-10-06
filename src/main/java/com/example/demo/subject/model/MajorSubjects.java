package com.example.demo.subject.model;

import com.example.demo.Curriculum.model.Curriculum;
import com.example.demo.major.model.Majors;
import com.example.demo.staff.model.Staffs;
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