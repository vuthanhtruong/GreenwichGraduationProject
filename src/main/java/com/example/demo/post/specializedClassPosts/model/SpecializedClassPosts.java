package com.example.demo.post.specializedClassPosts.model;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.comment.model.Comments;
import com.example.demo.comment.model.SpecializedComments;
import com.example.demo.comment.model.StudentComments;
import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.user.employe.model.MajorEmployes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@PrimaryKeyJoinColumn(name = "PostID")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@OnDelete(action = OnDeleteAction.CASCADE)
public class SpecializedClassPosts extends ClassPosts {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SpecializedClasses specializedClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatorID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorEmployes creator;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpecializedComments> specializedComments;

    @Override
    public String getCreatorId() {
        return creator != null ? creator.getId() : "Unknown";
    }

    @Override
    public String getClassPostsType() {
        return "Specialized Class Post";
    }

    @Override
    public long getTotalComments() {
        List<StudentComments> students = getStudentComments();
        List<SpecializedComments> specs = specializedComments;
        if (students == null && specs == null) return 0;
        return Stream.concat(
                specs != null ? specs.stream() : Stream.empty(),
                students != null ? students.stream() : Stream.empty()
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
