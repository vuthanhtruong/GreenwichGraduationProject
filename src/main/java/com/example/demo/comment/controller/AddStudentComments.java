package com.example.demo.comment.controller;

import com.example.demo.comment.model.StudentComments;
import com.example.demo.comment.service.StudentCommentsService;
import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.post.classPost.service.ClassPostsService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/student-home/student-classes-list/classroom")
public class AddStudentComments {

    private final StudentCommentsService studentCommentsService;
    private final ClassPostsService classPostsService;
    private final StudentsService studentsService;

    public AddStudentComments(
            StudentCommentsService studentCommentsService,
            ClassPostsService classPostsService,
            StudentsService studentsService) {
        this.studentCommentsService = studentCommentsService;
        this.classPostsService = classPostsService;
        this.studentsService = studentsService;
    }

    @PostMapping("/add-comment")
    public String addComment(
            @RequestParam("postId") String postId,
            @RequestParam("classId") String classId,
            @RequestParam("content") String content,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            // 1. Kiểm tra đầu vào
            if (postId == null || postId.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errors", List.of("Post ID is required"));
                return redirectToClassroom(classId, session);
            }
            if (classId == null || classId.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errors", List.of("Class ID is required"));
                return redirectToClassroom(classId, session);
            }
            if (content == null || content.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errors", List.of("Comment content cannot be empty"));
                return redirectToClassroom(classId, session);
            }

            // 2. Kiểm tra post tồn tại
            ClassPosts post = classPostsService.findPostById(postId);
            if (post == null) {
                redirectAttributes.addFlashAttribute("errors", List.of("Post not found"));
                return redirectToClassroom(classId, session);
            }

            // 3. Kiểm tra sinh viên
            Students commenter = studentsService.getStudent();
            if (commenter == null) {
                redirectAttributes.addFlashAttribute("errors", List.of("Student not authenticated"));
                return redirectToClassroom(classId, session);
            }

            // 4. Tạo comment
            StudentComments comment = createStudentComment(post, commenter, content, postId);

            // 5. Validate
            Map<String, String> errors = studentCommentsService.validateComment(comment);
            if (!errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("errors", new ArrayList<>(errors.values()));
                return redirectToClassroom(classId, session);
            }

            // 6. Lưu comment
            studentCommentsService.saveComment(comment);

            // 7. Thành công
            redirectAttributes.addFlashAttribute("message", "Comment added successfully!");
            return redirectToClassroom(classId, session);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errors", List.of("Failed to add comment: " + e.getMessage()));
            return redirectToClassroom(classId, session);
        }
    }

    // ================== TẠO COMMENT ==================
    private StudentComments createStudentComment(ClassPosts post, Students commenter, String content, String postId) {
        StudentComments comment = new StudentComments();
        comment.setCommentId(studentCommentsService.generateUniqueCommentId(postId, LocalDate.now()));
        comment.setContent(content.trim());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setPost(post);
        comment.setCommenter(commenter);
        comment.setNotification(null); // Có thể tạo notification ở đây nếu cần
        return comment;
    }

    // ================== HELPER ==================
    private String redirectToClassroom(String classId, HttpSession session) {
        session.setAttribute("classId", classId);
        return "redirect:/student-home/student-classes-list/classroom";
    }
}