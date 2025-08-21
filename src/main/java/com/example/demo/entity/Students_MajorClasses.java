package com.example.demo.entity;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.entity.AbstractClasses.Students_Classes;
import com.example.demo.majorstaff.model.Staffs;
import com.example.demo.student.model.Students;
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