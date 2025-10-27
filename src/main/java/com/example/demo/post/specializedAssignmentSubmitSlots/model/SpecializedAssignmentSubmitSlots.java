package com.example.demo.post.specializedAssignmentSubmitSlots.model;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
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
@Table(name = "SpecializedAssignmentSubmitSlots")
@PrimaryKeyJoinColumn(name = "PostID")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@OnDelete(action = OnDeleteAction.CASCADE)
public class SpecializedAssignmentSubmitSlots extends ClassPosts {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorEmployes creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SpecializedClasses classEntity;

    @Column(name = "Deadline")
    private LocalDateTime deadline;

    public SpecializedAssignmentSubmitSlots() {}

    public SpecializedAssignmentSubmitSlots(
            String postId,
            MajorEmployes creator,
            SpecializedClasses classEntity,
            String content,
            LocalDateTime deadline,
            LocalDateTime createdAt) {
        super(postId, null, content, createdAt);
        this.creator = creator;
        this.classEntity = classEntity;
        this.deadline = deadline;
    }

    // ✅ Override phương thức trừu tượng thay cho instanceof
    @Override
    public String getCreatorId() {
        return creator != null ? creator.getId() : "Unknown";
    }

    @Override
    public String getClassPostsType() {
        return "Specialized Assignment Submit Slot";
    }

    @Override
    public long getTotalComments() {
        List<StudentComments> studentComments = getStudentComments();
        return studentComments != null ? studentComments.size() : 0;
    }

    // ✅ Kết hợp và sắp xếp comment
    public List<Comments> getAllCommentsSorted() {
        List<StudentComments> studentComments = getStudentComments();
        return (studentComments != null ? studentComments.stream() : Stream.<StudentComments>empty())
                .sorted(Comparator.comparing(Comments::getCreatedAt))
                .collect(Collectors.toList());
    }
}
