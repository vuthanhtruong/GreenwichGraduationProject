package com.example.demo.service.impl;

import com.example.demo.dao.RoomsDAO;
import com.example.demo.dao.StaffsDAO;
import com.example.demo.entity.OfflineRooms;
import com.example.demo.entity.OnlineRooms;
import com.example.demo.entity.Rooms;
import com.example.demo.service.RoomsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoomsServiceImpl implements RoomsService {
    @Override
    public Rooms getRoomById(String id) {
        return roomsDAO.getRoomById(id);
    }

    @Override
    public Rooms getByName(String name) {
        return roomsDAO.getByName(name);
    }

    @Override
    public Rooms updateOfflineRoom(String id, OfflineRooms room) {
        return roomsDAO.updateOfflineRoom(id, room);
    }

    @Override
    public Rooms updateOnlineRoom(String id, OnlineRooms room) {
        return roomsDAO.updateOnlineRoom(id, room);
    }

    @Override
    public String generateUniqueJitsiMeetLink(String roomId) {
        return roomsDAO.generateUniqueJitsiMeetLink(roomId);
    }

    @Override
    public String generateRandomPassword(int length) {
        return roomsDAO.generateRandomPassword(length);
    }

    @Override
    public String generateRandomString(int length) {
        return roomsDAO.generateRandomString(length);
    }

    @Override
    public boolean isJitsiLinkExists(String link) {
        return roomsDAO.isJitsiLinkExists(link);
    }

    @Override
    public Boolean existsOfflineRoomsById(String id) {
        return roomsDAO.existsOfflineRoomsById(id);
    }

    @Override
    public void deleteOnlineRoom(String id) {
        roomsDAO.deleteOnlineRoom(id);
    }

    @Override
    public void deleteOfflineRoom(String id) {
        roomsDAO.deleteOfflineRoom(id);
    }

    @Override
    public Boolean existsOnlineRoomsById(String id) {
        return roomsDAO.existsOnlineRoomsById(id);
    }

    @Override
    public void addOnlineRoom(OnlineRooms rooms) {
        roomsDAO.addOnlineRoom(rooms);
    }

    @Override
    public void addOfflineRoom(OfflineRooms rooms) {
        roomsDAO.addOfflineRoom(rooms);
    }

    @Override
    public List<OfflineRooms> getPaginatedOfflineRooms(int firstResult, int pageSize,String sortOrder) {
        return roomsDAO.getPaginatedOfflineRooms(firstResult, pageSize, sortOrder);
    }

    @Override
    public List<OnlineRooms> getPaginatedOnlineRooms(int firstResult, int pageSize, String sortOrder) {
        return roomsDAO.getPaginatedOnlineRooms(firstResult, pageSize, sortOrder);
    }

    private final RoomsDAO roomsDAO;

    public RoomsServiceImpl(RoomsDAO roomsDAO) {
        this.roomsDAO = roomsDAO;
    }

    @Override
    public List<Rooms> getRooms() {
        return roomsDAO.getRooms();
    }

    @Override
    public List<OnlineRooms> getOnlineRooms() {
        return roomsDAO.getOnlineRooms();
    }

    @Override
    public List<OfflineRooms> getOfflineRooms() {
        return roomsDAO.getOfflineRooms();
    }

    @Override
    public long totalOfflineRooms() {
        return roomsDAO.totalOfflineRooms();
    }

    @Override
    public long totalOnlineRooms() {
        return roomsDAO.totalOnlineRooms();
    }
}
