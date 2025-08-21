package com.example.demo.subject.model;

import com.example.demo.major.model.Majors;
import com.example.demo.majorstaff.model.Staffs;
import com.example.demo.entity.Enums.LearningProgramTypes;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "LearningProgramType", nullable = true)
    private LearningProgramTypes learningProgramType;
}