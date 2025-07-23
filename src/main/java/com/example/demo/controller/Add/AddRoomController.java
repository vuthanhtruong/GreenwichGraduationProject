package com.example.demo.controller.Add;

import com.example.demo.entity.OfflineRooms;
import com.example.demo.entity.OnlineRooms;
import com.example.demo.entity.Staffs;
import com.example.demo.service.RoomsService;
import com.example.demo.service.StaffsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
import java.time.LocalDateTime;
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
            ModelMap model,
            Authentication authentication) {
        if (result.hasErrors()) {
            return "AddOfflineRoom";
        }

        try {
            String roomId = generateUniqueRoomId(true); // true for offline
            offlineRoom.setRoomId(roomId);
            offlineRoom.setCreatedAt(LocalDateTime.now());
            // Set creator from authenticated user
            String username = authentication.getName();
            Staffs creator = staffsService.getStaffs();
            offlineRoom.setCreator(creator);
            roomsService.addOfflineRoom(offlineRoom);
            redirectAttributes.addFlashAttribute("successMessage", "Offline room added successfully!");
            return "redirect:/staff-home/rooms-list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error adding offline room: " + e.getMessage());
            return "AddOfflineRoom";
        }
    }

    @GetMapping("/add-online-room")
    public String showAddOnlineRoomForm(ModelMap model) {
        model.addAttribute("onlineRoom", new OnlineRooms());
        return "AddOnlineRoom";
    }

    @PostMapping("/add-online-room")
    public String addOnlineRoom(
            @Valid @ModelAttribute("onlineRoom") OnlineRooms onlineRoom,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            ModelMap model,
            Authentication authentication) {
        if (result.hasErrors()) {
            return "AddOnlineRoom";
        }

        try {
            String roomId = generateUniqueRoomId(false); // false for online
            onlineRoom.setRoomId(roomId);
            onlineRoom.setCreatedAt(LocalDateTime.now());
            // Set creator from authenticated user
            String username = authentication.getName();
            Staffs creator = staffsService.getStaffs();
            onlineRoom.setCreator(creator);
            roomsService.addOnlineRoom(onlineRoom);
            redirectAttributes.addFlashAttribute("successMessage", "Online room added successfully!");
            return "redirect:/staff-home/rooms-list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error adding online room: " + e.getMessage());
            return "AddOnlineRoom";
        }
    }

    private String generateUniqueRoomId(boolean isOffline) {
        SecureRandom random = new SecureRandom();
        LocalDate currentDate = LocalDate.now();
        String datePart = currentDate.format(DateTimeFormatter.ofPattern("yMMdd")); // e.g., 5240724 for 2025-07-24
        String prefix = isOffline ? "GWOFF" : "GWONL";

        String roomId;
        do {
            String randomDigits = String.format("%02d", random.nextInt(100)); // 2-digit random number (00-99)
            roomId = prefix + datePart + randomDigits; // e.g., GWOFF524072412 or GWONL524072412
        } while (roomsService.existsOnlineRoomsById(roomId) || roomsService.existsOfflineRoomsById(roomId)); // Check both offline and online rooms
        return roomId;
    }
}