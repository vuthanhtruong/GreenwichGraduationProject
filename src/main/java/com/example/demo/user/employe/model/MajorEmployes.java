package com.example.demo.user.employe.model;

import com.example.demo.entity.Enums.Gender;
import com.example.demo.user.person.model.Persons;
import com.example.demo.campus.model.Campuses;
import com.example.demo.major.model.Majors;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.core.io.ClassPathResource;

import java.time.LocalDate;

@Entity
@Table(name = "MajorEmployes")
@PrimaryKeyJoinColumn(name = "ID")
@Getter
@Setter
@OnDelete(action = OnDeleteAction.CASCADE)
public class MajorEmployes extends Persons implements MajorEmployesInterface {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MajorID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Majors majorManagement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CampusID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Campuses campus;

    @Column(nullable = false, updatable = false)
    private LocalDate createdDate = LocalDate.now();

    @Override
    public String getDefaultAvatarPath() {
        if (getAvatar() != null) {
            return null;
        }
        return getGender() == Gender.MALE ? "/DefaultAvatar/Staff_Boy.png" : "/DefaultAvatar/Staff_Girl.png";
    }

    @Override
    public byte[] getAvatarBytes() {
        try {
            // Nếu avatar được lưu trong DB → trả về
            if (getAvatar() != null && getAvatar().length > 0) {
                return getAvatar();
            }

            // Lấy đường dẫn avatar mặc định
            String defaultPath = getDefaultAvatarPath();
            if (defaultPath == null) {
                return null;
            }

            // Load ảnh mặc định từ classpath
            ClassPathResource resource = new ClassPathResource(defaultPath);
            return resource.getInputStream().readAllBytes();

        } catch (Exception e) {
            // Nếu có lỗi (file không tồn tại, lỗi IO,...) → tránh crash
            return null;
        }
    }


    @Override
    public String getEmploymentInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Major Management: ").append(majorManagement != null ? majorManagement.getMajorName() : "N/A").append("\n");
        sb.append("Campus: ").append(campus != null ? campus.getCampusName() : "N/A").append("\n");
        sb.append("Created Date: ").append(createdDate != null ? createdDate.toString() : "N/A");
        return sb.toString();
    }

    @Override
    public String getRoleType() {
        return null;
    }
}