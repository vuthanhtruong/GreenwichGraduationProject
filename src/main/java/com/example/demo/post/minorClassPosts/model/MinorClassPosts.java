package com.example.demo.post.minorClassPosts.model;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.comment.model.Comments;
import com.example.demo.comment.model.MinorComments;
import com.example.demo.comment.model.StudentComments;
import com.example.demo.entity.Enums.OtherNotification;
import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.user.employe.model.MinorEmployes;
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
@Table(name = "MinorClassPosts")
@PrimaryKeyJoinColumn(name = "PostID")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MinorClassPosts extends ClassPosts {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    private MinorEmployes creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", nullable = false)
    private MinorClasses minorClass;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MinorComments> minorComments;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtherNotification notificationType;

    public MinorClassPosts() {
        this.notificationType = OtherNotification.MINOR_POST_CREATED;
    }

    public MinorClassPosts(String postId, MinorEmployes creator, MinorClasses minorClass,
                           String content, LocalDateTime createdAt, OtherNotification notificationType) {
        super(postId, content, createdAt);
        this.creator = creator;
        this.minorClass = minorClass;
        this.notificationType = notificationType;
    }

    @Override
    public String getCreatorId() {
        return creator != null ? creator.getId() : "Unknown";
    }

    @Override
    public String getClassPostsType() {
        return "Minor Class Post";
    }

    @Override
    public long getTotalComments() {
        return Stream.concat(
                minorComments != null ? minorComments.stream() : Stream.empty(),
                getStudentComments() != null ? getStudentComments().stream() : Stream.empty()
        ).count();
    }

    public List<Comments> getAllCommentsSorted() {
        return Stream.concat(
                        minorComments != null ? minorComments.stream() : Stream.empty(),
                        getStudentComments() != null ? getStudentComments().stream() : Stream.empty()
                )
                .sorted(Comparator.comparing(Comments::getCreatedAt))
                .collect(Collectors.toList());
    }
}
