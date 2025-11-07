package com.example.demo.RetakeSubjects.dao;

import com.example.demo.RetakeSubjects.model.RetakeSubjects;
import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.accountBalance.service.AccountBalancesService;
import com.example.demo.academicTranscript.model.AcademicTranscripts;
import com.example.demo.academicTranscript.service.AcademicTranscriptsService;
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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Repository
@Transactional
public class ReStudyPaymentDAOImpl implements ReStudyPaymentDAO {
    @Override
    public List<RetakeSubjects> getRetakeSubjectsBySubjectId(String subjectId) {
        return entityManager.createQuery("from RetakeSubjects r where r.subject.subjectId=:subjectId", RetakeSubjects.class)
                .setParameter("subjectId", subjectId).getResultList();
    }

    @PersistenceContext
    private EntityManager entityManager;

    private final AcademicTranscriptsService academicTranscriptsService;
    private final TuitionByYearService tuitionByYearService;
    private final AccountBalancesService accountBalancesService;
    private final PaymentHistoriesService paymentHistoriesService;

    public ReStudyPaymentDAOImpl(
            AcademicTranscriptsService academicTranscriptsService,
            TuitionByYearService tuitionByYearService,
            AccountBalancesService accountBalancesService,
            PaymentHistoriesService paymentHistoriesService) {
        this.academicTranscriptsService = academicTranscriptsService;
        this.tuitionByYearService = tuitionByYearService;
        this.accountBalancesService = accountBalancesService;
        this.paymentHistoriesService = paymentHistoriesService;
    }

    @Override
    public Map<String, Object> validateBalance(Students student, List<String> selectedSubjectIds) {
        Map<String, Object> result = new HashMap<>();

        if (selectedSubjectIds == null || selectedSubjectIds.isEmpty()) {
            result.put("valid", false);
            result.put("message", "Please select at least one subject.");
            return result;
        }

        Integer admissionYear = student.getAdmissionYear() != null ? student.getAdmissionYear() : null;
        String campusId = student.getCampus() != null ? student.getCampus().getCampusId() : null;
        if (admissionYear == null || campusId == null) {
            result.put("valid", false);
            result.put("message", "Missing campus or admission year.");
            return result;
        }

        // Lấy phí
        List<TuitionByYear> tuitions = tuitionByYearService.getTuitionsWithReStudyFeeByYear(admissionYear, student.getCampus());
        Map<String, Double> feeMap = new HashMap<>();
        for (TuitionByYear t : tuitions) {
            feeMap.put(t.getSubject().getSubjectId(), t.getReStudyTuition());
        }

        // Tính tổng
        double totalCost = 0.0;
        for (String subjectId : selectedSubjectIds) {
            Double fee = feeMap.get(subjectId);
            if (fee != null) totalCost += fee;
        }

        // KIỂM TRA SỐ DƯ
        boolean hasEnough = accountBalancesService.hasSufficientBalance(student.getId(), totalCost);

        result.put("valid", hasEnough);
        result.put("totalCost", totalCost);
        if (!hasEnough) {
            result.put("message", "Insufficient balance. Required: $" + String.format("%.2f", totalCost));
        }

        return result;
    }

    @Override
    public void processReStudyPayment(Students student, List<String> selectedSubjectIds) {
        Integer admissionYear = student.getAdmissionYear();
        String campusId = student.getCampus().getCampusId();

        // 1. Lấy phí
        List<TuitionByYear> tuitions = tuitionByYearService.getTuitionsWithReStudyFeeByYear(admissionYear, student.getCampus());
        Map<String, Double> feeMap = new HashMap<>();
        for (TuitionByYear t : tuitions) {
            feeMap.put(t.getSubject().getSubjectId(), t.getReStudyTuition());
        }

        // 2. Tính tổng + transcript
        double totalCost = 0.0;
        List<AcademicTranscripts> selectedTranscripts = new ArrayList<>();

        for (String subjectId : selectedSubjectIds) {
            Double fee = feeMap.get(subjectId);
            if (fee == null) continue;
            totalCost += fee;

            AcademicTranscripts t = academicTranscriptsService.getFailSubjectsByStudent(student)
                    .stream()
                    .filter(trans -> trans.getSubjectId().equals(subjectId))
                    .findFirst()
                    .orElse(null);
            if (t != null) selectedTranscripts.add(t);
        }

        LocalDateTime now = LocalDateTime.now();
        AccountBalances account = accountBalancesService.findByStudentId(student.getId());

        // 3. Lưu RetakeSubjects
        for (AcademicTranscripts t : selectedTranscripts) {
            com.example.demo.studentRequiredMajorSubjects.model.StudentRetakeSubjectsId id = new StudentRetakeSubjectsId(student.getId(), t.getSubjectId());
            RetakeSubjects retake = new RetakeSubjects();
            retake.setId(id);
            retake.setStudent(student);
            retake.setSubject(entityManager.find(Subjects.class, t.getSubjectId()));
            retake.setRetakeReason("Re-study failed subject");
            retake.setCreatedAt(now);
            entityManager.persist(retake);
        }

        // 4. Trừ tiền
        account.setBalance(account.getBalance() - totalCost);
        account.setLastUpdated(now);
        accountBalancesService.DepositMoneyIntoAccount(account);

        // 5. Lưu PaymentHistories
        for (AcademicTranscripts t : selectedTranscripts) {
            String historyId = UUID.randomUUID().toString();
            Subjects subject = entityManager.find(Subjects.class, t.getSubjectId());

            PaymentHistories payment = new PaymentHistories();
            payment.setHistoryId(historyId);
            payment.setStudent(student);
            payment.setSubject(subject);
            payment.setAccountBalance(account);
            payment.setPaymentTime(now);
            payment.setCurrentAmount(BigDecimal.valueOf(-feeMap.get(t.getSubjectId())));
            payment.setCreatedAt(now);
            payment.setStatus(Status.COMPLETED);
            paymentHistoriesService.save(payment);
        }
    }
}