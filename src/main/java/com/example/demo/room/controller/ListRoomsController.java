package com.example.demo.room.controller;

import com.example.demo.admin.service.AdminsService;
import com.example.demo.room.model.OfflineRooms;
import com.example.demo.room.model.OnlineRooms;
import com.example.demo.room.service.RoomsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/admin-home/rooms-list")
public class ListRoomsController {
    private static final Logger logger = LoggerFactory.getLogger(ListRoomsController.class);
    private final RoomsService roomsService;
    private final AdminsService adminsService;

    public ListRoomsController(RoomsService roomsService, AdminsService adminsService) {
        this.roomsService = roomsService;
        this.adminsService = adminsService;
    }

    @GetMapping("")
    public String listRooms(Model model) {
        try {
            List<OnlineRooms> onlineRooms = roomsService.getOnlineRooms();
            List<OfflineRooms> offlineRooms = roomsService.getOfflineRooms();
            model.addAttribute("onlineRoom", new OnlineRooms());
            model.addAttribute("offlineRoom", new OfflineRooms());
            model.addAttribute("onlineRooms", onlineRooms);
            model.addAttribute("offlineRooms", offlineRooms);
            return "ListRooms";
        } catch (Exception e) {
            logger.error("Error listing rooms: {}", e.getMessage());
            model.addAttribute("error", "Error listing rooms: " + e.getMessage());
            model.addAttribute("onlineRoom", new OnlineRooms());
            model.addAttribute("offlineRoom", new OfflineRooms());
            model.addAttribute("onlineRooms", List.of());
            model.addAttribute("offlineRooms", List.of());
            return "ListRooms";
        }
    }

    @GetMapping("/avatar/{roomId}")
    @ResponseBody
    public ResponseEntity<byte[]> getRoomAvatar(@PathVariable String roomId) {
        com.example.demo.room.model.Rooms room = roomsService.getRoomById(roomId);
        if (room != null && room.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(room.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}