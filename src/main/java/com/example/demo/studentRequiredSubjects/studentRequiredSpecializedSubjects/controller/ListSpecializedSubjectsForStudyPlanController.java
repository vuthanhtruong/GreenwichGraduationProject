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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Year;
import java.util.List;

@Controller
@RequestMapping("/staff-home")
@PreAuthorize("hasRole('STAFF')")
public class ListSpecializedSubjectsForStudyPlanController {

    private final StaffsService staffsService;
    private final CurriculumService curriculumService;
    private final TuitionByYearService tuitionByYearService;

    public ListSpecializedSubjectsForStudyPlanController(
            StaffsService staffsService,
            CurriculumService curriculumService,
            TuitionByYearService tuitionByYearService) {
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
        List<Integer> admissionYears = tuitionByYearService.findAllAdmissionYearsWithSpecializedTuition(campusId, staffsService.getStaffMajor());

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
                    staffsService.getStaffMajor(),
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
            RedirectAttributes redirectAttributes,
            Model model) {

        // === VALIDATION: Không cho phép cả curriculumId và admissionYear đều trống ===
        boolean isCurriculumEmpty = curriculumId == null || curriculumId.isBlank();
        boolean isAdmissionYearEmpty = admissionYear == null;

        if (isCurriculumEmpty && isAdmissionYearEmpty) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Please select at least one filter: Curriculum or Admission Year.");
            return "redirect:/staff-home/specialized-study-plan";
        }

        // === MẶC ĐỊNH ===
        if (admissionYear == null) {
            admissionYear = Year.now().getValue();
        }
        if (campusId == null || campusId.isBlank()) {
            campusId = staffsService.getCampusOfStaff().getCampusId();
        }

        // === LẤY DỮ LIỆU ===
        List<Integer> admissionYears = tuitionByYearService.findAllAdmissionYearsWithSpecializedTuition(campusId, staffsService.getStaffMajor());

        Curriculum selectedCurriculum = isCurriculumEmpty
                ? curriculumService.getCurriculums().getFirst()
                : curriculumService.getCurriculumById(curriculumId);

        if (selectedCurriculum != null && selectedCurriculum.getCurriculumId() != null) {
            curriculumId = selectedCurriculum.getCurriculumId();
        }

        List<SpecializedSubject> subjects = selectedCurriculum != null
                ? tuitionByYearService.getSpecializedSubjectsWithTuitionByYearAndCurriculum(
                admissionYear, selectedCurriculum, staffsService.getStaffMajor(), staffsService.getCampusOfStaff())
                : List.of();

        // === THÔNG BÁO NẾU KHÔNG CÓ KẾT QUẢ ===
        if (subjects.isEmpty() && selectedCurriculum != null) {
            model.addAttribute("errorMessage", "No specialized subjects found with tuition for the selected criteria.");
        }

        // === TRUYỀN DỮ LIỆU ===
        model.addAttribute("subjects", subjects);
        model.addAttribute("curriculums", curriculumService.getCurriculums());
        model.addAttribute("admissionYears", admissionYears);
        model.addAttribute("curriculumId", curriculumId);
        model.addAttribute("admissionYear", admissionYear);
        model.addAttribute("campusId", campusId);
        model.addAttribute("totalSubjects", subjects.size());

        return "SpecializedStudyPlan"; // Dùng chung 1 view
    }
}