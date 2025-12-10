package com.example.demo.document.model;
import com.example.demo.post.Blog.model.PublicPosts;
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
    @JoinColumn(name = "PostID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private PublicPosts post;

    @Column(name = "DocumentTitle", nullable = true, length = 255)
    private String documentTitle;

    @Column(name = "FilePath", nullable = true, length = 500)
    private String filePath;

    @Lob
    @Column(name = "FileData", nullable = true,columnDefinition = "LONGBLOB")
    private byte[] fileData;
}