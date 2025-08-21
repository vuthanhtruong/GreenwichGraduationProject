package com.example.demo.room.controller;

import com.example.demo.room.model.OfflineRooms;
import com.example.demo.room.model.OnlineRooms;
import com.example.demo.majorStaff.model.Staffs;
import com.example.demo.room.service.RoomsService;
import com.example.demo.majorStaff.service.StaffsService;
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
        return "AddOfflineRoom";
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
            model.addAttribute("editErrors", errors);
            return "AddOfflineRoom";
        }

        try {
            String roomId = roomsService.generateUniqueRoomId(true); // true for offline
            offlineRoom.setRoomId(roomId);
            offlineRoom.setCreatedAt(LocalDateTime.now());
            Staffs creator = staffsService.getStaff();
            if (creator == null) {
                errors.add("Authenticated staff not found.");
                model.addAttribute("editErrors", errors);
                return "AddOfflineRoom";
            }
            offlineRoom.setCreator(creator);
            roomsService.addOfflineRoom(offlineRoom);
            redirectAttributes.addFlashAttribute("message", "Offline room added successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/staff-home/rooms-list";
        } catch (Exception e) {
            errors.add("Error adding offline room: " + e.getMessage());
            model.addAttribute("editErrors", errors);
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
        List<String> errors = new ArrayList<>(roomsService.validateOnlineRoom(onlineRoom, onlineRoom.getLink()));
        if (result.hasErrors()) {
            result.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        if (!errors.isEmpty()) {
            model.addAttribute("editErrors", errors);
            return "AddOnlineRoom";
        }

        try {
            String roomId = roomsService.generateUniqueRoomId(false); // false for online
            onlineRoom.setRoomId(roomId);
            onlineRoom.setCreatedAt(LocalDateTime.now());
            Staffs creator = staffsService.getStaff();
            if (creator == null) {
                errors.add("Authenticated staff not found.");
                model.addAttribute("editErrors", errors);
                return "AddOnlineRoom";
            }
            onlineRoom.setCreator(creator);
            roomsService.addOnlineRoom(onlineRoom);
            redirectAttributes.addFlashAttribute("message", "Online room added successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/staff-home/rooms-list";
        } catch (Exception e) {
            errors.add("Error adding online room: " + e.getMessage());
            model.addAttribute("editErrors", errors);
            return "AddOnlineRoom";
        }
    }
}