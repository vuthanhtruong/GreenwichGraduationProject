package com.example.demo.post.classPost.model;

import com.example.demo.comment.model.StudentComments;
import com.example.demo.document.model.ClassDocuments;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ClassPosts")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public abstract class ClassPosts {

    @Id
    @Column(name = "PostID", nullable = false, updatable = false)
    private String postId;

    @Column(name = "Content", length = 1000)
    private String content;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClassDocuments> documents;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentComments> studentComments;

    public ClassPosts() {}

    public ClassPosts(String postId, String content, LocalDateTime createdAt) {
        this.postId = postId;
        this.content = content;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    // 🔹 Abstract methods — subclass override thay vì instanceof
    public abstract String getCreatorId();
    public abstract String getClassPostsType();
    public abstract long getTotalComments();
    public abstract String getCreatorAvatar();   // 🔥 HÀM BẮT BUỘC
    public abstract String getDefaultAvatarPath();
    public abstract String getCreatorName();     // tùy chọn nhưng nên có
}
