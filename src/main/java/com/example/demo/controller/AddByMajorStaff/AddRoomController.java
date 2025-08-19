package com.example.demo.controller.AddByMajorStaff;

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

import java.time.LocalDateTime;
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
        List<String> errors = roomsService.validateOfflineRoom(offlineRoom, offlineRoom.getAddress(), null);
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
                model.addAttribute("errorMessage", "Authenticated staff not found.");
                return "AddOfflineRoom";
            }
            offlineRoom.setCreator(creator);
            roomsService.addOfflineRoom(offlineRoom);
            redirectAttributes.addFlashAttribute("message", "Offline room added successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
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
        List<String> errors = roomsService.validateOnlineRoom(onlineRoom, onlineRoom.getLink(), null);
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
                model.addAttribute("errorMessage", "Authenticated staff not found.");
                return "AddOnlineRoom";
            }
            onlineRoom.setCreator(creator);
            roomsService.addOnlineRoom(onlineRoom);
            redirectAttributes.addFlashAttribute("message", "Online room added successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/staff-home/rooms-list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error adding online room: " + e.getMessage());
            return "AddOnlineRoom";
        }
    }
}