package com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.model;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.lecturers_Classes.abstractLecturers_Classes.model.Lecturers_Classes;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.time.LocalDateTime;

@Entity
@Table(name = "MinorLecturers_MinorClasses")
@Getter
@Setter
@PrimaryKeyJoinColumns({
        @PrimaryKeyJoinColumn(name = "LecturerID", referencedColumnName = "LecturerID"),
        @PrimaryKeyJoinColumn(name = "ClassID", referencedColumnName = "ClassID")
})
public class MinorLecturers_MinorClasses extends Lecturers_Classes {

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lecturerId")
    @JoinColumn(name = "LecturerID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorLecturers lecturer;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("classId")
    @JoinColumn(name = "ClassID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorClasses clazz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AddedBy")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private DeputyStaffs addedBy;

    public MinorLecturers_MinorClasses() {}

    public MinorLecturers_MinorClasses(MinorLecturers lecturer, MinorClasses clazz, LocalDateTime createdAt, DeputyStaffs addedBy) {
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


    @Override public String getLecturerId() { return lecturer != null ? lecturer.getId() : null; }
    @Override public String getLecturerName() { return lecturer != null ? lecturer.getFullName() : null; }
    @Override public Object getLecturerEntity() { return lecturer; }
    @Override public String getClassId() { return clazz != null ? clazz.getClassId() : null; }
    @Override public String getClassName() { return clazz != null ? clazz.getNameClass() : null; }
    @Override public String getSubjectName() { return clazz != null && clazz.getMinorSubject() != null ? clazz.getMinorSubject().getSubjectName() : null; }
    @Override public String getSubjectCode() { return clazz != null && clazz.getMinorSubject() != null ? clazz.getMinorSubject().getSubjectId() : null; }
}
