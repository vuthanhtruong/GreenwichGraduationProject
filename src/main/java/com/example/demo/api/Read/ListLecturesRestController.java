package com.example.demo.api.Read;

import com.example.demo.entity.Lecturers;
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
public class ListLecturesRestController {
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final LecturesService lecturesService;

    public ListLecturesRestController(StaffsService staffsService, LecturesService lecturesService, StudentsService studentsService) {
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.lecturesService = lecturesService;
    }

    @GetMapping("/lectures-list")
    public ResponseEntity<?> listLecturers(
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

            Long totalLecturers = lecturesService.numberOfLecturers();

            if (totalLecturers == 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("lecturers", new ArrayList<>());
                response.put("currentPage", 1);
                response.put("totalPages", 1);
                response.put("pageSize", pageSize);
                return ResponseEntity.ok(response);
            }

            int totalPages = (int) Math.ceil((double) totalLecturers / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;

            int firstResult = (page - 1) * pageSize;

            List<Lecturers> lecturers = lecturesService.getPaginatedLecturers(firstResult, pageSize);

            Map<String, Object> response = new HashMap<>();
            response.put("lecturers", lecturers);
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

    @GetMapping("/lectures-list/avatar/{id}")
    public ResponseEntity<byte[]> getLectureAvatar(@PathVariable String id) {
        Lecturers lecturer = lecturesService.getLecturerById(id);
        if (lecturer != null && lecturer.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // Adjust based on your avatar format (JPEG, PNG, etc.)
                    .body(lecturer.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}