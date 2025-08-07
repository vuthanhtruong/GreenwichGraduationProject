package com.example.demo.controller.AddByStaff;

import com.example.demo.entity.OfflineRooms;
import com.example.demo.entity.OnlineRooms;
import com.example.demo.entity.Rooms;
import com.example.demo.entity.Staffs;
import com.example.demo.service.RoomsService;
import com.example.demo.service.StaffsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        List<String> errors = new ArrayList<>();
        validateOfflineRoom(offlineRoom, result, offlineRoom.getAddress(), errors);

        if (!errors.isEmpty() || result.hasErrors()) {
            model.addAttribute("editErrors", errors);
            return "AddOfflineRoom";
        }

        try {
            String roomId = generateUniqueRoomId(true); // true for offline
            offlineRoom.setRoomId(roomId);
            offlineRoom.setCreatedAt(LocalDateTime.now());
            String username = authentication.getName();
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
        } catch (DataAccessException e) {
            model.addAttribute("errorMessage", "Database error while adding offline room: " + e.getMessage());
            return "AddOfflineRoom";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Unexpected error while adding offline room: " + e.getMessage());
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
        List<String> errors = new ArrayList<>();
        validateOnlineRoom(onlineRoom, result, onlineRoom.getLink(), errors);

        if (!errors.isEmpty() || result.hasErrors()) {
            model.addAttribute("editErrors", errors);
            return "AddOnlineRoom";
        }

        try {
            String roomId = generateUniqueRoomId(false); // false for online
            onlineRoom.setRoomId(roomId);
            onlineRoom.setCreatedAt(LocalDateTime.now());
            String username = authentication.getName();
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
        } catch (DataAccessException e) {
            model.addAttribute("errorMessage", "Database error while adding online room: " + e.getMessage());
            return "AddOnlineRoom";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Unexpected error while adding online room: " + e.getMessage());
            return "AddOnlineRoom";
        }
    }

    private String generateUniqueRoomId(boolean isOffline) {
        SecureRandom random = new SecureRandom();
        LocalDate currentDate = LocalDate.now();
        String datePart = currentDate.format(DateTimeFormatter.ofPattern("yMMdd"));
        String prefix = isOffline ? "GWOFF" : "GWONL";

        String roomId;
        do {
            String randomDigits = String.format("%02d", random.nextInt(100));
            roomId = prefix + datePart + randomDigits;
        } while (roomsService.existsOnlineRoomsById(roomId) || roomsService.existsOfflineRoomsById(roomId));
        return roomId;
    }

    private void validateOfflineRoom(OfflineRooms formRoom, BindingResult bindingResult, String address, List<String> errors) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        if (!isValidName(formRoom.getRoomName())) {
            errors.add("Room name is not valid. Only letters, numbers, spaces, and standard punctuation are allowed.");
        }

        Rooms existingRoomByName = roomsService.getByName(formRoom.getRoomName());
        if (formRoom.getRoomName() != null && existingRoomByName != null) {
            errors.add("Room name is already in use.");
        }

        if (address != null && !address.isEmpty() && !isValidAddress(address)) {
            errors.add("Invalid address format.");
        }
    }

    private void validateOnlineRoom(OnlineRooms formRoom, BindingResult bindingResult, String link, List<String> errors) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        if (!isValidName(formRoom.getRoomName())) {
            errors.add("Room name is not valid. Only letters, numbers, spaces, and standard punctuation are allowed.");
        }

        Rooms existingRoomByName = roomsService.getByName(formRoom.getRoomName());
        if (formRoom.getRoomName() != null && existingRoomByName != null) {
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