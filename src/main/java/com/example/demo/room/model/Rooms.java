package com.example.demo.room.model;

import com.example.demo.user.admin.model.Admins;
import com.example.demo.campus.model.Campuses;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "Rooms")
@Getter
@Setter
@NoArgsConstructor
public abstract class Rooms {

    @Id
    @Column(name = "RoomID")
    private String roomId;

    @Column(name = "RoomName", nullable = true, length = 255)
    private String roomName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = true, foreignKey = @ForeignKey(name = "FK_Room_Admin"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Admins creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CampusID", nullable = true, foreignKey = @ForeignKey(name = "FK_Room_Campus"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Campuses campus;

    @CreationTimestamp
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Lob
    @Column(name = "Avatar", columnDefinition = "LONGBLOB", nullable = true)
    private byte[] avatar;

    // === ABSTRACT: GET LINK (FOR ONLINE) ===
    public abstract String getLink();

    // === ABSTRACT: GET ADDRESS (FOR OFFLINE) ===
    public abstract String getAddress();

    // === CONCRETE: SAFE DISPLAY INFO ===
    public String getRoomDisplayInfo() {
        String link = getLink();
        String address = getAddress();

        StringBuilder sb = new StringBuilder();
        if (link != null && !link.isBlank()) {
            sb.append("Link: ").append(link);
        }
        if (address != null && !address.isBlank()) {
            if (sb.length() > 0) sb.append(" | ");
            sb.append("Address: ").append(address);
        }
        return sb.length() > 0 ? sb.toString() : "No location info";
    }

    public Rooms(String roomId, String roomName, Admins creator, Campuses campus, LocalDateTime createdAt, byte[] avatar) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.creator = creator;
        this.campus = campus;
        this.createdAt = createdAt;
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomId='" + roomId + '\'' +
                ", roomName='" + roomName + '\'' +
                ", display='" + getRoomDisplayInfo() + '\'' +
                ", campus=" + (campus != null ? campus.getCampusId() : null) +
                ", createdAt=" + createdAt +
                '}';
    }
}