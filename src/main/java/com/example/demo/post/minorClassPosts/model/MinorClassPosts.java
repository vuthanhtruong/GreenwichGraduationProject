package com.example.demo.post.minorClassPosts.model;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.user.employe.model.MinorEmployes;
import com.example.demo.entity.Enums.Notifications;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "MinorClassPosts")
@PrimaryKeyJoinColumn(name = "PostID")
@Getter
@Setter
public class MinorClassPosts extends ClassPosts {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorEmployes creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorClasses minorClass;

    public MinorClassPosts() {}

    public MinorClassPosts(String postId, MinorEmployes creator, MinorClasses minorClass, Notifications notification, String content, LocalDateTime createdAt) {
        super(postId, notification, content, createdAt);
        this.creator = creator;
        this.minorClass = minorClass;
    }
}