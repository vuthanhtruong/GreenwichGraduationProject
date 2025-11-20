package com.example.demo.post.specializedClassPosts.model;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.comment.model.Comments;
import com.example.demo.comment.model.SpecializedComments;
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
@PrimaryKeyJoinColumn(name = "PostID")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SpecializedClassPosts extends ClassPosts {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID")
    private SpecializedClasses specializedClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatorID")
    private MajorEmployes creator;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpecializedComments> specializedComments;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtherNotification notificationType;

    public SpecializedClassPosts() {
        this.notificationType = OtherNotification.SPECIALIZED_POST_CREATED;
    }

    public SpecializedClassPosts(String postId, MajorEmployes creator,
                                 SpecializedClasses specializedClass,
                                 String content, LocalDateTime createdAt,
                                 OtherNotification notificationType) {

        super(postId, content, createdAt);
        this.creator = creator;
        this.specializedClass = specializedClass;
        this.notificationType = notificationType;
    }

    @Override
    public String getCreatorId() {
        return creator != null ? creator.getId() : "Unknown";
    }

    @Override
    public String getClassPostsType() {
        return "Specialized Class Post";
    }

    @Override
    public String getCreatorAvatar() {
        if (creator == null)
            return getDefaultAvatarPath();

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
        return "/DefaultAvatar/Teacher_Boy.png";
    }

    @Override
    public long getTotalComments() {
        return Stream.concat(
                specializedComments != null ? specializedComments.stream() : Stream.empty(),
                getStudentComments() != null ? getStudentComments().stream() : Stream.empty()
        ).count();
    }

    public List<Comments> getAllCommentsSorted() {
        return Stream.concat(
                        specializedComments != null ? specializedComments.stream() : Stream.empty(),
                        getStudentComments() != null ? getStudentComments().stream() : Stream.empty()
                )
                .sorted(Comparator.comparing(Comments::getCreatedAt))
                .collect(Collectors.toList());
    }
}
