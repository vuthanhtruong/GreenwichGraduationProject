package com.example.demo.controller.ReadByStaff;

import com.example.demo.entity.News;
import com.example.demo.entity.Documents;
import com.example.demo.service.StaffsService;
import com.example.demo.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;

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