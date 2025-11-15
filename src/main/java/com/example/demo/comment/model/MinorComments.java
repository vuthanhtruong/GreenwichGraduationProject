package com.example.demo.comment.model;

import com.example.demo.entity.Enums.Notifications;
import com.example.demo.entity.Enums.OtherNotification;
import com.example.demo.post.minorClassPosts.model.MinorClassPosts;
import com.example.demo.user.employe.model.MinorEmployes;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "MinorComments")
@PrimaryKeyJoinColumn(name = "CommentID")
@Getter
@Setter
public class MinorComments extends Comments {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CommenterID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorEmployes commenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PostID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorClassPosts post;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtherNotification notificationType;

    public MinorComments() {
        this.notificationType = OtherNotification.COMMENT_MADE_ON_MINOR_POST;
    }

    public MinorComments(String commentId, MinorEmployes commenter, MinorClassPosts post,
                         Notifications notification, String content, LocalDateTime createdAt) {
        super(commentId, notification, content, createdAt);
        this.commenter = commenter;
        this.post = post;
    }

    @Override
    public String getCommenterId() { return commenter != null ? commenter.getId() : null; }

    @Override
    public String getCommenterName() { return commenter != null ? commenter.getFullName() : null; }

    @Override
    public Object getCommenterEntity() { return commenter; }

    @Override
    public String getPostId() { return post != null ? post.getPostId() : null; }

    @Override
    public Object getPostEntity() { return post; }
}
