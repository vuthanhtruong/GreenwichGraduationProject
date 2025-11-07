package com.example.demo.academicTranscript.controller;

import com.example.demo.RetakeSubjects.service.ReStudyPaymentService;
import com.example.demo.academicTranscript.model.AcademicTranscripts;
import com.example.demo.academicTranscript.service.AcademicTranscriptsService;
import com.example.demo.accountBalance.service.AccountBalancesService;
import com.example.demo.tuitionByYear.model.TuitionByYear;
import com.example.demo.tuitionByYear.service.TuitionByYearService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/student-home")
@PreAuthorize("hasRole('STUDENT')")
public class ReStudyRequestController {

    private final StudentsService studentsService;
    private final AcademicTranscriptsService academicTranscriptsService;
    private final TuitionByYearService tuitionByYearService;
    private final ReStudyPaymentService reStudyPaymentService;

    public ReStudyRequestController(
            StudentsService studentsService,
            AcademicTranscriptsService academicTranscriptsService,
            TuitionByYearService tuitionByYearService, ReStudyPaymentService reStudyPaymentService) {
        this.studentsService = studentsService;
        this.academicTranscriptsService = academicTranscriptsService;
        this.tuitionByYearService = tuitionByYearService;
        this.reStudyPaymentService = reStudyPaymentService;
    }

    @GetMapping("/re-study-request")
    public String showReStudyRequest(Model model) {
        Students student = studentsService.getStudent();

        if (student == null) {
            model.addAttribute("errorMessage", "Student information not found.");
            return "ReStudyRequest";
        }

        Integer admissionYear = student.getAdmissionYear() != null
                ? student.getAdmissionYear().getYear()
                : null;
        String campusId = student.getCampus() != null ? student.getCampus().getCampusId() : null;

        if (admissionYear == null || campusId == null) {
            model.addAttribute("errorMessage", "Missing admission year or campus.");
            model.addAttribute("student", student);
            return "ReStudyRequest";
        }

        // 1. Get all failed subjects (REFER)
        List<AcademicTranscripts> failTranscripts = academicTranscriptsService.getFailSubjectsByStudent(student);

        // 2. Get all TuitionByYear with reStudyTuition > 0 for this year + campus
        List<TuitionByYear> tuitionsWithReStudyFee = tuitionByYearService
                .getTuitionsWithReStudyFeeByYear(admissionYear, student.getCampus());

        // 3. Build Map<subjectId, reStudyTuition>
        Map<String, Double> reStudyFeeMap = new HashMap<>();
        for (TuitionByYear t : tuitionsWithReStudyFee) {
            reStudyFeeMap.put(t.getSubject().getSubjectId(), t.getReStudyTuition());
        }

        // 4. Filter only failed subjects that have re-study fee
        failTranscripts.removeIf(transcript -> {
            String subjectId = transcript.getSubjectId();
            return !reStudyFeeMap.containsKey(subjectId);
        });

        model.addAttribute("student", student);
        model.addAttribute("failTranscripts", failTranscripts);
        model.addAttribute("reStudyFeeMap", reStudyFeeMap);
        model.addAttribute("totalFail", failTranscripts.size());

        return "ReStudyRequest";
    }
}