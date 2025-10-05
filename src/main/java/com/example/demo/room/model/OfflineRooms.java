package com.example.demo.room.model;

import com.example.demo.admin.model.Admins;
import com.example.demo.campus.model.Campuses;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "OfflineRooms")
@PrimaryKeyJoinColumn(name = "RoomID") // Liên kết với ID từ Room
@Getter
@Setter
@NoArgsConstructor
public class OfflineRooms extends Rooms {
    @Column(name = "Address", nullable = true, length = 500)
    private String address;

    @Column(name = "Floor", nullable = true)
    private Integer floor;

    public OfflineRooms(String roomId, String roomName, Admins creator, Campuses campus, LocalDateTime createdAt, byte[] avatar, String address, Integer floor) {
        super(roomId, roomName, creator, campus, createdAt, avatar);
        this.address = address;
        this.floor = floor;
    }
}