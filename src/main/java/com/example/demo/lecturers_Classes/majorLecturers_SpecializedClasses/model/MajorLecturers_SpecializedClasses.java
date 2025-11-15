package com.example.demo.lecturers_Classes.majorLecturers_SpecializedClasses.model;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.lecturers_Classes.abstractLecturers_Classes.model.Lecturers_Classes;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.entity.Enums.YourNotification; // THÊM IMPORT

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "MajorLecturers_SpecializedClasses")
@Getter
@Setter
@PrimaryKeyJoinColumns({
        @PrimaryKeyJoinColumn(name = "LecturerID", referencedColumnName = "LecturerID"),
        @PrimaryKeyJoinColumn(name = "ClassID", referencedColumnName = "ClassID")
})
public class MajorLecturers_SpecializedClasses extends Lecturers_Classes {

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lecturerId")
    @JoinColumn(name = "LecturerID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorLecturers lecturer;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("classId")
    @JoinColumn(name = "ClassID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SpecializedClasses specializedClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AddedBy")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Staffs addedBy;

    // THÊM: Loại thông báo
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", length = 50)
    private YourNotification notificationType;

    // CONSTRUCTOR MẶC ĐỊNH – TỰ ĐỘNG SET NOTIFICATION_004
    public MajorLecturers_SpecializedClasses() {
        this.notificationType = YourNotification.NOTIFICATION_004; // "You have been added to a specialization class"
    }

    // Constructor có tham số – VẪN TỰ ĐỘNG GÁN
    public MajorLecturers_SpecializedClasses(MajorLecturers lecturer, SpecializedClasses specializedClass, LocalDateTime createdAt, Staffs addedBy) {
        super(lecturer.getId(), specializedClass.getClassId(), createdAt);
        this.lecturer = lecturer;
        this.specializedClass = specializedClass;
        this.addedBy = addedBy;
        this.notificationType = YourNotification.NOTIFICATION_004; // Vẫn giữ mặc định
    }

    @Override
    public String getSession() {
        return specializedClass != null ? String.valueOf(specializedClass.getSession()) : null;
    }

    @Override
    public Integer getSlotQuantity() {
        return specializedClass != null ? specializedClass.getSlotQuantity() : null;
    }

    // === Override Abstracts ===
    @Override public String getLecturerId() { return lecturer != null ? lecturer.getId() : null; }
    @Override public String getLecturerName() { return lecturer != null ? lecturer.getFullName() : null; }
    @Override public Object getLecturerEntity() { return lecturer; }
    @Override public String getClassId() { return specializedClass != null ? specializedClass.getClassId() : null; }
    @Override public String getClassName() { return specializedClass != null ? specializedClass.getNameClass() : null; }
    @Override public String getSubjectName() {
        return specializedClass != null && specializedClass.getSpecializedSubject() != null
                ? specializedClass.getSpecializedSubject().getSubjectName() : null;
    }
    @Override public String getSubjectCode() {
        return specializedClass != null && specializedClass.getSpecializedSubject() != null
                ? specializedClass.getSpecializedSubject().getSubjectId() : null;
    }
}