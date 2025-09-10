package com.example.demo.depositHistory.dao;

import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.depositHistory.model.DepositHistory;
import com.example.demo.entity.Enums.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class DepositHistoryDAOImpl implements DepositHistoryDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DepositHistory findByStudentIdAndStatus(String studentId, Status status) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DepositHistory> query = cb.createQuery(DepositHistory.class);
        Root<DepositHistory> root = query.from(DepositHistory.class);

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
    public void save(DepositHistory depositHistory) {
        entityManager.merge(depositHistory); // merge thay cho persist
    }

    @Override
    public void createDepositHistory(DepositHistory depositHistory) {
        entityManager.merge(depositHistory); // luôn dùng merge để tránh detached
    }
}
