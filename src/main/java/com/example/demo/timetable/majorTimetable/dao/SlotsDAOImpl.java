package com.example.demo.timetable.majorTimetable.dao;

import com.example.demo.timetable.majorTimetable.model.Slots;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class SlotsDAOImpl implements SlotsDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Slots> getSlots() {
        return em.createQuery("SELECT s FROM Slots s ORDER BY s.slotId", Slots.class)
                .getResultList();
    }

    @Override
    public Slots getSlotById(String slotId) {
        try {
            return em.createQuery("SELECT s FROM Slots s WHERE s.slotId = :slotId", Slots.class)
                    .setParameter("slotId", slotId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // Không tìm thấy
        }
    }
}