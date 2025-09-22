package com.example.demo.CampusSubjectsByYear.model;

import com.example.demo.admin.model.Admins;
import com.example.demo.campus.model.Campuses;
import com.example.demo.subject.model.Subjects;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "CampusSubjectsByYear")
@Data
public class CampusSubjectsByYear {

    @EmbeddedId
    private CampusSubjectsByYearId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("subjectId")
    @JoinColumn(name = "SubjectId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Subjects subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("campusId")
    @JoinColumn(name = "CampusId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Campuses campus;

    @Column(name = "AdmissionYear", insertable = false, updatable = false)
    private Integer admissionYear;

    // Khóa ngoại đến admin phê duyệt
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ApprovedById", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Admins approvedBy;

    // Additional fields
    @Column(name = "TuitionFee")
    private Double tuitionFee;

    // Constructors
    public CampusSubjectsByYear() {
        this.id = new CampusSubjectsByYearId();
    }

    public CampusSubjectsByYear(Subjects subject, Campuses campus, Integer admissionYear, Admins approvedBy) {
        this.id = new CampusSubjectsByYearId(subject.getSubjectId(), campus.getCampusId(), admissionYear);
        this.subject = subject;
        this.campus = campus;
        this.admissionYear = admissionYear;
        this.approvedBy = approvedBy;
    }
}