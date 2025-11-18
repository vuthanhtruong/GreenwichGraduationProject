package com.example.demo.entity.AbstractClasses;

import com.example.demo.comment.model.PublicComments;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PublicPosts")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class PublicPosts {

    @Id
    @Column(name = "PostID")
    private String postId;

    @Column(name = "Title", nullable = true, length = 255)
    private String title;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    // === NEW: One-to-Many with PublicComments ===
    @OneToMany(mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<PublicComments> comments = new ArrayList<>();

    public PublicPosts() {}

    public PublicPosts(String postId, String title, LocalDateTime createdAt) {
        this.postId = postId;
        this.title = title;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    // === Helper: Add comment with bidirectional sync ===
    public void addComment(PublicComments comment) {
        comments.add(comment);
        comment.setPost(this);
    }

    // === Helper: Remove comment ===
    public void removeComment(PublicComments comment) {
        comments.remove(comment);
        comment.setPost(null);
    }
}