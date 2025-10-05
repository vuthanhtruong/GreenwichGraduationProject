package com.example.demo.room.service;

import com.example.demo.room.dao.RoomsDAO;
import com.example.demo.room.model.OfflineRooms;
import com.example.demo.room.model.OnlineRooms;
import com.example.demo.room.model.Rooms;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class RoomsServiceImpl implements RoomsService {

    private final RoomsDAO roomsDAO;

    public RoomsServiceImpl(RoomsDAO roomsDAO) {
        this.roomsDAO = roomsDAO;
    }

    @Override
    public Rooms getRoomById(String id) {
        return roomsDAO.getRoomById(id);
    }

    @Override
    public Rooms getByName(String name) {
        return roomsDAO.getByName(name);
    }

    @Override
    public boolean existsByRoomExcludingName(String roomName, String roomId) {
        return roomsDAO.existsByRoomExcludingName(roomName, roomId);
    }

    @Override
    public Rooms updateOfflineRoom(String id, OfflineRooms room, MultipartFile avatarFile) throws IOException {
        return roomsDAO.updateOfflineRoom(id, room, avatarFile);
    }

    @Override
    public Rooms updateOnlineRoom(String id, OnlineRooms room, MultipartFile avatarFile) throws IOException {
        return roomsDAO.updateOnlineRoom(id, room, avatarFile);
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
    public Boolean existsOnlineRoomsById(String id) {
        return roomsDAO.existsOnlineRoomsById(id);
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
    public void addOnlineRoom(OnlineRooms room, MultipartFile avatarFile) throws IOException {
        roomsDAO.addOnlineRoom(room, avatarFile);
    }

    @Override
    public void addOfflineRoom(OfflineRooms room, MultipartFile avatarFile) throws IOException {
        roomsDAO.addOfflineRoom(room, avatarFile);
    }

    @Override
    public List<OfflineRooms> getPaginatedOfflineRooms(int firstResult, int pageSize, String sortOrder) {
        return roomsDAO.getPaginatedOfflineRooms(firstResult, pageSize, sortOrder);
    }

    @Override
    public List<OnlineRooms> getPaginatedOnlineRooms(int firstResult, int pageSize, String sortOrder) {
        return roomsDAO.getPaginatedOnlineRooms(firstResult, pageSize, sortOrder);
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

    @Override
    public String generateUniqueRoomId(boolean isOffline) {
        return roomsDAO.generateUniqueRoomId(isOffline);
    }

    @Override
    public Map<String, String> validateOfflineRoom(OfflineRooms room, String address, MultipartFile avatarFile) {
        return roomsDAO.validateOfflineRoom(room, address, avatarFile);
    }

    @Override
    public Map<String, String> validateOnlineRoom(OnlineRooms room, String link, MultipartFile avatarFile) {
        return roomsDAO.validateOnlineRoom(room, link, avatarFile);
    }
}
