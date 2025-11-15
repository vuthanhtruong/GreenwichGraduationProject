package com.example.demo.students_Classes.students_MajorClass.controller;

import com.example.demo.classes.abstractClasses.service.ClassesService;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.majorClasses.service.MajorClassesService;
import com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.service.MajorLecturers_MajorClassesService;
import com.example.demo.studentRequiredMajorSubjects.model.StudentRetakeSubjectsId;
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

        // 1. Danh sách sinh viên đang trong lớp
        List<Students> studentsInClass = studentsMajorClassesService.getStudentsByClass(clazz);
        Set<String> studentsInClassIds = studentsInClass.stream()
                .map(Students::getId)
                .collect(Collectors.toSet());

        // 2. Danh sách giảng viên trong lớp
        List<MajorLecturers> lecturersInClass = lecturersClassesService.listLecturersInClass(clazz);

        // 3. Danh sách giảng viên chưa có trong lớp
        List<MajorLecturers> lecturersNotInClass = lecturersClassesService.listLecturersNotInClass(clazz);

        // 4. Lấy danh sách sinh viên bắt buộc học môn này (Required)
        String subjectId = clazz.getSubject().getSubjectId();
        List<StudentRequiredMajorSubjects> requiredList = studentRequiredMajorSubjectsService
                .getStudentRequiredMajorSubjects(clazz.getSubject(), null);
        List<Students> requiredStudents = requiredList.stream()
                .map(StudentRequiredMajorSubjects::getStudent)
                .filter(Objects::nonNull)
                .filter(s -> !studentsInClassIds.contains(s.getId())) // Loại bỏ SV đã có lớp
                .toList();

        // 5. Lấy danh sách sinh viên đang trong retakeList (phải loại khỏi requiredStudents)
        List<RetakeSubjects> retakeList = retakeSubjectsService.getRetakeSubjectsBySubjectId(subjectId);
        Set<String> retakeStudentIds = retakeList.stream()
                .map(r -> r.getStudent().getId())
                .collect(Collectors.toSet());

        // Loại bỏ sinh viên đã có trong retakeList khỏi danh sách requiredStudents
        List<Students> eligibleRequiredStudents = requiredStudents.stream()
                .filter(s -> !retakeStudentIds.contains(s.getId()))
                .toList();

        // 6. Chia theo số dư: đủ tiền / không đủ tiền
        List<Students> studentsWithEnoughMoney = retakeSubjectsService
                .getStudentsWithSufficientBalance(subjectId, eligibleRequiredStudents);

        List<Students> studentsDoNotHaveEnoughMoney = retakeSubjectsService
                .getStudentsWithInsufficientBalance(subjectId, eligibleRequiredStudents);

        // 7. Lọc retakeList: chỉ giữ sinh viên KHÔNG có trong lớp
        List<RetakeSubjects> filteredRetakeList = retakeList.stream()
                .filter(r -> !studentsInClassIds.contains(r.getStudent().getId()))
                .toList();

        // Gán vào model
        model.addAttribute("class", clazz);
        model.addAttribute("studentsInClass", studentsInClass);
        model.addAttribute("lecturersInClass", lecturersInClass);
        model.addAttribute("lecturersNotInClass", lecturersNotInClass);
        model.addAttribute("studentsWithEnoughMoney", studentsWithEnoughMoney);
        model.addAttribute("studentsDoNotHaveEnoughMoney", studentsDoNotHaveEnoughMoney);
        model.addAttribute("retakeList", filteredRetakeList); // Đã lọc

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

    // ——— XÓA SINH VIÊN KHỎI LỚP → THÊM VÀO RETAKE (NẾU CHƯA CÓ) ———
    @PostMapping("/remove-student-from-class")
    public String removeStudent(@RequestParam("classId") String classId,
                                @RequestParam(value = "studentIds", required = false) List<String> studentIds,
                                RedirectAttributes ra, HttpSession session) {
        if (studentIds == null || studentIds.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Please select at least one student");
            session.setAttribute("currentClassId", classId);
            return "redirect:/staff-home/classes-list/member-arrangement";
        }

        MajorClasses clazz = (MajorClasses) classesService.getClassById(classId);
        if (clazz == null) {
            ra.addFlashAttribute("errorMessage", "Class not found");
            return "redirect:/staff-home/classes-list/member-arrangement";
        }

        String subjectId = clazz.getSubject().getSubjectId();
        int removedCount = 0;

        for (String sid : studentIds) {
            if (!studentsMajorClassesService.existsByStudentAndClass(sid, classId)) {
                continue;
            }

            // Xóa khỏi lớp
            studentsMajorClassesService.removeStudentFromClass(sid, classId);
            removedCount++;

            // Chỉ thêm vào retake nếu chưa có
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

        ra.addFlashAttribute("successMessage", removedCount + " student(s) removed and added to retake list if needed.");
        session.setAttribute("currentClassId", classId);
        return "redirect:/staff-home/classes-list/member-arrangement";
    }

    // ——— THÊM SINH VIÊN VÀO LỚP → KHÔNG TRỪ TIỀN NẾU ĐÃ Ở RETAKE ———
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

            if (studentsMajorClassesService.existsByStudentAndClass(sid, classId)) {
                errors.add(s.getFullName() + " already in class");
                continue;
            }

            // Nếu sinh viên đã ở trong retakeList → không trừ tiền
            boolean isInRetake = retakeSubjectsService.existsByStudentAndSubject(sid, subjectId);

            if (!isInRetake) {
                Double fee = getReStudyFee(subjectId, s);
                if (fee == null || fee <= 0) {
                    errors.add("Fee not defined for subject");
                    continue;
                }
                if (!accountBalancesService.hasSufficientBalance(sid, fee)) {
                    errors.add(s.getFullName() + " does not have enough money");
                    continue;
                }
            }

            // Thêm vào lớp
            Students_MajorClasses smc = new Students_MajorClasses();
            smc.setId(new StudentsClassesId(classId, sid));
            smc.setStudent(s);
            smc.setMajorClass(clazz);
            smc.setClassEntity(abtractClassesService.findClassById(clazz.getClassId()));
            smc.setAddedBy(staff);
            smc.setCreatedAt(LocalDateTime.now());
            studentsMajorClassesService.addStudentToClass(smc);
            added++;

            // Nếu không ở retake → trừ tiền
            if (!isInRetake) {
                Double fee = getReStudyFee(subjectId, s);
                if (fee != null && fee > 0) {
                    retakeSubjectsService.deductAndLogPayment(s, subjectId, fee);
                }
            }
        }

        if (!errors.isEmpty()) {
            populateError(model, clazz, errors);
            return "MemberArrangement";
        }

        ra.addFlashAttribute("successMessage", added + " student(s) added successfully.");
        session.setAttribute("currentClassId", classId);
        return "redirect:/staff-home/classes-list/member-arrangement";
    }

    // ——— THÊM GIẢNG VIÊN ———
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

    // ——— XÓA GIẢNG VIÊN ———
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

    // ——— HELPER: HIỂN THỊ LỖI ———
    private void populateError(Model model, MajorClasses clazz, List<String> errors) {
        model.addAttribute("errorMessage", String.join("; ", errors));
        model.addAttribute("class", clazz != null ? clazz : new MajorClasses());
        if (clazz != null) {
            List<Students> studentsInClass = studentsMajorClassesService.getStudentsByClass(clazz);
            model.addAttribute("studentsInClass", studentsInClass);
            model.addAttribute("lecturersInClass", lecturersClassesService.listLecturersInClass(clazz));
            model.addAttribute("lecturersNotInClass", lecturersClassesService.listLecturersNotInClass(clazz));

            Set<String> inClassIds = studentsInClass.stream().map(Students::getId).collect(Collectors.toSet());
            String subjectId = clazz.getSubject().getSubjectId();
            List<RetakeSubjects> retakeList = retakeSubjectsService.getRetakeSubjectsBySubjectId(subjectId)
                    .stream()
                    .filter(r -> !inClassIds.contains(r.getStudent().getId()))
                    .toList();
            model.addAttribute("retakeList", retakeList);
        } else {
            model.addAttribute("studentsInClass", new ArrayList<>());
            model.addAttribute("lecturersInClass", new ArrayList<>());
            model.addAttribute("lecturersNotInClass", new ArrayList<>());
            model.addAttribute("retakeList", new ArrayList<>());
        }
        model.addAttribute("studentsWithEnoughMoney", new ArrayList<>());
        model.addAttribute("studentsDoNotHaveEnoughMoney", new ArrayList<>());
    }

    // ——— HELPER: LẤY PHÍ HỌC LẠI ———
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