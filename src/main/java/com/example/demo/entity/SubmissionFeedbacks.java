package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "SubmissionFeedbacks")
@PrimaryKeyJoinColumn(name = "PostID")
@Getter
@Setter
public class SubmissionFeedbacks extends Posts {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AnnouncerID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Lecturers announcer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SubmissionID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Submissions submission;

    @Column(name = "Content", nullable = true, length = 1000)
    private String content;

    @Column(name = "Grade", nullable = true)
    private Double grade;
}