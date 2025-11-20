package com.example.demo.comment.model;

import com.example.demo.entity.Enums.OtherNotification;
import com.example.demo.post.majorAssignmentSubmitSlots.model.AssignmentSubmitSlots;
import com.example.demo.user.employe.model.MajorEmployes;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "MajorAssignmentComments")
@PrimaryKeyJoinColumn(name = "CommentID")
@Getter
@Setter
public class MajorAssignmentComments extends Comments {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CommenterID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorEmployes commenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PostID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AssignmentSubmitSlots post;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtherNotification notificationType =
            OtherNotification.COMMENT_MADE_ON_MAJOR_ASSIGNMENT;

    public MajorAssignmentComments() {}

    public MajorAssignmentComments(String commentId,
                                   MajorEmployes commenter,
                                   AssignmentSubmitSlots post,
                                   String content,
                                   LocalDateTime createdAt) {

        super(commentId, content, createdAt);
        this.commenter = commenter;
        this.post = post;
    }

    /* ========== COMMENTER INFO ========== */

    @Override
    public String getCommenterId() {
        return commenter != null ? commenter.getId() : null;
    }

    @Override
    public String getCommenterName() {
        return commenter != null ? commenter.getFullName() : null;
    }

    @Override
    public Object getCommenterEntity() {
        return commenter;
    }

    @Override
    public String getCommenterAvatar() {

        if (commenter == null)
            return getDefaultAvatarPath();

        if (commenter.getAvatar() != null)
            return "/persons/avatar/" + commenter.getId();

        return commenter.getDefaultAvatarPath();
    }

    @Override
    public String getDefaultAvatarPath() {
        return "/DefaultAvatar/Teacher_Boy.png";
    }

    /* ========== POST INFO ========== */

    @Override
    public String getPostId() {
        return post != null ? post.getPostId() : null;
    }

    @Override
    public Object getPostEntity() {
        return post;
    }
}
