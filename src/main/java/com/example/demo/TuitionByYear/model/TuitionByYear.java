package com.example.demo.TuitionByYear.model;

import com.example.demo.admin.model.Admins;
import com.example.demo.entity.TuitionByYearId;
import com.example.demo.subject.model.Subjects;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "TuitionByYear")
@Getter
@Setter
public class TuitionByYear {

    @EmbeddedId
    private TuitionByYearId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("subjectId")
    @JoinColumn(name = "SubjectID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Subjects subject;

    @Column(name = "Tuition", nullable = false)
    private Double tuition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatorID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Admins creator;

    public TuitionByYear() {}

    public TuitionByYear(Subjects subject, Integer admissionYear, Double tuition, Admins creator) {
        this.id = new TuitionByYearId(subject.getSubjectId(), admissionYear);
        this.subject = subject;
        this.tuition = tuition;
        this.creator = creator;
    }
}