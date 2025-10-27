package com.example.demo.comment.model;

import com.example.demo.entity.Enums.Notifications;
import com.example.demo.post.majorClassPosts.model.MajorClassPosts;
import com.example.demo.user.employe.model.MajorEmployes;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "MajorComments")
@PrimaryKeyJoinColumn(name = "CommentID")
@Getter
@Setter
public class MajorComments extends Comments {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CommenterID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorEmployes commenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PostID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorClassPosts post;

    public MajorComments() {}

    public MajorComments(String commentId, MajorEmployes commenter, MajorClassPosts post,
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
