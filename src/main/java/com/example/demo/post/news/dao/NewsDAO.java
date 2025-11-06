package com.example.demo.post.news.dao;

import com.example.demo.post.news.model.News;
import com.example.demo.user.staff.model.Staffs;

import java.util.List;
import java.util.Map;

public interface NewsDAO {
    List<News> getPaginatedNews(int firstResult, int pageSize, Staffs creator);
    long countNewsByCreator(Staffs creator);
    List<News> searchNews(String searchType, String keyword, int firstResult, int pageSize, Staffs creator);
    long countSearchResults(String searchType, String keyword, Staffs creator);
    List<News> getPublicNewsPaginated(int firstResult, int pageSize);
    long countAllPublicNews();
    List<News> searchPublicNews(String keyword, int firstResult, int pageSize);
    long countPublicSearch(String keyword);
    List<News> getAllPublicNews();                    // Optional
    List<News> getLatestPublicNews(int limit);        // Optional
    News getNewsById(String postId);
    void addNews(News news);
    void updateNews(News news);
    void deleteNews(String postId);
    String generateUniqueNewsId(Staffs creator);
    Map<String, String> validateNews(News news);
    News getNewsByIdWithDocuments(String postId);
    News getNewsByIdWithComments(String postId);
}