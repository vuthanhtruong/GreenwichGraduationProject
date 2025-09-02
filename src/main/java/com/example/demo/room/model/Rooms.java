package com.example.demo.room.model;

import com.example.demo.staff.model.Staffs;
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
    @JoinColumn(name = "Creator", nullable = true, foreignKey = @ForeignKey(name = "FK_Room_Employee"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    @CreationTimestamp
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
