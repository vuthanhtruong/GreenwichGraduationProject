package com.example.demo.entity.AbstractClasses;

import com.example.demo.entity.Staffs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // Chiến lược tạo bảng riêng cho từng lớp con
@Table(name = "Rooms") // Bảng chung cho các loại phòng
@Getter
@Setter
@NoArgsConstructor

public abstract class Rooms {
    @Id
    @Column(name = "RoomID")
    private String roomId;

    @Column(name = "RoomName", nullable = true, length = 255)
    private String roomName;

    @ManyToOne
    @JoinColumn(name = "Creator", nullable = true, foreignKey = @ForeignKey(name = "FK_Room_Employee"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

}
