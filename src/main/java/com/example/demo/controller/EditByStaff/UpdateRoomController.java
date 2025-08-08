package com.example.demo.controller.EditByStaff;

import com.example.demo.entity.OfflineRooms;
import com.example.demo.entity.OnlineRooms;
import com.example.demo.entity.AbstractClasses.Rooms;
import com.example.demo.service.RoomsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

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

        List<String> editErrors = new ArrayList<>();
        validateOfflineRoom(formRoom, (OfflineRooms) existingRoom, bindingResult, address, editErrors);

        if (!editErrors.isEmpty() || bindingResult.hasErrors()) {
            model.addAttribute("editErrors", editErrors);
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
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("message", "Database error while updating offline room: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Unexpected error while updating offline room: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
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

        List<String> editErrors = new ArrayList<>();
        validateOnlineRoom(formRoom, (OnlineRooms) existingRoom, bindingResult, link, editErrors);

        if (!editErrors.isEmpty() || bindingResult.hasErrors()) {
            model.addAttribute("editErrors", editErrors);
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
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("message", "Database error while updating online room: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Unexpected error while updating online room: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }

        return "redirect:/staff-home/rooms-list";
    }

    private void validateOfflineRoom(OfflineRooms formRoom, OfflineRooms existingRoom, BindingResult bindingResult, String address, List<String> errors) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        if (!isValidName(formRoom.getRoomName())) {
            errors.add("Room name is not valid. Only letters, numbers, spaces, and standard punctuation are allowed.");
        }

        Rooms existingRoomByName = roomsService.getByName(formRoom.getRoomName());
        if (formRoom.getRoomName() != null && existingRoomByName != null &&
                (existingRoom == null || !existingRoomByName.getRoomId().equals(formRoom.getRoomId()))) {
            errors.add("Room name is already in use.");
        }

        if (address != null && !address.isEmpty() && !isValidAddress(address)) {
            errors.add("Invalid address format.");
        }
    }

    private void validateOnlineRoom(OnlineRooms formRoom, OnlineRooms existingRoom, BindingResult bindingResult, String link, List<String> errors) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        if (!isValidName(formRoom.getRoomName())) {
            errors.add("Room name is not valid. Only letters, numbers, spaces, and standard punctuation are allowed.");
        }

        Rooms existingRoomByName = roomsService.getByName(formRoom.getRoomName());
        if (formRoom.getRoomName() != null && existingRoomByName != null &&
                (existingRoom == null || !existingRoomByName.getRoomId().equals(formRoom.getRoomId()))) {
            errors.add("Room name is already in use.");
        }

        if (link != null && !link.isEmpty() && !isValidLink(link)) {
            errors.add("Invalid meeting link format. Must be a valid URL.");
        }
    }

    private boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String nameRegex = "^[\\p{L}0-9][\\p{L}0-9 .'-]{0,49}$";
        return name.matches(nameRegex);
    }

    private boolean isValidLink(String link) {
        return link != null && link.matches("^https?://[\\w\\-\\.]+\\.[a-zA-Z]{2,}(/.*)?$");
    }

    private boolean isValidAddress(String address) {
        return address != null && address.matches("^[\\p{L}0-9][\\p{L}0-9 ,\\-./]{0,99}$");
    }
}