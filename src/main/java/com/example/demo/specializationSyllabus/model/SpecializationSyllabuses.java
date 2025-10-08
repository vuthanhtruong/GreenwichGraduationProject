package com.example.demo.specializationSyllabus.model;

import com.example.demo.Specialization.model.Specialization;
import com.example.demo.specializedSubject.model.SpecializedSubject;
import com.example.demo.staff.model.Staffs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "SpecializationSyllabuses")
@Getter
@Setter
public class SpecializationSyllabuses {

    @Id
    @Column(name = "SyllabusID")
    private String syllabusId;

    @Column(name = "SyllabusName", nullable = false, length = 255)
    private String syllabusName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SpecializedSubjectID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SpecializedSubject specializedSubject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    @Column(name = "file_type")
    private String fileType; // MIME type of the file

    @Column(name = "FilePath", nullable = true, length = 500)
    private String filePath;

    @Lob
    @Basic(fetch = FetchType.LAZY) // Lazy loading for large files
    @Column(name = "FileData", nullable = true, columnDefinition = "LONGBLOB")
    private byte[] fileData;

    @Column(name = "Status", nullable = true, length = 50)
    private String status;
}