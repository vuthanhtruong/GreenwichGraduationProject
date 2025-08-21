package com.example.demo.news.controller;

import com.example.demo.document.model.Documents;
import com.example.demo.news.model.News;
import com.example.demo.document.service.DocumentsService;
import com.example.demo.news.service.NewsService;
import com.example.demo.majorstaff.service.StaffsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/staff-home/news-list")
@PreAuthorize("hasRole('STAFF')")
public class EditNewsController {

    private static final Logger logger = LoggerFactory.getLogger(EditNewsController.class);

    private final NewsService newsService;
    private final DocumentsService DocumentsSevice;
    private final StaffsService staffsService;

    @Value("${file.upload-dir:/uploads}")
    private String uploadDir;

    @Autowired
    public EditNewsController(NewsService newsService,  DocumentsService DocumentsSevice, StaffsService staffsService) {
        if (newsService == null || DocumentsSevice == null || staffsService == null) {
            throw new IllegalArgumentException("Services cannot be null");
        }
        this.newsService = newsService;
        this.DocumentsSevice = DocumentsSevice;
        this.staffsService = staffsService;
    }

    @PostMapping("/edit-news-form")
    public String showEditForm(@RequestParam("id") String postId, Model model) {
        News news = newsService.getNewsById(postId);
        news.setDocuments(DocumentsSevice.getDocumentsByNews(news));
        model.addAttribute("news", news);
        return "EditNewsForm";
    }

    @PutMapping("/edit-news-form")
    public String updateNews(@Valid @ModelAttribute("news") News news,
                             BindingResult bindingResult,
                             @RequestParam(value = "deleteDocIds", required = false) List<String> deleteDocIds,
                             @RequestParam(value = "newFiles", required = false) List<MultipartFile> newFiles,
                             Model model) {
        logger.info("Updating news with postId: {}", news.getPostId());

        if (bindingResult.hasErrors()) {
            logger.error("Validation errors: {}", bindingResult.getAllErrors());
            model.addAttribute("errorMessage", "Invalid news data: " + bindingResult.getAllErrors().get(0).getDefaultMessage());
            model.addAttribute("news", news);
            return "EditNewsForm";
        }

        try {
            // Validate News fields
            if (news.getTitle() == null || news.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("News title cannot be empty");
            }

            // Create upload directory if it doesn't exist
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            // Handle deletion of documents
            if (deleteDocIds != null && !deleteDocIds.isEmpty()) {
                newsService.deleteDocuments(news.getPostId(), deleteDocIds);
                // Cập nhật danh sách tài liệu sau khi xóa
                news.setDocuments(DocumentsSevice.getDocumentsByNews(news));
            }

            // Handle new documents
            List<Documents> newDocuments = new ArrayList<>();
            if (newFiles != null && !newFiles.isEmpty()) {
                for (MultipartFile file : newFiles) {
                    if (!file.isEmpty()) {
                        if (file.getSize() > 5 * 1024 * 1024) {
                            throw new IllegalArgumentException("File " + file.getOriginalFilename() + " exceeds 5MB limit");
                        }
                        if (!file.getContentType().matches("application/pdf|application/msword|application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                            throw new IllegalArgumentException("File " + file.getOriginalFilename() + " must be PDF or Word");
                        }
                        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                        String filePath = uploadDir + File.separator + uniqueFileName;
                        file.transferTo(new File(filePath));
                        Documents doc = new Documents();
                        doc.setDocumentId(UUID.randomUUID().toString());
                        doc.setDocumentTitle(file.getOriginalFilename());
                        doc.setFilePath(filePath);
                        doc.setPost(news);
                        newDocuments.add(doc);
                    }
                }
                // Thêm tài liệu mới
                news.getDocuments().addAll(newDocuments);
            }

            // Update news and documents
            newsService.updateNews(news, newDocuments);
            model.addAttribute("successMessage", "News updated successfully!");
        } catch (IOException e) {
            logger.error("File upload error: {}", e.getMessage());
            model.addAttribute("errorMessage", "Failed to update news due to file upload error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            model.addAttribute("errorMessage", "Failed to update news: " + e.getMessage());
        }
        return "EditNewsForm";
    }
}