package com.example.demo.classPost.model;

import com.example.demo.employe.model.MajorEmployes;
import com.example.demo.specializedClasses.model.SpecializedClasses;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

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

    public SpecializedClassPosts() {
    }
}