package com.example.demo.students_Classes.students_MinorClasses.controller;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.minorClasses.service.MinorClassesService;
import com.example.demo.students_Classes.abstractStudents_Class.model.StudentsClassesId;
import com.example.demo.students_Classes.students_MinorClasses.model.Students_MinorClasses;
import com.example.demo.students_Classes.students_MinorClasses.service.StudentsMinorClassesService;
import com.example.demo.studentRequiredSubjects.studentRequiredMinorSubjects.model.StudentRequiredMinorSubjects;
import com.example.demo.studentRequiredSubjects.studentRequiredMinorSubjects.service.StudentRequiredMinorSubjectsService;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import com.example.demo.retakeSubjects.model.RetakeSubjects;
import com.example.demo.retakeSubjects.service.RetakeSubjectsService;
import com.example.demo.accountBalance.service.AccountBalancesService;
import com.example.demo.tuitionByYear.service.TuitionByYearService;
import com.example.demo.studentRequiredMajorSubjects.model.StudentRetakeSubjectsId;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/deputy-staff-home/minor-classes-list")
@PreAuthorize("hasRole('DEPUTY_STAFF')")
public class MinorStudentMemberController {

    private final StudentsMinorClassesService studentsMinorClassesService;
    private final MinorClassesService minorClassesService;
    private final DeputyStaffsService deputyStaffsService;
    private final StudentsService studentsService;
    private final RetakeSubjectsService retakeSubjectsService;
    private final AccountBalancesService accountBalancesService;
    private final TuitionByYearService tuitionByYearService;
    private final StudentRequiredMinorSubjectsService requiredSubjectsService;

    @Autowired
    public MinorStudentMemberController(
            StudentsMinorClassesService studentsMinorClassesService,
            MinorClassesService minorClassesService,
            DeputyStaffsService deputyStaffsService,
            StudentsService studentsService,
            RetakeSubjectsService retakeSubjectsService,
            AccountBalancesService accountBalancesService,
            TuitionByYearService tuitionByYearService,
            StudentRequiredMinorSubjectsService requiredSubjectsService) {
        this.studentsMinorClassesService = studentsMinorClassesService;
        this.minorClassesService = minorClassesService;
        this.deputyStaffsService = deputyStaffsService;
        this.studentsService = studentsService;
        this.retakeSubjectsService = retakeSubjectsService;
        this.accountBalancesService = accountBalancesService;
        this.tuitionByYearService = tuitionByYearService;
        this.requiredSubjectsService = requiredSubjectsService;
    }

    @PostMapping("/member-arrangement/select-class")
    public String selectClass(@RequestParam("classId") String classId,
                              HttpSession session,
                              RedirectAttributes ra) {
        MinorClasses clazz = minorClassesService.getClassById(classId);
        if (clazz == null) {
            ra.addFlashAttribute("errorMessage", "Class not found");
            return "redirect:/deputy-staff-home/minor-classes-list";
        }
        session.setAttribute("currentClassId", classId);
        return "redirect:/deputy-staff-home/minor-classes-list/member-arrangement/students";
    }

