package com.example.demo.post.minorClassPosts.model;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.comment.model.Comments;
import com.example.demo.comment.model.MinorComments;
import com.example.demo.comment.model.StudentComments;
import com.example.demo.entity.Enums.Notifications;
import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.user.employe.model.MinorEmployes;
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
@Table(name = "MinorClassPosts")
@PrimaryKeyJoinColumn(name = "PostID")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@OnDelete(action = OnDeleteAction.CASCADE)
public class MinorClassPosts extends ClassPosts {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    private MinorEmployes creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", nullable = false)
    private MinorClasses minorClass;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MinorComments> minorComments;

    public MinorClassPosts() {}

    public MinorClassPosts(String postId, MinorEmployes creator, MinorClasses minorClass,
                           Notifications notification, String content, LocalDateTime createdAt) {
        super(postId, notification, content, createdAt);
        this.creator = creator;
        this.minorClass = minorClass;
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
        List<StudentComments> students = getStudentComments();
        List<MinorComments> minors = minorComments;
        if (students == null && minors == null) return 0;
        return Stream.concat(
                minors != null ? minors.stream() : Stream.empty(),
                students != null ? students.stream() : Stream.empty()
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
