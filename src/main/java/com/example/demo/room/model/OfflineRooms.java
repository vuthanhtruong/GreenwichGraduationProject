package com.example.demo.room.model;

import com.example.demo.user.admin.model.Admins;
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
@PrimaryKeyJoinColumn(name = "RoomID")
@Getter
@Setter
@NoArgsConstructor
public class OfflineRooms extends Rooms {

    @Column(name = "Address", nullable = true, length = 500)
    private String address;

    @Column(name = "Floor", nullable = true)
    private Integer floor;

    public OfflineRooms(String roomId, String roomName, Admins creator, Campuses campus,
                        LocalDateTime createdAt, byte[] avatar, String address, Integer floor) {
        super(roomId, roomName, creator, campus, createdAt, avatar);
        this.address = address;
        this.floor = floor;
    }

    @Override
    public String getLink() {
        return null;
    }

    @Override
    public String getAddress() {
        StringBuilder sb = new StringBuilder();
        if (address != null) sb.append(address);
        if (floor != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("Floor ").append(floor);
        }
        return sb.length() > 0 ? sb.toString() : null;
    }
}