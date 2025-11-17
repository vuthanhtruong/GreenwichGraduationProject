package com.example.demo.students_Classes.students_MinorClasses.controller;

import com.example.demo.academicTranscript.service.AcademicTranscriptsService;
import com.example.demo.classes.abstractClasses.service.ClassesService;
import com.example.demo.retakeSubjects.model.RetakeSubjects;
import com.example.demo.retakeSubjects.model.TemporaryRetakeSubjects;
import com.example.demo.retakeSubjects.service.RetakeSubjectsService;
import com.example.demo.retakeSubjects.service.TemporaryRetakeSubjectsService;
import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.minorClasses.service.MinorClassesService;
import com.example.demo.lecturers_Classes.minorLecturers_MinorClasses.service.MinorLecturers_MinorClassesService;
import com.example.demo.scholarshipByYear.model.ScholarshipByYear;
import com.example.demo.scholarshipByYear.service.ScholarshipByYearService;
import com.example.demo.student_scholarship.model.Students_Scholarships;
import com.example.demo.student_scholarship.service.StudentScholarshipService;
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
    private final TemporaryRetakeSubjectsService temporaryRetakeSubjectsService;
    private final AccountBalancesService accountBalancesService;
    private final TuitionByYearService tuitionByYearService;
    private final StudentRequiredMinorSubjectsService requiredSubjectsService;
    private final ClassesService abstractClassesService;
    private final StudentScholarshipService studentScholarshipService;
    private final ScholarshipByYearService scholarshipByYearService;
    private final AcademicTranscriptsService academicTranscriptsService;

    @Autowired
    public MinorClassesMemberArrangementController(
            StudentsMinorClassesService studentsMinorClassesService,
            MinorClassesService minorClassesService,
            MinorLecturers_MinorClassesService lecturersClassesService,
            DeputyStaffsService deputyStaffsService,
            StudentsService studentsService,
            RetakeSubjectsService retakeSubjectsService,
            TemporaryRetakeSubjectsService temporaryRetakeSubjectsService,
            AccountBalancesService accountBalancesService,
            TuitionByYearService tuitionByYearService,
            StudentRequiredMinorSubjectsService requiredSubjectsService,
            ClassesService abstractClassesService,
            StudentScholarshipService studentScholarshipService,
            ScholarshipByYearService scholarshipByYearService, AcademicTranscriptsService academicTranscriptsService) {
        this.studentsMinorClassesService = studentsMinorClassesService;
        this.minorClassesService = minorClassesService;
        this.lecturersClassesService = lecturersClassesService;
        this.deputyStaffsService = deputyStaffsService;
        this.studentsService = studentsService;
        this.retakeSubjectsService = retakeSubjectsService;
        this.temporaryRetakeSubjectsService = temporaryRetakeSubjectsService;
        this.accountBalancesService = accountBalancesService;
        this.tuitionByYearService = tuitionByYearService;
        this.requiredSubjectsService = requiredSubjectsService;
        this.abstractClassesService = abstractClassesService;
        this.studentScholarshipService = studentScholarshipService;
        this.scholarshipByYearService = scholarshipByYearService;
        this.academicTranscriptsService = academicTranscriptsService;
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

        // 1. Students in class (entities)
        List<Students_MinorClasses> studentsInClassEntities = studentsMinorClassesService.getStudentsInClass(classId);
        List<Students> studentsInClass = studentsInClassEntities.stream()
                .map(Students_MinorClasses::getStudent)
                .filter(Objects::nonNull)
                .toList();
        Set<String> studentsInClassIds = studentsInClass.stream()
                .map(Students::getId)
                .collect(Collectors.toSet());

        // 2. Lecturers
        List<MinorLecturers> lecturersInClass = lecturersClassesService.listLecturersInClass(clazz);
        List<MinorLecturers> lecturersNotInClass = lecturersClassesService.listLecturersNotInClass(clazz);

        // 3. Required students for this minor subject (excluding those already in class)
        List<StudentRequiredMinorSubjects> requiredList = requiredSubjectsService
                .getStudentRequiredMinorSubjects(clazz.getMinorSubject());

        List<Students> requiredStudents = requiredList.stream()
                .map(StudentRequiredMinorSubjects::getStudent)
                .filter(Objects::nonNull)
                .filter(s -> !studentsInClassIds.contains(s.getId()))
                .toList();

        // 4. Retake list (paid) for this subject
        List<RetakeSubjects> retakeListAll = retakeSubjectsService.getRetakeSubjectsBySubjectId(subjectId);
        Set<String> retakeStudentIds = retakeListAll.stream()
                .map(r -> r.getStudent().getId())
                .collect(Collectors.toSet());

        // 5. Temporary retake list (removed from class)
        List<TemporaryRetakeSubjects> temporaryRetakeList = temporaryRetakeSubjectsService.getAllPending().stream()
                .filter(t -> t.getSubject() != null
                        && subjectId.equals(t.getSubject().getSubjectId()))
                .filter(t -> !studentsInClassIds.contains(t.getStudent().getId()))
                .toList();
        Set<String> tempStudentIds = temporaryRetakeList.stream()
                .map(t -> t.getStudent().getId())
                .collect(Collectors.toSet());

        // 6. Eligible required students (not in retake, not in temp, và chưa pass)
        List<Students> eligibleRequiredStudents = requiredStudents.stream()
                .filter(s -> !retakeStudentIds.contains(s.getId()))
                .filter(s -> !tempStudentIds.contains(s.getId()))
                .filter(s -> !academicTranscriptsService.hasPassedSubject(s, subjectId)) // <-- LỌC PASS
                .toList();

        // 7. Split by balance
        List<Students> studentsWithEnoughMoney = retakeSubjectsService
                .getStudentsWithSufficientBalance(subjectId, eligibleRequiredStudents);

        List<Students> studentsDoNotHaveEnoughMoney = retakeSubjectsService
                .getStudentsWithInsufficientBalance(subjectId, eligibleRequiredStudents);

        // 8. Retake list only for students not in class
        List<RetakeSubjects> filteredRetakeList = retakeListAll.stream()
                .filter(r -> !studentsInClassIds.contains(r.getStudent().getId()))
                .toList();

        model.addAttribute("class", clazz);
        model.addAttribute("studentsInClass", studentsInClassEntities);
        model.addAttribute("lecturersInClass", lecturersInClass);
        model.addAttribute("lecturersNotInClass", lecturersNotInClass);
        model.addAttribute("studentsWithEnoughMoney", studentsWithEnoughMoney);
        model.addAttribute("studentsDoNotHaveEnoughMoney", studentsDoNotHaveEnoughMoney);
        model.addAttribute("retakeList", filteredRetakeList);
        model.addAttribute("temporaryRetakeList", temporaryRetakeList);

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
        model.addAttribute("temporaryRetakeList", new ArrayList<>());
        return "MinorClassMemberArrangement";
    }

    // REMOVE → add Temporary
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

        int removedCount = 0;

        for (String sid : studentIds) {
            if (!studentsMinorClassesService.existsByStudentAndClass(sid, classId)) {
                continue;
            }

            studentsMinorClassesService.removeStudentFromClass(sid, classId);
            removedCount++;

            Students s = studentsService.getStudentById(sid);
            if (s != null) {
                temporaryRetakeSubjectsService.addToTemporary(
                        s,
                        clazz.getMinorSubject(),
                        "Removed from minor class " + classId
                );
            }
        }

        ra.addFlashAttribute("successMessage",
                removedCount + " student(s) removed and added to temporary retake list.");
        session.setAttribute("currentClassId", classId);
        return "redirect:/deputy-staff-home/minor-classes-list/member-arrangement";
    }

    // ADD → nếu từ Retake / Temporary thì xóa record
    @PostMapping("/add-student-to-class")
    public String addStudentToClass(
            @RequestParam("classId") String classId,
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
            errors.add("Deputy staff information not found");
            populateError(model, clazz, errors);
            return "MinorClassMemberArrangement";
        }

        if (studentIds == null || studentIds.isEmpty()) {
            errors.add("Please select at least one student");
            populateError(model, clazz, errors);
            return "MinorClassMemberArrangement";
        }

        String subjectId = clazz.getMinorSubject().getSubjectId();
        int addedCount = 0;

        for (String studentId : studentIds) {
            Students student = studentsService.getStudentById(studentId);
            if (student == null) {
                errors.add("Student ID " + studentId + " does not exist");
                continue;
            }

            if (studentsMinorClassesService.existsByStudentAndClass(studentId, classId)) {
                errors.add(student.getFullName() + " is already enrolled in this class");
                continue;
            }

            if (!requiredSubjectsService.isStudentAlreadyRequiredForSubject(studentId, subjectId)) {
                errors.add(student.getFullName() + " is not required to take this minor subject");
                continue;
            }

            boolean isInRetake = retakeSubjectsService.existsByStudentAndSubject(studentId, subjectId);
            boolean isInTemporary = temporaryRetakeSubjectsService.exists(studentId, subjectId);

            Double finalFeeToDeduct = 0.0;
            Double originalFee = null;

            if (!isInRetake && !isInTemporary) {
                originalFee = getReStudyFee(subjectId, student);
                if (originalFee == null || originalFee <= 0) {
                    errors.add("Re-study fee not configured for subject: " + subjectId);
                    continue;
                }

                finalFeeToDeduct = originalFee;

                Integer admissionYear = student.getAdmissionYear();
                if (admissionYear != null) {
                    Students_Scholarships activeScholarship = studentScholarshipService
                            .getActiveScholarshipByStudentIdAndYear(studentId, admissionYear);

                    if (activeScholarship != null) {
                        ScholarshipByYear scholarshipByYear = scholarshipByYearService
                                .getFinalizedScholarshipByIdAndYear(
                                        activeScholarship.getScholarship().getScholarshipId(),
                                        admissionYear);

                        if (scholarshipByYear != null
                                && scholarshipByYear.getDiscountPercentage() != null
                                && scholarshipByYear.getDiscountPercentage() > 0
                                && scholarshipByYear.getDiscountPercentage() <= 100) {

                            double discount = scholarshipByYear.getDiscountPercentage();
                            finalFeeToDeduct = originalFee * (100.0 - discount) / 100.0;
                            finalFeeToDeduct = Math.round(finalFeeToDeduct * 100.0) / 100.0;
                        }
                    }
                }

                if (!accountBalancesService.hasSufficientBalance(studentId, finalFeeToDeduct)) {
                    errors.add(student.getFullName() + " has insufficient balance (required: "
                            + String.format("%,.0f", finalFeeToDeduct) + " VND)");
                    continue;
                }
            }

            // ENROLL
            Students_MinorClasses enrollment = new Students_MinorClasses();
            StudentsClassesId id = new StudentsClassesId(studentId, classId);
            enrollment.setId(id);
            enrollment.setStudent(student);
            enrollment.setMinorClass(clazz);
            enrollment.setClassEntity(abstractClassesService.findClassById(classId));
            enrollment.setAddedBy(staff);
            enrollment.setCreatedAt(LocalDateTime.now());

            studentsMinorClassesService.addStudentToClass(enrollment);
            addedCount++;

            if (!isInRetake && !isInTemporary && finalFeeToDeduct > 0) {
                retakeSubjectsService.deductAndLogPayment(student, subjectId, finalFeeToDeduct);
            }

            // Đã vào lớp → xóa record Retake / Temporary
            if (isInRetake) {
                retakeSubjectsService.deleteByStudentAndSubject(studentId, subjectId);
            }
            if (isInTemporary) {
                temporaryRetakeSubjectsService.deleteByStudentAndSubject(studentId, subjectId);
            }
        }

        if (!errors.isEmpty()) {
            populateError(model, clazz, errors);
            return "MinorClassMemberArrangement";
        }

        ra.addFlashAttribute("successMessage",
                "Successfully added " + addedCount + " student(s) to the minor class.");
        session.setAttribute("currentClassId", classId);
        return "redirect:/deputy-staff-home/minor-classes-list/member-arrangement";
    }

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

            List<TemporaryRetakeSubjects> temporaryRetakeList = temporaryRetakeSubjectsService.getAllPending().stream()
                    .filter(t -> t.getSubject() != null
                            && subjectId.equals(t.getSubject().getSubjectId()))
                    .filter(t -> !inClassIds.contains(t.getStudent().getId()))
                    .toList();

            model.addAttribute("retakeList", retakeList);
            model.addAttribute("temporaryRetakeList", temporaryRetakeList);
        } else {
            model.addAttribute("studentsInClass", new ArrayList<>());
            model.addAttribute("lecturersInClass", new ArrayList<>());
            model.addAttribute("lecturersNotInClass", new ArrayList<>());
            model.addAttribute("retakeList", new ArrayList<>());
            model.addAttribute("temporaryRetakeList", new ArrayList<>());
        }
        model.addAttribute("studentsWithEnoughMoney", new ArrayList<>());
        model.addAttribute("studentsDoNotHaveEnoughMoney", new ArrayList<>());
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
