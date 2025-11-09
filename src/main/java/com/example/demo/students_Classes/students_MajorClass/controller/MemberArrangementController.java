package com.example.demo.students_Classes.students_MajorClass.controller;

import com.example.demo.classes.abstractClasses.service.ClassesService;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.majorClasses.service.MajorClassesService;
import com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.service.MajorLecturers_MajorClassesService;
import com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.model.StudentRequiredMajorSubjects;
import com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.service.StudentRequiredMajorSubjectsService;
import com.example.demo.students_Classes.abstractStudents_Class.model.StudentsClassesId;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import com.example.demo.students_Classes.students_MajorClass.model.Students_MajorClasses;
import com.example.demo.students_Classes.students_MajorClass.service.StudentsMajorClassesService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/staff-home/classes-list")
@PreAuthorize("hasRole('STAFF')")
public class MemberArrangementController {
    private final StudentsMajorClassesService studentsMajorClassesService;
    private final MajorClassesService classesService;
    private final MajorLecturers_MajorClassesService lecturersClassesService;
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final RetakeSubjectsService retakeSubjectsService;
    private final AccountBalancesService accountBalancesService;
    private final TuitionByYearService tuitionByYearService;
    private final StudentRequiredMajorSubjectsService studentRequiredMajorSubjectsService;
    private final ClassesService abtractClassesService;

    @Autowired
    public MemberArrangementController(
            StudentsMajorClassesService studentsMajorClassesService,
            MajorClassesService classesService,
            MajorLecturers_MajorClassesService lecturersClassesService,
            StaffsService staffsService,
            StudentsService studentsService,
            RetakeSubjectsService retakeSubjectsService,
            AccountBalancesService accountBalancesService,
            TuitionByYearService tuitionByYearService,
            StudentRequiredMajorSubjectsService studentRequiredMajorSubjectsService,
            ClassesService abtractClassesService) {
        this.studentsMajorClassesService = studentsMajorClassesService;
        this.classesService = classesService;
        this.lecturersClassesService = lecturersClassesService;
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.retakeSubjectsService = retakeSubjectsService;
        this.accountBalancesService = accountBalancesService;
        this.tuitionByYearService = tuitionByYearService;
        this.studentRequiredMajorSubjectsService = studentRequiredMajorSubjectsService;
        this.abtractClassesService = abtractClassesService;
    }

    @GetMapping("/member-arrangement")
    public String showMemberArrangement(Model model, HttpSession session) {
        String classId = (String) session.getAttribute("currentClassId");
        if (classId == null) {
            return handleNoClassSelected(model);
        }
        MajorClasses clazz = (MajorClasses) classesService.getClassById(classId);
        if (clazz == null) {
            return handleNoClassSelected(model);
        }

        // 1. Students in Class
        List<Students> studentsInClass = studentsMajorClassesService.getStudentsByClass(clazz);

        // 2. Lecturers in Class
        List<MajorLecturers> lecturersInClass = lecturersClassesService.listLecturersInClass(clazz);

        // 3. Lecturers Not in Class
        List<MajorLecturers> lecturersNotInClass = lecturersClassesService.listLecturersNotInClass(clazz);

        // 4. & 5. Retake students → split by balance
        String subjectId = clazz.getSubject().getSubjectId();
        List<RetakeSubjects> retakeList = retakeSubjectsService.getRetakeSubjectsBySubjectId(subjectId);

        // LẤY DANH SÁCH SINH VIÊN ĐƯỢC YÊU CẦU HỌC MÔN NÀY
        List<StudentRequiredMajorSubjects> requiredList = studentRequiredMajorSubjectsService
                .getStudentRequiredMajorSubjects(clazz.getSubject(), null);
        List<Students> requiredStudents = requiredList.stream()
                .map(StudentRequiredMajorSubjects::getStudent)
                .filter(Objects::nonNull)
                .filter(s -> !studentsInClass.contains(s)) // Loại bỏ SV đã có lớp
                .toList();

        // === LOẠI BỎ SV ĐÃ CÓ TRONG RETAKE LIST ===
        Set<String> retakeStudentIds = retakeList.stream()
                .map(r -> r.getStudent().getId())
                .collect(Collectors.toSet());

        List<Students> studentsWithEnoughMoney = retakeSubjectsService
                .getStudentsWithSufficientBalance(subjectId, requiredStudents)
                .stream()
                .filter(s -> !retakeStudentIds.contains(s.getId())) // Không hiện nếu đã ở retakeList
                .toList();

        List<Students> studentsDoNotHaveEnoughMoney = retakeSubjectsService
                .getStudentsWithInsufficientBalance(subjectId, requiredStudents)
                .stream()
                .filter(s -> !retakeStudentIds.contains(s.getId())) // Cũng loại ở đây
                .toList();

        model.addAttribute("class", clazz);
        model.addAttribute("studentsInClass", studentsInClass);
        model.addAttribute("lecturersInClass", lecturersInClass);
        model.addAttribute("lecturersNotInClass", lecturersNotInClass);
        model.addAttribute("studentsWithEnoughMoney", studentsWithEnoughMoney);
        model.addAttribute("studentsDoNotHaveEnoughMoney", studentsDoNotHaveEnoughMoney);
        model.addAttribute("retakeList", retakeList);
        return "MemberArrangement";
    }

