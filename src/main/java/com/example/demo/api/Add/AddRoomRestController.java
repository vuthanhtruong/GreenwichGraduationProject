package com.example.demo.api.Add;

import com.example.demo.entity.OfflineRooms;
import com.example.demo.entity.OnlineRooms;
import com.example.demo.entity.Rooms;
import com.example.demo.entity.Staffs;
import com.example.demo.service.RoomsService;
import com.example.demo.service.StaffsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/staff-home/rooms-list")
public class AddRoomRestController {

    private final RoomsService roomsService;
    private final StaffsService staffsService;

    @Autowired
    public AddRoomRestController(RoomsService roomsService, StaffsService staffsService) {
        this.roomsService = roomsService;
        this.staffsService = staffsService;
    }

    @GetMapping("/add-offline-room")
    public ResponseEntity<OfflineRooms> getAddOfflineRoomForm() {
        return ResponseEntity.ok(new OfflineRooms());
    }

    @PostMapping("/add-offline-room")
    public ResponseEntity<?> addOfflineRoom(
            @Valid @ModelAttribute("offlineRoom") OfflineRooms offlineRoom,
            Authentication authentication) {
        List<String> errors = new ArrayList<>();
        validateOfflineRoom(offlineRoom, offlineRoom.getAddress(), errors);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            String roomId = generateUniqueRoomId(true); // true for offline
            offlineRoom.setRoomId(roomId);
            offlineRoom.setCreatedAt(LocalDateTime.now());
            String username = authentication.getName();
            Staffs creator = staffsService.getStaffs();
            if (creator == null) {
                errors.add("Authenticated staff not found.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
            }
            offlineRoom.setCreator(creator);
            roomsService.addOfflineRoom(offlineRoom);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Offline room added successfully with ID: " + roomId);
        } catch (DataAccessException e) {
            errors.add("Database error while adding offline room: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
        } catch (Exception e) {
            errors.add("Unexpected error while adding offline room: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
        }
    }

    @GetMapping("/add-online-room")
    public ResponseEntity<OnlineRooms> getAddOnlineRoomForm() {
        return ResponseEntity.ok(new OnlineRooms());
    }

    @PostMapping("/add-online-room")
    public ResponseEntity<?> addOnlineRoom(
            @Valid @ModelAttribute("onlineRoom") OnlineRooms onlineRoom,
            Authentication authentication) {
        List<String> errors = new ArrayList<>();
        validateOnlineRoom(onlineRoom, onlineRoom.getLink(), errors);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            String roomId = generateUniqueRoomId(false); // false for online
            onlineRoom.setRoomId(roomId);
            onlineRoom.setCreatedAt(LocalDateTime.now());
            String username = authentication.getName();
            Staffs creator = staffsService.getStaffs();
            if (creator == null) {
                errors.add("Authenticated staff not found.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
            }
            onlineRoom.setCreator(creator);
            roomsService.addOnlineRoom(onlineRoom);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Online room added successfully with ID: " + roomId);
        } catch (DataAccessException e) {
            errors.add("Database error while adding online room: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
        } catch (Exception e) {
            errors.add("Unexpected error while adding online room: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
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

    private void validateOfflineRoom(OfflineRooms formRoom, String address, List<String> errors) {
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

    private void validateOnlineRoom(OnlineRooms formRoom, String link, List<String> errors) {
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