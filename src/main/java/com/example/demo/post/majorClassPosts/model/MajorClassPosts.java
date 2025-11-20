package com.example.demo.post.majorClassPosts.model;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.comment.model.Comments;
import com.example.demo.comment.model.MajorComments;
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
@Table(name = "MajorClassPosts")
@PrimaryKeyJoinColumn(name = "PostID")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MajorClassPosts extends ClassPosts {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    private MajorEmployes creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", nullable = false)
    private MajorClasses majorClass;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MajorComments> majorComments;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtherNotification notificationType;

    public MajorClassPosts() {
        this.notificationType = OtherNotification.MAJOR_POST_CREATED;
    }

    public MajorClassPosts(String postId, MajorEmployes creator, MajorClasses majorClass,
                           String content, LocalDateTime createdAt, OtherNotification notificationType) {
        super(postId, content, createdAt);
        this.creator = creator;
        this.majorClass = majorClass;
        this.notificationType = notificationType;
    }

    @Override
    public String getCreatorId() {
        return creator != null ? creator.getId() : "Unknown";
    }

    @Override
    public String getClassPostsType() {
        return "Major Class Post";
    }

    @Override
    public String getCreatorAvatar() {
        if (creator == null) return getDefaultAvatarPath();
        if (creator.getAvatar() != null)
            return "/persons/avatar/" + creator.getId();
        return creator.getDefaultAvatarPath();
    }
    @Override
    public String getCreatorName() {
        return creator != null ? creator.getFullName() : "Unknown User";
    }

    @Override
    public String getDefaultAvatarPath() {
        return "/DefaultAvatar/Teacher_Boy.png";  // hoặc tùy theo logic
    }

    @Override
    public long getTotalComments() {
        return Stream.concat(
                majorComments != null ? majorComments.stream() : Stream.empty(),
                getStudentComments() != null ? getStudentComments().stream() : Stream.empty()
        ).count();
    }

    public List<Comments> getAllCommentsSorted() {
        return Stream.concat(
                        majorComments != null ? majorComments.stream() : Stream.empty(),
                        getStudentComments() != null ? getStudentComments().stream() : Stream.empty()
                )
                .sorted(Comparator.comparing(Comments::getCreatedAt))
                .collect(Collectors.toList());
    }
}
