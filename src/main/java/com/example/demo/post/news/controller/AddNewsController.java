package com.example.demo.post.news.controller;

import com.example.demo.document.service.DocumentsService;
import com.example.demo.post.news.model.News;
import com.example.demo.post.news.service.NewsService;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;

@Controller
@RequestMapping("/staff-home/news-list")
@PreAuthorize("hasRole('STAFF')")
public class AddNewsController {

    private final NewsService newsService;
    private final StaffsService staffsService;
    private final DocumentsService documentsService;

    public AddNewsController(NewsService newsService, StaffsService staffsService, DocumentsService documentsService) {
        this.newsService = newsService;
        this.staffsService = staffsService;
        this.documentsService = documentsService;
    }

    @PostMapping("/add-news")
    public String addNews(
            @Valid @ModelAttribute("newNews") News news,
            BindingResult bindingResult,
            @RequestParam(value = "uploadFiles", required = false) MultipartFile[] files,
            Model model,
            RedirectAttributes ra) {

        var errors = new HashMap<String, String>();
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        }
        errors.putAll(newsService.validateNews(news));

        if (!errors.isEmpty()) {
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("newNews", news);
            return "forward:/staff-home/news-list";
        }

        try {
            String postId = newsService.generateUniqueNewsId(staffsService.getStaff());
            news.setPostId(postId);
            news.setCreator(staffsService.getStaff());
            news.setCreatedAt(LocalDate.now().atStartOfDay());

            documentsService.addDocuments(news, files);

            newsService.addNews(news);
            ra.addFlashAttribute("message", "News added successfully!");
            ra.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            errors.put("general", "Error: " + e.getMessage());
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("newNews", news);
            return "forward:/staff-home/news-list";
        }

        return "redirect:/staff-home/news-list";
    }
}