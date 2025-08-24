package com.example.demo.parentAccount.model;

import com.example.demo.person.model.Persons;
import com.example.demo.entity.Enums.Gender;
import com.example.demo.Staff.model.Staffs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "ParentAccounts")
@PrimaryKeyJoinColumn(name = "ID")
@Getter
@Setter
public class ParentAccounts extends Persons {

    @Column(name = "CreatedDate", nullable = false, updatable = false)
    private LocalDate createdDate = LocalDate.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CreatorID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    public String getDefaultAvatarPath() {
        if (getAvatar() != null) {
            return null; // Avatar exists, no default needed
        }
        return getGender() == Gender.MALE ? "/DefaultAvatar/Parent_Male.png" : "/DefaultAvatar/Parent_Female.png";
    }

    @Override
    public String getRoleType() {
        return "PARENT";
    }

}