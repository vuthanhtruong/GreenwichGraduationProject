package com.example.demo.depositHistory.dao;

import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.depositHistory.model.DepositHistory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class DepositHistoryDAOImpl implements DepositHistoryDAO {
    @Override
    public void save(AccountBalances accountBalances) {
        entityManager.persist(accountBalances);
    }

    @Override
    public void save(DepositHistory depositHistory) {

    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void createDepositHistory(DepositHistory depositHistory) {
        entityManager.persist(depositHistory);
    }
}