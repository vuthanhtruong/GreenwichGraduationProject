package com.example.demo.emailTemplates.service;

import com.example.demo.emailTemplates.dto.EmailTemplateDTO;
import com.example.demo.emailTemplates.model.EmailTemplates;
import com.example.demo.entity.Enums.EmailTemplateTypes;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EmailTemplatesService {
    Optional<EmailTemplateDTO> findDTOByType(EmailTemplateTypes type);
    Optional<EmailTemplates> findById(Integer id);
    Optional<EmailTemplates> findByType(EmailTemplateTypes type);
    Optional<EmailTemplates> findByTypeAndCampus(EmailTemplateTypes type, String campusId); // New method
    List<EmailTemplates> findAll();
    List<EmailTemplates> getPaginatedTemplates(int firstResult, int pageSize);
    long numberOfTemplates();
    void addTemplate(EmailTemplates template, MultipartFile headerImageFile, MultipartFile bannerImageFile);
    void updateTemplate(Integer id, EmailTemplates template, MultipartFile headerImageFile, MultipartFile bannerImageFile);
    void deleteTemplate(Integer id);
    Map<String, String> templateValidation(EmailTemplates template, MultipartFile headerImageFile, MultipartFile bannerImageFile);

}
