// src/main/java/com/example/demo/RetakeSubjects/dao/RetakeSubjectsDAOImpl.java
package com.example.demo.retakeSubjects.dao;

import com.example.demo.retakeSubjects.model.RetakeSubjects;
import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.accountBalance.service.AccountBalancesService;
import com.example.demo.entity.Enums.Status;
import com.example.demo.financialHistory.paymentHistories.model.PaymentHistories;
import com.example.demo.financialHistory.paymentHistories.service.PaymentHistoriesService;
import com.example.demo.studentRequiredMajorSubjects.model.StudentRetakeSubjectsId;
import com.example.demo.subject.abstractSubject.model.Subjects;
import com.example.demo.tuitionByYear.model.TuitionByYear;
import com.example.demo.tuitionByYear.service.TuitionByYearService;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class RetakeSubjectsDAOImpl implements RetakeSubjectsDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired private TuitionByYearService tuitionByYearService;
    @Autowired private AccountBalancesService accountBalancesService;
    @Autowired private PaymentHistoriesService paymentHistoriesService;

    @Override
    public void save(RetakeSubjects retakeSubjects) {
        entityManager.persist(retakeSubjects);
    }

    @Override
    public boolean existsByStudentAndSubject(String studentId, String subjectId) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(r) FROM RetakeSubjects r WHERE r.student.id = :studentId AND r.subject.subjectId = :subjectId",
                        Long.class)
                .setParameter("studentId", studentId)
                .setParameter("subjectId", subjectId)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public List<RetakeSubjects> getRetakeSubjectsBySubjectId(String subjectId) {
        return entityManager.createQuery(
                        "FROM RetakeSubjects r WHERE r.subject.subjectId = :subjectId",
                        RetakeSubjects.class)
                .setParameter("subjectId", subjectId)
                .getResultList();
    }

    // === LỌC SINH VIÊN ĐỦ TIỀN ===
    @Override
    public List<Students> getStudentsWithSufficientBalance(String subjectId, List<Students> candidates) {
        if (candidates.isEmpty()) return List.of();
        Double fee = getReStudyFee(subjectId, candidates.get(0));
        if (fee == null || fee <= 0) return List.of();
        return candidates.stream()
                .filter(s -> accountBalancesService.hasSufficientBalance(s.getId(), fee))
                .collect(Collectors.toList());
    }

    // === LỌC SINH VIÊN CHƯA ĐỦ TIỀN ===
    @Override
    public List<Students> getStudentsWithInsufficientBalance(String subjectId, List<Students> candidates) {
        if (candidates.isEmpty()) return List.of();
        Double fee = getReStudyFee(subjectId, candidates.get(0));
        if (fee == null || fee <= 0) return candidates;
        return candidates.stream()
                .filter(s -> !accountBalancesService.hasSufficientBalance(s.getId(), fee))
                .collect(Collectors.toList());
    }

    // === TRỪ TIỀN + GHI LOG KHI THÊM VÀO LỚP ===
    @Override
    public boolean deductAndLogPayment(Students student, String subjectId, Double amount) {
        boolean deducted = accountBalancesService.deductBalance(student.getId(), amount);
        if (!deducted) return false;

        Subjects subject = entityManager.find(Subjects.class, subjectId);
        AccountBalances account = accountBalancesService.findByStudentId(student.getId());

        PaymentHistories payment = new PaymentHistories();
        payment.setHistoryId(UUID.randomUUID().toString());
        payment.setStudent(student);
        payment.setSubject(subject);
        payment.setAccountBalance(account);
        payment.setCurrentAmount(BigDecimal.valueOf(-amount));
        payment.setCreatedAt(LocalDateTime.now());
        payment.setStatus(Status.COMPLETED);
        paymentHistoriesService.save(payment);

        return true;
    }

    // === THANH TOÁN HỌC LẠI (CŨ) ===
    @Override
    public Map<String, Object> validateBalance(Students student, List<String> selectedSubjectIds) {
        double total = 0;
        List<String> missing = new ArrayList<>();

        for (String subjectId : selectedSubjectIds) {
            Double fee = getReStudyFee(subjectId, student);
            if (fee == null || fee <= 0) {
                missing.add(subjectId);
                continue;
            }
            total += fee;
        }

        AccountBalances account = accountBalancesService.findByStudentId(student.getId());
        double balance = account != null ? account.getBalance() : 0;

        Map<String, Object> result = new HashMap<>();
        result.put("totalRequired", total);
        result.put("currentBalance", balance);
        result.put("canPay", balance >= total && missing.isEmpty());
        result.put("missingFees", missing);
        return result;
    }

    @Override
    public void processReStudyPayment(Students student, List<String> selectedSubjectIds) {
        if (student == null || selectedSubjectIds == null || selectedSubjectIds.isEmpty()) {
            return;
        }

        for (String subjectId : selectedSubjectIds) {
            Double fee = getReStudyFee(subjectId, student);
            if (fee == null || fee <= 0) {
                continue;
            }

            // 1. Trừ tiền + ghi payment history
            boolean paid = deductAndLogPayment(student, subjectId, fee);
            if (!paid) {
                // Không trừ được tiền thì bỏ qua môn này
                continue;
            }

            // 2. Nếu chưa có trong RetakeSubjects thì tạo mới
            if (!existsByStudentAndSubject(student.getId(), subjectId)) {
                Subjects subject = entityManager.find(Subjects.class, subjectId);

                RetakeSubjects retake = new RetakeSubjects();
                // dùng key ghép studentId + subjectId
                retake.setId(new StudentRetakeSubjectsId(student.getId(), subjectId));
                retake.setStudent(student);
                retake.setSubject(subject);
                retake.setCreatedAt(LocalDateTime.now());
                retake.setRetakeReason("Student re-study payment"); // lý do tùy bạn

                save(retake); // hoặc entityManager.persist(retake);
            }
        }
    }

    @Override
    public void deleteByStudentAndSubject(String studentId, String subjectId) {
        entityManager.createQuery(
                        "DELETE FROM RetakeSubjects r WHERE r.student.id = :studentId AND r.subject.subjectId = :subjectId")
                .setParameter("studentId", studentId)
                .setParameter("subjectId", subjectId)
                .executeUpdate();
    }


    // === HỖ TRỢ: LẤY HỌC PHÍ HỌC LẠI ===
    private Double getReStudyFee(String subjectId, Students student) {
        Integer year = student.getAdmissionYear();
        if (year == null || student.getCampus() == null) return null;
        return tuitionByYearService.getTuitionsWithReStudyFeeByYear(year, student.getCampus()).stream()
                .filter(t -> t.getSubject().getSubjectId().equals(subjectId))
                .map(TuitionByYear::getReStudyTuition)
                .findFirst()
                .orElse(null);
    }
}