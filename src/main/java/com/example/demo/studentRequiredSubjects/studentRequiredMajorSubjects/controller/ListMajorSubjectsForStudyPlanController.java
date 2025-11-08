package com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.controller;

import com.example.demo.curriculum.service.CurriculumService;
import com.example.demo.subject.majorSubject.model.MajorSubjects;
import com.example.demo.subject.majorSubject.service.MajorSubjectsService;
import com.example.demo.tuitionByYear.service.TuitionByYearService;
import com.example.demo.user.staff.service.StaffsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/staff-home")
@PreAuthorize("hasRole('STAFF')")
public class ListMajorSubjectsForStudyPlanController {

    private final StaffsService staffsService;
    private final CurriculumService curriculumService;
    private final TuitionByYearService tuitionByYearService;

    public ListMajorSubjectsForStudyPlanController(
            MajorSubjectsService subjectsService,
            StaffsService staffsService,
            CurriculumService curriculumService,
            TuitionByYearService tuitionByYearService) {
        this.staffsService = staffsService;
        this.curriculumService = curriculumService;
        this.tuitionByYearService = tuitionByYearService;
    }

    @GetMapping("/study-plan")
    public String getStudyPlan(
            @RequestParam(required = false) String curriculumId,
            @RequestParam(required = false) Integer admissionYear,
            Model model) {

        if (admissionYear == null) {
            admissionYear = java.time.Year.now().getValue();
        }

        List<Integer> admissionYears = tuitionByYearService.findAllAdmissionYearsWithMajorTuition(
                staffsService.getCampusOfStaff(), staffsService.getStaffMajor());

        List<MajorSubjects> subjects = tuitionByYearService.getMajorSubjectsWithTuitionByYearAndCurriculum(
                admissionYear,
                curriculumService.getCurriculums().getFirst(),
                staffsService.getStaffMajor(),
                staffsService.getCampusOfStaff()
        );

        model.addAttribute("subjects", subjects);
        model.addAttribute("curriculums", curriculumService.getCurriculums());
        model.addAttribute("admissionYears", admissionYears);
        model.addAttribute("curriculumId", curriculumId);
        model.addAttribute("admissionYear", admissionYear);
        model.addAttribute("totalSubjects", subjects.size());

        return "StudyPlan";
    }

    @PostMapping("/study-plan/filter-subjects")
    public String filterSubjects(
            @RequestParam(required = false) String curriculumId,
            @RequestParam(required = false) Integer admissionYear,
            RedirectAttributes redirectAttributes,
            Model model) {

        // === VALIDATION: Không cho phép cả 2 đều null/rỗng ===
        boolean isCurriculumEmpty = curriculumId == null || curriculumId.isBlank();
        boolean isAdmissionYearEmpty = admissionYear == null;

        if (isCurriculumEmpty && isAdmissionYearEmpty) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Please select at least one filter: Admission Year or Curriculum.");
            return "redirect:/staff-home/study-plan";
        }

        // === Mặc định admissionYear nếu null ===
        if (admissionYear == null) {
            admissionYear = java.time.Year.now().getValue();
        }

        // === Lấy dữ liệu ===
        List<Integer> admissionYears = tuitionByYearService.findAllAdmissionYearsWithMajorTuition(
                staffsService.getCampusOfStaff(), staffsService.getStaffMajor());

        List<MajorSubjects> subjects;

        // Nếu có curriculumId → dùng nó, không thì lấy mặc định
        var curriculum = isCurriculumEmpty
                ? curriculumService.getCurriculums().getFirst()
                : curriculumService.getCurriculumById(curriculumId);

        subjects = tuitionByYearService.getMajorSubjectsWithTuitionByYearAndCurriculum(
                admissionYear,
                curriculum,
                staffsService.getStaffMajor(),
                staffsService.getCampusOfStaff()
        );

        // === Truyền dữ liệu cho view ===
        model.addAttribute("subjects", subjects);
        model.addAttribute("curriculums", curriculumService.getCurriculums());
        model.addAttribute("admissionYears", admissionYears);
        model.addAttribute("curriculumId", curriculumId);
        model.addAttribute("admissionYear", admissionYear);
        model.addAttribute("totalSubjects", subjects.size());

        return "FilterSubjects"; // hoặc fragment nếu dùng AJAX
    }
}