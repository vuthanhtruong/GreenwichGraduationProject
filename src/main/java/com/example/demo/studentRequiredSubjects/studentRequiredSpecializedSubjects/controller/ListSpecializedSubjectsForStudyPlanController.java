package com.example.demo.studentRequiredSubjects.studentRequiredSpecializedSubjects.controller;

import com.example.demo.curriculum.model.Curriculum;
import com.example.demo.curriculum.service.CurriculumService;
import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import com.example.demo.subject.specializedSubject.service.SpecializedSubjectsService;
import com.example.demo.tuitionByYear.service.TuitionByYearService;
import com.example.demo.user.staff.service.StaffsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;

@Controller
@RequestMapping("/staff-home")
@PreAuthorize("hasRole('STAFF')")
public class ListSpecializedSubjectsForStudyPlanController {

    private final SpecializedSubjectsService subjectsService;
    private final StaffsService staffsService;
    private final CurriculumService curriculumService;
    private final TuitionByYearService tuitionByYearService;

    public ListSpecializedSubjectsForStudyPlanController(
            SpecializedSubjectsService subjectsService,
            StaffsService staffsService,
            CurriculumService curriculumService,
            TuitionByYearService tuitionByYearService) {
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;
        this.curriculumService = curriculumService;
        this.tuitionByYearService = tuitionByYearService;
    }

    @GetMapping("/specialized-study-plan")
    public String getStudyPlan(
            @RequestParam(required = false) String curriculumId,
            @RequestParam(required = false) Integer admissionYear,
            @RequestParam(required = false) String campusId,
            Model model) {

        // === 1. XÁC ĐỊNH MẶC ĐỊNH ===
        if (admissionYear == null) {
            admissionYear = Year.now().getValue();
        }

        // Lấy campusId từ staff nếu không truyền
        if (campusId == null || campusId.isBlank()) {
            campusId = staffsService.getCampusOfStaff().getCampusId();
        }

        // === 2. LẤY DANH SÁCH NĂM CÓ HỌC PHÍ CHUYÊN NGÀNH ===
        List<Integer> admissionYears = tuitionByYearService.findAllAdmissionYearsWithSpecializedTuition(campusId);

        // === 3. LẤY CURRICULUM MẶC ĐỊNH ===
        Curriculum selectedCurriculum;
        if (curriculumId == null || curriculumId.isBlank()) {
            selectedCurriculum = curriculumService.getCurriculums().getFirst();
            curriculumId = selectedCurriculum != null ? selectedCurriculum.getCurriculumId() : null;
        } else {
            selectedCurriculum = curriculumService.getCurriculumById(curriculumId);
        }

        // === 4. LẤY MÔN CHUYÊN NGÀNH CÓ HỌC PHÍ ===
        List<SpecializedSubject> subjects = List.of();
        if (selectedCurriculum != null) {
            subjects = tuitionByYearService.getSpecializedSubjectsWithTuitionByYearAndCurriculum(
                    admissionYear,
                    selectedCurriculum,
                    staffsService.getCampusOfStaff()
            );
        }

        // === 5. THÊM VÀO MODEL ===
        model.addAttribute("subjects", subjects);
        model.addAttribute("curriculums", curriculumService.getCurriculums());
        model.addAttribute("admissionYears", admissionYears);
        model.addAttribute("curriculumId", curriculumId);
        model.addAttribute("admissionYear", admissionYear);
        model.addAttribute("campusId", campusId);
        model.addAttribute("totalSubjects", subjects.size());

        return "SpecializedStudyPlan";
    }

    @PostMapping("/specialized-study-plan/filter-subjects")
    public String filterSubjects(
            @RequestParam(required = false) String curriculumId,
            @RequestParam(required = false) Integer admissionYear,
            @RequestParam(required = false) String campusId,
            Model model) {

        // === 1. MẶC ĐỊNH ===
        if (admissionYear == null) {
            admissionYear = Year.now().getValue();
        }
        if (campusId == null || campusId.isBlank()) {
            campusId = staffsService.getCampusOfStaff().getCampusId();
        }

        // === 2. LẤY NĂM CÓ HỌC PHÍ ===
        List<Integer> admissionYears = tuitionByYearService.findAllAdmissionYearsWithSpecializedTuition(campusId);

        // === 3. XỬ LÝ CURRICULUM ===
        Curriculum selectedCurriculum = null;
        if (curriculumId != null && !curriculumId.isBlank()) {
            selectedCurriculum = curriculumService.getCurriculumById(curriculumId);
        } else {
            selectedCurriculum = curriculumService.getCurriculums().getFirst();
            if (selectedCurriculum != null) {
                curriculumId = selectedCurriculum.getCurriculumId();
            }
        }

        // === 4. LẤY MÔN HỌC ===
        List<SpecializedSubject> subjects = List.of();
        if (selectedCurriculum != null) {
            subjects = tuitionByYearService.getSpecializedSubjectsWithTuitionByYearAndCurriculum(
                    admissionYear,
                    selectedCurriculum,
                    staffsService.getCampusOfStaff()
            );
        }

        if (subjects.isEmpty() && selectedCurriculum != null) {
            model.addAttribute("errorMessage", "Không tìm thấy môn chuyên ngành nào có học phí cho chương trình và năm đã chọn.");
        }

        // === 5. TRẢ KẾT QUẢ ===
        model.addAttribute("subjects", subjects);
        model.addAttribute("curriculums", curriculumService.getCurriculums());
        model.addAttribute("admissionYears", admissionYears);
        model.addAttribute("curriculumId", curriculumId);
        model.addAttribute("admissionYear", admissionYear);
        model.addAttribute("campusId", campusId);
        model.addAttribute("totalSubjects", subjects.size());

        return "FilterSpecializedSubjects";
    }
}