package com.example.demo.students_Classes.students_MinorClasses.controller;

import com.example.demo.RetakeSubjects.model.RetakeSubjects;
import com.example.demo.RetakeSubjects.service.RetakeSubjectsService;
import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.minorClasses.service.MinorClassesService;
import com.example.demo.lecturers_Classes.minorLecturers_MinorClasses.service.MinorLecturers_MinorClassesService;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import com.example.demo.students_Classes.abstractStudents_Class.model.StudentsClassesId;
import com.example.demo.students_Classes.students_MinorClasses.model.Students_MinorClasses;
import com.example.demo.students_Classes.students_MinorClasses.service.StudentsMinorClassesService;
import com.example.demo.accountBalance.service.AccountBalancesService;
import com.example.demo.tuitionByYear.service.TuitionByYearService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/deputy-staff-home/minor-classes-list")
@PreAuthorize("hasRole('DEPUTY_STAFF')")
public class MinorClassesMemberArrangementController {

    private final StudentsMinorClassesService studentsMinorClassesService;
    private final MinorClassesService minorClassesService;
    private final MinorLecturers_MinorClassesService lecturersClassesService;
    private final DeputyStaffsService deputyStaffsService;
    private final StudentsService studentsService;
    private final RetakeSubjectsService retakeSubjectsService;
    private final AccountBalancesService accountBalancesService;
    private final TuitionByYearService tuitionByYearService;

    @Autowired
    public MinorClassesMemberArrangementController(
            StudentsMinorClassesService studentsMinorClassesService,
            MinorClassesService minorClassesService,
            MinorLecturers_MinorClassesService lecturersClassesService,
            DeputyStaffsService deputyStaffsService,
            StudentsService studentsService,
            RetakeSubjectsService retakeSubjectsService,
            AccountBalancesService accountBalancesService,
            TuitionByYearService tuitionByYearService) {
        this.studentsMinorClassesService = studentsMinorClassesService;
        this.minorClassesService = minorClassesService;
        this.lecturersClassesService = lecturersClassesService;
        this.deputyStaffsService = deputyStaffsService;
        this.studentsService = studentsService;
        this.retakeSubjectsService = retakeSubjectsService;
        this.accountBalancesService = accountBalancesService;
        this.tuitionByYearService = tuitionByYearService;
    }

    @PostMapping("/member-arrangement")
    public String selectClass(@RequestParam("classId") String classId,
                              HttpSession session,
                              RedirectAttributes ra) {
        if (minorClassesService.getClassById(classId) == null) {
            ra.addFlashAttribute("errorMessage", "Class not found");
            return "redirect:/deputy-staff-home/minor-classes-list";
        }
        session.setAttribute("currentClassId", classId);
        return "redirect:/deputy-staff-home/minor-classes-list/member-arrangement";
    }

    @GetMapping("/member-arrangement")
    public String showMemberArrangement(Model model, HttpSession session) {
        String classId = (String) session.getAttribute("currentClassId");
        if (classId == null) return handleNoClassSelected(model);

        MinorClasses clazz = minorClassesService.getClassById(classId);
        if (clazz == null) return handleNoClassSelected(model);

        String subjectId = clazz.getMinorSubject().getSubjectId();

        // 1. Students in Class
        List<Students_MinorClasses> studentsInClass = studentsMinorClassesService.getStudentsInClass(classId);

        // 2. Lecturers in Class
        List<MinorLecturers> lecturersInClass = lecturersClassesService.listLecturersInClass(clazz);

        // 3. Lecturers Not in Class
        List<MinorLecturers> lecturersNotInClass = lecturersClassesService.listLecturersNotInClass(clazz);

        // 4. & 5. Retake students → split by balance
        List<RetakeSubjects> retakeList = retakeSubjectsService.getRetakeSubjectsBySubjectId(subjectId);
        List<Students> allRetakeStudents = retakeList.stream()
                .map(RetakeSubjects::getStudent)
                .filter(Objects::nonNull)
                .toList();

        List<Students> studentsWithEnoughMoney = retakeSubjectsService.getStudentsWithSufficientBalance(subjectId, allRetakeStudents);
        List<Students> studentsDoNotHaveEnoughMoney = retakeSubjectsService.getStudentsWithInsufficientBalance(subjectId, allRetakeStudents);

        // 6. Students Have Paid Tuition and Do Not Have This Class Yet
        List<Students> studentsFailedAndPaid = allRetakeStudents;

        model.addAttribute("class", clazz);
        model.addAttribute("studentsInClass", studentsInClass);
        model.addAttribute("lecturersInClass", lecturersInClass);
        model.addAttribute("lecturersNotInClass", lecturersNotInClass);
        model.addAttribute("studentsWithEnoughMoney", studentsWithEnoughMoney);
        model.addAttribute("studentsDoNotHaveEnoughMoney", studentsDoNotHaveEnoughMoney);
        model.addAttribute("studentsFailedAndPaid", studentsFailedAndPaid);
        model.addAttribute("retakeList", retakeList);

        return "MinorClassMemberArrangement";
    }

