package com.example.demo.entity;

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
public class MinorClassPosts extends Posts {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Persons creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MinorClassID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorClasses minorClass;

    @Column(name = "Content", nullable = true, length = 1000)
    private String content;

    public MinorClassPosts() {}

    public MinorClassPosts(String postId, Notifications notification, String title, LocalDateTime createdAt, Persons creator, MinorClasses minorClass, String content) {
        super(postId, notification, title, createdAt);
        this.creator = creator;
        this.minorClass = minorClass;
        this.content = content;
    }
}