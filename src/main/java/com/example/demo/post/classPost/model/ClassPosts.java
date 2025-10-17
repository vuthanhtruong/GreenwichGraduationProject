package com.example.demo.post.classPost.model;

import com.example.demo.entity.Enums.Notifications;
import com.example.demo.post.assignmentSubmitSlots.model.AssignmentSubmitSlots;
import com.example.demo.post.majorClassPosts.model.MajorClassPosts;
import com.example.demo.post.minorClassPosts.model.MinorClassPosts;
import com.example.demo.post.specializedClassPosts.model.SpecializedClassPosts;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;

@Entity
@Table(name = "ClassPosts")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class ClassPosts {

    @Id
    @Column(name = "PostID")
    private String postId;

    @Enumerated(EnumType.STRING)
    @Column(name = "Notification", nullable = true)
    private Notifications notification;

    @Column(name = "Content", nullable = true, length = 1000)
    private String content;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    public ClassPosts() {}

    public ClassPosts(String postId, Notifications notification, String content, LocalDateTime createdAt) {
        this.postId = postId;
        this.notification = notification;
        this.content = content;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public String getClassPostsType() {
        Hibernate.initialize(this); // Ensure the proxy is initialized
        if (this instanceof MajorClassPosts) {
            return "Major Class Post";
        } else if (this instanceof AssignmentSubmitSlots) {
            return "Assignment Submit Slot";
        } else if (this instanceof SpecializedClassPosts) {
            return "Specialized Class Post";
        } else if (this instanceof MinorClassPosts) {
            return "Minor Class Post";
        }
        return "Unknown";
    }

    public String getCreatorId() {
        Hibernate.initialize(this); // Ensure the proxy is initialized
        if (this instanceof MajorClassPosts majorClassPosts) {
            Hibernate.initialize(majorClassPosts.getCreator());
            return majorClassPosts.getCreator() != null ? majorClassPosts.getCreator().getId() : "Unknown";
        } else if (this instanceof AssignmentSubmitSlots assignmentSubmitSlots) {
            Hibernate.initialize(assignmentSubmitSlots.getCreator());
            return assignmentSubmitSlots.getCreator() != null ? assignmentSubmitSlots.getCreator().getId() : "Unknown";
        } else if (this instanceof SpecializedClassPosts specializedClassPosts) {
            Hibernate.initialize(specializedClassPosts.getCreator());
            return specializedClassPosts.getCreator() != null ? specializedClassPosts.getCreator().getId() : "Unknown";
        } else if (this instanceof MinorClassPosts minorClassPosts) {
            Hibernate.initialize(minorClassPosts.getCreator());
            return minorClassPosts.getCreator() != null ? minorClassPosts.getCreator().getId() : "Unknown";
        }
        return "Unknown";
    }
}