    private String handleNoClassSelected(Model model) {
        model.addAttribute("errorMessage", "No class selected");
        model.addAttribute("class", new MinorClasses());
        model.addAttribute("studentsInClass", new ArrayList<>());
        model.addAttribute("lecturersInClass", new ArrayList<>());
        model.addAttribute("lecturersNotInClass", new ArrayList<>());
        model.addAttribute("studentsWithEnoughMoney", new ArrayList<>());
        model.addAttribute("studentsDoNotHaveEnoughMoney", new ArrayList<>());
        model.addAttribute("studentsFailedAndPaid", new ArrayList<>());
        model.addAttribute("retakeList", new ArrayList<>());
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
                    r.setId(new com.example.demo.studentRequiredMajorSubjects.model.StudentRetakeSubjectsId(sid, subjectId));
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
        return "redirect:/deputy-staff-home/minor-classes-list/member-arrangement";
    }

    // ——— ADD STUDENT (from enough money list) ———
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

            // Add to class
            Students_MinorClasses smc = new Students_MinorClasses();
            StudentsClassesId id = new StudentsClassesId();
            id.setStudentId(sid);
            id.setClassId(classId);
            smc.setId(id);
            smc.setStudent(s);
            smc.setClassEntity(clazz);
            smc.setAddedBy(staff);
            smc.setCreatedAt(LocalDateTime.now());
            studentsMinorClassesService.addStudentToClass(smc);
            added++;

            // Deduct & log
            retakeSubjectsService.deductAndLogPayment(s, subjectId, fee);
        }

        if (!errors.isEmpty()) {
            populateError(model, clazz, errors);
            return "MinorClassMemberArrangement";
        }

        ra.addFlashAttribute("successMessage", added + " student(s) added and payment deducted.");
        session.setAttribute("currentClassId", classId);
        return "redirect:/deputy-staff-home/minor-classes-list/member-arrangement";
    }

    // ——— ADD/REMOVE LECTURERS ———
    @PostMapping("/add-lecturer-to-class")
    public String addLecturers(@RequestParam("classId") String classId,
                               @RequestParam(value = "lecturerIds", required = false) List<String> lecturerIds,
                               RedirectAttributes ra, HttpSession session) {
        if (lecturerIds != null && !lecturerIds.isEmpty()) {
            MinorClasses clazz = minorClassesService.getClassById(classId);
            if (clazz != null) {
                lecturersClassesService.addLecturersToClass(clazz, lecturerIds);
                ra.addFlashAttribute("successMessage", lecturerIds.size() + " lecturer(s) added.");
            }
        }
        session.setAttribute("currentClassId", classId);
        return "redirect:/deputy-staff-home/minor-classes-list/member-arrangement";
    }

    @PostMapping("/remove-lecturer-from-class")
    public String removeLecturers(@RequestParam("classId") String classId,
                                  @RequestParam(value = "lecturerIds", required = false) List<String> lecturerIds,
                                  RedirectAttributes ra, HttpSession session) {
        if (lecturerIds != null && !lecturerIds.isEmpty()) {
            MinorClasses clazz = minorClassesService.getClassById(classId);
            if (clazz != null) {
                lecturersClassesService.removeLecturerFromClass(clazz, lecturerIds);
                ra.addFlashAttribute("successMessage", lecturerIds.size() + " lecturer(s) removed.");
            }
        }
        session.setAttribute("currentClassId", classId);
        return "redirect:/deputy-staff-home/minor-classes-list/member-arrangement";
    }

    // ——— HELPERS ———
    private void populateError(Model model, MinorClasses clazz, List<String> errors) {
        model.addAttribute("errorMessage", String.join("; ", errors));
        model.addAttribute("class", clazz != null ? clazz : new MinorClasses());
        if (clazz != null) {
            model.addAttribute("studentsInClass", studentsMinorClassesService.getStudentsInClass(clazz.getClassId()));
            model.addAttribute("lecturersInClass", lecturersClassesService.listLecturersInClass(clazz));
            model.addAttribute("lecturersNotInClass", lecturersClassesService.listLecturersNotInClass(clazz));
        } else {
            model.addAttribute("studentsInClass", new ArrayList<>());
            model.addAttribute("lecturersInClass", new ArrayList<>());
            model.addAttribute("lecturersNotInClass", new ArrayList<>());
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
}