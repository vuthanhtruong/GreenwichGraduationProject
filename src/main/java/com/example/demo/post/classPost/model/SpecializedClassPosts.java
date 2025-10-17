package com.example.demo.post.classPost.model;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.user.employe.model.MajorEmployes;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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