    private String handleNoClassSelected(Model model) {
        model.addAttribute("errorMessage", "No class selected");
        model.addAttribute("class", new MajorClasses());
        model.addAttribute("studentsInClass", new ArrayList<>());
        model.addAttribute("lecturersInClass", new ArrayList<>());
        model.addAttribute("lecturersNotInClass", new ArrayList<>());
        model.addAttribute("studentsWithEnoughMoney", new ArrayList<>());
        model.addAttribute("studentsDoNotHaveEnoughMoney", new ArrayList<>());
        model.addAttribute("retakeList", new ArrayList<>());
        return "MemberArrangement";
    }

    @PostMapping("/member-arrangement")
    public String selectClass(@RequestParam("classId") String classId,
                              HttpSession session,
                              RedirectAttributes ra) {
        if (classesService.getClassById(classId) == null) {
            ra.addFlashAttribute("errorMessage", "Class not found");
            return "redirect:/staff-home/classes-list";
        }
        session.setAttribute("currentClassId", classId);
        return "redirect:/staff-home/classes-list/member-arrangement";
    }

    // ——— REMOVE STUDENT ———
    @PostMapping("/remove-student-from-class")
    public String removeStudent(@RequestParam("classId") String classId,
                                @RequestParam(value = "studentIds", required = false) List<String> studentIds,
                                Model model, RedirectAttributes ra, HttpSession session) {
        List<String> errors = new ArrayList<>();
        MajorClasses clazz = (MajorClasses) classesService.getClassById(classId);
        if (clazz == null) {
            errors.add("Class not found");
            populateError(model, null, errors);
            return "MemberArrangement";
        }
        if (studentIds == null || studentIds.isEmpty()) {
            errors.add("Please select at least one student");
            populateError(model, clazz, errors);
            return "MemberArrangement";
        }
        String subjectId = clazz.getSubject().getSubjectId();
        for (String sid : studentIds) {
            if (studentsMajorClassesService.existsByStudentAndClass(sid, classId)) {
                studentsMajorClassesService.removeStudentFromClass(sid, classId);
            }
            if (!retakeSubjectsService.existsByStudentAndSubject(sid, subjectId)) {
                Students s = studentsService.getStudentById(sid);
                if (s != null) {
                    RetakeSubjects r = new RetakeSubjects();
                    r.setId(new StudentRetakeSubjectsId(sid, subjectId));
                    r.setStudent(s);
                    r.setSubject(clazz.getSubject());
                    r.setRetakeReason("Removed from class");
                    r.setCreatedAt(LocalDateTime.now());
                    retakeSubjectsService.save(r);
                }
            }
        }
        ra.addFlashAttribute("successMessage", "Students removed and added to retake list.");
        session.setAttribute("currentClassId", classId);
        return "redirect:/staff-home/classes-list/member-arrangement";
    }

