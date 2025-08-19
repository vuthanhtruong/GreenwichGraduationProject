package com.example.demo.dao;

import com.example.demo.entity.OfflineRooms;
import com.example.demo.entity.OnlineRooms;
import com.example.demo.entity.AbstractClasses.Rooms;

import java.io.IOException;
import java.util.List;

public interface RoomsDAO {
    Rooms getRoomById(String id);
    Rooms getByName(String name);
    Rooms updateOfflineRoom(String id, OfflineRooms room);
    Rooms updateOnlineRoom(String id, OnlineRooms room);
    String generateUniqueJitsiMeetLink(String roomId);
    String generateRandomPassword(int length);
    String generateRandomString(int length);
    boolean isJitsiLinkExists(String link);
    Boolean existsOfflineRoomsById(String id);
    void deleteOnlineRoom(String id);
    void deleteOfflineRoom(String id);
    Boolean existsOnlineRoomsById(String id);
    void addOnlineRoom(OnlineRooms room);
    void addOfflineRoom(OfflineRooms room);
    List<OfflineRooms> getPaginatedOfflineRooms(int firstResult, int pageSize, String sortOrder);
    List<OnlineRooms> getPaginatedOnlineRooms(int firstResult, int pageSize, String sortOrder);
    List<Rooms> getRooms();
    List<OnlineRooms> getOnlineRooms();
    List<OfflineRooms> getOfflineRooms();
    long totalOfflineRooms();
    long totalOnlineRooms();
    String generateUniqueRoomId(boolean isOffline);
    boolean existsByRoomExcludingName(String roomName, String excludeId);
    List<String> validateOnlineRoom(OnlineRooms room, String link);
    List<String> validateOfflineRoom(OfflineRooms room, String address);
}
