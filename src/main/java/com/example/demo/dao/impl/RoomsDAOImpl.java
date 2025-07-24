package com.example.demo.dao.impl;

import com.example.demo.dao.RoomsDAO;
import com.example.demo.entity.OfflineRooms;
import com.example.demo.entity.OnlineRooms;
import com.example.demo.entity.Rooms;
import com.example.demo.entity.Staffs;
import com.example.demo.service.EmailServiceForLectureService;
import com.example.demo.service.EmailServiceForStudentService;
import com.example.demo.service.RoomsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
@PreAuthorize("hasRole('STAFF')")
public class RoomsDAOImpl implements RoomsDAO {
    @Override
    public String generateUniqueJitsiMeetLink(String roomId) {
        String baseUrl = "https://meet.jit.si/";
        // Use roomId as base, append random string for extra uniqueness
        String jitsiRoomName = roomId != null ? roomId : "Room" + System.currentTimeMillis();
        String uniqueRoomName = jitsiRoomName + "-" + generateRandomString(4); // Add 4-char random suffix

        // Check if link already exists in database
        String jitsiLink = baseUrl + uniqueRoomName;
        while (isJitsiLinkExists(jitsiLink)) {
            uniqueRoomName = jitsiRoomName + "-" + generateRandomString(4); // Generate new suffix
            jitsiLink = baseUrl + uniqueRoomName;
        }

        // Add random password for security
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
        Long count = entityManager.createQuery(
                        "SELECT COUNT(r) FROM OnlineRooms r WHERE r.link = :link", Long.class)
                .setParameter("link", link)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public Boolean existsOfflineRoomsById(String id) {
        return entityManager.find(OfflineRooms.class, id) != null;
    }

    @Override
    public void deleteOnlineRoom(String id) {
        OnlineRooms rooms=entityManager.find(OnlineRooms.class, id);
        entityManager.remove(rooms);
    }

    @Override
    public void deleteOfflineRoom(String id) {
        OfflineRooms rooms=entityManager.find(OfflineRooms.class, id);
        entityManager.remove(rooms);
    }

    @Override
    public Boolean existsOnlineRoomsById(String id) {

        return entityManager.find(OnlineRooms.class, id) != null;
    }

    @Override
    public void addOnlineRoom(OnlineRooms rooms) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Staffs staff = entityManager.find(Staffs.class, username);
        rooms.setCreator(staff);
        rooms.setCreatedAt(LocalDateTime.now());
        // Generate and check Jitsi Meet link
        String jitsiLink = generateUniqueJitsiMeetLink(rooms.getRoomId());
        rooms.setLink(jitsiLink);

        entityManager.persist(rooms);
    }

    @Override
    public void addOfflineRoom(OfflineRooms rooms) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Staffs staff = entityManager.find(Staffs.class, username);
        rooms.setRoomName(rooms.getRoomName().toUpperCase());
        rooms.setCreator(staff);
        rooms.setCreatedAt(LocalDateTime.now());
        entityManager.persist(rooms);
    }

    @Override
    public List<OfflineRooms> getPaginatedOfflineRooms(int firstResult, int pageSize, String sortOrder) {
        String query = "from OfflineRooms" + (sortOrder != null ? " ORDER BY createdAt " + sortOrder : "");
        return entityManager.createQuery(query, OfflineRooms.class)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public List<OnlineRooms> getPaginatedOnlineRooms(int firstResult, int pageSize, String sortOrder) {
        String query = "from OnlineRooms" + (sortOrder != null ? " ORDER BY createdAt " + sortOrder : "");
        return entityManager.createQuery(query, OnlineRooms.class)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public List<Rooms> getRooms() {
        List<Rooms> rooms = entityManager.createQuery("from Rooms", Rooms.class).getResultList();
        return rooms;
    }

    @Override
    public List<OnlineRooms> getOnlineRooms() {
        List<OnlineRooms>  rooms = entityManager.createQuery("from OnlineRooms", OnlineRooms.class).getResultList();
        return rooms;
    }

    @Override
    public List<OfflineRooms> getOfflineRooms() {
        List<OfflineRooms> offlineRooms=entityManager.createQuery("from OfflineRooms", OfflineRooms.class).getResultList();
        return offlineRooms;
    }

    @Override
    public long totalOfflineRooms() {
        long count=entityManager.createQuery("select count(*) from OfflineRooms").getResultList().size();
        return count;
    }

    @Override
    public long totalOnlineRooms() {
        long count=entityManager.createQuery("select count(*) from OnlineRooms").getResultList().size();
        return count;
    }

    @PersistenceContext
    private EntityManager entityManager;
}
