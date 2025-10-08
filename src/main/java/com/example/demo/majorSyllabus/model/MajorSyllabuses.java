package com.example.demo.majorSyllabus.model;

import com.example.demo.majorSubject.model.MajorSubjects;
import com.example.demo.staff.model.Staffs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "MajorSyllabuses")
@Getter
@Setter
public class MajorSyllabuses {

    @Id
    @Column(name = "SyllabusID")
    private String syllabusId;

    @Column(name = "SyllabusName", nullable = false, length = 255)
    private String syllabusName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SubjectID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorSubjects subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    @Column(name = "file_type")
    private String fileType; // New field to store MIME type

    @Column(name = "FilePath", nullable = true, length = 500)
    private String filePath;

    @Lob
    @Basic(fetch = FetchType.LAZY) // Lazy loading cho file lớn
    @Column(name = "FileData", nullable = true, columnDefinition = "LONGBLOB")
    private byte[] fileData;

    @Column(name = "Status", nullable = true, length = 50)
    private String status;
}