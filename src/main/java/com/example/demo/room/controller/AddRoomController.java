package com.example.demo.room.controller;

import com.example.demo.room.model.OfflineRooms;
import com.example.demo.room.model.OnlineRooms;
import com.example.demo.staff.model.Staffs;
import com.example.demo.room.service.RoomsService;
import com.example.demo.staff.service.StaffsService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        return "RoomsList"; // Return to RoomsList to show overlay
    }

    @PostMapping("/add-offline-room")
    public String addOfflineRoom(
            @Valid @ModelAttribute("offlineRoom") OfflineRooms offlineRoom,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            ModelMap model,
            Authentication authentication) {
        List<String> errors = new ArrayList<>(roomsService.validateOfflineRoom(offlineRoom, offlineRoom.getAddress()));
        if (result.hasErrors()) {
            result.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("editErrors", errors);
            redirectAttributes.addFlashAttribute("offlineRoom", offlineRoom); // Preserve form data
            return "redirect:/staff-home/rooms-list";
        }

        try {
            String roomId = roomsService.generateUniqueRoomId(true); // true for offline
            offlineRoom.setRoomId(roomId);
            offlineRoom.setCreatedAt(LocalDateTime.now());
            Staffs creator = staffsService.getStaff(); // Pass authentication
            if (creator == null) {
                redirectAttributes.addFlashAttribute("editErrors", List.of("Authenticated staff not found."));
                return "redirect:/staff-home/rooms-list";
            }
            offlineRoom.setCreator(creator);
            roomsService.addOfflineRoom(offlineRoom);
            redirectAttributes.addFlashAttribute("message", "Offline room added successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/staff-home/rooms-list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("editErrors", List.of("Error adding offline room: " + e.getMessage()));
            redirectAttributes.addFlashAttribute("offlineRoom", offlineRoom); // Preserve form data
            return "redirect:/staff-home/rooms-list";
        }
    }

    @GetMapping("/add-online-room")
    public String showAddOnlineRoomForm(ModelMap model) {
        model.addAttribute("onlineRoom", new OnlineRooms());
        return "RoomsList"; // Return to RoomsList to show overlay
    }

    @PostMapping("/add-online-room")
    public String addOnlineRoom(
            @Valid @ModelAttribute("onlineRoom") OnlineRooms onlineRoom,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            ModelMap model,
            Authentication authentication) {
        List<String> errors = new ArrayList<>(roomsService.validateOnlineRoom(onlineRoom, onlineRoom.getLink()));
        if (result.hasErrors()) {
            result.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        if (!errors.isEmpty()) {
            model.addAttribute("openAddOverlay", true); // 👈 thêm cờ này
            redirectAttributes.addFlashAttribute("editErrors", errors);
            redirectAttributes.addFlashAttribute("onlineRoom", onlineRoom); // Preserve form data
            return "redirect:/staff-home/rooms-list";
        }

        try {
            String roomId = roomsService.generateUniqueRoomId(false); // false for online
            onlineRoom.setRoomId(roomId);
            onlineRoom.setCreatedAt(LocalDateTime.now());
            Staffs creator = staffsService.getStaff(); // Pass authentication
            if (creator == null) {
                redirectAttributes.addFlashAttribute("editErrors", List.of("Authenticated staff not found."));
                return "redirect:/staff-home/rooms-list";
            }
            onlineRoom.setCreator(creator);
            roomsService.addOnlineRoom(onlineRoom);
            redirectAttributes.addFlashAttribute("message", "Online room added successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/staff-home/rooms-list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("editErrors", List.of("Error adding online room: " + e.getMessage()));
            redirectAttributes.addFlashAttribute("onlineRoom", onlineRoom); // Preserve form data
            return "redirect:/staff-home/rooms-list";
        }
    }
}