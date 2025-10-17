package com.example.demo.post.assignmentSubmitSlots.dao;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.post.assignmentSubmitSlots.model.AssignmentSubmitSlots;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class AssignmentSubmitSlotsDAOImpl implements AssignmentSubmitSlotsDAO {
    @Override
    public List<AssignmentSubmitSlots> getAllAssignmentSubmitSlotsByClass(MajorClasses majorClass) {
        return entityManager.createQuery("from AssignmentSubmitSlots a where a.classEntity=:majorClass", AssignmentSubmitSlots.class)
                .setParameter("majorClass", majorClass).getResultList();
    }

    @PersistenceContext
    private EntityManager entityManager;


}
