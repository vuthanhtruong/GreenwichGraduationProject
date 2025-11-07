package com.example.demo.subject.abstractSubject.model;

import com.example.demo.user.admin.model.Admins;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "Subjects")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class Subjects {

    @Id
    @Column(name = "SubjectID")
    private String subjectId;

    @Column(name = "SubjectName", nullable = false, length = 255)
    private String subjectName;

    @Column(name = "Semester")
    private Integer semester;

    @Column(name = "isAccepted", nullable = false)
    private Boolean isAccepted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AcceptorID")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Admins acceptor;

    public abstract String getSubjectType();        // "Major", "Minor", "Specialized"
    public abstract String getSubjectMajor();       // Tên ngành/chuyên ngành
    public abstract String getCreatorName();        // Tên người tạo
}