package com.example.demo.post.majorClassPosts.model;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.comment.model.Comments;
import com.example.demo.comment.model.MajorComments;
import com.example.demo.comment.model.StudentComments;
import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.user.employe.model.MajorEmployes;
import com.example.demo.entity.Enums.Notifications;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "MajorClassPosts")
@PrimaryKeyJoinColumn(name = "PostID")
@Getter
@Setter
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

    public MajorClassPosts(String postId, MajorEmployes creator, MajorClasses majorClass, Notifications notification, String content, LocalDateTime createdAt) {
        super(postId, notification, content, createdAt);
        this.creator = creator;
        this.majorClass = majorClass;
    }

    // Method to safely initialize and return majorComments
    public List<MajorComments> getInitializedMajorComments() {
        if (this.majorComments == null) {
            this.majorComments = new ArrayList<>();
        } else {
            Hibernate.initialize(this.majorComments);
        }
        return this.majorComments;
    }

    // New method to combine and sort MajorComments and StudentComments
    public List<Comments> getAllCommentsSorted() {
        // Initialize studentComments (from parent ClassPosts)
        List<StudentComments> studentComments = getStudentComments();
        // Initialize majorComments
        List<MajorComments> majorComments = getMajorComments();

        // Combine and sort by createdAt
        return Stream.concat(
                        majorComments.stream(),
                        studentComments.stream()
                )
                .sorted(Comparator.comparing(Comments::getCreatedAt))
                .collect(Collectors.toList());
    }
}