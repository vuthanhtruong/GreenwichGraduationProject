package com.example.demo.accountBalance.dao;

import com.example.demo.accountBalance.model.AccountBalances;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class AccountBalancesDAOImpl implements AccountBalancesDAO {

    @Override
    public long totalStudentsWithAccount() {
        return entityManager.createQuery("SELECT COUNT(a) FROM AccountBalances a", Long.class)
                .getSingleResult();
    }

    @Override
    public double totalBalanceAllStudents() {
        Object result = entityManager.createQuery("SELECT COALESCE(SUM(a.balance), 0) FROM AccountBalances a")
                .getSingleResult();
        return result == null ? 0.0 : ((Number) result).doubleValue();
    }

    @Override
    public double averageBalance() {
        Object result = entityManager.createQuery("SELECT COALESCE(AVG(a.balance), 0) FROM AccountBalances a")
                .getSingleResult();
        return result == null ? 0.0 : ((Number) result).doubleValue();
    }

    @Override
    public long countStudentsWithZeroBalance() {
        return entityManager.createQuery("SELECT COUNT(a) FROM AccountBalances a WHERE a.balance = 0", Long.class)
                .getSingleResult();
    }

    @Override
    public long countStudentsWithNegativeBalance() {
        return entityManager.createQuery("SELECT COUNT(a) FROM AccountBalances a WHERE a.balance < 0", Long.class)
                .getSingleResult();
    }

    @Override
    public long countStudentsWithBalanceOver(double threshold) {
        return entityManager.createQuery(
                        "SELECT COUNT(a) FROM AccountBalances a WHERE a.balance > :threshold", Long.class)
                .setParameter("threshold", threshold)
                .getSingleResult();
    }

    @Override
    public Map<String, Long> balanceDistribution() {
        String jpql = """
        SELECT 
            CASE 
                WHEN a.balance < 0 THEN 'Negative Balance'
                WHEN a.balance = 0 THEN 'Zero Balance'
                WHEN a.balance < 5000000 THEN 'Under 5M VND'
                WHEN a.balance < 20000000 THEN '5M - 20M VND'
                WHEN a.balance < 50000000 THEN '20M - 50M VND'
                ELSE 'Over 50M VND'
            END,
            COUNT(a)
        FROM AccountBalances a
        GROUP BY 
            CASE 
                WHEN a.balance < 0 THEN 'Negative Balance'
                WHEN a.balance = 0 THEN 'Zero Balance'
                WHEN a.balance < 5000000 THEN 'Under 5M VND'
                WHEN a.balance < 20000000 THEN '5M - 20M VND'
                WHEN a.balance < 50000000 THEN '20M - 50M VND'
                ELSE 'Over 50M VND'
            END
        ORDER BY 
            MIN(a.balance) ASC 
        """;

        List<Object[]> rows = entityManager.createQuery(jpql, Object[].class)
                .getResultList();

        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }

    @Override
    public List<Object[]> top10HighestBalanceStudents() {
        return entityManager.createQuery(
                        "SELECT s.id, s.firstName, s.lastName, a.balance " +
                                "FROM AccountBalances a JOIN a.student s " +
                                "ORDER BY a.balance DESC", Object[].class)
                .setMaxResults(10)
                .getResultList();
        // Returns: [studentId, firstName, lastName, balance]
    }

    @Override
    public List<Object[]> top10LowestBalanceStudents() {
        return entityManager.createQuery(
                        "SELECT s.id, s.firstName, s.lastName, a.balance " +
                                "FROM AccountBalances a JOIN a.student s " +
                                "ORDER BY a.balance ASC", Object[].class)
                .setMaxResults(10)
                .getResultList();
    }
    // File: AccountBalancesServiceImpl.java
    @Override
    public boolean refundBalance(String studentId, double amount) {
        AccountBalances account = findByStudentId(studentId);
        if (account == null) return false;

        account.setBalance(account.getBalance() + amount);
        account.setLastUpdated(LocalDateTime.now());
        entityManager.merge(account);
        return true;
    }

    @Override
    public void DepositMoneyIntoAccount(AccountBalances accountBalances) {
        entityManager.persist(accountBalances);
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void createAccountBalances(AccountBalances accountBalances) {
        entityManager.persist(accountBalances);
    }

    @Override
    public AccountBalances findByStudentId(String studentId) {
        return entityManager.find(AccountBalances.class, studentId);
    }
    @Override
    public boolean hasSufficientBalance(String studentId, double requiredAmount) {
        AccountBalances account = findByStudentId(studentId);
        if (account == null) {
            return false;
        }
        return account.getBalance() >= requiredAmount;
    }

    @Override
    public boolean deductBalance(String studentId, double amount) {
        AccountBalances account = findByStudentId(studentId);
        if (account == null || account.getBalance() < amount) {
            return false;
        }
        account.setBalance(account.getBalance() - amount);
        account.setLastUpdated(LocalDateTime.now());
        entityManager.merge(account); // dùng merge
        return true;
    }
}