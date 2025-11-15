package com.example.demo.students_Classes.students_SpecializedClasses.controller;

import com.example.demo.retakeSubjects.model.RetakeSubjects;
import com.example.demo.retakeSubjects.service.RetakeSubjectsService;
import com.example.demo.classes.abstractClasses.service.ClassesService;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.classes.specializedClasses.service.SpecializedClassesService;
import com.example.demo.lecturers_Classes.majorLecturers_SpecializedClasses.service.MajorLecturers_SpecializedClassesService;
import com.example.demo.studentRequiredMajorSubjects.model.StudentRetakeSubjectsId;
import com.example.demo.studentRequiredSubjects.studentRequiredSpecializedSubjects.model.StudentRequiredSpecializedSubjects;
import com.example.demo.studentRequiredSubjects.studentRequiredSpecializedSubjects.service.StudentRequiredSpecializedSubjectsService;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import com.example.demo.students_Classes.abstractStudents_Class.model.StudentsClassesId;
import com.example.demo.students_Classes.students_SpecializedClasses.model.Students_SpecializedClasses;
import com.example.demo.students_Classes.students_SpecializedClasses.service.StudentsSpecializedClassesService;
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
@RequestMapping("/staff-home/specialized-classes-list")
@PreAuthorize("hasRole('STAFF')")
public class SpecializedClassesMemberArrangementController {

    private final StudentsSpecializedClassesService studentsSpecializedClassesService;
    private final SpecializedClassesService specializedClassesService;
    private final MajorLecturers_SpecializedClassesService lecturersClassesService;
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final ClassesService classesService;
    private final RetakeSubjectsService retakeSubjectsService;
    private final AccountBalancesService accountBalancesService;
    private final TuitionByYearService tuitionByYearService;
    private final StudentRequiredSpecializedSubjectsService studentRequiredSpecializedSubjectsService;

    @Autowired
    public SpecializedClassesMemberArrangementController(
            StudentsSpecializedClassesService studentsSpecializedClassesService,
            SpecializedClassesService specializedClassesService,
            MajorLecturers_SpecializedClassesService lecturersClassesService,
            StaffsService staffsService,
            StudentsService studentsService,
            ClassesService classesService,
            RetakeSubjectsService retakeSubjectsService,
            AccountBalancesService accountBalancesService,
            TuitionByYearService tuitionByYearService,
            StudentRequiredSpecializedSubjectsService studentRequiredSpecializedSubjectsService) {
        this.studentsSpecializedClassesService = studentsSpecializedClassesService;
        this.specializedClassesService = specializedClassesService;
        this.lecturersClassesService = lecturersClassesService;
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.classesService = classesService;
        this.retakeSubjectsService = retakeSubjectsService;
        this.accountBalancesService = accountBalancesService;
        this.tuitionByYearService = tuitionByYearService;
        this.studentRequiredSpecializedSubjectsService = studentRequiredSpecializedSubjectsService;
    }

    @PostMapping("/member-arrangement")
    public String selectClass(@RequestParam("classId") String classId,
                              HttpSession session,
                              RedirectAttributes ra) {
        if (specializedClassesService.getClassById(classId) == null) {
            ra.addFlashAttribute("errorMessage", "Class not found");
            return "redirect:/staff-home/specialized-classes-list";
        }
        session.setAttribute("currentClassId", classId);
        return "redirect:/staff-home/specialized-classes-list/member-arrangement";
    }

