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
    private MajorClasses clazz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AddedBy")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Staffs addedBy;

    public MajorLecturers_MajorClasses() {}

    public MajorLecturers_MajorClasses(MajorLecturers lecturer, MajorClasses clazz, LocalDateTime createdAt, Staffs addedBy) {
        super(lecturer.getId(), clazz.getClassId(), createdAt);
        this.lecturer = lecturer;
        this.clazz = clazz;
        this.addedBy = addedBy;
    }
    @Override
    public String getSession() {
        return clazz != null ? String.valueOf(clazz.getSession()) : null;
    }

    @Override
    public Integer getSlotQuantity() {
        return clazz != null ? clazz.getSlotQuantity() : null;
    }


    // === Override Abstracts ===
    @Override public String getLecturerId() { return lecturer != null ? lecturer.getId() : null; }
    @Override public String getLecturerName() { return lecturer != null ? lecturer.getFullName() : null; }
    @Override public Object getLecturerEntity() { return lecturer; }
    @Override public String getClassId() { return clazz != null ? clazz.getClassId() : null; }
    @Override public String getClassName() { return clazz != null ? clazz.getNameClass() : null; }
    @Override public String getSubjectName() { return clazz != null && clazz.getSubject() != null ? clazz.getSubject().getSubjectName() : null; }
    @Override public String getSubjectCode() { return clazz != null && clazz.getSubject() != null ? clazz.getSubject().getSubjectId() : null; }
}
