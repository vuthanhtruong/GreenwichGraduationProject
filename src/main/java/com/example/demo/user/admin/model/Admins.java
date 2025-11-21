package com.example.demo.user.admin.model;

import com.example.demo.campus.model.Campuses;
import com.example.demo.entity.Enums.Gender;
import com.example.demo.user.person.model.Persons;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.core.io.ClassPathResource;

import java.time.LocalDateTime;

@Entity
@Table(name = "Admins")
@PrimaryKeyJoinColumn(name = "ID")
@Getter
@Setter
public class Admins extends Persons {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CampusID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Campuses campus;

    @Lob
    @Column(name = "FaceData", columnDefinition = "LONGTEXT", nullable = true)
    private String faceData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatorID", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Admins creator;

    @Column(name = "CreatedDate", nullable = true)
    private LocalDateTime createdDate;

    @Override
    public String getRoleType() {
        return "ADMIN";
    }

    public String getDefaultAvatarPath() {
        if (getAvatar() != null) {
            return null;
        }
        return getGender() == Gender.MALE ? "/DefaultAvatar/Admin_Male.png" : "/DefaultAvatar/Admin_Female.png";
    }
    @Override
    public byte[] getAvatarBytes() {
        try {
            // Avatar lưu trong DB → trả về
            if (getAvatar() != null && getAvatar().length > 0) {
                return getAvatar();
            }

            // Lấy đường dẫn avatar mặc định
            String defaultPath = getDefaultAvatarPath();
            if (defaultPath == null) {
                return null;
            }

            // Load file avatar mặc định
            ClassPathResource resource = new ClassPathResource(defaultPath);
            return resource.getInputStream().readAllBytes();

        } catch (Exception e) {
            // Không cho hệ thống crash nếu ảnh lỗi hoặc không tồn tại
            return null;
        }
    }

}