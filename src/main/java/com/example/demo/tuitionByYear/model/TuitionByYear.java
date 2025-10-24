package com.example.demo.tuitionByYear.model;

import com.example.demo.user.admin.model.Admins;
import com.example.demo.campus.model.Campuses;
import com.example.demo.entity.Enums.ContractStatus;
import com.example.demo.subject.majorSubject.model.MajorSubjects;
import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import com.example.demo.subject.abstractSubject.model.MinorSubjects;
import com.example.demo.subject.abstractSubject.model.Subjects;
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

    @Column(name = "ReStudyTuition", nullable = true)
    private Double reStudyTuition;

    @Enumerated(EnumType.STRING)
    @Column(name = "ContractStatus", nullable = true)
    private ContractStatus contractStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatorId", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Admins creator;

    public String getSubjectMajor() {
        Subjects subjects = getSubject();
        if (subjects instanceof MajorSubjects majorSubject) {
            return majorSubject.getMajor().getMajorName();
        } else if (subjects instanceof MinorSubjects minorSubject) {
            return "General";
        } else if (subjects instanceof SpecializedSubject specializedSubject) {
            return specializedSubject.getSubjectName();
        }
        return "N/A";
    }
    public String getSubjectType() {
        Subjects subjects = getSubject();
        if (subjects instanceof MajorSubjects majorSubject) {
            return "Major Subject";
        } else if (subjects instanceof MinorSubjects minorSubject) {
            return "Minor Subject";
        } else if (subjects instanceof SpecializedSubject specializedSubject) {
            return "Specialized Subject";
        }
        return "N/A";
    }

    // Constructors
    public TuitionByYear() {
        this.id = new TuitionByYearId();
    }

    public TuitionByYear(Subjects subject, Campuses campus, Integer admissionYear, Double tuition, Double reStudyTuition, ContractStatus contractStatus, Admins creator) {
        this.id = new TuitionByYearId(subject.getSubjectId(), admissionYear, campus.getCampusId());
        this.subject = subject;
        this.campus = campus;
        this.admissionYear = admissionYear;
        this.tuition = tuition;
        this.reStudyTuition = reStudyTuition;
        this.contractStatus = contractStatus;
        this.creator = creator;
    }
}