    @GetMapping("/member-arrangement/students")
    public String showStudentArrangement(Model model, HttpSession session) {
        String classId = (String) session.getAttribute("currentClassId");
        if (classId == null) return handleNoClassSelected(model);

        MinorClasses clazz = minorClassesService.getClassById(classId);
        if (clazz == null) return handleNoClassSelected(model);

        String subjectId = clazz.getMinorSubject().getSubjectId();

        // === 1. Tất cả sinh viên được yêu cầu học môn này ===
        List<StudentRequiredMinorSubjects> requiredList = requiredSubjectsService
                .getStudentRequiredMinorSubjects(clazz.getMinorSubject());

        List<Students> requiredStudents = requiredList.stream()
                .map(StudentRequiredMinorSubjects::getStudent)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // === 2. Sinh viên đang trong lớp ===
        List<Students_MinorClasses> studentsInClassEntity = studentsMinorClassesService.getStudentsInClass(classId);
        Set<String> studentIdsInClass = studentsInClassEntity.stream()
                .map(smc -> smc.getStudent().getId())
                .collect(Collectors.toSet());

        // === 3. Retake: chỉ từ sinh viên được yêu cầu + chưa trong lớp ===
        List<Students> retakeCandidates = requiredStudents.stream()
                .filter(s -> !studentIdsInClass.contains(s.getId()))
                .collect(Collectors.toList());

        List<RetakeSubjects> retakeList = retakeSubjectsService.getRetakeSubjectsBySubjectId(subjectId);
        List<Students> retakeStudents = retakeList.stream()
                .map(RetakeSubjects::getStudent)
                .filter(retakeCandidates::contains)
                .collect(Collectors.toList());

        List<Students> studentsWithEnoughMoney = retakeSubjectsService.getStudentsWithSufficientBalance(subjectId, retakeStudents);
        List<Students> studentsDoNotHaveEnoughMoney = retakeSubjectsService.getStudentsWithInsufficientBalance(subjectId, retakeStudents);
        List<Students> studentsFailedAndPaid = retakeStudents;

        model.addAttribute("class", clazz);
        model.addAttribute("studentsInClass", studentsInClassEntity);
        model.addAttribute("studentsWithEnoughMoney", studentsWithEnoughMoney);
        model.addAttribute("studentsDoNotHaveEnoughMoney", studentsDoNotHaveEnoughMoney);
        model.addAttribute("studentsFailedAndPaid", studentsFailedAndPaid);
        model.addAttribute("retakeList", retakeList);

        return "MinorClassMemberArrangement";
    }

    // ——— REMOVE STUDENT ———
    @PostMapping("/remove-student-from-class")
    public String removeStudent(@RequestParam("classId") String classId,
                                @RequestParam(value = "studentIds", required = false) List<String> studentIds,
                                Model model, RedirectAttributes ra, HttpSession session) {
        List<String> errors = new ArrayList<>();
        MinorClasses clazz = minorClassesService.getClassById(classId);
        if (clazz == null) {
            errors.add("Class not found");
            populateError(model, null, errors);
            return "MinorClassMemberArrangement";
        }

        if (studentIds == null || studentIds.isEmpty()) {
            errors.add("Please select at least one student");
            populateError(model, clazz, errors);
            return "MinorClassMemberArrangement";
        }

        String subjectId = clazz.getMinorSubject().getSubjectId();
        for (String sid : studentIds) {
            if (studentsMinorClassesService.existsByStudentAndClass(sid, classId)) {
                studentsMinorClassesService.removeStudentFromClass(sid, classId);
            }

            if (!retakeSubjectsService.existsByStudentAndSubject(sid, subjectId)) {
                Students s = studentsService.getStudentById(sid);
                if (s != null) {
                    RetakeSubjects r = new RetakeSubjects();
                    r.setId(new StudentRetakeSubjectsId(sid, subjectId));
                    r.setStudent(s);
                    r.setSubject(clazz.getMinorSubject());
                    r.setRetakeReason("Removed from minor class");
                    r.setCreatedAt(LocalDateTime.now());
                    retakeSubjectsService.save(r);
                }
            }
        }

        ra.addFlashAttribute("successMessage", "Students removed and added to retake list.");
        session.setAttribute("currentClassId", classId);
        return "redirect:/deputy-staff-home/minor-classes-list/member-arrangement/students";
    }

