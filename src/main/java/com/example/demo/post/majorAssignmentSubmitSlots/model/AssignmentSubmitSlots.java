package com.example.demo.post.majorAssignmentSubmitSlots.model;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "AssignmentSubmitSlots")
@PrimaryKeyJoinColumn(name = "PostID")
@Getter
@Setter
public class AssignmentSubmitSlots extends ClassPosts {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorLecturers creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorClasses classEntity;

    @Column(name = "Content", nullable = true, length = 1000)
    private String content;

    @Column(name = "Deadline", nullable = false)
    private LocalDateTime deadline;
}