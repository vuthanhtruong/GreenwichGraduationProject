package com.example.demo.post.majorAssignmentSubmitSlots.model;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.comment.model.Comments;
import com.example.demo.comment.model.StudentComments;
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
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorEmployes creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorClasses classEntity;

    @Column(name = "Deadline")
    private LocalDateTime deadline;

    public AssignmentSubmitSlots() {}

    public AssignmentSubmitSlots(
            String postId,
            MajorEmployes creator,
            MajorClasses classEntity,
            String content,
            LocalDateTime deadline,
            LocalDateTime createdAt) {
        super(postId, null, content, createdAt);
        this.creator = creator;
        this.classEntity = classEntity;
        this.deadline = deadline;
    }

    // ✅ Override các phương thức trừu tượng của ClassPosts (không còn instanceof)
    @Override
    public String getCreatorId() {
        return creator != null ? creator.getId() : "Unknown";
    }

    @Override
    public String getClassPostsType() {
        return "Major Assignment Submit Slot";
    }

    @Override
    public long getTotalComments() {
        List<StudentComments> studentComments = getStudentComments();
        return studentComments != null ? studentComments.size() : 0;
    }

    // ✅ Gộp và sắp xếp comment theo thời gian
    public List<Comments> getAllCommentsSorted() {
        List<StudentComments> studentComments = getStudentComments();
        return (studentComments != null ? studentComments.stream() : Stream.<StudentComments>empty())
                .sorted(Comparator.comparing(Comments::getCreatedAt))
                .collect(Collectors.toList());
    }
}
