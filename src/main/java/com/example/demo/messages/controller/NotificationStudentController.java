package com.example.demo.messages.controller;

import com.example.demo.academicTranscript.service.AcademicTranscriptsService;
import com.example.demo.post.majorAssignmentSubmitSlots.service.AssignmentSubmitSlotsService;
import com.example.demo.post.majorClassPosts.service.MajorClassPostsService;
import com.example.demo.post.minorClassPosts.service.MinorClassPostsService;
import com.example.demo.post.specializedAssignmentSubmitSlots.service.SpecializedAssignmentSubmitSlotsService;
import com.example.demo.post.specializedClassPosts.service.SpecializedClassPostsService;
import com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.model.StudentRequiredMajorSubjects;
import com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.service.StudentRequiredMajorSubjectsService;
import com.example.demo.studentRequiredSubjects.studentRequiredMinorSubjects.model.StudentRequiredMinorSubjects;
import com.example.demo.studentRequiredSubjects.studentRequiredMinorSubjects.service.StudentRequiredMinorSubjectsService;
import com.example.demo.studentRequiredSubjects.studentRequiredSpecializedSubjects.model.StudentRequiredSpecializedSubjects;
import com.example.demo.studentRequiredSubjects.studentRequiredSpecializedSubjects.service.StudentRequiredSpecializedSubjectsService;
import com.example.demo.students_Classes.students_MajorClass.service.StudentsMajorClassesService;
import com.example.demo.students_Classes.students_MinorClasses.service.StudentsMinorClassesService;
import com.example.demo.students_Classes.students_SpecializedClasses.service.StudentsSpecializedClassesService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Controller
@RequestMapping("/student-home/notifications")
public class NotificationStudentController {
    private final StudentsSpecializedClassesService studentsSpecializedClassesService;
    private final StudentsMajorClassesService studentsMajorClassesService;
    private final StudentsMinorClassesService studentsMinorClassesService;
    private final MinorClassPostsService minorClassPostsService;
    private final MajorClassPostsService majorClassPostsService;
    private final SpecializedClassPostsService specializedClassPostsService;
    private final StudentsService studentsService;
    private final AssignmentSubmitSlotsService assignmentSubmitSlotsService;
    private final SpecializedAssignmentSubmitSlotsService specializedAssignmentSubmitSlotsService;
    private final AcademicTranscriptsService academicTranscriptsService;
    private final StudentRequiredMajorSubjectsService studentRequiredMajorSubjectsService;
    private final StudentRequiredSpecializedSubjectsService studentRequiredSpecializedSubjectsService;
    private final StudentRequiredMinorSubjectsService studentRequiredMinorSubjectsService;

    public NotificationStudentController(StudentsSpecializedClassesService studentsSpecializedClassesService, StudentsMajorClassesService studentsMajorClassesService, StudentsMinorClassesService studentsMinorClassesService, MinorClassPostsService minorClassPostsService, MajorClassPostsService majorClassPostsService, SpecializedClassPostsService specializedClassPostsService, StudentsService studentsService, AssignmentSubmitSlotsService assignmentSubmitSlotsService, SpecializedAssignmentSubmitSlotsService specializedAssignmentSubmitSlotsService, AcademicTranscriptsService academicTranscriptsService, StudentRequiredMajorSubjectsService studentRequiredMajorSubjectsService, StudentRequiredSpecializedSubjectsService studentRequiredSpecializedSubjectsService, StudentRequiredMinorSubjectsService studentRequiredMinorSubjectsService) {
        this.studentsSpecializedClassesService = studentsSpecializedClassesService;
        this.studentsMajorClassesService = studentsMajorClassesService;
        this.studentsMinorClassesService = studentsMinorClassesService;
        this.minorClassPostsService = minorClassPostsService;
        this.majorClassPostsService = majorClassPostsService;
        this.specializedClassPostsService = specializedClassPostsService;
        this.studentsService = studentsService;
        this.assignmentSubmitSlotsService = assignmentSubmitSlotsService;
        this.specializedAssignmentSubmitSlotsService = specializedAssignmentSubmitSlotsService;
        this.academicTranscriptsService = academicTranscriptsService;
        this.studentRequiredMajorSubjectsService = studentRequiredMajorSubjectsService;
        this.studentRequiredSpecializedSubjectsService = studentRequiredSpecializedSubjectsService;
        this.studentRequiredMinorSubjectsService = studentRequiredMinorSubjectsService;
    }
    @PostMapping
    public String showNotifications(Model model, HttpSession session, @RequestParam String studentId) {
        List<String> notifications = Stream.of(
                        // 1. Major Posts
                        majorClassPostsService.getNotificationsForMemberId(studentId),
                        // 2. Minor Posts
                        minorClassPostsService.getNotificationsForMemberId(studentId),
                        // 3. Specialized Posts
                        specializedClassPostsService.getNotificationsForMemberId(studentId),
                        // 4. Major Assignment
                        assignmentSubmitSlotsService.getNotificationsForMemberId(studentId),
                        // 5. Specialized Assignment
                        specializedAssignmentSubmitSlotsService.getNotificationsForMemberId(studentId),
                        // 6. Major Enrollment
                        studentsMajorClassesService.getClassNotificationsForStudent(studentId),
                        // 7. Minor Enrollment
                        studentsMinorClassesService.getClassNotificationsForStudent(studentId),
                        // 8. Specialized Enrollment
                        studentsSpecializedClassesService.getClassNotificationsForStudent(studentId),
                        // 9. Major Required Subject
                        studentRequiredMajorSubjectsService.getRequiredSubjectNotificationsForStudent(studentId),
                        // 10. Minor Required Subject
                        studentRequiredMinorSubjectsService.getRequiredSubjectNotificationsForStudent(studentId),
                        // 11. Specialized Required Subject
                        studentRequiredSpecializedSubjectsService.getRequiredSubjectNotificationsForStudent(studentId),
                        // 12. Grade Updates (Major/Minor/Specialized)
                        academicTranscriptsService.getNotificationsForMemberId(studentId)
                )
                .flatMap(List::stream)
                .filter(notif -> notif != null && notif.contains(" on ")) // chỉ giữ thông báo có thời gian
                .sorted(Comparator.comparing(this::extractTimeFromNotification).reversed())
                .toList();

        model.addAttribute("notifications", notifications);
        return "NotificationsStudent";
    }
    private java.time.LocalDateTime extractTimeFromNotification(String notification) {
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("Deadline: ([\\d\\-T:]+)").matcher(notification);
        if (matcher.find()) {
            try {
                return java.time.LocalDateTime.parse(matcher.group(1));
            } catch (Exception e) {
                // ignore
            }
        }
        return java.time.LocalDateTime.now(); // đẩy xuống cuối nếu không có deadline
    }
}
