package com.example.demo.history.depositHistory.dao;

import com.example.demo.history.depositHistory.model.DepositHistories;
import com.example.demo.entity.Enums.Status;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class DepositHistoriesDAOImpl implements DepositHistoriesDAO {
    @Override
    public List<DepositHistories> getStudentDepositHistories(Students student) {
        return entityManager.createQuery("from DepositHistories d where d.student=:student", DepositHistories.class).setParameter("student", student).getResultList();
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DepositHistories findByStudentIdAndStatus(String studentId, Status status) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DepositHistories> query = cb.createQuery(DepositHistories.class);
        Root<DepositHistories> root = query.from(DepositHistories.class);

        query.select(root).where(
                cb.and(
                        cb.equal(root.get("student").get("id"), studentId),
                        cb.equal(root.get("status"), status)
                )
        );

        return entityManager.createQuery(query)
                .setMaxResults(1)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public void save(DepositHistories depositHistory) {
        entityManager.merge(depositHistory); // merge thay cho persist
    }

    @Override
    public void createDepositHistory(DepositHistories depositHistory) {
        entityManager.merge(depositHistory); // luôn dùng merge để tránh detached
    }
}
