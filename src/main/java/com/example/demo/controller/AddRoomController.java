package com.example.demo.controller;

import com.example.demo.entity.OfflineRooms;
import com.example.demo.entity.Staffs;
import com.example.demo.service.RoomsService;
import com.example.demo.service.StaffsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/staff-home/rooms-list")
public class AddRoomController {

    private final RoomsService roomsService;
    private final StaffsService staffsService;

    @Autowired
    public AddRoomController(RoomsService roomsService, StaffsService staffsService) {
        this.roomsService = roomsService;
        this.staffsService = staffsService;
    }

    @GetMapping("/add-offline-room")
    public String showAddOfflineRoomForm(ModelMap model) {
        model.addAttribute("offlineRoom", new OfflineRooms());
        return "AddOfflineRoom";
    }

    @PostMapping("/add-offline-room")
    public String addOfflineRoom(
            @Valid @ModelAttribute("offlineRoom") OfflineRooms offlineRoom,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            ModelMap model
    ) {
        if (result.hasErrors()) {
            return "AddOfflineRoom";
        }

        try {
            String roomId = generateUniqueRoomId();
            offlineRoom.setRoomId(roomId);
            roomsService.addOfflineRoom(offlineRoom);
            redirectAttributes.addFlashAttribute("successMessage", "Offline room added successfully!");
            return "redirect:/staff-home/rooms-list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error adding offline room: " + e.getMessage());
            return "AddOfflineRoom";
        }
    }
    private String generateUniqueRoomId() {
        SecureRandom random = new SecureRandom();
        LocalDate currentDate = LocalDate.now();
        String datePart = currentDate.format(DateTimeFormatter.ofPattern("yyMMdd")); // e.g., 250127 for 2025-01-27
        String prefix = "GW";

        String roomId;
        do {
            String randomDigits = String.format("%03d", random.nextInt(1000)); // 3-digit random number (000-999)
            roomId = prefix + datePart + randomDigits; // e.g., GW250127123
        } while (roomsService.existsOnlineRoomsById(roomId));
        return roomId;
    }
}