package com.example.demo.dao.impl;

import com.example.demo.dao.NewsDAO;
import com.example.demo.entity.Documents;
import com.example.demo.entity.Majors;
import com.example.demo.entity.News;
import com.example.demo.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Transactional
public class NewsDAOImpl implements NewsDAO {
    @PersistenceContext
    private EntityManager entityManager;
    private final StaffsService  staffsService;

    public NewsDAOImpl(StaffsService staffsService) {
        this.staffsService = staffsService;
    }

    @Override
    public void addNews(News news, List<Documents> documents) {

    }

    @Override
    public List<News> getNewsByMajor(Majors major) {
        return entityManager.createQuery("select n from News n where n.creator.majorManagement=:major", News.class).
                setParameter("major", major).getResultList();
    }
}
