package com.example.demo.TuitionByYear.model;

import com.example.demo.CampusSubjectsByYear.model.CampusSubjectsByYear;
import com.example.demo.admin.model.Admins;
import com.example.demo.campus.model.Campuses;
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

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "SubjectID", referencedColumnName = "SubjectId"),
            @JoinColumn(name = "CampusID", referencedColumnName = "CampusId"),
            @JoinColumn(name = "AdmissionYear", referencedColumnName = "AdmissionYear")
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CampusSubjectsByYear campusSubjectsByYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SubjectID", referencedColumnName = "SubjectId", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Subjects subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CampusID", referencedColumnName = "CampusId", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Campuses campus;

    @Column(name = "Tuition", nullable = true)
    private Double tuition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatorID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Admins creator;

    public TuitionByYear() {
        this.id = new TuitionByYearId();
    }

    public TuitionByYear(Subjects subject, Integer admissionYear, Campuses campus, Double tuition, Admins creator, CampusSubjectsByYear campusSubjectsByYear) {
        this.id = new TuitionByYearId(subject.getSubjectId(), admissionYear, campus.getCampusId());
        this.subject = subject;
        this.campus = campus;
        this.tuition = tuition;
        this.creator = creator;
        this.campusSubjectsByYear = campusSubjectsByYear;
    }
}