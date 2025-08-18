package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "Student_ParentAccounts")
@Getter
@Setter
public class Student_ParentAccounts {

    @EmbeddedId
    private StudentParentAccountsId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("studentId")
    @JoinColumn(name = "StudentID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students student;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("parentId")
    @JoinColumn(name = "ParentID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ParentAccounts parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AddedBy", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs addedBy;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "RelationshipToStudent", nullable = false, length = 50)
    private String relationshipToStudent;

    public Student_ParentAccounts() {}

    public Student_ParentAccounts(Students student, ParentAccounts parent, Staffs addedBy, LocalDateTime createdAt, String relationshipToStudent) {
        this.id = new StudentParentAccountsId(student.getId(), parent.getId());
        this.student = student;
        this.parent = parent;
        this.addedBy = addedBy;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.relationshipToStudent = relationshipToStudent;
    }
}