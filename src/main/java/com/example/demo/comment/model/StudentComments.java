package com.example.demo.comment.model;

import com.example.demo.entity.Enums.OtherNotification;
import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "StudentComments")
@PrimaryKeyJoinColumn(name = "CommentID")
@Getter
@Setter
public class StudentComments extends Comments {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CommenterID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students commenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PostID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ClassPosts post;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtherNotification notificationType;

    public StudentComments() {
        this.notificationType = OtherNotification.STUDENT_COMMENTED_ON_POST;
    }

    public StudentComments(String commentId, Students commenter, ClassPosts post, String content, LocalDateTime createdAt) {
        super(commentId, content, createdAt);
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
