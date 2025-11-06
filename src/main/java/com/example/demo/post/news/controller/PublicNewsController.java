package com.example.demo.post.news.controller;

import com.example.demo.post.news.model.News;
import com.example.demo.post.news.service.NewsService;
import com.example.demo.user.admin.model.Admins;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.user.person.service.PersonsService;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.student.model.Students;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/check-news")
public class PublicNewsController {

    private final NewsService newsService;
    private final PersonsService personsService;

    public PublicNewsController(NewsService newsService, PersonsService personsService) {
        this.newsService = newsService;
        this.personsService = personsService;
    }

    @GetMapping
    public String showPublicNews(
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String keyword) {

        pageSize = (pageSize != null && pageSize > 0) ? pageSize : 6; // 6 per page (newspaper style)

        List<News> allNews = new ArrayList<>();
        long totalItems = 0;

        if (keyword != null && !keyword.trim().isEmpty()) {
            totalItems = newsService.countPublicSearch(keyword);
            int firstResult = (page - 1) * pageSize;
            allNews = newsService.searchPublicNews(keyword, firstResult, pageSize);
            model.addAttribute("keyword", keyword);
        } else {
            totalItems = newsService.countAllPublicNews();
            int firstResult = (page - 1) * pageSize;
            allNews = newsService.getPublicNewsPaginated(firstResult, pageSize);
        }

        int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / pageSize));
        page = Math.max(1, Math.min(page, totalPages));

        if(personsService.getPerson() instanceof Students){
            model.addAttribute("home", "/student-home");
        } else if (personsService.getPerson() instanceof Staffs) {
            model.addAttribute("home", "/staff-home");
        }
        else if (personsService.getPerson() instanceof DeputyStaffs) {
            model.addAttribute("home", "/deputy-staff-home");
        }
        else if (personsService.getPerson() instanceof Admins) {
            model.addAttribute("home", "/admin-home");
        }
        else if (personsService.getPerson() instanceof MajorLecturers) {
            model.addAttribute("home", "major-lecturer-home");
        }
        else if (personsService.getPerson() instanceof MinorLecturers) {
            model.addAttribute("home", "minor-lecturer-home");
        }

        model.addAttribute("newsList", allNews);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalNews", totalItems);

        if (totalItems == 0) {
            model.addAttribute("message", keyword != null ? "No news found." : "No news available.");
        }

        return "PublicNews";
    }
}