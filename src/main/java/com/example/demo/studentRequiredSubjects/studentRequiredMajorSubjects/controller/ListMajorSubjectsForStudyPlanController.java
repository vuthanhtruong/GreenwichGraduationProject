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

        // Nếu không truyền admissionYear → mặc định là năm hiện tại
        if (admissionYear == null) {
            admissionYear = java.time.Year.now().getValue();
        }

        List<Integer> admissionYears = tuitionByYearService.findAllAdmissionYearsWithMajorTuition(staffsService.getCampusOfStaff());
        List<MajorSubjects> subjects;

        // Lấy danh sách môn học có học phí theo năm và chương trình
        subjects = tuitionByYearService.getMajorSubjectsWithTuitionByYearAndCurriculum(
                admissionYear,
                curriculumService.getCurriculums().getFirst(),
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


    // Lọc theo curriculum + admissionYear
    @PostMapping("/study-plan/filter-subjects")
    public String filterSubjects(
            @RequestParam(required = false) String curriculumId,
            @RequestParam(required = false) Integer admissionYear,
            Model model) {

        // Nếu không truyền admissionYear → mặc định là năm hiện tại
        if (admissionYear == null) {
            admissionYear = java.time.Year.now().getValue();
        }

        List<Integer> admissionYears = tuitionByYearService.findAllAdmissionYearsWithMajorTuition(staffsService.getCampusOfStaff());
        List<MajorSubjects> subjects;

        // Lấy danh sách môn học có học phí theo năm và chương trình
        subjects = tuitionByYearService.getMajorSubjectsWithTuitionByYearAndCurriculum(
                admissionYear,
                curriculumService.getCurriculumById(curriculumId),
                staffsService.getCampusOfStaff()
        );

        model.addAttribute("subjects", subjects);
        model.addAttribute("curriculums", curriculumService.getCurriculums());
        model.addAttribute("admissionYears", admissionYears);
        model.addAttribute("curriculumId", curriculumId);
        model.addAttribute("admissionYear", admissionYear);
        model.addAttribute("totalSubjects", subjects.size());

        return "FilterSubjects";
    }
}