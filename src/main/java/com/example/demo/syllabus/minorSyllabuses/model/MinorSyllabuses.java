// File: MinorSyllabuses.java
package com.example.demo.syllabus.minorSyllabuses.model;

import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.staff.model.Staffs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "MinorSyllabuses")
@Getter
@Setter
public class MinorSyllabuses {

    @Id
    @Column(name = "SyllabusID")
    private String syllabusId;

    @Column(name = "SyllabusName", nullable = false, length = 255)
    private String syllabusName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SubjectID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorSubjects subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DeputyStaffs creator;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "FilePath", nullable = true, length = 500)
    private String filePath;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "FileData", nullable = true, columnDefinition = "LONGBLOB")
    private byte[] fileData;

    @Column(name = "Status", nullable = true, length = 50)
    private String status;
}