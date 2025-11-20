package com.example.demo.comment.controller;

import com.example.demo.comment.model.*;
import com.example.demo.comment.service.*;
import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.post.classPost.service.ClassPostsService;
import com.example.demo.post.majorAssignmentSubmitSlots.model.AssignmentSubmitSlots;
import com.example.demo.post.specializedAssignmentSubmitSlots.model.SpecializedAssignmentSubmitSlots;
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
import java.util.*;

@Controller
@RequestMapping("/classroom")
public class AddComments {

    // ========= SERVICES =========
    private final MajorClassPostsService majorClassPostsService;
    private final MinorClassPostsService minorClassPostsService;
    private final ClassPostsService classPostsService;

    private final MajorCommentsService majorCommentsService;
    private final MinorCommentsService minorCommentsService;
    private final StudentCommentsService studentCommentsService;

    private final MajorAssignmentCommentsService majorAssignmentCommentsService;
    private final SpecializedAssignmentCommentsService specializedAssignmentCommentsService;

    private final PersonsService personsService;

    public AddComments(
            MajorClassPostsService majorClassPostsService,
            MinorClassPostsService minorClassPostsService,
            ClassPostsService classPostsService,

            MajorCommentsService majorCommentsService,
            MinorCommentsService minorCommentsService,
            StudentCommentsService studentCommentsService,

            MajorAssignmentCommentsService majorAssignmentCommentsService,
            SpecializedAssignmentCommentsService specializedAssignmentCommentsService,

            PersonsService personsService
    ) {
        this.majorClassPostsService = majorClassPostsService;
        this.minorClassPostsService = minorClassPostsService;
        this.classPostsService = classPostsService;

        this.majorCommentsService = majorCommentsService;
        this.minorCommentsService = minorCommentsService;
        this.studentCommentsService = studentCommentsService;

        this.majorAssignmentCommentsService = majorAssignmentCommentsService;
        this.specializedAssignmentCommentsService = specializedAssignmentCommentsService;

        this.personsService = personsService;
    }

    // =============================== MAIN ENTRY ===============================
    @PostMapping("/add-comment")
    public String addComment(
            @RequestParam("postId") String postId,
            @RequestParam("classId") String classId,
            @RequestParam("content") String content,
            HttpSession session,
            RedirectAttributes redirect) {

        if (isEmpty(postId) || isEmpty(classId) || isEmpty(content)) {
            return redirectError("Invalid input", classId, session, redirect);
        }

        try {
            ClassPosts post = classPostsService.findPostById(postId);
            if (post == null) {
                return redirectError("Post not found", classId, session, redirect);
            }

            Object person = personsService.getPerson();

            // ---------------- Major employee (Lecturer, Staff…) ----------------
            if (person instanceof MajorEmployes major) {
                if (post instanceof MajorClassPosts majorPost) {
                    return handleMajorPostComment(majorPost, major, content, classId, session, redirect);
                }
                if (post instanceof AssignmentSubmitSlots majorAsm) {
                    return handleMajorAssignmentComment(majorAsm, major, content, classId, session, redirect);
                }
            }

            // ---------------- Minor employee ----------------
            if (person instanceof MinorEmployes minor) {
                if (post instanceof MinorClassPosts minorPost) {
                    return handleMinorPostComment(minorPost, minor, content, classId, session, redirect);
                }
            }

            // ---------------- Student ----------------
            if (person instanceof Students student) {
                return handleStudentComment(post, student, content, classId, session, redirect);
            }

            return redirectError("Unauthorized user", classId, session, redirect);

        } catch (Exception ex) {
            return redirectError("Error adding comment: " + ex.getMessage(), classId, session, redirect);
        }
    }

    // =============================== MAJOR POST COMMENT ===============================
    private String handleMajorPostComment(
            MajorClassPosts post, MajorEmployes commenter, String content,
            String classId, HttpSession session, RedirectAttributes redirect) {

        MajorComments c = new MajorComments();
        c.setCommentId(majorCommentsService.generateUniqueCommentId(post.getPostId(), LocalDate.now()));
        c.setPost(post);
        c.setContent(content.trim());
        c.setCreatedAt(LocalDateTime.now());
        c.setCommenter(commenter);

        Map<String, String> errors = majorCommentsService.validateComment(c);
        if (!errors.isEmpty()) {
            redirect.addFlashAttribute("errors", errors.values());
            return redirectClassroom(classId, session);
        }

        majorCommentsService.saveComment(c);
        redirect.addFlashAttribute("message", "Comment added!");
        return redirectClassroom(classId, session);
    }

    // ============================ MAJOR ASSIGNMENT COMMENT ============================
    private String handleMajorAssignmentComment(
            AssignmentSubmitSlots assignment, MajorEmployes commenter, String content,
            String classId, HttpSession session, RedirectAttributes redirect) {

        MajorAssignmentComments c = new MajorAssignmentComments();
        c.setCommentId(majorAssignmentCommentsService.generateUniqueCommentId(assignment.getPostId(), LocalDate.now()));
        c.setPost(assignment);
        c.setContent(content.trim());
        c.setCreatedAt(LocalDateTime.now());
        c.setCommenter(commenter);

        Map<String, String> errors = majorAssignmentCommentsService.validateComment(c);
        if (!errors.isEmpty()) {
            redirect.addFlashAttribute("errors", errors.values());
            return redirectClassroom(classId, session);
        }

        majorAssignmentCommentsService.saveComment(c);
        redirect.addFlashAttribute("message", "Assignment comment added!");
        return redirectClassroom(classId, session);
    }

    // =============================== MINOR POST COMMENT ===============================
    private String handleMinorPostComment(
            MinorClassPosts post, MinorEmployes commenter, String content,
            String classId, HttpSession session, RedirectAttributes redirect) {

        MinorComments c = new MinorComments();
        c.setCommentId(minorCommentsService.generateUniqueCommentId(post.getPostId(), LocalDate.now()));
        c.setPost(post);
        c.setContent(content.trim());
        c.setCreatedAt(LocalDateTime.now());
        c.setCommenter(commenter);

        Map<String, String> errors = minorCommentsService.validateComment(c);
        if (!errors.isEmpty()) {
            redirect.addFlashAttribute("errors", errors.values());
            return redirectClassroom(classId, session);
        }

        minorCommentsService.saveComment(c);
        redirect.addFlashAttribute("message", "Comment added!");
        return redirectClassroom(classId, session);
    }

    // =============================== STUDENT COMMENT ===============================
    private String handleStudentComment(
            ClassPosts post, Students commenter, String content,
            String classId, HttpSession session, RedirectAttributes redirect) {

        StudentComments c = new StudentComments();
        c.setCommentId(studentCommentsService.generateUniqueCommentId(post.getPostId(), LocalDate.now()));
        c.setPost(post);
        c.setContent(content.trim());
        c.setCreatedAt(LocalDateTime.now());
        c.setCommenter(commenter);

        Map<String, String> errors = studentCommentsService.validateComment(c);
        if (!errors.isEmpty()) {
            redirect.addFlashAttribute("errors", errors.values());
            return redirectClassroom(classId, session);
        }

        studentCommentsService.saveComment(c);
        redirect.addFlashAttribute("message", "Comment added!");
        return redirectClassroom(classId, session);
    }

    // =============================== HELPER ===================================
    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String redirectError(
            String msg, String classId, HttpSession session, RedirectAttributes redirect) {

        redirect.addFlashAttribute("errors", List.of(msg));
        return redirectClassroom(classId, session);
    }

    private String redirectClassroom(String classId, HttpSession session) {
        session.setAttribute("classId", classId);
        return "redirect:/classroom";
    }
}
