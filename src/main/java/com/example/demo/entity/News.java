package com.example.demo.entity;

import com.example.demo.entity.AbstractClasses.Posts;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "News")
@PrimaryKeyJoinColumn(name = "PostID")
@Getter
@Setter
public class News extends Posts {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    @Column(name = "Content", nullable = true, length = 1000)
    private String content;
}