    @GetMapping("/member-arrangement")
    public String showMemberArrangement(Model model, HttpSession session) {
        String classId = (String) session.getAttribute("currentClassId");
        if (classId == null) {
            return handleNoClassSelected(model);
        }

        SpecializedClasses clazz = specializedClassesService.getClassById(classId);
        if (clazz == null) {
            return handleNoClassSelected(model);
        }

        String subjectId = clazz.getSpecializedSubject().getSubjectId();

        // 1. Danh sách sinh viên đang trong lớp
        List<Students_SpecializedClasses> studentsInClassEntities = studentsSpecializedClassesService.getStudentsInClass(classId);
        List<Students> studentsInClass = studentsInClassEntities.stream()
                .map(Students_SpecializedClasses::getStudent)
                .filter(Objects::nonNull)
                .toList();
        Set<String> studentsInClassIds = studentsInClass.stream()
                .map(Students::getId)
                .collect(Collectors.toSet());

        // 2. Danh sách giảng viên
        List<MajorLecturers> lecturersInClass = lecturersClassesService.listLecturersInClass(clazz);
        List<MajorLecturers> lecturersNotInClass = lecturersClassesService.listLecturersNotInClass(clazz);

        // 3. Sinh viên bắt buộc học môn này (Required)
        List<StudentRequiredSpecializedSubjects> requiredList = studentRequiredSpecializedSubjectsService
                .getStudentRequiredSpecializedSubjects(clazz.getSpecializedSubject(), null);

        List<Students> requiredStudents = requiredList.stream()
                .map(StudentRequiredSpecializedSubjects::getStudent)
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
        model.addAttribute("studentsInClass", studentsInClassEntities); // Giữ entity để hiển thị
        model.addAttribute("lecturersInClass", lecturersInClass);
        model.addAttribute("lecturersNotInClass", lecturersNotInClass);
        model.addAttribute("studentsWithEnoughMoney", studentsWithEnoughMoney);
        model.addAttribute("studentsDoNotHaveEnoughMoney", studentsDoNotHaveEnoughMoney);
        model.addAttribute("retakeList", filteredRetakeList); // ĐÃ LỌC

        return "SpecializedClassMemberArrangement";
    }

    private String handleNoClassSelected(Model model) {
        model.addAttribute("errorMessage", "No class selected");
        model.addAttribute("class", new SpecializedClasses());
        model.addAttribute("studentsInClass", new ArrayList<>());
        model.addAttribute("lecturersInClass", new ArrayList<>());
        model.addAttribute("lecturersNotInClass", new ArrayList<>());
        model.addAttribute("studentsWithEnoughMoney", new ArrayList<>());
        model.addAttribute("studentsDoNotHaveEnoughMoney", new ArrayList<>());
        model.addAttribute("retakeList", new ArrayList<>());
        return "SpecializedClassMemberArrangement";
    }

    // ——— XÓA SINH VIÊN KHỎI LỚP → THÊM VÀO RETAKE (NẾU CHƯA CÓ) ———
    @PostMapping("/remove-student-from-class")
    public String removeStudent(@RequestParam("classId") String classId,
                                @RequestParam(value = "studentIds", required = false) List<String> studentIds,
                                RedirectAttributes ra, HttpSession session) {
        if (studentIds == null || studentIds.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Please select at least one student");
            session.setAttribute("currentClassId", classId);
            return "redirect:/staff-home/specialized-classes-list/member-arrangement";
        }

        SpecializedClasses clazz = specializedClassesService.getClassById(classId);
        if (clazz == null) {
            ra.addFlashAttribute("errorMessage", "Class not found");
            return "redirect:/staff-home/specialized-classes-list/member-arrangement";
        }

        String subjectId = clazz.getSpecializedSubject().getSubjectId();
        int removedCount = 0;

        for (String sid : studentIds) {
            if (!studentsSpecializedClassesService.existsByStudentAndClass(sid, classId)) {
                continue;
            }

            // Xóa khỏi lớp
            studentsSpecializedClassesService.removeStudentFromClass(sid, classId);
            removedCount++;

            // Chỉ thêm vào retake nếu chưa có
            if (!retakeSubjectsService.existsByStudentAndSubject(sid, subjectId)) {
                Students s = studentsService.getStudentById(sid);
                if (s != null) {
                    RetakeSubjects r = new RetakeSubjects();
                    r.setId(new StudentRetakeSubjectsId(sid, subjectId));
                    r.setStudent(s);
                    r.setSubject(clazz.getSpecializedSubject());
                    r.setRetakeReason("Removed from specialized class");
                    r.setCreatedAt(LocalDateTime.now());
                    retakeSubjectsService.save(r);
                }
            }
        }

        ra.addFlashAttribute("successMessage", removedCount + " student(s) removed and added to retake list if needed.");
        session.setAttribute("currentClassId", classId);
        return "redirect:/staff-home/specialized-classes-list/member-arrangement";
    }