    // ——— ADD STUDENT (from enough money list) ———
    @PostMapping("/add-students-to-class")
    public String addStudent(@RequestParam("classId") String classId,
                             @RequestParam(value = "studentIds", required = false) List<String> studentIds,
                             Model model, RedirectAttributes ra, HttpSession session) {
        List<String> errors = new ArrayList<>();
        MajorClasses clazz = (MajorClasses) classesService.getClassById(classId);
        if (clazz == null) {
            errors.add("Class not found");
            populateError(model, null, errors);
            return "MemberArrangement";
        }
        Staffs staff = staffsService.getStaff();
        if (staff == null) {
            errors.add("Staff not found");
            populateError(model, clazz, errors);
            return "MemberArrangement";
        }
        if (studentIds == null || studentIds.isEmpty()) {
            errors.add("Please select at least one student");
            populateError(model, clazz, errors);
            return "MemberArrangement";
        }
        String subjectId = clazz.getSubject().getSubjectId();
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
            if (studentsMajorClassesService.existsByStudentAndClass(sid, classId)) {
                errors.add(s.getFullName() + " already in class");
                continue;
            }
            // Add to class
            Students_MajorClasses smc = new Students_MajorClasses();
            smc.setId(new StudentsClassesId(classId, sid));
            smc.setStudent(s);
            smc.setMajorClass(clazz);
            smc.setClassEntity(abtractClassesService.findClassById(clazz.getClassId()));
            smc.setAddedBy(staff);
            smc.setCreatedAt(LocalDateTime.now());
            studentsMajorClassesService.addStudentToClass(smc);
            added++;
            // Deduct & log
            retakeSubjectsService.deductAndLogPayment(s, subjectId, fee);
        }
        if (!errors.isEmpty()) {
            populateError(model, clazz, errors);
            return "MemberArrangement";
        }
        ra.addFlashAttribute("successMessage", added + " student(s) added and payment deducted.");
        session.setAttribute("currentClassId", classId);
        return "redirect:/staff-home/classes-list/member-arrangement";
    }

    // ——— ADD/REMOVE LECTURERS ———
    @PostMapping("/add-lecturers-to-class")
    public String addLecturers(@RequestParam("classId") String classId,
                               @RequestParam(value = "lecturerIds", required = false) List<String> lecturerIds,
                               RedirectAttributes ra, HttpSession session) {
        if (lecturerIds != null && !lecturerIds.isEmpty()) {
            MajorClasses clazz = (MajorClasses) classesService.getClassById(classId);
            if (clazz != null) {
                lecturersClassesService.addLecturersToClass(clazz, lecturerIds);
                ra.addFlashAttribute("successMessage", lecturerIds.size() + " lecturer(s) added.");
            }
        }
        session.setAttribute("currentClassId", classId);
        return "redirect:/staff-home/classes-list/member-arrangement";
    }

    @PostMapping("/remove-lecturer-from-class")
    public String removeLecturers(@RequestParam("classId") String classId,
                                  @RequestParam(value = "lecturerIds", required = false) List<String> lecturerIds,
                                  RedirectAttributes ra, HttpSession session) {
        if (lecturerIds != null && !lecturerIds.isEmpty()) {
            MajorClasses clazz = (MajorClasses) classesService.getClassById(classId);
            if (clazz != null) {
                lecturersClassesService.removeLecturerFromClass(clazz, lecturerIds);
                ra.addFlashAttribute("successMessage", lecturerIds.size() + " lecturer(s) removed.");
            }
        }
        session.setAttribute("currentClassId", classId);
        return "redirect:/staff-home/classes-list/member-arrangement";
    }

    // ——— HELPERS ———
    private void populateError(Model model, MajorClasses clazz, List<String> errors) {
        model.addAttribute("errorMessage", String.join("; ", errors));
        model.addAttribute("class", clazz != null ? clazz : new MajorClasses());
        if (clazz != null) {
            model.addAttribute("studentsInClass", studentsMajorClassesService.getStudentsByClass(clazz));
            model.addAttribute("lecturersInClass", lecturersClassesService.listLecturersInClass(clazz));
            model.addAttribute("lecturersNotInClass", lecturersClassesService.listLecturersNotInClass(clazz));
        } else {
            model.addAttribute("studentsInClass", new ArrayList<>());
            model.addAttribute("lecturersInClass", new ArrayList<>());
            model.addAttribute("lecturersNotInClass", new ArrayList<>());
        }
        model.addAttribute("studentsWithEnoughMoney", new ArrayList<>());
        model.addAttribute("studentsDoNotHaveEnoughMoney", new ArrayList<>());
        model.addAttribute("retakeList", new ArrayList<>());
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