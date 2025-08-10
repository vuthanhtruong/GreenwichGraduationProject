package com.example.demo.dao;

import com.example.demo.entity.Documents;
import com.example.demo.entity.Majors;
import com.example.demo.entity.News;

import java.util.List;

public interface NewsDAO {
    void addNews(News news, List<Documents>  documents);
    List<News> getNewsByMajor(Majors major);
    News getNewsById(String id);
    void updateNews(News news, List<Documents> newDocuments);
    void deleteDocuments(String id,List<String> documentIds);
}
