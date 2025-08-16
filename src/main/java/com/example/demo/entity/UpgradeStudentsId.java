package com.example.demo.entity;

import com.example.demo.entity.Enums.UpgradeStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
public class UpgradeStudentsId implements Serializable {

    @Column(name = "StudentID", nullable = false)
    private String studentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "UpgradeStatus", nullable = false)
    private UpgradeStatus upgradeStatus;

    public UpgradeStudentsId() {}

    public UpgradeStudentsId(String studentId, UpgradeStatus upgradeStatus) {
        this.studentId = studentId;
        this.upgradeStatus = upgradeStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpgradeStudentsId)) return false;
        UpgradeStudentsId that = (UpgradeStudentsId) o;
        return Objects.equals(studentId, that.studentId) &&
                upgradeStatus == that.upgradeStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, upgradeStatus);
    }
}