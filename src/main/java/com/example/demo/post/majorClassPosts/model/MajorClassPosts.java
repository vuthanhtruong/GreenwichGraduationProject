package com.example.demo.post.majorClassPosts.model;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.comment.model.Comments;
import com.example.demo.comment.model.MajorComments;
import com.example.demo.comment.model.StudentComments;
import com.example.demo.entity.Enums.Notifications;
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
@Table(name = "MajorClassPosts")
@PrimaryKeyJoinColumn(name = "PostID")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@OnDelete(action = OnDeleteAction.CASCADE)
public class MajorClassPosts extends ClassPosts {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorEmployes creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorClasses majorClass;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MajorComments> majorComments;

    public MajorClassPosts() {}

    public MajorClassPosts(String postId, MajorEmployes creator, MajorClasses majorClass,
                           Notifications notification, String content, LocalDateTime createdAt) {
        super(postId, notification, content, createdAt);
        this.creator = creator;
        this.majorClass = majorClass;
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
    public long getTotalComments() {
        List<StudentComments> students = getStudentComments();
        List<MajorComments> majors = majorComments;
        if (students == null && majors == null) return 0;
        return Stream.concat(
                majors != null ? majors.stream() : Stream.empty(),
                students != null ? students.stream() : Stream.empty()
        ).count();
    }

    public List<Comments> getAllCommentsSorted() {
        List<StudentComments> students = getStudentComments();
        List<MajorComments> majors = majorComments;
        return Stream.concat(
                        majors != null ? majors.stream() : Stream.empty(),
                        students != null ? students.stream() : Stream.empty()
                )
                .sorted(Comparator.comparing(Comments::getCreatedAt))
                .collect(Collectors.toList());
    }
}
