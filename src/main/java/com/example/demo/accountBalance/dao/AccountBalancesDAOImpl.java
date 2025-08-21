package com.example.demo.accountBalance.dao;

import com.example.demo.accountBalance.model.AccountBalances;
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
