package com.example.demo.post.news.controller;

import com.example.demo.post.news.model.News;
import com.example.demo.post.news.service.NewsService;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping("/staff-home/news-list")
@PreAuthorize("hasRole('STAFF')")
public class NewsListController {

    private final NewsService newsService;
    private final StaffsService staffsService;

    public NewsListController(NewsService newsService, StaffsService staffsService) {
        this.newsService = newsService;
        this.staffsService = staffsService;
    }

    @GetMapping("")
    public String showNewsList(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword) {

        var creator = staffsService.getStaff();
        if (pageSize == null) {
            pageSize = (Integer) session.getAttribute("newsPageSize");
            if (pageSize == null) pageSize = 10;
        }
        session.setAttribute("newsPageSize", pageSize);

        long totalItems;
        var newsList = new ArrayList<News>();

        if (keyword != null && !keyword.trim().isEmpty() && searchType != null) {
            totalItems = newsService.countSearchResults(searchType, keyword, creator);
            int firstResult = (page - 1) * pageSize;
            newsList = (ArrayList<News>) newsService.searchNews(searchType, keyword, firstResult, pageSize, creator);
            model.addAttribute("keyword", keyword);
            model.addAttribute("searchType", searchType);
        } else {
            totalItems = newsService.countNewsByCreator(creator);
            int firstResult = (page - 1) * pageSize;
            newsList = (ArrayList<News>) newsService.getPaginatedNews(firstResult, pageSize, creator);
        }

        int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / pageSize));
        page = Math.max(1, Math.min(page, totalPages));

        model.addAttribute("newsList", newsList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalNews", totalItems);
        model.addAttribute("newNews", new News());

        if (totalItems == 0) {
            model.addAttribute("message", keyword != null ? "No results found." : "No news yet.");
            model.addAttribute("alertClass", "alert-warning");
        }

        return "NewsList";
    }
}