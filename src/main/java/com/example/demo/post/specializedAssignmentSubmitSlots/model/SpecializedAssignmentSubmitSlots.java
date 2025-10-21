package com.example.demo.post.specializedAssignmentSubmitSlots.model;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.comment.model.Comments;
import com.example.demo.comment.model.MajorComments;
import com.example.demo.comment.model.StudentComments;
import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.user.employe.model.MajorEmployes;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
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
@Table(name = "SpecializedAssignmentSubmitSlots")
@PrimaryKeyJoinColumn(name = "PostID")
@Getter
@Setter
public class SpecializedAssignmentSubmitSlots extends ClassPosts {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorEmployes creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SpecializedClasses classEntity;

    @Column(name = "Content", nullable = true, length = 1000)
    private String content;

    @Column(name = "Deadline", nullable = true)
    private LocalDateTime deadline;

    public SpecializedAssignmentSubmitSlots() {}

    public SpecializedAssignmentSubmitSlots(String postId, MajorEmployes creator, SpecializedClasses classEntity, String content, LocalDateTime deadline, LocalDateTime createdAt) {
        super(postId, null, content, createdAt); // Assuming notification is optional
        this.creator = creator;
        this.classEntity = classEntity;
        this.deadline = deadline;
    }
    // New method to combine and sort MajorComments and StudentComments
    public List<Comments> getAllCommentsSorted() {
        // Initialize studentComments (from parent ClassPosts)
        List<StudentComments> studentComments = getStudentComments();
        // Initialize majorComments

        // Combine and sort by createdAt
        return studentComments.stream()

                .sorted(Comparator.comparing(Comments::getCreatedAt))
                .collect(Collectors.toList());
    }
}