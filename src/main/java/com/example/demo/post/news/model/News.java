package com.example.demo.post.news.model;

import com.example.demo.document.model.Documents;
import com.example.demo.post.Blog.model.PublicPosts;
import com.example.demo.user.staff.model.Staffs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "News")
@PrimaryKeyJoinColumn(name = "PostID")
@Getter
@Setter
public class News extends PublicPosts {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    @Column(name = "Content", nullable = true, length = 1000)
    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Documents> documents = new ArrayList<>();
}