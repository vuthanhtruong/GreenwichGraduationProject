package com.example.demo.students_Classes.students_MinorClasses.controller;

import com.example.demo.classes.abstractClasses.service.ClassesService;
import com.example.demo.retakeSubjects.model.RetakeSubjects;
import com.example.demo.retakeSubjects.service.RetakeSubjectsService;
import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.minorClasses.service.MinorClassesService;
import com.example.demo.lecturers_Classes.minorLecturers_MinorClasses.service.MinorLecturers_MinorClassesService;
import com.example.demo.students_Classes.abstractStudents_Class.model.StudentsClassesId;
import com.example.demo.students_Classes.students_MinorClasses.model.Students_MinorClasses;
import com.example.demo.students_Classes.students_MinorClasses.service.StudentsMinorClassesService;
import com.example.demo.studentRequiredSubjects.studentRequiredMinorSubjects.model.StudentRequiredMinorSubjects;
import com.example.demo.studentRequiredSubjects.studentRequiredMinorSubjects.service.StudentRequiredMinorSubjectsService;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
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
public class MinorClassesMemberArrangementController {

    private final StudentsMinorClassesService studentsMinorClassesService;
    private final MinorClassesService minorClassesService;
    private final MinorLecturers_MinorClassesService lecturersClassesService;
    private final DeputyStaffsService deputyStaffsService;
    private final StudentsService studentsService;
    private final RetakeSubjectsService retakeSubjectsService;
    private final AccountBalancesService accountBalancesService;
    private final TuitionByYearService tuitionByYearService;
    private final StudentRequiredMinorSubjectsService requiredSubjectsService;
    private final ClassesService abstractClassesService;

    @Autowired
    public MinorClassesMemberArrangementController(
            StudentsMinorClassesService studentsMinorClassesService,
            MinorClassesService minorClassesService,
            MinorLecturers_MinorClassesService lecturersClassesService,
            DeputyStaffsService deputyStaffsService,
            StudentsService studentsService,
            RetakeSubjectsService retakeSubjectsService,
            AccountBalancesService accountBalancesService,
            TuitionByYearService tuitionByYearService,
            StudentRequiredMinorSubjectsService requiredSubjectsService,
            ClassesService abstractClassesService) {
        this.studentsMinorClassesService = studentsMinorClassesService;
        this.minorClassesService = minorClassesService;
        this.lecturersClassesService = lecturersClassesService;
        this.deputyStaffsService = deputyStaffsService;
        this.studentsService = studentsService;
        this.retakeSubjectsService = retakeSubjectsService;
        this.accountBalancesService = accountBalancesService;
        this.tuitionByYearService = tuitionByYearService;
        this.requiredSubjectsService = requiredSubjectsService;
        this.abstractClassesService = abstractClassesService;
    }

