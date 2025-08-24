package com.example.demo.room.dao;

import com.example.demo.room.model.OfflineRooms;
import com.example.demo.room.model.OnlineRooms;
import com.example.demo.room.model.Rooms;
import com.example.demo.Staff.model.Staffs;
import com.example.demo.Staff.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
@PreAuthorize("hasRole('STAFF')")
public class RoomsDAOImpl implements RoomsDAO {
    private final StaffsService staffsService;

    @PersistenceContext
    private EntityManager entityManager;

    public RoomsDAOImpl(StaffsService staffsService) {
        this.staffsService = staffsService;
    }

    @Override
    public Rooms getRoomById(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Room ID cannot be null");
        }
        return entityManager.find(Rooms.class, id);
    }

    @Override
    public Rooms getByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        List<Rooms> rooms = entityManager.createQuery(
                        "SELECT s FROM Rooms s WHERE s.roomName = :name", Rooms.class)
                .setParameter("name", name.trim())
                .getResultList();
        return rooms.isEmpty() ? null : rooms.get(0);
    }

    @Override
    public boolean existsByRoomExcludingName(String roomName, String roomId) {
        if (roomName == null || roomName.trim().isEmpty()) {
            return false;
        }
        List<Rooms> rooms = entityManager.createQuery(
                        "SELECT s FROM Rooms s WHERE s.roomName = :name AND s.roomId != :roomId", Rooms.class)
                .setParameter("name", roomName.trim())
                .setParameter("roomId", roomId != null ? roomId : "")
                .getResultList();
        return !rooms.isEmpty();
    }

    @Override
    public Rooms updateOfflineRoom(String id, OfflineRooms room) {
        Rooms existingRoom = entityManager.find(Rooms.class, id);
        existingRoom.setRoomName(room.getRoomName());
        if (room.getCreator() != null) existingRoom.setCreator(room.getCreator());
        if (room.getCreatedAt() != null) existingRoom.setCreatedAt(room.getCreatedAt());
        ((OfflineRooms) existingRoom).setAddress(room.getAddress());

        return entityManager.merge(existingRoom);
    }

    @Override
    public Rooms updateOnlineRoom(String id, OnlineRooms room) {
        Rooms existingRoom = entityManager.find(Rooms.class, id);
        List<String> errors = validateOnlineRoom(room, room.getLink());
        existingRoom.setRoomName(room.getRoomName());
        if (room.getCreator() != null) existingRoom.setCreator(room.getCreator());
        if (room.getCreatedAt() != null) existingRoom.setCreatedAt(room.getCreatedAt());
        ((OnlineRooms) existingRoom).setLink(room.getLink());

        return entityManager.merge(existingRoom);
    }

    @Override
    public String generateUniqueJitsiMeetLink(String roomId) {
        String baseUrl = "https://meet.jit.si/";
        String jitsiRoomName = (roomId != null && !roomId.trim().isEmpty()) ? roomId.trim() : "Room" + System.currentTimeMillis();
        String uniqueRoomName = jitsiRoomName + "-" + generateRandomString(4);
        String jitsiLink = baseUrl + uniqueRoomName;

        while (isJitsiLinkExists(jitsiLink)) {
            uniqueRoomName = jitsiRoomName + "-" + generateRandomString(4);
            jitsiLink = baseUrl + uniqueRoomName;
        }

        String password = generateRandomPassword(8);
        return jitsiLink + "#config.roomPassword=" + password;
    }

    @Override
    public String generateRandomPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        return password.toString();
    }

    @Override
    public String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < length; i++) {
            randomString.append(characters.charAt(random.nextInt(characters.length())));
        }
        return randomString.toString();
    }

    @Override
    public boolean isJitsiLinkExists(String link) {
        if (link == null) {
            return false;
        }
        Long count = entityManager.createQuery(
                        "SELECT COUNT(r) FROM OnlineRooms r WHERE r.link = :link", Long.class)
                .setParameter("link", link)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public Boolean existsOfflineRoomsById(String id) {
        if (id == null) {
            return false;
        }
        return entityManager.find(OfflineRooms.class, id) != null;
    }

    @Override
    public void deleteOnlineRoom(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Room ID cannot be null");
        }
        OnlineRooms room = entityManager.find(OnlineRooms.class, id);
        if (room != null) {
            entityManager.remove(room);
        }
    }

    @Override
    public void deleteOfflineRoom(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Room ID cannot be null");
        }
        OfflineRooms room = entityManager.find(OfflineRooms.class, id);
        if (room != null) {
            entityManager.remove(room);
        }
    }

    @Override
    public Boolean existsOnlineRoomsById(String id) {
        if (id == null) {
            return false;
        }
        return entityManager.find(OnlineRooms.class, id) != null;
    }

    @Override
    public void addOnlineRoom(OnlineRooms room) {
        Staffs staff = staffsService.getStaff();
        if (staff == null) {
            throw new IllegalArgumentException("Authenticated staff not found");
        }
        room.setCreator(staff);
        room.setCreatedAt(LocalDateTime.now());
        if (room.getLink() == null || room.getLink().isEmpty()) {
            room.setLink(generateUniqueJitsiMeetLink(room.getRoomId()));
        }
        entityManager.persist(room);
    }

    @Override
    public void addOfflineRoom(OfflineRooms room) {
        Staffs staff = staffsService.getStaff();
        if (staff == null) {
            throw new IllegalArgumentException("Authenticated staff not found");
        }
        room.setRoomName(room.getRoomName() != null ? room.getRoomName().toUpperCase() : null);
        room.setCreator(staff);
        room.setCreatedAt(LocalDateTime.now());
        entityManager.persist(room);
    }

    @Override
    public List<OfflineRooms> getPaginatedOfflineRooms(int firstResult, int pageSize, String sortOrder) {
        String query = "FROM OfflineRooms" + (sortOrder != null && !sortOrder.trim().isEmpty() ? " ORDER BY createdAt " + sortOrder : "");
        return entityManager.createQuery(query, OfflineRooms.class)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public List<OnlineRooms> getPaginatedOnlineRooms(int firstResult, int pageSize, String sortOrder) {
        String query = "FROM OnlineRooms" + (sortOrder != null && !sortOrder.trim().isEmpty() ? " ORDER BY createdAt " + sortOrder : "");
        return entityManager.createQuery(query, OnlineRooms.class)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public List<Rooms> getRooms() {
        return entityManager.createQuery("FROM Rooms", Rooms.class).getResultList();
    }

    @Override
    public List<OnlineRooms> getOnlineRooms() {
        return entityManager.createQuery("FROM OnlineRooms", OnlineRooms.class).getResultList();
    }

    @Override
    public List<OfflineRooms> getOfflineRooms() {
        return entityManager.createQuery("FROM OfflineRooms", OfflineRooms.class).getResultList();
    }

    @Override
    public long totalOfflineRooms() {
        return (Long) entityManager.createQuery("SELECT COUNT(r) FROM OfflineRooms r").getSingleResult();
    }

    @Override
    public long totalOnlineRooms() {
        return (Long) entityManager.createQuery("SELECT COUNT(r) FROM OnlineRooms r").getSingleResult();
    }

    @Override
    public String generateUniqueRoomId(boolean isOffline) {
        SecureRandom random = new SecureRandom();
        LocalDate currentDate = LocalDate.now();
        String datePart = currentDate.format(DateTimeFormatter.ofPattern("yMMdd"));
        String prefix = isOffline ? "GWOFF" : "GWONL";

        String roomId;
        do {
            String randomDigits = String.format("%02d", random.nextInt(100));
            roomId = prefix + datePart + randomDigits;
        } while (existsOnlineRoomsById(roomId) || existsOfflineRoomsById(roomId));
        return roomId;
    }

    @Override
    public List<String> validateOfflineRoom(OfflineRooms room, String address) {
        List<String> errors = new ArrayList<>();

        if (room.getRoomName() == null || room.getRoomName().trim().isEmpty()) {
            errors.add("Room name cannot be blank.");
        } else if (!isValidName(room.getRoomName())) {
            errors.add("Room name is not valid. Only letters, numbers, spaces, and standard punctuation are allowed.");
        }

        if (room.getRoomName() != null && existsByRoomExcludingName(room.getRoomName(), room.getRoomId())) {
            errors.add("Room name is already in use.");
        }

        if (address != null && !address.isEmpty() && !isValidAddress(address)) {
            errors.add("Invalid address format.");
        }

        return errors;
    }

    @Override
    public List<String> validateOnlineRoom(OnlineRooms room, String link) {
        List<String> errors = new ArrayList<>();

        if (room.getRoomName() == null || room.getRoomName().trim().isEmpty()) {
            errors.add("Room name cannot be blank.");
        } else if (!isValidName(room.getRoomName())) {
            errors.add("Room name is not valid. Only letters, numbers, spaces, and standard punctuation are allowed.");
        }

        if (room.getRoomName() != null && existsByRoomExcludingName(room.getRoomName(), room.getRoomId())) {
            errors.add("Room name is already in use.");
        }

        if (link != null && !link.isEmpty() && !isValidLink(link)) {
            errors.add("Invalid meeting link format. Must be a valid URL.");
        }

        return errors;
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