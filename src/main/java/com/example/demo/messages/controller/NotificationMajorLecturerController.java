package com.example.demo.messages.controller;

import com.example.demo.academicTranscript.service.AcademicTranscriptsService;
import com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.service.MajorLecturers_MajorClassesService;
import com.example.demo.majorLecturers_Specializations.service.MajorLecturersSpecializationsService;
import com.example.demo.post.majorAssignmentSubmitSlots.service.AssignmentSubmitSlotsService;
import com.example.demo.post.majorClassPosts.service.MajorClassPostsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Controller
@RequestMapping("/major-lecturer-home/notifications")
public class NotificationMajorLecturerController {

    private final MajorLecturers_MajorClassesService majorLecturersMajorClassesService;
    private final MajorClassPostsService majorClassPostsService;
    private final AssignmentSubmitSlotsService assignmentSubmitSlotsService;
    private final AcademicTranscriptsService academicTranscriptsService;
    private final MajorLecturersSpecializationsService majorLecturersSpecializationsService;

    public NotificationMajorLecturerController(
            MajorLecturers_MajorClassesService majorLecturersMajorClassesService,
            MajorClassPostsService majorClassPostsService,
            AssignmentSubmitSlotsService assignmentSubmitSlotsService,
            AcademicTranscriptsService academicTranscriptsService, MajorLecturersSpecializationsService majorLecturersSpecializationsService) {
        this.majorLecturersMajorClassesService = majorLecturersMajorClassesService;
        this.majorClassPostsService = majorClassPostsService;
        this.assignmentSubmitSlotsService = assignmentSubmitSlotsService;
        this.academicTranscriptsService = academicTranscriptsService;
        this.majorLecturersSpecializationsService = majorLecturersSpecializationsService;
    }

    @PostMapping
    public String showNotifications(Model model, HttpSession session, @RequestParam String lecturerId) {
        List<String> notifications = Stream.of(
                        // 1. Được thêm vào lớp Major
                        majorLecturersMajorClassesService.getClassNotificationsForLecturer(lecturerId),
                        // 2. Bài đăng trong lớp Major
                        majorClassPostsService.getNotificationsForMemberId(lecturerId),
                        // 3. Bài tập trong lớp Major
                        assignmentSubmitSlotsService.getNotificationsForMemberId(lecturerId),
                        // 4. Cập nhật điểm (Major)
                        academicTranscriptsService.getNotificationsForMemberId(lecturerId),
                majorLecturersSpecializationsService.getSpecializationAssignmentNotifications(lecturerId)
                )
                .flatMap(List::stream)
                .filter(notif -> notif != null && notif.contains(" on "))
                .sorted(Comparator.comparing(this::extractTimeFromNotification).reversed())
                .toList();

        model.addAttribute("notifications", notifications);
        model.addAttribute("lecturerId", lecturerId);
        return "NotificationsMajorLecturer";
    }

    private LocalDateTime extractTimeFromNotification(String notification) {
        try {
            int onIndex = notification.lastIndexOf(" on ");
            if (onIndex == -1) return LocalDateTime.MIN;

            String timeStr = notification.substring(onIndex + 4).trim();
            DateTimeFormatter formatter = timeStr.contains(".")
                    ? DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
                    : DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

            return LocalDateTime.parse(timeStr, formatter);
        } catch (Exception e) {
            return LocalDateTime.MIN;
        }
    }
}