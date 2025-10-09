package com.example.demo.specializationSyllabus.controller;

import com.example.demo.specializationSyllabus.model.SpecializationSyllabuses;
import com.example.demo.specializationSyllabus.service.SpecializationSyllabusesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/staff-home/specialized-subjects-list/")
public class DownloadSpecializedSyllabusController {

    private final SpecializationSyllabusesService syllabusesService;

    @Autowired
    public DownloadSpecializedSyllabusController(SpecializationSyllabusesService syllabusesService) {
        this.syllabusesService = syllabusesService;
    }

    @PostMapping("/syllabuses-list/download-file")
    public ResponseEntity<byte[]> downloadFile(@RequestParam("syllabusId") String syllabusId) {
        SpecializationSyllabuses syllabus = syllabusesService.getSyllabusById(syllabusId);
        if (syllabus == null || syllabus.getFileData() == null || syllabus.getFileData().length == 0) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(syllabus.getFileType()));
        String fileName = syllabus.getSyllabusName().replaceAll("[^a-zA-Z0-9.-]", "_") + getFileExtension(syllabus.getFileType());
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(fileName)
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(syllabus.getFileData());
    }

    private String getFileExtension(String contentType) {
        switch (contentType) {
            case "application/pdf":
                return ".pdf";
            case "application/msword":
                return ".doc";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                return ".docx";
            case "text/plain":
                return ".txt";
            case "application/vnd.ms-powerpoint":
                return ".ppt";
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
                return ".pptx";
            case "application/zip":
            case "application/x-zip-compressed":
            case "application/octet-stream":
                return ".zip";
            default:
                return "";
        }
    }
}