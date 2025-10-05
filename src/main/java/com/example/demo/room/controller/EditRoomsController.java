package com.example.demo.room.controller;

import com.example.demo.room.model.OfflineRooms;
import com.example.demo.room.model.OnlineRooms;
import com.example.demo.room.service.RoomsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        try {
            OnlineRooms room = (OnlineRooms) roomsService.getRoomById(id);
            if (room == null) {
                model.addAttribute("error", "Online room with ID " + id + " not found");
                model.addAttribute("onlineRoom", new OnlineRooms());
                model.addAttribute("offlineRoom", new OfflineRooms());
                model.addAttribute("onlineRooms", roomsService.getOnlineRooms());
                model.addAttribute("offlineRooms", roomsService.getOfflineRooms());
                return "ListRooms";
            }
            model.addAttribute("onlineRoom", room);
            return "EditFormOnlineRoom";
        } catch (Exception e) {
            logger.error("Error retrieving online room with ID {}: {}", id, e.getMessage());
            model.addAttribute("error", "Error retrieving online room: " + e.getMessage());
            model.addAttribute("onlineRoom", new OnlineRooms());
            model.addAttribute("offlineRoom", new OfflineRooms());
            model.addAttribute("onlineRooms", roomsService.getOnlineRooms());
            model.addAttribute("offlineRooms", roomsService.getOfflineRooms());
            return "ListRooms";
        }
    }

    @PostMapping("/edit-offline-room-form")
    public String showEditOfflineRoomForm(@RequestParam String id, Model model) {
        try {
            OfflineRooms room = (OfflineRooms) roomsService.getRoomById(id);
            if (room == null) {
                model.addAttribute("error", "Offline room with ID " + id + " not found");
                model.addAttribute("onlineRoom", new OnlineRooms());
                model.addAttribute("offlineRoom", new OfflineRooms());
                model.addAttribute("onlineRooms", roomsService.getOnlineRooms());
                model.addAttribute("offlineRooms", roomsService.getOfflineRooms());
                return "ListRooms";
            }
            model.addAttribute("offlineRoom", room);
            return "EditFormOfflineRoom";
        } catch (Exception e) {
            logger.error("Error retrieving offline room with ID {}: {}", id, e.getMessage());
            model.addAttribute("error", "Error retrieving offline room: " + e.getMessage());
            model.addAttribute("onlineRoom", new OnlineRooms());
            model.addAttribute("offlineRoom", new OfflineRooms());
            model.addAttribute("onlineRooms", roomsService.getOnlineRooms());
            model.addAttribute("offlineRooms", roomsService.getOfflineRooms());
            return "ListRooms";
        }
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
            model.addAttribute("offlineRoom", new OfflineRooms());
            model.addAttribute("onlineRooms", roomsService.getOnlineRooms());
            model.addAttribute("offlineRooms", roomsService.getOfflineRooms());
            return "ListRooms";
        }
        try {
            roomsService.updateOnlineRoom(onlineRoom.getRoomId(), onlineRoom, avatarFile);
            redirectAttributes.addFlashAttribute("message", "Online room updated successfully!");
            return "redirect:/admin-home/rooms-list";
        } catch (IOException e) {
            logger.error("IO error updating online room: {}", e.getMessage());
            model.addAttribute("errors", Map.of("avatarFile", "Failed to process avatar: " + e.getMessage()));
            model.addAttribute("onlineRoom", onlineRoom);
            model.addAttribute("offlineRoom", new OfflineRooms());
            model.addAttribute("onlineRooms", roomsService.getOnlineRooms());
            model.addAttribute("offlineRooms", roomsService.getOfflineRooms());
            return "ListRooms";
        } catch (Exception e) {
            logger.error("Error updating online room: {}", e.getMessage());
            model.addAttribute("errors", Map.of("general", "Error updating online room: " + e.getMessage()));
            model.addAttribute("onlineRoom", onlineRoom);
            model.addAttribute("offlineRoom", new OfflineRooms());
            model.addAttribute("onlineRooms", roomsService.getOnlineRooms());
            model.addAttribute("offlineRooms", roomsService.getOfflineRooms());
            return "ListRooms";
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
            model.addAttribute("onlineRoom", new OnlineRooms());
            model.addAttribute("offlineRoom", offlineRoom);
            model.addAttribute("onlineRooms", roomsService.getOnlineRooms());
            model.addAttribute("offlineRooms", roomsService.getOfflineRooms());
            return "ListRooms";
        }
        try {
            roomsService.updateOfflineRoom(offlineRoom.getRoomId(), offlineRoom, avatarFile);
            redirectAttributes.addFlashAttribute("message", "Offline room updated successfully!");
            return "redirect:/admin-home/rooms-list";
        } catch (IOException e) {
            logger.error("IO error updating offline room: {}", e.getMessage());
            model.addAttribute("errors", Map.of("avatarFile", "Failed to process avatar: " + e.getMessage()));
            model.addAttribute("onlineRoom", new OnlineRooms());
            model.addAttribute("offlineRoom", offlineRoom);
            model.addAttribute("onlineRooms", roomsService.getOnlineRooms());
            model.addAttribute("offlineRooms", roomsService.getOfflineRooms());
            return "ListRooms";
        } catch (Exception e) {
            logger.error("Error updating offline room: {}", e.getMessage());
            model.addAttribute("errors", Map.of("general", "Error updating offline room: " + e.getMessage()));
            model.addAttribute("onlineRoom", new OnlineRooms());
            model.addAttribute("offlineRoom", offlineRoom);
            model.addAttribute("onlineRooms", roomsService.getOnlineRooms());
            model.addAttribute("offlineRooms", roomsService.getOfflineRooms());
            return "ListRooms";
        }
    }
}