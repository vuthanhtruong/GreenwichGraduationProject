package com.example.demo.post.specializedAssignmentSubmitSlots.dao;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.post.specializedAssignmentSubmitSlots.model.SpecializedAssignmentSubmitSlots;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class SpecializedAssignmentSubmitSlotsDAOImpl implements SpecializedAssignmentSubmitSlotsDAO {
    @Override
    public List<SpecializedAssignmentSubmitSlots> getAllAssignmentSubmitSlotsByClass(SpecializedClasses majorClass) {
        return entityManager.createQuery("from SpecializedAssignmentSubmitSlots a where a.classEntity=:majorClass", SpecializedAssignmentSubmitSlots.class)
                .setParameter("majorClass", majorClass).getResultList();
    }

    @PersistenceContext
    private EntityManager entityManager;
}
