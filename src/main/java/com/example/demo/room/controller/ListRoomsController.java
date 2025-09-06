package com.example.demo.room.controller;

import com.example.demo.room.model.OfflineRooms;
import com.example.demo.room.model.OnlineRooms;
import com.example.demo.lecturer.service.LecturesService;
import com.example.demo.room.service.RoomsService;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/staff-home")
public class ListRoomsController {

    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final LecturesService lecturesService;
    private final RoomsService roomsService;

    @Autowired
    public ListRoomsController(StaffsService staffsService, LecturesService lecturesService, StudentsService studentsService, RoomsService roomsService) {
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.lecturesService = lecturesService;
        this.roomsService = roomsService;
    }

    @GetMapping("/rooms-list")
    public String roomsList(
            ModelMap model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int pageOffline,
            @RequestParam(defaultValue = "1") int pageOnline,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String sortOrder
    ) {
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
                .map(room -> room.getAddress() != null ? URLEncoder.encode(room.getAddress(), StandardCharsets.UTF_8) : "")
                .collect(Collectors.toList());

        // Handle pagination for online rooms
        long totalOnlineRooms = roomsService.totalOnlineRooms();
        int totalOnlinePages = Math.max(1, (int) Math.ceil((double) totalOnlineRooms / pageSize));
        pageOnline = Math.max(1, Math.min(pageOnline, totalOnlinePages));
        int firstOnlineResult = (pageOnline - 1) * pageSize;

        List<OnlineRooms> onlineRooms = roomsService.getPaginatedOnlineRooms(firstOnlineResult, pageSize, validatedSortOrder);

        // Add data to the model
        model.addAttribute("rooms", offlineRooms);
        model.addAttribute("encodedAddresses", encodedAddresses);
        model.addAttribute("roomsonline", onlineRooms);
        model.addAttribute("currentPageOffline", pageOffline);
        model.addAttribute("totalPagesOffline", totalOfflinePages);
        model.addAttribute("currentPageOnline", pageOnline);
        model.addAttribute("totalPagesOnline", totalOnlinePages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortOrder", validatedSortOrder);
        model.addAttribute("offlineRoom", new OfflineRooms());
        model.addAttribute("onlineRoom", new OnlineRooms());

        return "RoomsList";
    }
}