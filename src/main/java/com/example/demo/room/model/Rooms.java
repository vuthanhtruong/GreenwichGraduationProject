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

    public Rooms(String roomId, String roomName, Admins creator, Campuses campuses, LocalDateTime createdAt, byte[] avatar) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.creator = creator;
        this.campus = campuses;
        this.createdAt = createdAt;
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomId='" + roomId + '\'' +
                ", roomName='" + roomName + '\'' +
                ", creator=" + (creator != null ? creator.getId() : null) +
                ", campus=" + (campus != null ? campus.getCampusId() : null) +
                ", createdAt=" + createdAt +
                ", avatar=" + (avatar != null ? "present" : "null") +
                '}';
    }
}