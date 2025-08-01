package com.example.demo.api.Add;

import com.example.demo.entity.Classes;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff-home/classes-list")
@PreAuthorize("hasRole('STAFF')")
public class AddLecturesToClassRestController {

    private final Students_ClassesService studentsClassesService;
    private final Lecturers_ClassesService lecturersClassesService;
    private final ClassesService classesService;
    private final StudentsService studentsService;
    private final LecturesService lecturersService;
    private final PersonsService personsService;

    @Autowired
    public AddLecturesToClassRestController(Students_ClassesService studentsClassesService,
                              Lecturers_ClassesService lecturersClassesService,
                              ClassesService classesService,
                              StudentsService studentsService,
                              LecturesService lecturersService,
                              PersonsService personsService) {
        this.studentsClassesService = studentsClassesService;
        this.lecturersClassesService = lecturersClassesService;
        this.classesService = classesService;
        this.studentsService = studentsService;
        this.lecturersService = lecturersService;
        this.personsService = personsService;
    }

    @PostMapping("/add-lecturers-to-class")
    public ResponseEntity<?> addLecturersToClass(
            @RequestParam("classId") String classId,
            @RequestParam(value = "lecturerIds", required = false) List<String> lecturerIds) {

        Classes selectedClass = classesService.getClassById(classId);
        if (selectedClass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Class not found.");
        }

        try {
            lecturersClassesService.addLecturersToClass(selectedClass, lecturerIds);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Lecturers added successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add lecturers: " + e.getMessage());
        }
    }
}