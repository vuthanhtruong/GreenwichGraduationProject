package com.example.demo.post.specializedAssignmentSubmitSlots.model;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.comment.model.Comments;
import com.example.demo.comment.model.StudentComments;
import com.example.demo.entity.Enums.OtherNotification;
import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.user.employe.model.MajorEmployes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "SpecializedAssignmentSubmitSlots")
@PrimaryKeyJoinColumn(name = "PostID")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SpecializedAssignmentSubmitSlots extends ClassPosts {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    private MajorEmployes creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", nullable = false)
    private SpecializedClasses classEntity;

    @Column(name = "Deadline")
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtherNotification notificationType;

    public SpecializedAssignmentSubmitSlots() {
        this.notificationType = OtherNotification.SPECIALIZED_ASSIGNMENT_SLOT_CREATED;
    }

    public SpecializedAssignmentSubmitSlots(
            String postId,
            MajorEmployes creator,
            SpecializedClasses classEntity,
            String content,
            LocalDateTime deadline,
            LocalDateTime createdAt,
            OtherNotification notificationType
    ) {
        super(postId, content, createdAt);
        this.creator = creator;
        this.classEntity = classEntity;
        this.deadline = deadline;
        this.notificationType = notificationType;
    }

    @Override
    public String getCreatorId() {
        return creator != null ? creator.getId() : "Unknown";
    }

    @Override
    public String getClassPostsType() {
        return "Specialized Assignment Submit Slot";
    }

    @Override
    public String getCreatorAvatar() {
        if (creator == null) return getDefaultAvatarPath();

        // Nếu MajorEmployes có avatar
        if (creator.getAvatar() != null)
            return "/persons/avatar/" + creator.getId();

        // fallback theo giới tính
        return creator.getDefaultAvatarPath();
    }

    @Override
    public String getDefaultAvatarPath() {
        // Default cho Specialized posts (có thể tùy chỉnh)
        return "/DefaultAvatar/Teacher_Boy.png";
    }
    @Override
    public String getCreatorName() {
        return creator != null ? creator.getFullName() : "Unknown User";
    }


    @Override
    public long getTotalComments() {
        List<StudentComments> list = getStudentComments();
        return list != null ? list.size() : 0;
    }

    public List<Comments> getAllCommentsSorted() {
        List<StudentComments> list = getStudentComments();
        return (list != null ? list.stream() : Stream.<StudentComments>empty())
                .sorted(Comparator.comparing(Comments::getCreatedAt))
                .collect(Collectors.toList());
    }
}
