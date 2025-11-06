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
    @Override
    public News getNewsByIdWithComments(String postId) {
        return newsDAO.getNewsByIdWithComments(postId);
    }

    @Override
    public News getNewsByIdWithDocuments(String postId) {
        return newsDAO.getNewsByIdWithDocuments(postId);
    }

    @Override
    public List<News> getPublicNewsPaginated(int firstResult, int pageSize) {
        return newsDAO.getPublicNewsPaginated(firstResult, pageSize);
    }

    @Override
    public long countAllPublicNews() {
        return newsDAO.countAllPublicNews();
    }

    @Override
    public List<News> searchPublicNews(String keyword, int firstResult, int pageSize) {
        return newsDAO.searchPublicNews(keyword, firstResult, pageSize);
    }

    @Override
    public long countPublicSearch(String keyword) {
        return newsDAO.countPublicSearch(keyword);
    }

    @Override
    public List<News> getAllPublicNews() {
        return newsDAO.getAllPublicNews();
    }

    @Override
    public List<News> getLatestPublicNews(int limit) {
        return newsDAO.getLatestPublicNews(limit);
    }

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