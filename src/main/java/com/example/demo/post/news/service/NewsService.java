package com.example.demo.post.news.service;

import com.example.demo.document.model.Documents;
import com.example.demo.major.model.Majors;
import com.example.demo.post.news.model.News;
import com.example.demo.user.staff.model.Staffs;

import java.util.List;
import java.util.Map;

public interface NewsService {
    List<News> getPaginatedNews(int firstResult, int pageSize, Staffs creator);
    long countNewsByCreator(Staffs creator);
    News getNewsById(String postId);
    void addNews(News news);
    void updateNews(News news);
    void deleteNews(String postId);
    List<News> searchNews(String searchType, String keyword, int firstResult, int pageSize, Staffs creator);
    long countSearchResults(String searchType, String keyword, Staffs creator);
    String generateUniqueNewsId(Staffs creator);
    Map<String, String> validateNews(News news);
}
