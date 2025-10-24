package com.example.demo.emailTemplates.service;

import com.example.demo.emailTemplates.dao.EmailTemplatesDAO;
import com.example.demo.emailTemplates.dto.EmailTemplateDTO;
import com.example.demo.emailTemplates.model.EmailTemplates;
import com.example.demo.entity.Enums.EmailTemplateTypes;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EmailTemplatesServiceImpl implements EmailTemplatesService {
    @Override
    public Optional<EmailTemplateDTO> findDTOByType(EmailTemplateTypes type) {
        return emailTemplatesDAO.findDTOByType(type);
    }

    @Override
    public Optional<EmailTemplates> findByTypeAndCampus(EmailTemplateTypes type, String campusId) {
        return emailTemplatesDAO.findByTypeAndCampus(type, campusId);
    }

    @Override
    public List<EmailTemplates> getPaginatedTemplates(int firstResult, int pageSize) {
        return emailTemplatesDAO.getPaginatedTemplates(firstResult, pageSize);
    }

    @Override
    public long numberOfTemplates() {
        return emailTemplatesDAO.numberOfTemplates();
    }

    @Override
    public void addTemplate(EmailTemplates template, MultipartFile headerImageFile, MultipartFile bannerImageFile) {
        emailTemplatesDAO.addTemplate(template, headerImageFile, bannerImageFile);
    }

    @Override
    public void updateTemplate(Integer id, EmailTemplates template, MultipartFile headerImageFile, MultipartFile bannerImageFile) {
        emailTemplatesDAO.updateTemplate(id, template, headerImageFile, bannerImageFile);
    }

    @Override
    public void deleteTemplate(Integer id) {
        emailTemplatesDAO.deleteTemplate(id);
    }

    @Override
    public Map<String, String> templateValidation(EmailTemplates template, MultipartFile headerImageFile, MultipartFile bannerImageFile) {
        return emailTemplatesDAO.templateValidation(template, headerImageFile, bannerImageFile);
    }

    private final EmailTemplatesDAO emailTemplatesDAO;

    public EmailTemplatesServiceImpl(EmailTemplatesDAO emailTemplatesDAO) {
        this.emailTemplatesDAO = emailTemplatesDAO;
    }

    @Override
    public Optional<EmailTemplates> findById(Integer id) {
        return emailTemplatesDAO.findById(id);
    }

    @Override
    public Optional<EmailTemplates> findByType(EmailTemplateTypes type) {
        return emailTemplatesDAO.findByType(type);
    }

    @Override
    public List<EmailTemplates> findAll() {
        return emailTemplatesDAO.findAll();
    }


}
