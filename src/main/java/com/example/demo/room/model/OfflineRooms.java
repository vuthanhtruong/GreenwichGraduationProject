package com.example.demo.room.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "OfflineRooms")
@PrimaryKeyJoinColumn(name = "RoomID") // Liên kết với ID từ Room
@Getter
@Setter
@NoArgsConstructor
public class OfflineRooms extends Rooms {
    @Column(name = "Address", nullable = true, length = 500)
    private String address;
}
