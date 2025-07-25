package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "Syllabuses")
@Getter
@Setter
public class Syllabuses {

    @Id
    @Column(name = "SyllabusID")
    private String syllabusId;

    @Column(name = "SyllabusName", nullable = false, length = 255)
    private String syllabusName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SubjectID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Subjects subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    @Column(name = "FilePath", nullable = true, length = 500)
    private String filePath;

    @Lob
    @Basic(fetch = FetchType.LAZY) // Lazy loading cho file lớn
    @Column(name = "FileData", nullable = true, columnDefinition = "LONGBLOB")
    private byte[] fileData;

    @Column(name = "Status", nullable = true, length = 50)
    private String status;
}