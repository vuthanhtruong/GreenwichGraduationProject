package com.example.demo.room.controller;

import com.example.demo.user.admin.model.Admins;
import com.example.demo.user.admin.service.AdminsService;
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
@RequestMapping("/admin-home")
public class AddRoomsController {
    private static final Logger logger = LoggerFactory.getLogger(AddRoomsController.class);
    private final RoomsService roomsService;
    private final AdminsService adminsService;

    public AddRoomsController(RoomsService roomsService, AdminsService adminsService) {
        this.roomsService = roomsService;
        this.adminsService = adminsService;
    }

    @PostMapping("/rooms-list/add-online-room")
    public String addOnlineRoom(
            @ModelAttribute("onlineRoom") OnlineRooms onlineRoom,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam(value = "link", required = false) String link,
            Model model,
            RedirectAttributes redirectAttributes) {
        Map<String, String> errors = roomsService.validateOnlineRoom(onlineRoom, link, avatarFile);

        if (onlineRoom.getRoomId() == null || onlineRoom.getRoomId().isBlank()) {
            onlineRoom.setRoomId(roomsService.generateUniqueRoomId(false));
        }

        if (!errors.isEmpty()) {
            model.addAttribute("openOnlineOverlay", true);
            model.addAttribute("onlineRoom", onlineRoom);
            model.addAttribute("errors", errors);
            model.addAttribute("onlineRooms", roomsService.getOnlineRooms());
            model.addAttribute("offlineRooms", roomsService.getOfflineRooms());
            return "ListRooms";
        }

        try {
            Admins currentAdmin = adminsService.getAdmin();
            onlineRoom.setCreator(currentAdmin);
            roomsService.addOnlineRoom(onlineRoom, avatarFile);
            redirectAttributes.addFlashAttribute("message", "Online room added successfully!");
            return "redirect:/admin-home/rooms-list";
        } catch (IOException e) {
            logger.error("Failed to process avatar: {}", e.getMessage());
            errors.put("avatarFile", "Failed to process avatar: " + e.getMessage());
            model.addAttribute("openOnlineOverlay", true);
            model.addAttribute("onlineRoom", onlineRoom);
            model.addAttribute("errors", errors);
            model.addAttribute("onlineRooms", roomsService.getOnlineRooms());
            model.addAttribute("offlineRooms", roomsService.getOfflineRooms());
            return "ListRooms";
        } catch (Exception e) {
            logger.error("Error adding online room: {}", e.getMessage());
            errors.put("general", "An error occurred while adding the online room: " + e.getMessage());
            model.addAttribute("openOnlineOverlay", true);
            model.addAttribute("onlineRoom", onlineRoom);
            model.addAttribute("errors", errors);
            model.addAttribute("onlineRooms", roomsService.getOnlineRooms());
            model.addAttribute("offlineRooms", roomsService.getOfflineRooms());
            return "ListRooms";
        }
    }

    @PostMapping("/rooms-list/add-offline-room")
    public String addOfflineRoom(
            @ModelAttribute("offlineRoom") OfflineRooms offlineRoom,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam(value = "address", required = false) String address,
            Model model,
            RedirectAttributes redirectAttributes) {
        Map<String, String> errors = roomsService.validateOfflineRoom(offlineRoom, address, avatarFile);

        if (offlineRoom.getRoomId() == null || offlineRoom.getRoomId().isBlank()) {
            offlineRoom.setRoomId(roomsService.generateUniqueRoomId(true));
        }

        if (!errors.isEmpty()) {
            model.addAttribute("openOfflineOverlay", true);
            model.addAttribute("offlineRoom", offlineRoom);
            model.addAttribute("errors", errors);
            model.addAttribute("onlineRooms", roomsService.getOnlineRooms());
            model.addAttribute("offlineRooms", roomsService.getOfflineRooms());
            return "ListRooms";
        }

        try {
            Admins currentAdmin = adminsService.getAdmin();
            offlineRoom.setCreator(currentAdmin);
            roomsService.addOfflineRoom(offlineRoom, avatarFile);
            redirectAttributes.addFlashAttribute("message", "Offline room added successfully!");
            return "redirect:/admin-home/rooms-list";
        } catch (IOException e) {
            logger.error("Failed to process avatar: {}", e.getMessage());
            errors.put("avatarFile", "Failed to process avatar: " + e.getMessage());
            model.addAttribute("openOfflineOverlay", true);
            model.addAttribute("offlineRoom", offlineRoom);
            model.addAttribute("errors", errors);
            model.addAttribute("onlineRooms", roomsService.getOnlineRooms());
            model.addAttribute("offlineRooms", roomsService.getOfflineRooms());
            return "ListRooms";
        } catch (Exception e) {
            logger.error("Error adding offline room: {}", e.getMessage());
            errors.put("general", "An error occurred while adding the offline room: " + e.getMessage());
            model.addAttribute("openOfflineOverlay", true);
            model.addAttribute("offlineRoom", offlineRoom);
            model.addAttribute("errors", errors);
            model.addAttribute("onlineRooms", roomsService.getOnlineRooms());
            model.addAttribute("offlineRooms", roomsService.getOfflineRooms());
            return "ListRooms";
        }
    }
}