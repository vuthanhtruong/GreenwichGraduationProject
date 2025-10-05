package com.example.demo.room.dao;

import com.example.demo.admin.model.Admins;
import com.example.demo.admin.service.AdminsService;
import com.example.demo.room.model.OfflineRooms;
import com.example.demo.room.model.OnlineRooms;
import com.example.demo.room.model.Rooms;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
@PreAuthorize("hasRole('ADMIN')")
public class RoomsDAOImpl implements RoomsDAO {
    private static final Logger logger = LoggerFactory.getLogger(RoomsDAOImpl.class);
    private final AdminsService adminsService;

    @PersistenceContext
    private EntityManager entityManager;

    public RoomsDAOImpl(AdminsService adminsService) {
        this.adminsService = adminsService;
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
    public Rooms updateOfflineRoom(String id, OfflineRooms room, MultipartFile avatarFile) throws IOException {
        Rooms existingRoom = entityManager.find(Rooms.class, id);
        if (existingRoom == null) {
            throw new IllegalArgumentException("Room with ID " + id + " not found");
        }
        existingRoom.setRoomName(room.getRoomName());
        if (room.getCreator() != null) existingRoom.setCreator(room.getCreator());
        if (room.getCampus() != null) existingRoom.setCampus(room.getCampus());
        if (avatarFile != null && !avatarFile.isEmpty()) {
            existingRoom.setAvatar(avatarFile.getBytes());
        } else if (room.getAvatar() != null) {
            existingRoom.setAvatar(room.getAvatar());
        }
        ((OfflineRooms) existingRoom).setAddress(room.getAddress());
        return entityManager.merge(existingRoom);
    }

    @Override
    public Rooms updateOnlineRoom(String id, OnlineRooms room, MultipartFile avatarFile) throws IOException {
        Rooms existingRoom = entityManager.find(Rooms.class, id);
        if (existingRoom == null) {
            throw new IllegalArgumentException("Room with ID " + id + " not found");
        }
        existingRoom.setRoomName(room.getRoomName());
        if (room.getCreator() != null) existingRoom.setCreator(room.getCreator());
        if (room.getCampus() != null) existingRoom.setCampus(room.getCampus());
        if (avatarFile != null && !avatarFile.isEmpty()) {
            existingRoom.setAvatar(avatarFile.getBytes());
        } else if (room.getAvatar() != null) {
            existingRoom.setAvatar(room.getAvatar());
        }
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
            logger.info("Deleted online room with ID: {}", id);
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
            logger.info("Deleted offline room with ID: {}", id);
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
    public void addOnlineRoom(OnlineRooms room, MultipartFile avatarFile) throws IOException {
        Admins admin = adminsService.getAdmin();
        if (admin == null) {
            throw new IllegalArgumentException("Authenticated admin not found");
        }
        room.setCreator(admin);
        room.setCampus(admin.getCampus());
        room.setCreatedAt(LocalDateTime.now());
        if (room.getLink() == null || room.getLink().isEmpty()) {
            room.setLink(generateUniqueJitsiMeetLink(room.getRoomId()));
        }
        if (avatarFile != null && !avatarFile.isEmpty()) {
            room.setAvatar(avatarFile.getBytes());
        }
        entityManager.persist(room);
        logger.info("Added online room with ID: {} for campus: {}", room.getRoomId(), room.getCampus() != null ? room.getCampus().getCampusId() : "null");
    }

    @Override
    public void addOfflineRoom(OfflineRooms room, MultipartFile avatarFile) throws IOException {
        Admins admin = adminsService.getAdmin();
        if (admin == null) {
            throw new IllegalArgumentException("Authenticated admin not found");
        }
        room.setRoomName(room.getRoomName() != null ? room.getRoomName().toUpperCase() : null);
        room.setCreator(admin);
        room.setCampus(admin.getCampus());
        room.setCreatedAt(LocalDateTime.now());
        if (avatarFile != null && !avatarFile.isEmpty()) {
            room.setAvatar(avatarFile.getBytes());
        }
        entityManager.persist(room);
        logger.info("Added offline room with ID: {} for campus: {}", room.getRoomId(), room.getCampus() != null ? room.getCampus().getCampusId() : "null");
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
        return entityManager.createQuery("SELECT COUNT(r) FROM OfflineRooms r", Long.class).getSingleResult();
    }

    @Override
    public long totalOnlineRooms() {
        return entityManager.createQuery("SELECT COUNT(r) FROM OnlineRooms r", Long.class).getSingleResult();
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
    public Map<String, String> validateOfflineRoom(OfflineRooms room, String address, MultipartFile avatarFile) {
        Map<String, String> errors = new HashMap<>();

        if (room.getRoomName() == null || room.getRoomName().trim().isEmpty()) {
            errors.put("roomName", "Room name cannot be blank.");
        } else if (!isValidName(room.getRoomName())) {
            errors.put("roomName", "Room name is not valid. Only letters, numbers, spaces, and standard punctuation are allowed.");
        }

        if (room.getRoomName() != null && existsByRoomExcludingName(room.getRoomName(), room.getRoomId())) {
            errors.put("roomName", "Room name is already in use.");
        }

        if (address != null && !address.isEmpty() && !isValidAddress(address)) {
            errors.put("address", "Invalid address format.");
        }

        if (avatarFile != null && !avatarFile.isEmpty()) {
            String contentType = avatarFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                errors.put("avatarFile", "Avatar must be an image file.");
            }
            if (avatarFile.getSize() > 5 * 1024 * 1024) {
                errors.put("avatarFile", "Avatar file size must not exceed 5MB.");
            }
        }

        if (room.getCampus() == null) {
            errors.put("campus", "Campus cannot be null.");
        }

        return errors;
    }

    @Override
    public Map<String, String> validateOnlineRoom(OnlineRooms room, String link, MultipartFile avatarFile) {
        Map<String, String> errors = new HashMap<>();

        if (room.getRoomName() == null || room.getRoomName().trim().isEmpty()) {
            errors.put("roomName", "Room name cannot be blank.");
        } else if (!isValidName(room.getRoomName())) {
            errors.put("roomName", "Room name is not valid. Only letters, numbers, spaces, and standard punctuation are allowed.");
        }

        if (room.getRoomName() != null && existsByRoomExcludingName(room.getRoomName(), room.getRoomId())) {
            errors.put("roomName", "Room name is already in use.");
        }

        if (link != null && !link.isEmpty() && !isValidLink(link)) {
            errors.put("link", "Invalid link format.");
        }

        if (avatarFile != null && !avatarFile.isEmpty()) {
            String contentType = avatarFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                errors.put("avatarFile", "Avatar must be an image file.");
            }
            if (avatarFile.getSize() > 5 * 1024 * 1024) {
                errors.put("avatarFile", "Avatar file size must not exceed 5MB.");
            }
        }

        if (room.getCampus() == null) {
            errors.put("campus", "Campus cannot be null.");
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