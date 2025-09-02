package com.example.demo.lecturer_class.model;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.staff.model.Staffs;
import com.example.demo.lecturer.model.MajorLecturers;
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
        @PrimaryKeyJoinColumn(name = "ClassID",    referencedColumnName = "ClassID")
})
public class MajorLecturers_MajorClasses extends Lecturers_Classes {

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lecturerId")
    @JoinColumn(name = "LecturerID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorLecturers majorLecturer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorClasses majorClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AddedBy")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Staffs addedBy;

    public MajorLecturers_MajorClasses() {}

    public MajorLecturers_MajorClasses(MajorLecturers majorLecturer, MajorClasses majorClass, LocalDateTime createdAt, Staffs addedBy) {
        super(majorLecturer.getId(), majorClass, createdAt);
        this.majorLecturer = majorLecturer;
        this.majorClass = majorClass;
        this.addedBy = addedBy;
    }
}