    // ——— ADD STUDENT ———
    @PostMapping("/add-student-to-class")
    public String addStudent(@RequestParam("classId") String classId,
                             @RequestParam(value = "studentIds", required = false) List<String> studentIds,
                             Model model, RedirectAttributes ra, HttpSession session) {
        List<String> errors = new ArrayList<>();
        MinorClasses clazz = minorClassesService.getClassById(classId);
        if (clazz == null) {
            errors.add("Class not found");
            populateError(model, null, errors);
            return "MinorClassMemberArrangement";
        }

        DeputyStaffs staff = deputyStaffsService.getDeputyStaff();
        if (staff == null) {
            errors.add("Deputy staff not found");
            populateError(model, clazz, errors);
            return "MinorClassMemberArrangement";
        }

        if (studentIds == null || studentIds.isEmpty()) {
            errors.add("Please select at least one student");
            populateError(model, clazz, errors);
            return "MinorClassMemberArrangement";
        }

        String subjectId = clazz.getMinorSubject().getSubjectId();
        int added = 0;

        for (String sid : studentIds) {
            Students s = studentsService.getStudentById(sid);
            if (s == null) continue;

            if (!requiredSubjectsService.isStudentAlreadyRequiredForSubject(sid, subjectId)) {
                errors.add(s.getFullName() + " is not required to take this subject");
                continue;
            }

            Double fee = getReStudyFee(subjectId, s);
            if (fee == null || fee <= 0) {
                errors.add("Fee not defined for subject");
                continue;
            }

            if (!accountBalancesService.hasSufficientBalance(sid, fee)) {
                errors.add(s.getFullName() + " does not have enough money");
                continue;
            }

            if (studentsMinorClassesService.existsByStudentAndClass(sid, classId)) {
                errors.add(s.getFullName() + " already in class");
                continue;
            }

            Students_MinorClasses smc = new Students_MinorClasses();
            StudentsClassesId id = new StudentsClassesId(classId, sid);
            smc.setId(id);
            smc.setStudent(s);
            smc.setClassEntity(clazz);
            smc.setAddedBy(staff);
            smc.setCreatedAt(LocalDateTime.now());
            studentsMinorClassesService.addStudentToClass(smc);
            added++;

            retakeSubjectsService.deductAndLogPayment(s, subjectId, fee);
        }

        if (!errors.isEmpty()) {
            populateError(model, clazz, errors);
            return "MinorClassMemberArrangement";
        }

        ra.addFlashAttribute("successMessage", added + " student(s) added and payment deducted.");
        session.setAttribute("currentClassId", classId);
        return "redirect:/deputy-staff-home/minor-classes-list/member-arrangement/students";
    }

    // ——— HELPERS ———
    private void populateError(Model model, MinorClasses clazz, List<String> errors) {
        model.addAttribute("errorMessage", String.join("; ", errors));
        model.addAttribute("class", clazz != null ? clazz : new MinorClasses());
        if (clazz != null) {
            model.addAttribute("studentsInClass", studentsMinorClassesService.getStudentsInClass(clazz.getClassId()));
        } else {
            model.addAttribute("studentsInClass", new ArrayList<>());
        }
        model.addAttribute("studentsWithEnoughMoney", new ArrayList<>());
        model.addAttribute("studentsDoNotHaveEnoughMoney", new ArrayList<>());
        model.addAttribute("studentsFailedAndPaid", new ArrayList<>());
    }

    private Double getReStudyFee(String subjectId, Students student) {
        Integer year = student.getAdmissionYear();
        if (year == null || student.getCampus() == null) return null;
        return tuitionByYearService.getTuitionsWithReStudyFeeByYear(year, student.getCampus()).stream()
                .filter(t -> t.getSubject().getSubjectId().equals(subjectId))
                .map(t -> t.getReStudyTuition())
                .findFirst()
                .orElse(null);
    }

    private String handleNoClassSelected(Model model) {
        model.addAttribute("errorMessage", "No class selected");
        model.addAttribute("class", new MinorClasses());
        model.addAttribute("studentsInClass", new ArrayList<>());
        model.addAttribute("studentsWithEnoughMoney", new ArrayList<>());
        model.addAttribute("studentsDoNotHaveEnoughMoney", new ArrayList<>());
        model.addAttribute("studentsFailedAndPaid", new ArrayList<>());
        return "MinorClassMemberArrangement";
    }
}