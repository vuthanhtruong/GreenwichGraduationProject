package com.example.demo.dao;

import com.example.demo.entity.OfflineRooms;
import com.example.demo.entity.OnlineRooms;
import com.example.demo.entity.Rooms;
import com.example.demo.entity.Students;

import java.util.List;

public interface RoomsDAO {
    List<Rooms> getRooms();
    List<OnlineRooms> getOnlineRooms();
    List<OfflineRooms> getOfflineRooms();
    long totalOfflineRooms();
    long totalOnlineRooms();
    List<OfflineRooms> getPaginatedOfflineRooms(int firstResult, int pageSize,String sortOrder);
    List<OnlineRooms> getPaginatedOnlineRooms(int firstResult, int pageSize,String sortOrder);
    void addOnlineRoom(OnlineRooms rooms);
    void addOfflineRoom(OfflineRooms rooms);
    Boolean existsOnlineRoomsById(String id);
    Boolean existsOfflineRoomsById(String id);
    void deleteOnlineRoom(String id);
    void deleteOfflineRoom(String id);
    boolean isJitsiLinkExists(String link);
    String generateRandomPassword(int length);
    String generateRandomString(int length);
    String generateUniqueJitsiMeetLink(String roomId);
    Rooms updateOfflineRoom(String id, OfflineRooms room);
    Rooms updateOnlineRoom(String id, OnlineRooms room);
    Rooms getByName(String name);
    Rooms getRoomById(String id);
}
