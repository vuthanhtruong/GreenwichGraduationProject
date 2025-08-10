package com.example.demo.service.impl;

import com.example.demo.dao.NewsDAO;
import com.example.demo.entity.Documents;
import com.example.demo.entity.Majors;
import com.example.demo.entity.News;
import com.example.demo.service.NewsService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class NewsServiceImpl implements NewsService {
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
