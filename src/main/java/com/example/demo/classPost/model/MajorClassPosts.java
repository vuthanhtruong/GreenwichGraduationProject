package com.example.demo.classPost.model;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.employe.model.MajorEmployes;
import com.example.demo.entity.Enums.Notifications;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "MajorClassPosts")
@PrimaryKeyJoinColumn(name = "PostID")
@Getter
@Setter
public class MajorClassPosts extends ClassPosts {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorEmployes creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorClasses majorClass;

    public MajorClassPosts() {}

    public MajorClassPosts(String postId, MajorEmployes creator, MajorClasses majorClass, Notifications notification, String content, LocalDateTime createdAt) {
        super(postId, notification, content, createdAt);
        this.creator = creator;
        this.majorClass = majorClass;
    }
}