package com.example.demo.post.news.service;

import com.example.demo.document.service.DocumentsService;
import com.example.demo.post.news.dao.NewsDAO;
import com.example.demo.post.news.model.News;
import com.example.demo.user.staff.model.Staffs;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NewsServiceImpl implements NewsService {

    private final NewsDAO newsDAO;
    private final DocumentsService documentsService;

    public NewsServiceImpl(NewsDAO newsDAO, DocumentsService documentsService) {
        this.newsDAO = newsDAO;
        this.documentsService = documentsService;
    }

    @Override
    public List<News> getPaginatedNews(int firstResult, int pageSize, Staffs creator) {
        return newsDAO.getPaginatedNews(firstResult, pageSize, creator);
    }

    @Override
    public long countNewsByCreator(Staffs creator) {
        return newsDAO.countNewsByCreator(creator);
    }

    @Override
    public News getNewsById(String postId) {
        return newsDAO.getNewsById(postId);
    }

    @Override
    public void addNews(News news) {
        newsDAO.addNews(news);
    }

    @Override
    public void updateNews(News news) {
        newsDAO.updateNews(news);
    }

    @Override
    public void deleteNews(String postId) {
        News news = getNewsById(postId);
        if (news != null && news.getDocuments() != null) {
            documentsService.deleteDocuments(news.getDocuments().stream()
                    .map(d -> d.getDocumentId()).toList());
        }
        newsDAO.deleteNews(postId);
    }

    @Override
    public List<News> searchNews(String searchType, String keyword, int firstResult, int pageSize, Staffs creator) {
        return newsDAO.searchNews(searchType, keyword, firstResult, pageSize, creator);
    }

    @Override
    public long countSearchResults(String searchType, String keyword, Staffs creator) {
        return newsDAO.countSearchResults(searchType, keyword, creator);
    }

    @Override
    public String generateUniqueNewsId(Staffs creator) {
        return newsDAO.generateUniqueNewsId(creator);
    }

    @Override
    public Map<String, String> validateNews(News news) {
        return newsDAO.validateNews(news);
    }
}