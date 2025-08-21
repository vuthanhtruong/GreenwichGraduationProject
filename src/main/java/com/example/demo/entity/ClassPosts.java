package com.example.demo.entity;

import com.example.demo.classes.model.Classes;
import com.example.demo.person.model.Persons;
import com.example.demo.entity.AbstractClasses.Posts;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "ClassPosts")
@PrimaryKeyJoinColumn(name = "PostID")
@Getter
@Setter
public class ClassPosts extends Posts {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Persons creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Classes classEntity;

    @Column(name = "Content", nullable = true, length = 1000)
    private String content;
}