package com.example.demo.TuitionByYear.model;

import com.example.demo.admin.model.Admins;
import com.example.demo.campus.model.Campuses;
import com.example.demo.subject.model.Subjects;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "TuitionByYear")
@Data
public class TuitionByYear {

    @EmbeddedId
    private TuitionByYearId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("subjectId")
    @JoinColumn(name = "SubjectId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Subjects subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("campusId")
    @JoinColumn(name = "CampusId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Campuses campus;

    @Column(name = "AdmissionYear", insertable = false, updatable = false)
    private Integer admissionYear;

    @Column(name = "Tuition", nullable = true)
    private Double tuition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatorId", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Admins creator;

    // Constructors
    public TuitionByYear() {
        this.id = new TuitionByYearId();
    }

    public TuitionByYear(Subjects subject, Campuses campus, Integer admissionYear, Double tuition, Admins creator) {
        this.id = new TuitionByYearId(subject.getSubjectId(), admissionYear, campus.getCampusId());
        this.subject = subject;
        this.campus = campus;
        this.admissionYear = admissionYear;
        this.tuition = tuition;
        this.creator = creator;
    }
}