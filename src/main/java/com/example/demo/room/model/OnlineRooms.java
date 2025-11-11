package com.example.demo.room.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "OnlineRooms")
@PrimaryKeyJoinColumn(name = "RoomID")
@Getter
@Setter
@NoArgsConstructor
public class OnlineRooms extends Rooms {

    @Column(name = "Link", nullable = true, length = 500)
    private String link;

    @Override
    public String getLink() {
        return link;
    }

    @Override
    public String getAddress() {
        return null;
    }
}