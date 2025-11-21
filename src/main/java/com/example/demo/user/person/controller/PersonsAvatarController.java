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
    @ResponseBody
    public ResponseEntity<byte[]> getAvatar(@PathVariable String id) {

        Persons person = personsService.getPersonById(id);

        if (person != null && person.getAvatar() != null && person.getAvatar().length > 0) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)  // hoặc IMAGE_PNG tùy bạn lưu dạng nào
                    .body(person.getAvatar());
        }

        return ResponseEntity.notFound().build();
    }
}
