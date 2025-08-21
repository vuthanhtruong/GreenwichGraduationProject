package com.example.demo.news.service;

import com.example.demo.news.dao.NewsDAO;
import com.example.demo.document.model.Documents;
import com.example.demo.major.model.Majors;
import com.example.demo.news.model.News;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class NewsServiceImpl implements NewsService {
    @Override
    public void deleteDocuments(String id, List<String> documentIds) {
        newsDAO.deleteDocuments(id, documentIds);
    }

    @Override
    public News getNewsById(String id) {
        return newsDAO.getNewsById(id);
    }

    @Override
    public void updateNews(News news, List<Documents> newDocuments) {
        newsDAO.updateNews(news, newDocuments);
    }

    private  final NewsDAO newsDAO;

    public NewsServiceImpl(NewsDAO newsDAO) {
        this.newsDAO = newsDAO;
    }

    @Override
    public void addNews(News news, List<Documents> documents) {
        newsDAO.addNews(news,documents);
    }

    @Override
    public List<News> getNewsByMajor(Majors major) {
        return newsDAO.getNewsByMajor(major);
    }
}
