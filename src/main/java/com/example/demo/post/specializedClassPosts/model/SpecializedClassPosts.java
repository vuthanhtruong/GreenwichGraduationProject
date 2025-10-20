package com.example.demo.post.specializedClassPosts.model;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.comment.model.Comments;
import com.example.demo.comment.model.SpecializedComments;
import com.example.demo.comment.model.StudentComments;
import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.user.employe.model.MajorEmployes;
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

    public SpecializedClassPosts() {
    }

    // Method to combine and sort SpecializedComments and StudentComments
    public List<Comments> getAllCommentsSorted() {
        // Initialize studentComments (from parent ClassPosts)
        List<StudentComments> studentComments = getStudentComments();
        // Initialize specializedComments
        List<SpecializedComments> specializedComments = getSpecializedComments();

        // Combine and sort by createdAt
        return Stream.concat(
                        specializedComments.stream(),
                        studentComments.stream()
                )
                .sorted(Comparator.comparing(Comments::getCreatedAt))
                .collect(Collectors.toList());
    }
}