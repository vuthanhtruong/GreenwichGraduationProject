package com.example.demo.service;

import com.example.demo.entity.Documents;
import com.example.demo.entity.Majors;
import com.example.demo.entity.News;

import java.util.List;

public interface NewsService {
    void addNews(News news, List<Documents> documents);
    List<News> getNewsByMajor(Majors major);
}
