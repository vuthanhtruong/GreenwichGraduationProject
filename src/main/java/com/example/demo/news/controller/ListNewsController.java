package com.example.demo.news.controller;

import com.example.demo.news.model.News;
import com.example.demo.document.model.Documents;
import com.example.demo.Staff.service.StaffsService;
import com.example.demo.news.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

@Controller
@RequestMapping("/staff-home")
@PreAuthorize("hasRole('STAFF')")
public class ListNewsController {

    private final NewsService newsService;
    private final StaffsService staffsService;

    @Autowired
    public ListNewsController(NewsService newsService, StaffsService staffsService) {
        if (newsService == null || staffsService == null) {
            throw new IllegalArgumentException("Services cannot be null");
        }
        this.newsService = newsService;
        this.staffsService = staffsService;
    }

    @GetMapping("/news-list")
    public String showNewsList(Model model) {
        model.addAttribute("newNews", new News());
        model.addAttribute("newsList", newsService.getNewsByMajor(staffsService.getStaffMajor()));
        model.addAttribute("documents", new ArrayList<Documents>());
        return "NewsList";
    }
}