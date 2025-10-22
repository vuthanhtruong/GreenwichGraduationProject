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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
            Model model) {
        try {
            ClassPosts post = classPostsService.findPostById(postId);
            if (post == null) {
                model.addAttribute("errors", List.of("Post not found"));
                return "redirect:/student-home/student-classes-list/classroom";
            }

            Students commenter = studentsService.getStudent();
            if (commenter == null) {
                model.addAttribute("errors", List.of("Student not authenticated"));
                return "redirect:/student-home/student-classes-list/classroom";
            }

            StudentComments comment = new StudentComments();
            comment.setCommentId(UUID.randomUUID().toString());
            comment.setCommenter(commenter);
            comment.setPost(post);
            comment.setContent(content);
            comment.setCreatedAt(LocalDateTime.now());
            comment.setNotification(null); // Set notification as needed

            studentCommentsService.saveComment(comment);

            session.setAttribute("classId", classId);
            return "redirect:/student-home/student-classes-list/classroom";
        } catch (Exception e) {
            model.addAttribute("errors", List.of("Failed to add comment: " + e.getMessage()));
            return "redirect:/student-home/student-classes-list/classroom";
        }
    }
}