package com.example.demo.user.minorLecturer.model;

import com.example.demo.user.employe.model.MinorEmployes;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.entity.Enums.EmploymentTypes;
import com.example.demo.entity.Enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.core.io.ClassPathResource;

@Entity
@Table(name = "MinorLecturers")
@PrimaryKeyJoinColumn(name = "ID")
@Getter
@Setter
public class MinorLecturers extends MinorEmployes implements MinorLecturersInterface {

    @Column(name = "Type", nullable = true, length = 50)
    @Enumerated(EnumType.STRING)
    private EmploymentTypes employmentTypes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AddedBy", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DeputyStaffs creator;

    @Override
    public String getDefaultAvatarPath() {
        if (getAvatar() != null) {
            return null; // Avatar exists, no default needed
        }
        return getGender() == Gender.MALE ? "/DefaultAvatar/Teacher_Boy.png" : "/DefaultAvatar/Teacher_Girl.png";
    }

    @Override
    public byte[] getAvatarBytes() {
        try {
            // Nếu đã có avatar trong DB → trả về luôn
            if (getAvatar() != null && getAvatar().length > 0) {
                return getAvatar();
            }

            // Lấy đường dẫn avatar mặc định
            String defaultPath = getDefaultAvatarPath();
            if (defaultPath == null) {
                return null;
            }

            // Load ảnh mặc định từ thư mục resources
            ClassPathResource resource = new ClassPathResource(defaultPath);
            return resource.getInputStream().readAllBytes();

        } catch (Exception e) {
            // Nếu xảy ra lỗi đọc file → tránh crash
            return null;
        }
    }


    @Override
    public String getEmploymentInfo() {
        StringBuilder sb = new StringBuilder(super.getEmploymentInfo());
        sb.append("\nEmployment Type: ").append(employmentTypes != null ? employmentTypes.toString() : "N/A");
        sb.append("\nAdded By: ").append(creator != null ? creator.getFullName() : "N/A");
        return sb.toString();
    }

    @Override
    public String getLecturerInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Employment Type: ").append(employmentTypes != null ? employmentTypes.toString() : "N/A").append("\n");
        sb.append("Added By: ").append(creator != null ? creator.getFullName() : "N/A");
        return sb.toString();
    }

    @Override
    public String getRoleType() {
        return "MINOR_LECTURER";
    }
}