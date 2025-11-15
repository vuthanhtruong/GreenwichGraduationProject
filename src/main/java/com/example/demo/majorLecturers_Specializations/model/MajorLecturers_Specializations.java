package com.example.demo.majorLecturers_Specializations.model;

import com.example.demo.specialization.model.Specialization;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.entity.Enums.YourNotification;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", length = 50)
    private YourNotification notificationType;

    public MajorLecturers_Specializations() {
        this.createdAt = LocalDateTime.now();
        this.notificationType = YourNotification.NOTIFICATION_001;
    }

    public MajorLecturers_Specializations(MajorLecturers majorLecturer, Specialization specialization) {
        this.id = new MajorLecturersSpecializationsId(majorLecturer.getId(), specialization.getSpecializationId());
        this.majorLecturer = majorLecturer;
        this.specialization = specialization;
        this.createdAt = LocalDateTime.now();
        this.notificationType = YourNotification.NOTIFICATION_001;
    }
}