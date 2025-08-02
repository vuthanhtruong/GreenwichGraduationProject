package com.example.demo.api.Read;

import com.example.demo.entity.Students;
import com.example.demo.service.LecturesService;
import com.example.demo.service.StaffsService;
import com.example.demo.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staff-home")
public class ListStudentsRestController {
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final LecturesService lecturesService;

    public ListStudentsRestController(StaffsService staffsService, LecturesService lecturesService, StudentsService studentsService) {
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.lecturesService = lecturesService;
    }

    @GetMapping("/students-list")
    public ResponseEntity<?> listStudents(
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            staffsService.getStaffs(); // Check for staff authentication

            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("pageSize");
                if (pageSize == null) {
                    pageSize = 5;
                }
            }
            session.setAttribute("pageSize", pageSize);

            Long totalStudents = studentsService.numberOfStudents();

            if (totalStudents == 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("students", new ArrayList<>());
                response.put("currentPage", 1);
                response.put("totalPages", 1);
                response.put("pageSize", pageSize);
                return ResponseEntity.ok(response);
            }

            int totalPages = (int) Math.ceil((double) totalStudents / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;

            int firstResult = (page - 1) * pageSize;

            List<Students> students = studentsService.getPaginatedStudents(firstResult, pageSize);

            Map<String, Object> response = new HashMap<>();
            response.put("students", students);
            response.put("currentPage", page);
            response.put("totalPages", totalPages);
            response.put("pageSize", pageSize);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Security error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/students-list/avatar/{id}")
    public ResponseEntity<byte[]> getStudentAvatar(@PathVariable String id) {
        Students student = studentsService.getStudentById(id);
        if (student != null && student.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // Adjust based on your avatar format (JPEG, PNG, etc.)
                    .body(student.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}