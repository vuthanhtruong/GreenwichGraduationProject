package com.example.demo.room.dao;

import com.example.demo.room.model.OfflineRooms;
import com.example.demo.room.model.OnlineRooms;
import com.example.demo.room.model.Rooms;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface RoomsDAO {

    Rooms getRoomById(String id);

    Rooms getByName(String name);

    boolean existsByRoomExcludingName(String roomName, String roomId);

    Rooms updateOfflineRoom(String id, OfflineRooms room, MultipartFile avatarFile) throws IOException;

    Rooms updateOnlineRoom(String id, OnlineRooms room, MultipartFile avatarFile) throws IOException;

    String generateUniqueJitsiMeetLink(String roomId);

    String generateRandomPassword(int length);

    String generateRandomString(int length);

    boolean isJitsiLinkExists(String link);

    Boolean existsOfflineRoomsById(String id);

    Boolean existsOnlineRoomsById(String id);

    void deleteOnlineRoom(String id);

    void deleteOfflineRoom(String id);

    void addOnlineRoom(OnlineRooms room, MultipartFile avatarFile) throws IOException;

    void addOfflineRoom(OfflineRooms room, MultipartFile avatarFile) throws IOException;

    List<OfflineRooms> getPaginatedOfflineRooms(int firstResult, int pageSize, String sortOrder);

    List<OnlineRooms> getPaginatedOnlineRooms(int firstResult, int pageSize, String sortOrder);

    List<Rooms> getRooms();

    List<OnlineRooms> getOnlineRooms();

    List<OfflineRooms> getOfflineRooms();

    long totalOfflineRooms();

    long totalOnlineRooms();

    String generateUniqueRoomId(boolean isOffline);

    Map<String, String> validateOfflineRoom(OfflineRooms room, String address, MultipartFile avatarFile);

    Map<String, String> validateOnlineRoom(OnlineRooms room, String link, MultipartFile avatarFile);
}
