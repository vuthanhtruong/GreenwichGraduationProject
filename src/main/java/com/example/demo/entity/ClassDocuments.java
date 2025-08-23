package com.example.demo.entity;

import com.example.demo.entity.AbstractClasses.ClassPosts;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "ClassDocuments")
@Getter
@Setter
public class ClassDocuments {

    @Id
    @Column(name = "DocumentID")
    private String documentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PostID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ClassPosts post;

    @Column(name = "DocumentTitle", nullable = true, length = 255)
    private String documentTitle;

    @Column(name = "FilePath", nullable = true)
    private String filePath;

    @Column(name = "FileData", nullable = true)
    private byte[] fileData;

    public ClassDocuments() {}

    public ClassDocuments(String documentId, ClassPosts post, String documentTitle, String filePath, byte[] fileData) {
        this.documentId = documentId;
        this.post = post;
        this.documentTitle = documentTitle;
        this.filePath = filePath;
        this.fileData = fileData;
    }
}