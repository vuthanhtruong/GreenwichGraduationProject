package com.example.demo.post.majorAssignmentSubmitSlots.model;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.comment.model.Comments;
import com.example.demo.comment.model.StudentComments;
import com.example.demo.entity.Enums.OtherNotification;
import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.user.employe.model.MajorEmployes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "AssignmentSubmitSlots")
@PrimaryKeyJoinColumn(name = "PostID")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@OnDelete(action = OnDeleteAction.CASCADE)
public class AssignmentSubmitSlots extends ClassPosts {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    private MajorEmployes creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", nullable = false)
    private MajorClasses classEntity;

    @Column(name = "Deadline")
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtherNotification notificationType;

    public AssignmentSubmitSlots() {
        this.notificationType = OtherNotification.MAJOR_ASSIGNMENT_SLOT_CREATED;
    }

    public AssignmentSubmitSlots(
            String postId,
            MajorEmployes creator,
            MajorClasses classEntity,
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
        return "Major Assignment Submit Slot";
    }

    @Override
    public String getCreatorName() {
        return creator != null ? creator.getFullName() : "Unknown User";
    }

    @Override
    public String getCreatorAvatar() {
        if (creator == null) return getDefaultAvatarPath();

        // Nếu nhân viên Major có avatar
        if (creator.getAvatar() != null)
            return "/persons/avatar/" + creator.getId();

        // fallback theo gender
        return creator.getDefaultAvatarPath();
    }

    @Override
    public String getDefaultAvatarPath() {
        // Default theo style của Major Lecturer / Teacher
        return "/DefaultAvatar/Teacher_Boy.png";
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
