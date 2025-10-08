package com.example.demo.MajorLecturers_Specializations.model;

import com.example.demo.Specialization.model.Specialization;
import com.example.demo.lecturer.model.MajorLecturers;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "MajorLecturers_Specializations")
@Getter
@Setter
public class MajorLecturers_Specializations {
    @EmbeddedId
    private MajorLecturersSpecializationsId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lecturerId")
    @JoinColumn(name = "LecturerID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorLecturers majorLecturer;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("specializationId")
    @JoinColumn(name = "SpecializationID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Specialization specialization;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    public MajorLecturers_Specializations() {
        this.createdAt = LocalDateTime.now();
    }

    public MajorLecturers_Specializations(MajorLecturers majorLecturer, Specialization specialization) {
        this.id = new MajorLecturersSpecializationsId(majorLecturer.getId(), specialization.getSpecializationId());
        this.majorLecturer = majorLecturer;
        this.specialization = specialization;
        this.createdAt = LocalDateTime.now();
    }
}