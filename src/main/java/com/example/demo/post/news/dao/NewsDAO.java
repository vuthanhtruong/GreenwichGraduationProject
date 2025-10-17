package com.example.demo.post.news.dao;

import com.example.demo.document.model.Documents;
import com.example.demo.major.model.Majors;
import com.example.demo.post.news.model.News;

import java.util.List;

public interface NewsDAO {
    void addNews(News news, List<Documents>  documents);
    List<News> getNewsByMajor(Majors major);
    News getNewsById(String id);
    void updateNews(News news, List<Documents> newDocuments);
    void deleteDocuments(String id,List<String> documentIds);
}