    // ——— THÊM SINH VIÊN VÀO LỚP → KHÔNG TRỪ TIỀN NẾU ĐÃ Ở RETAKE ———
    @PostMapping("/add-student-to-class")
    public String addStudent(@RequestParam("classId") String classId,
                             @RequestParam(value = "studentIds", required = false) List<String> studentIds,
                             Model model, RedirectAttributes ra, HttpSession session) {
        List<String> errors = new ArrayList<>();
        SpecializedClasses clazz = specializedClassesService.getClassById(classId);
        if (clazz == null) {
            errors.add("Class not found");
            populateError(model, null, errors);
            return "SpecializedClassMemberArrangement";
        }

        Staffs staff = staffsService.getStaff();
        if (staff == null) {
            errors.add("Staff not found");
            populateError(model, clazz, errors);
            return "SpecializedClassMemberArrangement";
        }

        if (studentIds == null || studentIds.isEmpty()) {
            errors.add("Please select at least one student");
            populateError(model, clazz, errors);
            return "SpecializedClassMemberArrangement";
        }

        String subjectId = clazz.getSpecializedSubject().getSubjectId();
        int added = 0;

        for (String sid : studentIds) {
            Students s = studentsService.getStudentById(sid);
            if (s == null) continue;

            if (studentsSpecializedClassesService.existsByStudentAndClass(sid, classId)) {
                errors.add(s.getFullName() + " already in class");
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
            Students_SpecializedClasses ssc = new Students_SpecializedClasses();
            StudentsClassesId id = new StudentsClassesId();
            id.setStudentId(sid);
            id.setClassId(classId);
            ssc.setId(id);
            ssc.setStudent(s);
            ssc.setSpecializedClass(clazz);
            ssc.setClassEntity(classesService.findClassById(classId));
            ssc.setAddedBy(staff);
            ssc.setCreatedAt(LocalDateTime.now());
            studentsSpecializedClassesService.addStudentToClass(ssc);
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
            return "SpecializedClassMemberArrangement";
        }

        ra.addFlashAttribute("successMessage", added + " student(s) added successfully.");
        session.setAttribute("currentClassId", classId);
        return "redirect:/staff-home/specialized-classes-list/member-arrangement";
    }

    // ——— THÊM GIẢNG VIÊN ———
    @PostMapping("/add-lecturer-to-class")
    public String addLecturers(@RequestParam("classId") String classId,
                               @RequestParam(value = "lecturerIds", required = false) List<String> lecturerIds,
                               RedirectAttributes ra, HttpSession session) {
        if (lecturerIds != null && !lecturerIds.isEmpty()) {
            SpecializedClasses clazz = specializedClassesService.getClassById(classId);
            if (clazz != null) {
                lecturersClassesService.addLecturersToClass(clazz, lecturerIds);
                ra.addFlashAttribute("successMessage", lecturerIds.size() + " lecturer(s) added.");
            }
        }
        session.setAttribute("currentClassId", classId);
        return "redirect:/staff-home/specialized-classes-list/member-arrangement";
    }

    // ——— XÓA GIẢNG VIÊN ———
    @PostMapping("/remove-lecturer-from-class")
    public String removeLecturers(@RequestParam("classId") String classId,
                                  @RequestParam(value = "lecturerIds", required = false) List<String> lecturerIds,
                                  RedirectAttributes ra, HttpSession session) {
        if (lecturerIds != null && !lecturerIds.isEmpty()) {
            SpecializedClasses clazz = specializedClassesService.getClassById(classId);
            if (clazz != null) {
                lecturersClassesService.removeLecturerFromClass(clazz, lecturerIds);
                ra.addFlashAttribute("successMessage", lecturerIds.size() + " lecturer(s) removed.");
            }
        }
        session.setAttribute("currentClassId", classId);
        return "redirect:/staff-home/specialized-classes-list/member-arrangement";
    }

    // ——— HELPER: HIỂN THỊ LỖI ———
    private void populateError(Model model, SpecializedClasses clazz, List<String> errors) {
        model.addAttribute("errorMessage", String.join("; ", errors));
        model.addAttribute("class", clazz != null ? clazz : new SpecializedClasses());
        if (clazz != null) {
            List<Students_SpecializedClasses> studentsInClass = studentsSpecializedClassesService.getStudentsInClass(clazz.getClassId());
            model.addAttribute("studentsInClass", studentsInClass);
            model.addAttribute("lecturersInClass", lecturersClassesService.listLecturersInClass(clazz));
            model.addAttribute("lecturersNotInClass", lecturersClassesService.listLecturersNotInClass(clazz));

            Set<String> inClassIds = studentsInClass.stream()
                    .map(ssc -> ssc.getStudent().getId())
                    .collect(Collectors.toSet());
            String subjectId = clazz.getSpecializedSubject().getSubjectId();
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