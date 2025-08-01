package com.example.demo.api.Read;

import com.example.demo.entity.OfflineRooms;
import com.example.demo.entity.OnlineRooms;
import com.example.demo.service.LecturesService;
import com.example.demo.service.RoomsService;
import com.example.demo.service.StaffsService;
import com.example.demo.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/staff-home")
public class ListRoomsRestController {

    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final LecturesService lecturesService;
    private final RoomsService roomsService;

    @Autowired
    public ListRoomsRestController(StaffsService staffsService, LecturesService lecturesService, StudentsService studentsService, RoomsService roomsService) {
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.lecturesService = lecturesService;
        this.roomsService = roomsService;
    }

    @GetMapping("/rooms-list")
    public ResponseEntity<?> listRooms(
            HttpSession session,
            @RequestParam(defaultValue = "1") int pageOffline,
            @RequestParam(defaultValue = "1") int pageOnline,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String sortOrder) {
        try {
            // Handle pageSize
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("roomPageSize");
                if (pageSize == null) {
                    pageSize = 5; // Default to 5 if not set
                }
            }
            session.setAttribute("roomPageSize", pageSize);

            // Validate sortOrder
            String validatedSortOrder = sortOrder != null && (sortOrder.equalsIgnoreCase("asc") || sortOrder.equalsIgnoreCase("desc")) ? sortOrder : null;

            // Handle pagination for offline rooms
            long totalOfflineRooms = roomsService.totalOfflineRooms();
            int totalOfflinePages = Math.max(1, (int) Math.ceil((double) totalOfflineRooms / pageSize));
            pageOffline = Math.max(1, Math.min(pageOffline, totalOfflinePages));
            int firstOfflineResult = (pageOffline - 1) * pageSize;

            List<OfflineRooms> offlineRooms = roomsService.getPaginatedOfflineRooms(firstOfflineResult, pageSize, validatedSortOrder);

            // Encode addresses for offline rooms
            List<String> encodedAddresses = offlineRooms.stream()
                    .map(room -> encodeAddress(room.getAddress()))
                    .collect(Collectors.toList());

            // Handle pagination for online rooms
            long totalOnlineRooms = roomsService.totalOnlineRooms();
            int totalOnlinePages = Math.max(1, (int) Math.ceil((double) totalOnlineRooms / pageSize));
            pageOnline = Math.max(1, Math.min(pageOnline, totalOnlinePages));
            int firstOnlineResult = (pageOnline - 1) * pageSize;

            List<OnlineRooms> onlineRooms = roomsService.getPaginatedOnlineRooms(firstOnlineResult, pageSize, validatedSortOrder);

            // Prepare JSON response
            Map<String, Object> response = new HashMap<>();
            response.put("offlineRooms", offlineRooms);
            response.put("encodedAddresses", encodedAddresses);
            response.put("onlineRooms", onlineRooms);
            response.put("currentPageOffline", pageOffline);
            response.put("totalPagesOffline", totalOfflinePages);
            response.put("currentPageOnline", pageOnline);
            response.put("totalPagesOnline", totalOnlinePages);
            response.put("pageSize", pageSize);
            response.put("sortOrder", validatedSortOrder);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    private String encodeAddress(String address) {
        if (address == null || address.isEmpty()) {
            return "";
        }
        try {
            return URLEncoder.encode(address, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            return address; // Fallback to raw address if encoding fails
        }
    }
}