package com.example.demo.subject.majorSubject.dao;

import com.example.demo.major.model.Majors;
import com.example.demo.subject.majorSubject.model.MajorSubjects;

import java.util.List;
import java.util.Map;

public interface MajorSubjectsDAO {
    List<MajorSubjects> searchSubjects(String searchType, String keyword, int firstResult, int pageSize, Majors major);
    long countSearchResults(String searchType, String keyword, Majors major);
    boolean existsBySubjectExcludingName(String subjectName, String subjectId);
    void addSubject(MajorSubjects subject);
    MajorSubjects getSubjectById(String subjectId);
    MajorSubjects getSubjectByName(String subjectName);
    MajorSubjects checkNameSubject(MajorSubjects subject);
    List<MajorSubjects> subjectsByMajor(Majors major);
    List<MajorSubjects> AcceptedSubjectsByMajor(Majors major);
    List<MajorSubjects> getSubjects();
    MajorSubjects editSubject(String id, MajorSubjects subject);
    void deleteSubject(String id);
    String generateUniqueSubjectId(String majorId, java.time.LocalDate createdDate);
    Map<String, String> validateSubject(MajorSubjects subject, String curriculumId);
    List<MajorSubjects> getPaginatedSubjects(int firstResult, int pageSize, Majors major);
    long numberOfSubjects(Majors major);
    boolean isDuplicateSubjectName(String subjectName, String subjectId);
    List<MajorSubjects> getSubjectsByCurriculumId(String curriculumId);

    // ==================== DASHBOARD SIÊU THỰC TẾ CHO STAFF ====================
    /**
     * Tổng số môn học chính ngành hiện tại (của staff đang login)
     */
    long totalSubjectsInMyMajor();

    /**
     * Phân bố môn học theo học kỳ trong ngành hiện tại
     * Kết quả: List<[semester, count]>
     */
    List<Object[]> subjectsBySemesterInMyMajor();

    /**
     * Số môn học trong học kỳ hiện tại (có thể tùy chỉnh logic)
     */
    long subjectsInCurrentSemesterInMyMajor();

    /**
     * Số môn học chưa được gắn chương trình đào tạo (rất hay để cảnh báo)
     */
    long subjectsWithoutCurriculumInMyMajor();

    /**
     * Phân bố môn học theo từng chương trình đào tạo trong ngành
     * Kết quả: List<[curriculumName hoặc "Chưa gắn CTĐT", count]>
     */
    List<Object[]> subjectsByCurriculumInMyMajor();

    /**
     * Top 10 môn có tên dài nhất (vui nhưng thực tế dùng để dọn dữ liệu bậy bạ)
     */
    List<Object[]> top10LongestSubjectNamesInMyMajor();
}