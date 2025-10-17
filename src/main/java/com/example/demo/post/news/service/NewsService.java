package com.example.demo.post.news.service;

import com.example.demo.document.model.Documents;
import com.example.demo.major.model.Majors;
import com.example.demo.post.news.model.News;

import java.util.List;

public interface NewsService {
    void addNews(News news, List<Documents> documents);
    List<News> getNewsByMajor(Majors major);
    void updateNews(News news, List<Documents> newDocuments);
    News getNewsById(String id);
    void deleteDocuments(String id,List<String> documentIds);
}
