package com.example.demo.comment.model;

import com.example.demo.entity.Enums.OtherNotification;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 50)
    private OtherNotification notificationType;

    public MajorComments() {
        this.notificationType = OtherNotification.COMMENT_MADE_ON_MAJOR_POST;
    }

    @Override
    public String getCommenterAvatar() {

        if (commenter == null)
            return getDefaultAvatarPath();

        // Nếu lecturer có avatar custom trong DB
        if (commenter.getAvatar() != null)
            return "/persons/avatar/" + commenter.getId();

        // Dùng default avatar theo giới tính
        return commenter.getDefaultAvatarPath();
    }

    @Override
    public String getDefaultAvatarPath() {
        // fallback khi không tìm được avatar từ entity
        return "/DefaultAvatar/Teacher_Boy.png";
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
