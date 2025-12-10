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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/classroom")
public class AddComments {

    private final ClassPostsService classPostsService;

    private final MajorCommentsService majorCommentsService;
    private final MinorCommentsService minorCommentsService;
    private final StudentCommentsService studentCommentsService;

    // NEW
    private final MajorAssignmentCommentsService majorAssignmentCommentsService;
    private final SpecializedAssignmentCommentsService specializedAssignmentCommentsService;

    private final PersonsService personsService;

    public AddComments(
            ClassPostsService classPostsService,
            MajorCommentsService majorCommentsService,
            MinorCommentsService minorCommentsService,
            StudentCommentsService studentCommentsService,
            MajorAssignmentCommentsService majorAssignmentCommentsService,
            SpecializedAssignmentCommentsService specializedAssignmentCommentsService,
            PersonsService personsService) {

        this.classPostsService = classPostsService;
        this.majorCommentsService = majorCommentsService;
        this.minorCommentsService = minorCommentsService;
        this.studentCommentsService = studentCommentsService;

        this.majorAssignmentCommentsService = majorAssignmentCommentsService;
        this.specializedAssignmentCommentsService = specializedAssignmentCommentsService;

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
            if (post == null)
                return redirectWithError("Post not found", classId, session, redirectAttributes);

            Object person = personsService.getPerson();


            if (post instanceof MajorClassPosts majorPost && person instanceof MajorEmployes emp) {
                return handleMajorComment(majorPost, content, emp, classId, session, redirectAttributes);
            }

            if (post instanceof MinorClassPosts minorPost && person instanceof MinorEmployes emp) {
                return handleMinorComment(minorPost, content, emp, classId, session, redirectAttributes);
            }

            // New — assignment comments Major
            if (post instanceof AssignmentSubmitSlots majorAsmPost && person instanceof MajorEmployes emp) {
                return handleMajorAssignmentComment(majorAsmPost, content, emp, classId, session, redirectAttributes);
            }

            // New — assignment comments Specialized
            if (post instanceof SpecializedAssignmentSubmitSlots specAsmPost && person instanceof MajorEmployes emp) {
                return handleSpecializedAssignmentComment(specAsmPost, content, emp, classId, session, redirectAttributes);
            }

            // Students comment everywhere
            if (person instanceof Students stu) {
                return handleStudentComment(post, content, stu, classId, session, redirectAttributes);
            }

            return redirectWithError("Unauthorized user type", classId, session, redirectAttributes);

        } catch (Exception e) {
            return redirectWithError("Failed to add comment: " + e.getMessage(), classId, session, redirectAttributes);
        }
    }

    private String handleMajorComment(MajorClassPosts post, String content, MajorEmployes commenter,
                                      String classId, HttpSession session, RedirectAttributes ra) {

        MajorComments comment = new MajorComments();
        comment.setCommentId(majorCommentsService.generateUniqueCommentId(post.getPostId(), LocalDate.now()));
        comment.setCommenter(commenter);
        comment.setPost(post);
        comment.setContent(content.trim());
        comment.setCreatedAt(LocalDateTime.now());

        Map<String, String> errors = majorCommentsService.validateComment(comment);
        if (!errors.isEmpty()) {
            ra.addFlashAttribute("errors", new ArrayList<>(errors.values()));
            return redirectToClassroom(classId, session);
        }

        majorCommentsService.saveComment(comment);
        return redirectSuccess(classId, session, ra);
    }

    private String handleMinorComment(MinorClassPosts post, String content, MinorEmployes commenter,
                                      String classId, HttpSession session, RedirectAttributes ra) {

        MinorComments comment = new MinorComments();
        comment.setCommentId(minorCommentsService.generateUniqueCommentId(post.getPostId(), LocalDate.now()));
        comment.setCommenter(commenter);
        comment.setPost(post);
        comment.setContent(content.trim());
        comment.setCreatedAt(LocalDateTime.now());

        Map<String, String> errors = minorCommentsService.validateComment(comment);
        if (!errors.isEmpty()) {
            ra.addFlashAttribute("errors", new ArrayList<>(errors.values()));
            return redirectToClassroom(classId, session);
        }

        minorCommentsService.saveComment(comment);
        return redirectSuccess(classId, session, ra);
    }

    private String handleMajorAssignmentComment(AssignmentSubmitSlots post, String content, MajorEmployes commenter,
                                                String classId, HttpSession session, RedirectAttributes ra) {

        MajorAssignmentComments comment = new MajorAssignmentComments();
        comment.setCommentId(majorAssignmentCommentsService.generateUniqueCommentId(post.getPostId(), LocalDate.now()));
        comment.setPost(post);
        comment.setCommenter(commenter);
        comment.setContent(content.trim());
        comment.setCreatedAt(LocalDateTime.now());

        Map<String, String> errors = majorAssignmentCommentsService.validateComment(comment);
        if (!errors.isEmpty()) {
            ra.addFlashAttribute("errors", new ArrayList<>(errors.values()));
            return redirectToClassroom(classId, session);
        }

        majorAssignmentCommentsService.saveComment(comment);
        return redirectSuccess(classId, session, ra);
    }

    private String handleSpecializedAssignmentComment(SpecializedAssignmentSubmitSlots post, String content,
                                                      MajorEmployes commenter, String classId,
                                                      HttpSession session, RedirectAttributes ra) {

        SpecializedAssignmentComments comment = new SpecializedAssignmentComments();
        comment.setCommentId(specializedAssignmentCommentsService.generateUniqueCommentId(post.getPostId(), LocalDate.now()));
        comment.setPost(post);
        comment.setCommenter(commenter);
        comment.setContent(content.trim());
        comment.setCreatedAt(LocalDateTime.now());

        Map<String, String> errors = specializedAssignmentCommentsService.validateComment(comment);
        if (!errors.isEmpty()) {
            ra.addFlashAttribute("errors", new ArrayList<>(errors.values()));
            return redirectToClassroom(classId, session);
        }

        specializedAssignmentCommentsService.saveComment(comment);
        return redirectSuccess(classId, session, ra);
    }

    private String handleStudentComment(ClassPosts post, String content, Students commenter,
                                        String classId, HttpSession session, RedirectAttributes ra) {

        StudentComments comment = new StudentComments();
        comment.setCommentId(studentCommentsService.generateUniqueCommentId(post.getPostId(), LocalDate.now()));
        comment.setPost(post);
        comment.setCommenter(commenter);
        comment.setContent(content.trim());
        comment.setCreatedAt(LocalDateTime.now());

        Map<String, String> errors = studentCommentsService.validateComment(comment);
        if (!errors.isEmpty()) {
            ra.addFlashAttribute("errors", new ArrayList<>(errors.values()));
            return redirectToClassroom(classId, session);
        }

        studentCommentsService.saveComment(comment);
        return redirectSuccess(classId, session, ra);
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String redirectWithError(String error, String classId, HttpSession session, RedirectAttributes ra) {
        ra.addFlashAttribute("errors", List.of(error));
        session.setAttribute("classId", classId);
        return "redirect:/classroom";
    }

    private String redirectSuccess(String classId, HttpSession session, RedirectAttributes ra) {
        ra.addFlashAttribute("message", "Comment added successfully!");
        session.setAttribute("classId", classId);
        return "redirect:/classroom";
    }

    private String redirectToClassroom(String classId, HttpSession session) {
        session.setAttribute("classId", classId);
        return "redirect:/classroom";
    }
}
