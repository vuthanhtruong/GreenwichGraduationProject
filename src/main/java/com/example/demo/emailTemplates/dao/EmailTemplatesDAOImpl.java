package com.example.demo.emailTemplates.dao;

import com.example.demo.emailTemplates.dto.EmailTemplateDTO;
import com.example.demo.emailTemplates.model.EmailTemplates;
import com.example.demo.user.admin.service.AdminsService;
import com.example.demo.campus.dao.CampusesDAO;
import com.example.demo.entity.Enums.EmailTemplateTypes;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Repository
@Transactional
public class EmailTemplatesDAOImpl implements EmailTemplatesDAO {
    @Override
    public Optional<EmailTemplateDTO> findDTOByType(EmailTemplateTypes type) {
        try {
            EmailTemplates template = entityManager.createQuery(
                            "SELECT et FROM EmailTemplates et " +
                                    "LEFT JOIN FETCH et.campus " +
                                    "LEFT JOIN FETCH et.creator " +
                                    "WHERE et.type = :type",
                            EmailTemplates.class)
                    .setParameter("type", type)
                    .getSingleResult();
            return Optional.of(new EmailTemplateDTO(template));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(EmailTemplatesDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final AdminsService adminsService;

    public EmailTemplatesDAOImpl(CampusesDAO campusesDAO, AdminsService adminsService) {
        this.adminsService = adminsService;
    }

    @Override
    public Optional<EmailTemplates> findByTypeAndCampus(EmailTemplateTypes type, String campusId) {
        try {
            EmailTemplates template = entityManager.createQuery(
                            "SELECT et FROM EmailTemplates et " +
                                    "LEFT JOIN FETCH et.campus " +
                                    "LEFT JOIN FETCH et.creator " +
                                    "WHERE et.type = :type AND et.campus.id = :campusId",
                            EmailTemplates.class)
                    .setParameter("type", type)
                    .setParameter("campusId", campusId)
                    .getSingleResult();
            return Optional.of(template);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<EmailTemplates> findById(Integer id) {
        try {
            EmailTemplates template = entityManager.createQuery(
                            "SELECT et FROM EmailTemplates et " +
                                    "LEFT JOIN FETCH et.campus " +
                                    "LEFT JOIN FETCH et.creator " +
                                    "WHERE et.id = :id",
                            EmailTemplates.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(template);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<EmailTemplates> findByType(EmailTemplateTypes type) {
        try {
            EmailTemplates template = entityManager.createQuery(
                            "SELECT et FROM EmailTemplates et " +
                                    "LEFT JOIN FETCH et.campus " +
                                    "LEFT JOIN FETCH et.creator " +
                                    "WHERE et.type = :type",
                            EmailTemplates.class)
                    .setParameter("type", type)
                    .getSingleResult();
            return Optional.of(template);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<EmailTemplates> findAll() {
        return entityManager.createQuery(
                        "SELECT DISTINCT et FROM EmailTemplates et " +
                                "LEFT JOIN FETCH et.campus " +
                                "LEFT JOIN FETCH et.creator " +
                                "ORDER BY et.id ASC",
                        EmailTemplates.class)
                .getResultList();
    }

    @Override
    public List<EmailTemplates> getPaginatedTemplates(int firstResult, int pageSize) {
        return entityManager.createQuery(
                        "SELECT DISTINCT et FROM EmailTemplates et " +
                                "LEFT JOIN FETCH et.campus " +
                                "LEFT JOIN FETCH et.creator " +
                                "ORDER BY et.id ASC",
                        EmailTemplates.class)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long numberOfTemplates() {
        return entityManager.createQuery("SELECT COUNT(et) FROM EmailTemplates et", Long.class)
                .getSingleResult();
    }

    @Override
    public void addTemplate(EmailTemplates template, MultipartFile headerImageFile, MultipartFile bannerImageFile) {
        try {
            if (headerImageFile != null && !headerImageFile.isEmpty()) {
                template.setHeaderImage(headerImageFile.getBytes());
            }
            if (bannerImageFile != null && !bannerImageFile.isEmpty()) {
                template.setBannerImage(bannerImageFile.getBytes());
            }
        } catch (IOException e) {
            logger.error("Failed to process images for template {}: {}", template.getType(), e.getMessage());
            throw new RuntimeException("Failed to process images: " + e.getMessage());
        }

        template.setCreatedAt(java.time.LocalDateTime.now());
        template.setCreator(adminsService.getAdmin());
        template.setCampus(adminsService.getAdmin().getCampus());
        entityManager.persist(template);
    }

    @Override
    public void updateTemplate(Integer id, EmailTemplates template, MultipartFile headerImageFile, MultipartFile bannerImageFile) {
        EmailTemplates existing = entityManager.find(EmailTemplates.class, id);
        if (existing == null) {
            throw new IllegalArgumentException("Email template with ID " + id + " not found");
        }

        updateTemplateFields(existing, template);

        try {
            if (headerImageFile != null && !headerImageFile.isEmpty()) {
                existing.setHeaderImage(headerImageFile.getBytes());
            }
            if (bannerImageFile != null && !bannerImageFile.isEmpty()) {
                existing.setBannerImage(bannerImageFile.getBytes());
            }
        } catch (IOException e) {
            logger.error("Failed to process images for template {}: {}", existing.getType(), e.getMessage());
            throw new RuntimeException("Failed to process images: " + e.getMessage());
        }

        existing.setUpdatedAt(java.time.LocalDateTime.now());
        entityManager.merge(existing);
    }

    @Override
    public void deleteTemplate(Integer id) {
        EmailTemplates template = entityManager.find(EmailTemplates.class, id);
        if (template == null) {
            throw new IllegalArgumentException("Email template with ID " + id + " not found");
        }
        entityManager.remove(template);
    }

    @Override
    public Map<String, String> templateValidation(EmailTemplates template, MultipartFile headerImageFile, MultipartFile bannerImageFile) {
        Map<String, String> errors = new HashMap<>();

        if (template.getType() == null) {
            errors.put("type", "Template type is required.");
        } else if (template.getId() == null && findByType(template.getType()).isPresent()) {
            errors.put("type", "A template for this type already exists.");
        }

        if (headerImageFile != null && !headerImageFile.isEmpty()) {
            String contentType = headerImageFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                errors.put("headerImageFile", "Header image must be an image file.");
            }
            if (headerImageFile.getSize() > 5 * 1024 * 1024) {
                errors.put("headerImageFile", "Header image file size must not exceed 5MB.");
            }
        }

        if (bannerImageFile != null && !bannerImageFile.isEmpty()) {
            String contentType = bannerImageFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                errors.put("bannerImageFile", "Banner image must be an image file.");
            }
            if (bannerImageFile.getSize() > 5 * 1024 * 1024) {
                errors.put("bannerImageFile", "Banner image file size must not exceed 5MB.");
            }
        }

        return errors;
    }

    private void updateTemplateFields(EmailTemplates existing, EmailTemplates updated) {
        if (updated.getType() != null) existing.setType(updated.getType());
        if (updated.getGreeting() != null) existing.setGreeting(updated.getGreeting());
        if (updated.getSalutation() != null) existing.setSalutation(updated.getSalutation());
        if (updated.getBody() != null) existing.setBody(updated.getBody());
        if (updated.getLinkCta() != null) existing.setLinkCta(updated.getLinkCta());
        if (updated.getSupport() != null) existing.setSupport(updated.getSupport());
        if (updated.getCopyrightNotice() != null) existing.setCopyrightNotice(updated.getCopyrightNotice());
        if (updated.getCampus() != null) existing.setCampus(updated.getCampus());
        if (updated.getLinkFacebook() != null) existing.setLinkFacebook(updated.getLinkFacebook());
        if (updated.getLinkYoutube() != null) existing.setLinkYoutube(updated.getLinkYoutube());
        if (updated.getLinkTiktok() != null) existing.setLinkTiktok(updated.getLinkTiktok());
    }
}
