package com.example.demo.controller.DeleteByStaff;

import ch.qos.logback.core.model.Model;
import com.example.demo.service.RoomsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff-home/rooms-list")
public class DeleteRoomController {
    private final RoomsService roomsService;

    public DeleteRoomController(RoomsService roomsService) {

        this.roomsService = roomsService;
    }
    @DeleteMapping("/delete-offline-room/{id}")
    public String deleteRoom(Model model, @PathVariable String id, RedirectAttributes redirectAttributes) {
        roomsService.deleteOfflineRoom(id);
        redirectAttributes.addFlashAttribute("message", "Delete offline room ID member: " + id);
        return  "redirect:/staff-home/rooms-list";
    }

    @DeleteMapping("/delete-online-room/{id}")
    public String deleteOnlineRoom(Model model, @PathVariable String id, RedirectAttributes redirectAttributes) {
        roomsService.deleteOnlineRoom(id);
        redirectAttributes.addFlashAttribute("message", "Delete online room ID member: " + id);
        return  "redirect:/staff-home/rooms-list";
    }
}
