package com.example.demo.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "Documents")
@Getter
@Setter
public class Documents {

    @Id
    @Column(name = "DocumentID")
    private String documentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Persons creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PostID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Posts post;

    @Column(name = "DocumentTitle", nullable = true, length = 255)
    private String documentTitle;

    @Column(name = "FilePath", nullable = true, length = 500)
    private String filePath;

    @Lob
    @Column(name = "FileData", nullable = true)
    private byte[] fileData;
}
