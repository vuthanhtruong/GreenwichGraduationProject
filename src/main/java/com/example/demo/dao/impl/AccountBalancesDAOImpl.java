package com.example.demo.dao.impl;

import com.example.demo.dao.AccountBalancesDAO;
import com.example.demo.entity.AccountBalances;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class AccountBalancesDAOImpl implements AccountBalancesDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void createAccountBalances(AccountBalances accountBalances) {
        entityManager.persist(accountBalances);
    }
}
