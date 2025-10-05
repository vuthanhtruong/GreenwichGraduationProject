package com.example.demo.room.controller;

import com.example.demo.room.service.RoomsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin-home/rooms-list")
public class DeleteRoomsController {
    private static final Logger logger = LoggerFactory.getLogger(DeleteRoomsController.class);
    private final RoomsService roomsService;

    public DeleteRoomsController(RoomsService roomsService) {
        this.roomsService = roomsService;
    }

    @PostMapping("/delete-online-room")
    public String deleteOnlineRoom(@RequestParam String id, RedirectAttributes redirectAttributes) {
        try {
            roomsService.deleteOnlineRoom(id);
            redirectAttributes.addFlashAttribute("message", "Online room deleted successfully!");
        } catch (Exception e) {
            logger.error("Error deleting online room: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error deleting online room: " + e.getMessage());
        }
        return "redirect:/admin-home/rooms-list";
    }

    @PostMapping("/delete-offline-room")
    public String deleteOfflineRoom(@RequestParam String id, RedirectAttributes redirectAttributes) {
        try {
            roomsService.deleteOfflineRoom(id);
            redirectAttributes.addFlashAttribute("message", "Offline room deleted successfully!");
        } catch (Exception e) {
            logger.error("Error deleting offline room: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error deleting offline room: " + e.getMessage());
        }
        return "redirect:/admin-home/rooms-list";
    }
}