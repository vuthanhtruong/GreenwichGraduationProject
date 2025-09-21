package com.example.demo.EmailTemplates.model;

import com.example.demo.admin.model.Admins;
import com.example.demo.campus.model.Campuses;
import com.example.demo.entity.Enums.EmailTemplateTypes;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "EmailTemplates")
@Getter
@Setter
public class EmailTemplates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50, nullable = false, unique = true)
    private EmailTemplateTypes type;

    // 🖼 Header image lưu trực tiếp dưới dạng BLOB
    @Lob
    @Column(name = "header_image", columnDefinition = "LONGBLOB")
    private byte[] headerImage;

    @Column(name = "greeting", length = 255)
    private String greeting;

    @Column(name = "salutation", length = 255)
    private String salutation;

    @Lob
    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Column(name = "link_cta", length = 255)
    private String linkCta;

    @Column(name = "support", length = 255)
    private String support;

    @Column(name = "copyright_notice", length = 255)
    private String copyrightNotice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campus_id", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Campuses campus;

    @Column(name = "link_facebook", length = 255)
    private String linkFacebook;

    @Column(name = "link_youtube", length = 255)
    private String linkYoutube;

    @Column(name = "link_tiktok", length = 255)
    private String linkTiktok;

    @Lob
    @Column(name = "banner_image", columnDefinition = "LONGBLOB")
    private byte[] bannerImage;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Admins creator;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
