package com.example.demo.api.Read;

import com.example.demo.service.StaffsService;
import com.example.demo.service.MajorSubjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/api/staff-home")
@PreAuthorize("hasRole('STAFF')")
public class ListSubjectRestController {

    private final MajorSubjectsService subjectsService;
    private final StaffsService staffsService;

    @Autowired
    public ListSubjectRestController(MajorSubjectsService subjectsService, StaffsService staffsService) {
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;
    }

    @GetMapping("/major-subjects-list")
    public ResponseEntity<?> getSubjectsList() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("subjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }
}