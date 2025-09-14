package com.example.demo.TuitionByYear.model;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("subjectId")
    @JoinColumn(name = "SubjectID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Subjects subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("campusId") // Ánh xạ CampusID vào khóa chính
    @JoinColumn(name = "CampusID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Campuses campus; // Thêm quan hệ với Campuses

    @Column(name = "Tuition", nullable = true)
    private Double tuition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatorID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Admins creator;

    public TuitionByYear() {}

    public TuitionByYear(Subjects subject, Integer admissionYear, Campuses campus, Double tuition, Admins creator) {
        this.id = new TuitionByYearId(subject.getSubjectId(), admissionYear, campus.getCampusId());
        this.subject = subject;
        this.campus = campus;
        this.tuition = tuition;
        this.creator = creator;
    }
}