    @PostMapping("/member-arrangement")
    public String selectClass(@RequestParam("classId") String classId,
                              HttpSession session,
                              RedirectAttributes ra) {
        MinorClasses clazz = minorClassesService.getClassById(classId);
        if (clazz == null) {
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

        // 1. Danh sách sinh viên đang trong lớp
        List<Students_MinorClasses> studentsInClassEntities = studentsMinorClassesService.getStudentsInClass(classId);
        List<Students> studentsInClass = studentsInClassEntities.stream()
                .map(Students_MinorClasses::getStudent)
                .filter(Objects::nonNull)
                .toList();
        Set<String> studentsInClassIds = studentsInClass.stream()
                .map(Students::getId)
                .collect(Collectors.toSet());

        // 2. Danh sách giảng viên
        List<MinorLecturers> lecturersInClass = lecturersClassesService.listLecturersInClass(clazz);
        List<MinorLecturers> lecturersNotInClass = lecturersClassesService.listLecturersNotInClass(clazz);

        // 3. Sinh viên bắt buộc học môn này (Required)
        List<StudentRequiredMinorSubjects> requiredList = requiredSubjectsService
                .getStudentRequiredMinorSubjects(clazz.getMinorSubject());

        List<Students> requiredStudents = requiredList.stream()
                .map(StudentRequiredMinorSubjects::getStudent)
                .filter(Objects::nonNull)
                .filter(s -> !studentsInClassIds.contains(s.getId())) // Loại bỏ SV đã có lớp
                .toList();

        // 4. Danh sách retake
        List<RetakeSubjects> retakeList = retakeSubjectsService.getRetakeSubjectsBySubjectId(subjectId);
        Set<String> retakeStudentIds = retakeList.stream()
                .map(r -> r.getStudent().getId())
                .collect(Collectors.toSet());

        // Loại bỏ SV đã ở retakeList khỏi requiredStudents
        List<Students> eligibleRequiredStudents = requiredStudents.stream()
                .filter(s -> !retakeStudentIds.contains(s.getId()))
                .toList();

        // 5. Chia theo số dư
        List<Students> studentsWithEnoughMoney = retakeSubjectsService
                .getStudentsWithSufficientBalance(subjectId, eligibleRequiredStudents);

        List<Students> studentsDoNotHaveEnoughMoney = retakeSubjectsService
                .getStudentsWithInsufficientBalance(subjectId, eligibleRequiredStudents);

        // 6. Lọc retakeList: chỉ giữ SV KHÔNG có trong lớp
        List<RetakeSubjects> filteredRetakeList = retakeList.stream()
                .filter(r -> !studentsInClassIds.contains(r.getStudent().getId()))
                .toList();

        model.addAttribute("class", clazz);
        model.addAttribute("studentsInClass", studentsInClassEntities); // Giữ entity để hiển thị trong HTML
        model.addAttribute("lecturersInClass", lecturersInClass);
        model.addAttribute("lecturersNotInClass", lecturersNotInClass);
        model.addAttribute("studentsWithEnoughMoney", studentsWithEnoughMoney);
        model.addAttribute("studentsDoNotHaveEnoughMoney", studentsDoNotHaveEnoughMoney);
        model.addAttribute("retakeList", filteredRetakeList); // ĐÃ LỌC

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
        model.addAttribute("retakeList", new ArrayList<>());
        return "MinorClassMemberArrangement";
    }

    // ——— XÓA SINH VIÊN KHỎI LỚP → THÊM VÀO RETAKE (NẾU CHƯA CÓ) ———
    @PostMapping("/remove-student-from-class")
    public String removeStudent(@RequestParam("classId") String classId,
                                @RequestParam(value = "studentIds", required = false) List<String> studentIds,
                                RedirectAttributes ra, HttpSession session) {
        if (studentIds == null || studentIds.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Please select at least one student");
            session.setAttribute("currentClassId", classId);
            return "redirect:/deputy-staff-home/minor-classes-list/member-arrangement";
        }

        MinorClasses clazz = minorClassesService.getClassById(classId);
        if (clazz == null) {
            ra.addFlashAttribute("errorMessage", "Class not found");
            return "redirect:/deputy-staff-home/minor-classes-list/member-arrangement";
        }

        String subjectId = clazz.getMinorSubject().getSubjectId();
        int removedCount = 0;

        for (String sid : studentIds) {
            if (!studentsMinorClassesService.existsByStudentAndClass(sid, classId)) {
                continue;
            }

            // Xóa khỏi lớp
            studentsMinorClassesService.removeStudentFromClass(sid, classId);
            removedCount++;

            // Chỉ thêm vào retake nếu chưa có
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

        ra.addFlashAttribute("successMessage", removedCount + " student(s) removed and added to retake list if needed.");
        session.setAttribute("currentClassId", classId);
        return "redirect:/deputy-staff-home/minor-classes-list/member-arrangement";
    }

    // ——— THÊM SINH VIÊN VÀO LỚP → KHÔNG TRỪ TIỀN NẾU ĐÃ Ở RETAKE ———
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

            if (studentsMinorClassesService.existsByStudentAndClass(sid, classId)) {
                errors.add(s.getFullName() + " already in class");
                continue;
            }

            // Kiểm tra bắt buộc học
            if (!requiredSubjectsService.isStudentAlreadyRequiredForSubject(sid, subjectId)) {
                errors.add(s.getFullName() + " is not required to take this subject");
                continue;
            }

            // Kiểm tra đã ở retake chưa → nếu có, không trừ tiền
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
            Students_MinorClasses smc = new Students_MinorClasses();
            StudentsClassesId id = new StudentsClassesId();
            id.setStudentId(sid);
            id.setClassId(classId);
            smc.setId(id);
            smc.setStudent(s);
            smc.setMinorClass(clazz);
            smc.setClassEntity(abstractClassesService.findClassById(classId));
            smc.setAddedBy(staff);
            smc.setCreatedAt(LocalDateTime.now());
            studentsMinorClassesService.addStudentToClass(smc);
            added++;

            // Chỉ trừ tiền nếu chưa ở retake
            if (!isInRetake) {
                Double fee = getReStudyFee(subjectId, s);
                if (fee != null && fee > 0) {
                    retakeSubjectsService.deductAndLogPayment(s, subjectId, fee);
                }
            }
        }

        if (!errors.isEmpty()) {
            populateError(model, clazz, errors);
            return "MinorClassMemberArrangement";
        }

        ra.addFlashAttribute("successMessage", added + " student(s) added successfully.");
        session.setAttribute("currentClassId", classId);
        return "redirect:/deputy-staff-home/minor-classes-list/member-arrangement";
    }

    // ——— THÊM GIẢNG VIÊN ———
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

    // ——— XÓA GIẢNG VIÊN ———
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

    // ——— HELPER: HIỂN THỊ LỖI ———
    private void populateError(Model model, MinorClasses clazz, List<String> errors) {
        model.addAttribute("errorMessage", String.join("; ", errors));
        model.addAttribute("class", clazz != null ? clazz : new MinorClasses());
        if (clazz != null) {
            List<Students_MinorClasses> studentsInClass = studentsMinorClassesService.getStudentsInClass(clazz.getClassId());
            model.addAttribute("studentsInClass", studentsInClass);
            model.addAttribute("lecturersInClass", lecturersClassesService.listLecturersInClass(clazz));
            model.addAttribute("lecturersNotInClass", lecturersClassesService.listLecturersNotInClass(clazz));

            Set<String> inClassIds = studentsInClass.stream()
                    .map(smc -> smc.getStudent().getId())
                    .collect(Collectors.toSet());
            String subjectId = clazz.getMinorSubject().getSubjectId();
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