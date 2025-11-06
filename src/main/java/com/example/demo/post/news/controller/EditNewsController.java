package com.example.demo.post.news.controller;

import com.example.demo.document.service.DocumentsService;
import com.example.demo.post.news.model.News;
import com.example.demo.post.news.service.NewsService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/staff-home/news-list")
@PreAuthorize("hasRole('STAFF')")
public class EditNewsController {

    private final NewsService newsService;
    private final DocumentsService documentsService;

    public EditNewsController(NewsService newsService, DocumentsService documentsService) {
        this.newsService = newsService;
        this.documentsService = documentsService;
    }

    @GetMapping("/edit-news-form")
    public String showEditForm(@RequestParam("postId") String postId, Model model, RedirectAttributes ra) {
        News news = newsService.getNewsById(postId);
        if (news == null) {
            ra.addFlashAttribute("message", "News not found.");
            ra.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/staff-home/news-list";
        }
        model.addAttribute("editNews", news);
        return "EditNews";
    }

    @PostMapping("/edit-news")
    public String editNews(
            @Valid @ModelAttribute("editNews") News editNews,
            BindingResult bindingResult,
            @RequestParam(value = "uploadFiles", required = false) MultipartFile[] files,
            @RequestParam("postId") String postId,
            @RequestParam(value = "deleteDocIds", required = false) List<String> deleteDocIds,
            Model model,
            RedirectAttributes ra) {

        editNews.setPostId(postId);
        News existing = newsService.getNewsById(postId);
        if (existing == null) {
            ra.addFlashAttribute("message", "News not found.");
            ra.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/staff-home/news-list";
        }

        var errors = new HashMap<String, String>();
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        }
        errors.putAll(newsService.validateNews(editNews));

        if (!errors.isEmpty()) {
            model.addAttribute("editNews", editNews);
            model.addAttribute("errors", errors);
            return "EditNews";
        }

        try {
            existing.setTitle(editNews.getTitle());
            existing.setContent(editNews.getContent());

            if (deleteDocIds != null && !deleteDocIds.isEmpty()) {
                documentsService.deleteDocuments(deleteDocIds);
                existing.getDocuments().removeIf(d -> deleteDocIds.contains(d.getDocumentId()));
            }

            documentsService.addDocuments(existing, files);

            newsService.updateNews(existing);
            ra.addFlashAttribute("message", "News updated successfully!");
            ra.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            errors.put("general", "Error: " + e.getMessage());
            model.addAttribute("editNews", editNews);
            model.addAttribute("errors", errors);
            return "EditNews";
        }

        return "redirect:/staff-home/news-list";
    }
}