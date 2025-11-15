package com.example.demo.messages.controller;

import com.example.demo.academicTranscript.service.AcademicTranscriptsService;
import com.example.demo.comment.service.MinorCommentsService;
import com.example.demo.lecturers_Classes.minorLecturers_MinorClasses.service.MinorLecturers_MinorClassesService;
import com.example.demo.post.minorClassPosts.service.MinorClassPostsService;
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
@RequestMapping("/minor-lecturer-home/notifications")
public class NotificationMinorLecturerController {

    private final MinorLecturers_MinorClassesService minorLecturersMinorClassesService;
    private final MinorClassPostsService minorClassPostsService;
    private final AcademicTranscriptsService academicTranscriptsService;
    private final MinorCommentsService minorCommentsService; // THÊM: BÌNH LUẬN

    public NotificationMinorLecturerController(
            MinorLecturers_MinorClassesService minorLecturersMinorClassesService,
            MinorClassPostsService minorClassPostsService,
            AcademicTranscriptsService academicTranscriptsService,
            MinorCommentsService minorCommentsService // THÊM VÀO CONSTRUCTOR
    ) {
        this.minorLecturersMinorClassesService = minorLecturersMinorClassesService;
        this.minorClassPostsService = minorClassPostsService;
        this.academicTranscriptsService = academicTranscriptsService;
        this.minorCommentsService = minorCommentsService;
    }

    @PostMapping
    public String showNotifications(Model model, HttpSession session, @RequestParam String lecturerId) {
        List<String> notifications = Stream.of(
                        // 1. Được thêm vào lớp Minor
                        minorLecturersMinorClassesService.getClassNotificationsForLecturer(lecturerId),
                        // 2. Bài đăng mới trong lớp Minor (của mình hoặc người khác)
                        minorClassPostsService.getNotificationsForMemberId(lecturerId),
                        // 3. Cập nhật điểm (Minor)
                        academicTranscriptsService.getNotificationsForMemberId(lecturerId),
                        // 4. Có người bình luận vào bài đăng của mình
                        minorCommentsService.getCommentNotificationsForLecturer(lecturerId)
                )
                .flatMap(List::stream)
                .filter(notif -> notif != null && notif.contains(" on "))
                .sorted(Comparator.comparing(this::extractTimeFromNotification).reversed())
                .toList();

        model.addAttribute("notifications", notifications);
        model.addAttribute("lecturerId", lecturerId);
        return "NotificationsMinorLecturer";
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