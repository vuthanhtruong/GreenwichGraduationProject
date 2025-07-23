package com.example.demo.service;

import com.example.demo.entity.OfflineRooms;
import com.example.demo.entity.OnlineRooms;
import com.example.demo.entity.Rooms;

import java.util.List;

public interface RoomsService {
    List<Rooms> getRooms();
    List<OnlineRooms> getOnlineRooms();
    List<OfflineRooms> getOfflineRooms();
    long totalOfflineRooms();
    long totalOnlineRooms();
    List<OfflineRooms> getPaginatedOfflineRooms(int firstResult, int pageSize, String sortOrder);
    List<OnlineRooms> getPaginatedOnlineRooms(int firstResult, int pageSize, String sortOrder);
    void addOnlineRoom(OnlineRooms rooms);
    void addOfflineRoom(OfflineRooms rooms);
    Boolean existsOnlineRoomsById(String id);
}
