package com.example.demo.room.controller;

import com.example.demo.room.model.OfflineRooms;
import com.example.demo.room.model.OnlineRooms;
import com.example.demo.room.model.Rooms;
import com.example.demo.room.service.RoomsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/staff-home/rooms-list")
@PreAuthorize("hasRole('STAFF')")
public class UpdateRoomController {

    private final RoomsService roomsService;

    @Autowired
    public UpdateRoomController(RoomsService roomsService) {
        this.roomsService = roomsService;
    }

    @PostMapping("/edit-offline-room")
    public String showEditOfflineRoomForm(
            @RequestParam("id") String roomId,
            Model model,
            RedirectAttributes redirectAttributes) {
        Rooms room = roomsService.getRoomById(roomId);
        if (room == null || !(room instanceof OfflineRooms)) {
            redirectAttributes.addFlashAttribute("message", "Offline room not found.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/staff-home/rooms-list";
        }

        OfflineRooms offlineRoom = new OfflineRooms();
        offlineRoom.setRoomId(room.getRoomId());
        offlineRoom.setRoomName(room.getRoomName());
        offlineRoom.setCreator(room.getCreator());
        offlineRoom.setCreatedAt(room.getCreatedAt());
        offlineRoom.setAddress(((OfflineRooms) room).getAddress());
        model.addAttribute("room", offlineRoom);
        return "EditOfflineRoomForm";
    }

    @PostMapping("/edit-online-room")
    public String showEditOnlineRoomForm(
            @RequestParam("id") String roomId,
            Model model,
            RedirectAttributes redirectAttributes) {
        Rooms room = roomsService.getRoomById(roomId);
        if (room == null || !(room instanceof OnlineRooms)) {
            redirectAttributes.addFlashAttribute("message", "Online room not found.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/staff-home/rooms-list";
        }

        OnlineRooms onlineRoom = new OnlineRooms();
        onlineRoom.setRoomId(room.getRoomId());
        onlineRoom.setRoomName(room.getRoomName());
        onlineRoom.setCreator(room.getCreator());
        onlineRoom.setCreatedAt(room.getCreatedAt());
        onlineRoom.setLink(((OnlineRooms) room).getLink());
        model.addAttribute("room", onlineRoom);
        return "EditOnlineRoomForm";
    }

    @PostMapping("/update-offline-room")
    public String updateOfflineRoom(
            @Valid @ModelAttribute("room") OfflineRooms formRoom,
            BindingResult bindingResult,
            @RequestParam(value = "address", required = false) String address,
            RedirectAttributes redirectAttributes,
            Model model) {
        Rooms existingRoom = roomsService.getRoomById(formRoom.getRoomId());
        if (existingRoom == null || !(existingRoom instanceof OfflineRooms)) {
            redirectAttributes.addFlashAttribute("message", "Offline room not found.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/staff-home/rooms-list";
        }

        Map<String, String> errors = new HashMap<>(roomsService.validateOfflineRoom(formRoom, address));
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> {
                String field = bindingResult.getFieldError() != null ? bindingResult.getFieldError().getField() : "general";
                errors.put(field, error.getDefaultMessage());
            });
        }

        if (!errors.isEmpty()) {
            model.addAttribute("editErrors", errors);
            model.addAttribute("room", formRoom);
            return "EditOfflineRoomForm";
        }

        try {
            OfflineRooms offlineRoom = (OfflineRooms) existingRoom;
            offlineRoom.setRoomName(formRoom.getRoomName() != null ? formRoom.getRoomName().toUpperCase() : offlineRoom.getRoomName());
            if (address != null && !address.isEmpty()) {
                offlineRoom.setAddress(address);
            }
            roomsService.updateOfflineRoom(offlineRoom.getRoomId(), offlineRoom);
            redirectAttributes.addFlashAttribute("message", "Offline room updated successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            errors.put("general", "Error updating offline room: " + e.getMessage());
            model.addAttribute("editErrors", errors);
            model.addAttribute("room", formRoom);
            return "EditOfflineRoomForm";
        }

        return "redirect:/staff-home/rooms-list";
    }

    @PostMapping("/update-online-room")
    public String updateOnlineRoom(
            @Valid @ModelAttribute("room") OnlineRooms formRoom,
            BindingResult bindingResult,
            @RequestParam(value = "link", required = false) String link,
            RedirectAttributes redirectAttributes,
            Model model) {
        Rooms existingRoom = roomsService.getRoomById(formRoom.getRoomId());
        if (existingRoom == null || !(existingRoom instanceof OnlineRooms)) {
            redirectAttributes.addFlashAttribute("message", "Online room not found.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/staff-home/rooms-list";
        }

        Map<String, String> errors = new HashMap<>(roomsService.validateOnlineRoom(formRoom, link));
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> {
                String field = bindingResult.getFieldError() != null ? bindingResult.getFieldError().getField() : "general";
                errors.put(field, error.getDefaultMessage());
            });
        }

        if (!errors.isEmpty()) {
            model.addAttribute("editErrors", errors);
            model.addAttribute("room", formRoom);
            return "EditOnlineRoomForm";
        }

        try {
            OnlineRooms onlineRoom = (OnlineRooms) existingRoom;
            onlineRoom.setRoomName(formRoom.getRoomName() != null ? formRoom.getRoomName().toUpperCase() : onlineRoom.getRoomName());
            if (link != null && !link.isEmpty()) {
                onlineRoom.setLink(link);
            }
            roomsService.updateOnlineRoom(onlineRoom.getRoomId(), onlineRoom);
            redirectAttributes.addFlashAttribute("message", "Online room updated successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            errors.put("general", "Error updating online room: " + e.getMessage());
            model.addAttribute("editErrors", errors);
            model.addAttribute("room", formRoom);
            return "EditOnlineRoomForm";
        }

        return "redirect:/staff-home/rooms-list";
    }
}