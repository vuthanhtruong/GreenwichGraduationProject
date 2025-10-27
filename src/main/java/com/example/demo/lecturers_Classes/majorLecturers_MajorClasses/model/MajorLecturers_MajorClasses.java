package com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.model;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.lecturers_Classes.abstractLecturers_Classes.model.Lecturers_Classes;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.time.LocalDateTime;

@Entity
@Table(name = "MajorLecturers_MajorClasses")
@Getter
@Setter
@PrimaryKeyJoinColumns({
        @PrimaryKeyJoinColumn(name = "LecturerID", referencedColumnName = "LecturerID"),
        @PrimaryKeyJoinColumn(name = "ClassID", referencedColumnName = "ClassID")
})
public class MajorLecturers_MajorClasses extends Lecturers_Classes {

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lecturerId")
    @JoinColumn(name = "LecturerID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorLecturers lecturer;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("classId")
    @JoinColumn(name = "ClassID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorClasses majorClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AddedBy")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Staffs addedBy;

    public MajorLecturers_MajorClasses() {}

    public MajorLecturers_MajorClasses(MajorLecturers lecturer, MajorClasses majorClass, LocalDateTime createdAt, Staffs addedBy) {
        super(lecturer.getId(), majorClass.getClassId(), createdAt);
        this.lecturer = lecturer;
        this.majorClass = majorClass;
        this.addedBy = addedBy;
    }
    @Override
    public String getSession() {
        return majorClass != null ? String.valueOf(majorClass.getSession()) : null;
    }

    @Override
    public Integer getSlotQuantity() {
        return majorClass != null ? majorClass.getSlotQuantity() : null;
    }


    // === Override Abstracts ===
    @Override public String getLecturerId() { return lecturer != null ? lecturer.getId() : null; }
    @Override public String getLecturerName() { return lecturer != null ? lecturer.getFullName() : null; }
    @Override public Object getLecturerEntity() { return lecturer; }
    @Override public String getClassId() { return majorClass != null ? majorClass.getClassId() : null; }
    @Override public String getClassName() { return majorClass != null ? majorClass.getNameClass() : null; }
    @Override public String getSubjectName() { return majorClass != null && majorClass.getSubject() != null ? majorClass.getSubject().getSubjectName() : null; }
    @Override public String getSubjectCode() { return majorClass != null && majorClass.getSubject() != null ? majorClass.getSubject().getSubjectId() : null; }
}
