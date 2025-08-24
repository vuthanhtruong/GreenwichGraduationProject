package com.example.demo.news.controller;

import com.example.demo.document.model.Documents;
import com.example.demo.news.model.News;
import com.example.demo.news.service.NewsService;
import com.example.demo.Staff.service.StaffsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home")
@PreAuthorize("hasRole('STAFF')")
public class AddNewsController {

    private static final Logger logger = LoggerFactory.getLogger(AddNewsController.class);

    private final NewsService newsService;
    private final StaffsService staffsService;

    @Autowired
    public AddNewsController(NewsService newsService, StaffsService staffsService) {
        if (newsService == null || staffsService == null) {
            throw new IllegalArgumentException("Services cannot be null");
        }
        this.newsService = newsService;
        this.staffsService = staffsService;
    }

    @PostMapping("/news-list/add-news")
    public String addNews(@Valid @ModelAttribute("newNews") News news,
                          BindingResult bindingResult,
                          @RequestParam(value = "files", required = false) List<MultipartFile> files,
                          Model model) {
        logger.info("Processing addNews request with title: {}", news.getTitle());

        // Check for validation errors in News
        if (bindingResult.hasErrors()) {
            logger.error("Validation errors: {}", bindingResult.getAllErrors());
            model.addAttribute("errorMessage", "Invalid news data: " + bindingResult.getAllErrors().get(0).getDefaultMessage());
            model.addAttribute("newNews", news);
            model.addAttribute("newsList", newsService.getNewsByMajor(staffsService.getStaffMajor()));
            return "NewsList";
        }

        try {
            // Validate News fields
            if (news.getTitle() == null || news.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("News title cannot be empty");
            }

            // Convert MultipartFile to List<Documents>
            List<Documents> documents = new ArrayList<>();
            if (files != null && !files.isEmpty()) {
                logger.info("Received {} files", files.size());
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        // Validate file size and type
                        if (file.getSize() > 5 * 1024 * 1024) { // 5MB limit
                            throw new IllegalArgumentException("File " + file.getOriginalFilename() + " exceeds 5MB limit");
                        }
                        if (!file.getContentType().matches("application/pdf|application/msword|application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                            throw new IllegalArgumentException("File " + file.getOriginalFilename() + " must be PDF or Word");
                        }
                        Documents doc = new Documents();
                        doc.setDocumentTitle(file.getOriginalFilename());
                        doc.setFilePath(file.getOriginalFilename()); // Update with actual storage path if needed
                        doc.setFileData(file.getBytes());
                        documents.add(doc);
                    }
                }
            }

            // Call service to save News and Documents
            newsService.addNews(news, documents);
            logger.info("News added successfully with {} documents", documents.size());
            model.addAttribute("successMessage", "News added successfully!");
        } catch (IOException e) {
            logger.error("File upload error: {}", e.getMessage());
            model.addAttribute("errorMessage", "Failed to add news due to file upload error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            model.addAttribute("errorMessage", "Failed to add news: " + e.getMessage());
        } catch (DataIntegrityViolationException e) {
            logger.error("Database error: {}", e.getMessage());
            model.addAttribute("errorMessage", "Failed to add news due to database error: File size too large for storage");
        }

        // Refresh model for form and list
        model.addAttribute("newNews", new News());
        model.addAttribute("newsList", newsService.getNewsByMajor(staffsService.getStaffMajor()));
        return "NewsList";
    }
}