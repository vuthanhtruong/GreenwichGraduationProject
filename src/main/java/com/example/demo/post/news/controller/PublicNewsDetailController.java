package com.example.demo.post.news.controller;

import com.example.demo.comment.model.PublicComments;
import com.example.demo.entity.Enums.Notifications;
import com.example.demo.post.news.model.News;
import com.example.demo.post.news.service.NewsService;
import com.example.demo.user.person.model.Persons;
import com.example.demo.user.person.service.PersonsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/check-news")
public class PublicNewsDetailController {

    private final NewsService newsService;
    private final PersonsService personService;

    public PublicNewsDetailController(NewsService newsService, PersonsService personService) {
        this.newsService = newsService;
        this.personService = personService;
    }

    // === GET: Detail page (no ID in URL) ===
    @GetMapping("/detail")
    public String showNewsDetail(@RequestParam String postId, Model model, RedirectAttributes ra) {
        News news = newsService.getNewsByIdWithComments(postId);
        if (news == null) {
            ra.addFlashAttribute("message", "News not found.");
            ra.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/check-news";
        }
        model.addAttribute("news", news);
        return "PublicNewsDetail";
    }

    // === POST: Add comment (hidden postId) ===
    @PostMapping("/comment")
    public String addComment(
            @RequestParam String postId,
            @RequestParam String content,
            RedirectAttributes ra,
            HttpServletRequest request) {

        if (content == null || content.trim().length() < 5 || content.length() > 1000) {
            ra.addFlashAttribute("error", "Comment must be 5–1000 characters.");
            return "redirect:/check-news/detail?postId=" + postId;
        }

        try {
            Persons currentUser = personService.getPerson();
            if (currentUser == null) {
                ra.addFlashAttribute("error", "Please log in to comment.");
                return "redirect:/check-news/detail?postId=" + postId;
            }

            News news = newsService.getNewsById(postId);
            if (news == null) {
                ra.addFlashAttribute("error", "News not found.");
                return "redirect:/check-news";
            }

            PublicComments comment = new PublicComments();
            comment.setCommentId("COM" + System.currentTimeMillis());
            comment.setCommenter(currentUser);
            comment.setPost(news);
            comment.setContent(content.trim());
            comment.setCreatedAt(LocalDateTime.now());
            comment.setNotification(Notifications.NOTIFICATION_006);

            news.addComment(comment);
            newsService.updateNews(news);

            ra.addFlashAttribute("message", "Comment posted!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to post comment.");
        }

        return "redirect:/check-news/detail?postId=" + postId;
    }
}