package com.example.demo.dao.impl;

import com.example.demo.dao.RoomsDAO;
import com.example.demo.entity.OfflineRooms;
import com.example.demo.entity.OnlineRooms;
import com.example.demo.entity.Rooms;
import com.example.demo.entity.Staffs;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
@PreAuthorize("hasRole('STAFF')")
public class RoomsDAOImpl implements RoomsDAO {
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
