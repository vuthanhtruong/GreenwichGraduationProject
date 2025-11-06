package com.example.demo.post.news.controller;

import com.example.demo.post.news.service.NewsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff-home/news-list")
@PreAuthorize("hasRole('STAFF')")
public class DeleteNewsController {

    private final NewsService newsService;

    public DeleteNewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @PostMapping("/delete-news")
    public String deleteNews(@RequestParam("postId") String postId, RedirectAttributes ra) {
        try {
            newsService.deleteNews(postId);
            ra.addFlashAttribute("message", "News deleted successfully!");
            ra.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            ra.addFlashAttribute("message", "Delete failed: " + e.getMessage());
            ra.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/staff-home/news-list";
    }
}