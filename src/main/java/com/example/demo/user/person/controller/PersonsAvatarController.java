package com.example.demo.user.person.controller;

import com.example.demo.user.person.model.Persons;
import com.example.demo.user.person.service.PersonsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/persons")
public class PersonsAvatarController {

    @Autowired
    private PersonsService personsService;

    @GetMapping("/avatar/{id}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable String id) throws IOException {

        Persons person = personsService.getPersonById(id);

        if (person == null) {
            return ResponseEntity.notFound().build();
        }

        // 1️⃣ Nếu có avatar thật → trả byte[]
        if (person.getAvatar() != null && person.getAvatar().length > 0) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(person.getAvatar());
        }

        // 2️⃣ Nếu không có avatar → trả avatar mặc định theo role
        String defaultPath = person.getDefaultAvatarPath();  // ví dụ "/DefaultAvatar/student.png"

        ClassPathResource resource = new ClassPathResource(defaultPath);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        InputStream inputStream = resource.getInputStream();
        byte[] imageBytes = StreamUtils.copyToByteArray(inputStream);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageBytes);
    }
}
