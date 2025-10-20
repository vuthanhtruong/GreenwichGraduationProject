package com.example.demo.comment.model;

import com.example.demo.entity.Enums.Notifications;
import com.example.demo.post.majorAssignmentSubmitSlots.model.AssignmentSubmitSlots;
import com.example.demo.post.majorClassPosts.model.MajorClassPosts;
import com.example.demo.post.minorClassPosts.model.MinorClassPosts;
import com.example.demo.post.specializedAssignmentSubmitSlots.model.SpecializedAssignmentSubmitSlots;
import com.example.demo.post.specializedClassPosts.model.SpecializedClassPosts;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;

@Entity
@Table(name = "Comments")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class Comments {

    @Id
    @Column(name = "CommentID")
    private String commentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "Notification", nullable = true)
    private Notifications notification;

    @Column(name = "Content", nullable = true, length = 1000)
    private String content;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    public String Commenter() {
        Hibernate.initialize(this);
        if (this instanceof MajorComments majorComments) {
            return majorComments.getCommenter().getFullName();
        } else if (this instanceof MinorComments minorComments) {
            return minorComments.getCommenter().getFullName();
        } else if (this instanceof StudentComments studentComments) {
            return studentComments.getCommenter().getFullName();
        }
        return "Unknown";
    }
    public String getContent() {
        Hibernate.initialize(this);
        if (this instanceof MajorComments majorComments) {
            return majorComments.getContent();
        } else if (this instanceof MinorComments minorComments) {
            return minorComments.getContent();
        } else if (this instanceof StudentComments studentComments) {
            return studentComments.getContent();
        }
        return "Unknown";
    }

    public Comments() {}

    public Comments(String commentId, Notifications notification, String content, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.notification = notification;
        this.content = content;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}