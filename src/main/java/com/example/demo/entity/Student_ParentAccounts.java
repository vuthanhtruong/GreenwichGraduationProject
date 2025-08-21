package com.example.demo.entity;

import com.example.demo.entity.Enums.RelationshipToStudent;
import com.example.demo.majorstaff.model.Staffs;
import com.example.demo.parentAccount.model.ParentAccounts;
import com.example.demo.student.model.Students;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "RelationshipToStudent", nullable = true, length = 50)
    private RelationshipToStudent relationshipToStudent;

    @Column(name = "SupportPhoneNumber", nullable = true, length = 15)
    private String supportPhoneNumber;

    public Student_ParentAccounts() {}

    public Student_ParentAccounts(Students student, ParentAccounts parent, Staffs addedBy, LocalDateTime createdAt, RelationshipToStudent relationshipToStudent, String supportPhoneNumber) {
        this.id = new StudentParentAccountsId(student.getId(), parent.getId());
        this.student = student;
        this.parent = parent;
        this.addedBy = addedBy;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.relationshipToStudent = relationshipToStudent;
        this.supportPhoneNumber = supportPhoneNumber;
    }
}