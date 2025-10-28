package com.example.demo.comment.controller;

import com.example.demo.comment.model.MajorComments;
import com.example.demo.comment.model.MinorComments;
import com.example.demo.comment.model.SpecializedComments;
import com.example.demo.comment.service.MajorCommentsService;
import com.example.demo.comment.service.MinorCommentsService;
import com.example.demo.comment.service.SpecializedCommentsService;
import com.example.demo.post.majorClassPosts.model.MajorClassPosts;
import com.example.demo.post.majorClassPosts.service.MajorClassPostsService;
import com.example.demo.post.minorClassPosts.model.MinorClassPosts;
import com.example.demo.post.minorClassPosts.service.MinorClassPostsService;
import com.example.demo.post.specializedClassPosts.model.SpecializedClassPosts;
import com.example.demo.post.specializedClassPosts.service.SpecializedClassPostsService;
import com.example.demo.user.employe.model.MajorEmployes;
import com.example.demo.user.employe.model.MinorEmployes;
import com.example.demo.user.employe.service.EmployesService;
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
@RequestMapping("/classroom")
public class AddComments {

    private final MajorClassPostsService majorClassPostsService;
    private final SpecializedClassPostsService specializedClassPostsService;
    private final MinorClassPostsService minorClassPostsService;

    private final MajorCommentsService majorCommentsService;
    private final SpecializedCommentsService specializedCommentsService;
    private final MinorCommentsService minorCommentsService;

    private final EmployesService employesService;

    public AddComments(
            MajorClassPostsService majorClassPostsService,
            SpecializedClassPostsService specializedClassPostsService,
            MinorClassPostsService minorClassPostsService,
            MajorCommentsService majorCommentsService,
            SpecializedCommentsService specializedCommentsService,
            MinorCommentsService minorCommentsService,
            EmployesService employesService) {

        this.majorClassPostsService = majorClassPostsService;
        this.specializedClassPostsService = specializedClassPostsService;
        this.minorClassPostsService = minorClassPostsService;
        this.majorCommentsService = majorCommentsService;
        this.specializedCommentsService = specializedCommentsService;
        this.minorCommentsService = minorCommentsService;
        this.employesService = employesService;
    }

    @PostMapping("/add-comment")
    public String addComment(
            @RequestParam("postId") String postId,
            @RequestParam("classId") String classId,
            @RequestParam("content") String content,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            // 1. Kiểm tra postId và classId
            if (postId == null || postId.trim().isEmpty() || classId == null || classId.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errors", List.of("Invalid post or class ID"));
                return redirectToClassroom(classId, session);
            }

            // 2. Xác định loại post và xử lý tương ứng
            if (majorClassPostsService.getMajorClassPost(postId) != null) {
                return handleMajorComment(postId, content, redirectAttributes);
            } else if (specializedClassPostsService.getSpecializedClassPost(postId) != null) {
                return handleSpecializedComment(postId, content, redirectAttributes);
            } else if (minorClassPostsService.getMinorClassPost(postId) != null) {
                return handleMinorComment(postId, content, redirectAttributes);
            } else {
                redirectAttributes.addFlashAttribute("errors", List.of("Post not found"));
                return redirectToClassroom(classId, session);
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errors", List.of("Failed to add comment: " + e.getMessage()));
            return redirectToClassroom(classId, session);
        }
    }

    // ================== MAJOR COMMENT ==================
    private String handleMajorComment(String postId, String content, RedirectAttributes redirectAttributes) {
        MajorEmployes commenter = employesService.getMajorEmployee();
        if (commenter == null) {
            redirectAttributes.addFlashAttribute("errors", List.of("Unauthorized: Major employee not found"));
            return "redirect:/classroom";
        }

        MajorClassPosts post = majorClassPostsService.getMajorClassPost(postId);
        MajorComments comment = createMajorComment(post, commenter, content);
        Map<String, String> errors = majorCommentsService.validateComment(comment);

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errors", new ArrayList<>(errors.values()));
            return "redirect:/classroom";
        }

        majorCommentsService.saveComment(comment);
        redirectAttributes.addFlashAttribute("message", "Comment added successfully!");
        return "redirect:/classroom";
    }

    private MajorComments createMajorComment(MajorClassPosts post, MajorEmployes commenter, String content) {
        MajorComments comment = new MajorComments();
        comment.setCommentId(majorCommentsService.generateUniqueCommentId(post.getPostId(), LocalDate.now()));
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setPost(post);
        comment.setCommenter(commenter);
        return comment;
    }

    // ================== SPECIALIZED COMMENT ==================
    private String handleSpecializedComment(String postId, String content, RedirectAttributes redirectAttributes) {
        MajorEmployes commenter = employesService.getMajorEmployee(); // Có thể dùng chung
        if (commenter == null) {
            redirectAttributes.addFlashAttribute("errors", List.of("Unauthorized: Employee not found"));
            return "redirect:/classroom";
        }

        SpecializedClassPosts post = specializedClassPostsService.getSpecializedClassPost(postId);
        SpecializedComments comment = createSpecializedComment(post, commenter, content);
        Map<String, String> errors = specializedCommentsService.validateComment(comment);

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errors", new ArrayList<>(errors.values()));
            return "redirect:/classroom";
        }

        specializedCommentsService.saveComment(comment);
        redirectAttributes.addFlashAttribute("message", "Comment added successfully!");
        return "redirect:/classroom";
    }

    private SpecializedComments createSpecializedComment(SpecializedClassPosts post, MajorEmployes commenter, String content) {
        SpecializedComments comment = new SpecializedComments();
        comment.setCommentId(specializedCommentsService.generateUniqueCommentId(post.getPostId(), LocalDate.now()));
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setPost(post);
        comment.setCommenter(commenter);
        return comment;
    }

    // ================== MINOR COMMENT ==================
    private String handleMinorComment(String postId, String content, RedirectAttributes redirectAttributes) {
        MinorEmployes commenter = employesService.getMinorEmployee();
        if (commenter == null) {
            redirectAttributes.addFlashAttribute("errors", List.of("Unauthorized: Minor employee not found"));
            return "redirect:/classroom";
        }

        MinorClassPosts post = minorClassPostsService.getMinorClassPost(postId);
        MinorComments comment = createMinorComment(post, commenter, content);
        Map<String, String> errors = minorCommentsService.validateComment(comment);

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errors", new ArrayList<>(errors.values()));
            return "redirect:/classroom";
        }

        minorCommentsService.saveComment(comment);
        redirectAttributes.addFlashAttribute("message", "Comment added successfully!");
        return "redirect:/classroom";
    }

    private MinorComments createMinorComment(MinorClassPosts post, MinorEmployes commenter, String content) {
        MinorComments comment = new MinorComments();
        comment.setCommentId(minorCommentsService.generateUniqueCommentId(post.getPostId(), LocalDate.now()));
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setPost(post);
        comment.setCommenter(commenter);
        return comment;
    }

    // ================== HELPER ==================
    private String redirectToClassroom(String classId, HttpSession session) {
        session.setAttribute("classId", classId);
        return "redirect:/classroom";
    }
}