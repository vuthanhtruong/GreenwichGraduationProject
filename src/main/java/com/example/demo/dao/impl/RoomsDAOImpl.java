
        package com.example.demo.dao.impl;

import com.example.demo.dao.RoomsDAO;
import com.example.demo.entity.OfflineRooms;
import com.example.demo.entity.OnlineRooms;
import com.example.demo.entity.Rooms;
import com.example.demo.entity.Staffs;
import com.example.demo.service.EmailServiceForLectureService;
import com.example.demo.service.EmailServiceForStudentService;
import com.example.demo.service.RoomsService;
import com.example.demo.service.StaffsService;
import com.example.demo.service.GoogleCalendarService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
@PreAuthorize("hasRole('STAFF')")
public class RoomsDAOImpl implements RoomsDAO {

    private static final Logger logger = LoggerFactory.getLogger(RoomsDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private StaffsService staffsService;

    @Autowired
    private GoogleCalendarService googleCalendarService;

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
    public Rooms updateOfflineRoom(String id, OfflineRooms room) {
        if (room == null || id == null) {
            throw new IllegalArgumentException("Room object or ID cannot be null");
        }

        Rooms existingRoom = entityManager.find(Rooms.class, id);
        if (existingRoom == null || !(existingRoom instanceof OfflineRooms)) {
            throw new IllegalArgumentException("Offline room with ID " + id + " not found");
        }

        if (room.getRoomName() == null) {
            throw new IllegalArgumentException("Room name cannot be null");
        }

        existingRoom.setRoomName(room.getRoomName());
        if (room.getCreator() != null) existingRoom.setCreator(room.getCreator());
        if (room.getCreatedAt() != null) existingRoom.setCreatedAt(room.getCreatedAt());
        ((OfflineRooms) existingRoom).setAddress(room.getAddress());

        return entityManager.merge(existingRoom);
    }

    @Override
    public Rooms updateOnlineRoom(String id, OnlineRooms room) {
        if (room == null || id == null) {
            throw new IllegalArgumentException("Room object or ID cannot be null");
        }

        Rooms existingRoom = entityManager.find(Rooms.class, id);
        if (existingRoom == null || !(existingRoom instanceof OnlineRooms)) {
            throw new IllegalArgumentException("Online room with ID " + id + " not found");
        }

        if (room.getRoomName() == null) {
            throw new IllegalArgumentException("Room name cannot be null");
        }

        existingRoom.setRoomName(room.getRoomName());
        if (room.getCreator() != null) existingRoom.setCreator(room.getCreator());
        if (room.getCreatedAt() != null) existingRoom.setCreatedAt(room.getCreatedAt());
        ((OnlineRooms) existingRoom).setLink(room.getLink());

        return entityManager.merge(existingRoom);
    }

    @Override
    public String generateUniqueGoogleMeetLink(String roomId) throws IOException {
        try {
            // Validate roomId
            String safeRoomId = (roomId != null && !roomId.trim().isEmpty()) ? roomId.trim() : "room-" + System.currentTimeMillis();
            String meetLink = googleCalendarService.createMeetingEvent(safeRoomId);
            int attempts = 0;
            int maxAttempts = 5; // Limit retries to avoid infinite loops
            while (isMeetLinkExists(meetLink) && attempts < maxAttempts) {
                safeRoomId = "room-" + System.currentTimeMillis() + generateRandomString(3);
                meetLink = googleCalendarService.createMeetingEvent(safeRoomId);
                attempts++;
            }
            if (isMeetLinkExists(meetLink)) {
                logger.error("Failed to generate unique Google Meet link after {} attempts for roomId: {}", maxAttempts, safeRoomId);
                throw new IOException("Unable to generate unique Google Meet link");
            }
            logger.info("Generated Google Meet link: {} for roomId: {}", meetLink, safeRoomId);
            return meetLink;
        } catch (IOException e) {
            logger.error("Failed to generate Google Meet link for roomId: {}", roomId, e);
            throw e;
        }
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
        String characters = "abcdefghijklmnopqrstuvwxyz";
        SecureRandom random = new SecureRandom();
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < length; i++) {
            randomString.append(characters.charAt(random.nextInt(characters.length())));
        }
        return randomString.toString();
    }

    @Override
    public boolean isMeetLinkExists(String link) {
        if (link == null || link.trim().isEmpty() || link.contains("meet.jit.si")) {
            logger.warn("Invalid or Jitsi link detected: {}", link);
            return true; // Treat invalid/Jitsi links as existing to force regeneration
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
    public void addOnlineRoom(OnlineRooms room) throws IOException {
        if (room == null) {
            throw new IllegalArgumentException("Room object cannot be null");
        }
        Staffs staff = staffsService.getStaff();
        if (staff == null) {
            throw new IllegalArgumentException("Staff not found");
        }

        room.setCreator(staff);
        room.setCreatedAt(LocalDateTime.now());
        if (room.getLink() == null || room.getLink().isEmpty() || room.getLink().contains("meet.jit.si")) {
            try {
                String meetLink = generateUniqueGoogleMeetLink(room.getRoomId());
                room.setLink(meetLink);
            } catch (IOException e) {
                logger.error("Failed to add online room due to Google Meet link generation error for roomId: {}", room.getRoomId(), e);
                throw e;
            }
        }
        entityManager.persist(room);
        logger.info("Persisted online room with ID: {} and link: {}", room.getRoomId(), room.getLink());
    }

    @Override
    public void addOfflineRoom(OfflineRooms room) {
        if (room == null) {
            throw new IllegalArgumentException("Room object cannot be null");
        }
        Staffs staff = staffsService.getStaff();
        if (staff == null) {
            throw new IllegalArgumentException("Staff not found");
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
}
