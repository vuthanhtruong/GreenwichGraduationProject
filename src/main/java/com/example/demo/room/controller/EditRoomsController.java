package com.example.demo.room.controller;

import com.example.demo.room.model.OfflineRooms;
import com.example.demo.room.model.OnlineRooms;
import com.example.demo.room.service.RoomsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/admin-home/rooms-list")
public class EditRoomsController {

    private static final Logger logger = LoggerFactory.getLogger(EditRoomsController.class);
    private final RoomsService roomsService;

    public EditRoomsController(RoomsService roomsService) {
        this.roomsService = roomsService;
    }

    @PostMapping("/edit-online-room-form")
    public String showEditOnlineRoomForm(@RequestParam String id, Model model) {
        OnlineRooms room = (OnlineRooms) roomsService.getRoomById(id);
        if (room == null) {
            model.addAttribute("error", "Online room with ID " + id + " not found");
            return "redirect:/admin-home/rooms-list";
        }
        model.addAttribute("onlineRoom", room);
        return "EditFormOnlineRoom";
    }

    @PostMapping("/edit-offline-room-form")
    public String showEditOfflineRoomForm(@RequestParam String id, Model model) {
        OfflineRooms room = (OfflineRooms) roomsService.getRoomById(id);
        if (room == null) {
            model.addAttribute("error", "Offline room with ID " + id + " not found");
            return "redirect:/admin-home/rooms-list";
        }
        model.addAttribute("offlineRoom", room);
        return "EditFormOfflineRoom";
    }

    @PostMapping("/edit-online-room")
    public String editOnlineRoom(
            @ModelAttribute("onlineRoom") OnlineRooms onlineRoom,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam(value = "link", required = false) String link,
            RedirectAttributes redirectAttributes,
            Model model) {

        Map<String, String> errors = roomsService.validateOnlineRoom(onlineRoom, link, avatarFile);

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("onlineRoom", onlineRoom);
            return "EditFormOnlineRoom"; // TRẢ VỀ FORM EDIT ĐỂ HIỂN THỊ LỖI
        }

        try {
            roomsService.updateOnlineRoom(onlineRoom.getRoomId(), onlineRoom, avatarFile);
            redirectAttributes.addFlashAttribute("message", "Online room updated successfully!");
            return "redirect:/admin-home/rooms-list";
        } catch (IOException e) {
            logger.error("IO error updating online room: {}", e.getMessage());
            model.addAttribute("errors", Map.of("avatarFile", "Failed to upload avatar: " + e.getMessage()));
            model.addAttribute("onlineRoom", onlineRoom);
            return "EditFormOnlineRoom";
        } catch (Exception e) {
            logger.error("Error updating online room: {}", e.getMessage());
            model.addAttribute("errors", Map.of("general", "Update failed: " + e.getMessage()));
            model.addAttribute("onlineRoom", onlineRoom);
            return "EditFormOnlineRoom";
        }
    }

    @PostMapping("/edit-offline-room")
    public String editOfflineRoom(
            @ModelAttribute("offlineRoom") OfflineRooms offlineRoom,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam(value = "address", required = false) String address,
            RedirectAttributes redirectAttributes,
            Model model) {

        Map<String, String> errors = roomsService.validateOfflineRoom(offlineRoom, address, avatarFile);

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("offlineRoom", offlineRoom);
            return "EditFormOfflineRoom"; // TRẢ VỀ FORM EDIT
        }

        try {
            roomsService.updateOfflineRoom(offlineRoom.getRoomId(), offlineRoom, avatarFile);
            redirectAttributes.addFlashAttribute("message", "Offline room updated successfully!");
            return "redirect:/admin-home/rooms-list";
        } catch (IOException e) {
            logger.error("IO error updating offline room: {}", e.getMessage());
            model.addAttribute("errors", Map.of("avatarFile", "Failed to upload avatar: " + e.getMessage()));
            model.addAttribute("offlineRoom", offlineRoom);
            return "EditFormOfflineRoom";
        } catch (Exception e) {
            logger.error("Error updating offline room: {}", e.getMessage());
            model.addAttribute("errors", Map.of("general", "Update failed: " + e.getMessage()));
            model.addAttribute("offlineRoom", offlineRoom);
            return "EditFormOfflineRoom";
        }
    }
}