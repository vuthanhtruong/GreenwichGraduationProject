package com.example.demo.students_Classes.students_MajorClass.model;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.students_Classes.abstractStudents_Class.model.Students_Classes;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "Students_MajorClasses")
@Getter
@Setter
@PrimaryKeyJoinColumns({
        @PrimaryKeyJoinColumn(name = "StudentID", referencedColumnName = "StudentID"),
        @PrimaryKeyJoinColumn(name = "ClassID",   referencedColumnName = "ClassID")
})
public class Students_MajorClasses extends Students_Classes {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorClasses majorClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AddedBy")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Staffs addedBy;

    public Students_MajorClasses() {}

    public Students_MajorClasses(Students student, MajorClasses majorClass, LocalDateTime createdAt, Staffs addedBy) {
        super(student, majorClass, createdAt);
        this.majorClass = majorClass;
        this.addedBy = addedBy;
    }
}