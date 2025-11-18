package com.example.demo.comment.controller;

import com.example.demo.comment.model.*;
import com.example.demo.comment.service.*;
import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.post.classPost.service.ClassPostsService;
import com.example.demo.post.majorClassPosts.model.MajorClassPosts;
import com.example.demo.post.majorClassPosts.service.MajorClassPostsService;
import com.example.demo.post.minorClassPosts.model.MinorClassPosts;
import com.example.demo.post.minorClassPosts.service.MinorClassPostsService;
import com.example.demo.user.employe.model.MajorEmployes;
import com.example.demo.user.employe.model.MinorEmployes;
import com.example.demo.user.person.service.PersonsService;
import com.example.demo.user.student.model.Students;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
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
@RequestMapping("/classroom")
public class AddComments {

    private final MajorClassPostsService majorClassPostsService;
    private final MinorClassPostsService minorClassPostsService;
    private final ClassPostsService classPostsService;
    private final MajorCommentsService majorCommentsService;
    private final MinorCommentsService minorCommentsService;
    private final StudentCommentsService studentCommentsService;
    private final PersonsService personsService;

    public AddComments(
            MajorClassPostsService majorClassPostsService,
            MinorClassPostsService minorClassPostsService,
            ClassPostsService classPostsService,
            MajorCommentsService majorCommentsService,
            MinorCommentsService minorCommentsService,
            StudentCommentsService studentCommentsService,
            PersonsService personsService) {

        this.majorClassPostsService = majorClassPostsService;
        this.minorClassPostsService = minorClassPostsService;
        this.classPostsService = classPostsService;
        this.majorCommentsService = majorCommentsService;
        this.minorCommentsService = minorCommentsService;
        this.studentCommentsService = studentCommentsService;
        this.personsService = personsService;
    }

    @PostMapping("/add-comment")
    public String addComment(
            @RequestParam("postId") String postId,
            @RequestParam("classId") String classId,
            @RequestParam("content") String content,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            if (isEmpty(postId) || isEmpty(classId) || isEmpty(content)) {
                return redirectWithError("Invalid input", classId, session, redirectAttributes);
            }

            ClassPosts post = classPostsService.findPostById(postId);
            Object person = personsService.getPerson();
            if (person instanceof MajorEmployes majorEmployee) {
                return handleMajorComment(postId, content, majorEmployee, classId, session, redirectAttributes);
            } else if (person instanceof MinorEmployes minorEmployee) {
                return handleMinorComment(postId, content, minorEmployee, classId, session, redirectAttributes);
            } else if (person instanceof Students student) {
                return handleStudentComment(post, content, student, classId, session, redirectAttributes);
            } else {
                return redirectWithError("Unauthorized user type", classId, session, redirectAttributes);
            }

        } catch (Exception e) {
            return redirectWithError("Failed to add comment: " + e.getMessage(), classId, session, redirectAttributes);
        }
    }

    // ================== MAJOR COMMENT ==================
    private String handleMajorComment(String postId, String content, MajorEmployes commenter,
                                      String classId, HttpSession session, RedirectAttributes ra) {
        MajorClassPosts post = majorClassPostsService.getMajorClassPost(postId);
        if (post == null) {
            return redirectWithError("Major post not found", classId, session, ra);
        }

        MajorComments comment = createMajorComment(post, commenter, content);
        Map<String, String> errors = majorCommentsService.validateComment(comment);
        if (!errors.isEmpty()) {
            ra.addFlashAttribute("errors", new ArrayList<>(errors.values()));
            return redirectToClassroom(classId, session);
        }

        majorCommentsService.saveComment(comment);
        ra.addFlashAttribute("message", "Comment added successfully!");
        return redirectToClassroom(classId, session);
    }

    private MajorComments createMajorComment(MajorClassPosts post, MajorEmployes commenter, String content) {
        MajorComments comment = new MajorComments();
        comment.setCommentId(majorCommentsService.generateUniqueCommentId(post.getPostId(), LocalDate.now()));
        comment.setContent(content.trim());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setPost(post);
        comment.setCommenter(commenter);
        return comment;
    }

    // ================== MINOR COMMENT ==================
    private String handleMinorComment(String postId, String content, MinorEmployes commenter,
                                      String classId, HttpSession session, RedirectAttributes ra) {
        MinorClassPosts post = minorClassPostsService.getMinorClassPost(postId);
        if (post == null) {
            return redirectWithError("Minor post not found", classId, session, ra);
        }

        MinorComments comment = createMinorComment(post, commenter, content);
        Map<String, String> errors = minorCommentsService.validateComment(comment);
        if (!errors.isEmpty()) {
            ra.addFlashAttribute("errors", new ArrayList<>(errors.values()));
            return redirectToClassroom(classId, session);
        }

        minorCommentsService.saveComment(comment);
        ra.addFlashAttribute("message", "Comment added successfully!");
        return redirectToClassroom(classId, session);
    }

    private MinorComments createMinorComment(MinorClassPosts post, MinorEmployes commenter, String content) {
        MinorComments comment = new MinorComments();
        comment.setCommentId(minorCommentsService.generateUniqueCommentId(post.getPostId(), LocalDate.now()));
        comment.setContent(content.trim());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setPost(post);
        comment.setCommenter(commenter);
        return comment;
    }

    // ================== STUDENT COMMENT ==================
    private String handleStudentComment(ClassPosts post, String content, Students commenter,
                                        String classId, HttpSession session, RedirectAttributes ra) {
        StudentComments comment = createStudentComment(post, commenter, content);
        Map<String, String> errors = studentCommentsService.validateComment(comment);
        if (!errors.isEmpty()) {
            ra.addFlashAttribute("errors", new ArrayList<>(errors.values()));
            return redirectToClassroom(classId, session);
        }

        studentCommentsService.saveComment(comment);
        ra.addFlashAttribute("message", "Comment added successfully!");
        return redirectToClassroom(classId, session);
    }

    private StudentComments createStudentComment(ClassPosts post, Students commenter, String content) {
        StudentComments comment = new StudentComments();
        comment.setCommentId(studentCommentsService.generateUniqueCommentId(post.getPostId(), LocalDate.now()));
        comment.setContent(content.trim());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setPost(post);
        comment.setCommenter(commenter);
        return comment;
    }

    // ================== HELPER ==================
    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String redirectWithError(String error, String classId, HttpSession session, RedirectAttributes ra) {
        ra.addFlashAttribute("errors", List.of(error));
        return redirectToClassroom(classId, session);
    }

    private String redirectToClassroom(String classId, HttpSession session) {
        session.setAttribute("classId", classId);
        return "redirect:/classroom";
    }
}