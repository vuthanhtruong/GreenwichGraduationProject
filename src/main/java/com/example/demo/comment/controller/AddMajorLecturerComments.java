package com.example.demo.comment.controller;

import com.example.demo.comment.model.MajorComments;
import com.example.demo.comment.model.SpecializedComments;
import com.example.demo.comment.service.MajorCommentsService;
import com.example.demo.comment.service.SpecializedCommentsService;
import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.post.classPost.service.ClassPostsService;
import com.example.demo.post.majorClassPosts.model.MajorClassPosts;
import com.example.demo.post.majorClassPosts.service.MajorClassPostsService;
import com.example.demo.post.specializedClassPosts.model.SpecializedClassPosts;
import com.example.demo.post.specializedClassPosts.service.SpecializedClassPostsService;
import com.example.demo.user.employe.model.MajorEmployes;
import com.example.demo.user.employe.service.EmployesService;
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
@RequestMapping("/classroom")
public class AddMajorLecturerComments {
    private final SpecializedCommentsService specializedCommentsService;
    private final MajorCommentsService majorCommentsService;
    private final MajorClassPostsService majorClassPostsService;
    private final EmployesService employesService;
    private final SpecializedClassPostsService specializedClassPostsService;

    public AddMajorLecturerComments(SpecializedCommentsService specializedCommentsService, MajorCommentsService majorCommentsService, MajorClassPostsService majorClassPostsService, EmployesService employesService, SpecializedClassPostsService specializedClassPostsService) {
        this.specializedCommentsService = specializedCommentsService;
        this.majorCommentsService = majorCommentsService;
        this.majorClassPostsService = majorClassPostsService;
        this.employesService = employesService;
        this.specializedClassPostsService = specializedClassPostsService;
    }

    @PostMapping("/add-comment")
    public String addComment(
            @RequestParam("postId") String postId,
            @RequestParam("classId") String classId,
            @RequestParam("content") String content,
            HttpSession session,
            Model model) {
        try {
            if(majorClassPostsService.getClassPostByClass(postId) != null) {
                MajorEmployes commenter = employesService.getMajorEmployee();
                MajorClassPosts post = majorClassPostsService.getMajorClassPost(postId);
                MajorComments comment = new MajorComments();
                comment.setCommentId(UUID.randomUUID().toString());
                comment.setCommenter(commenter);
                comment.setPost(post);
                comment.setContent(content);
                comment.setCreatedAt(LocalDateTime.now());
                majorCommentsService.saveComment(comment);
            } else if (specializedClassPostsService.getClassPostsByClass(postId) != null) {
                MajorEmployes commenter = employesService.getMajorEmployee();
                SpecializedClassPosts post = specializedClassPostsService.getSpecializedClassPost(postId);
                SpecializedComments comment = new SpecializedComments();
                comment.setCommentId(UUID.randomUUID().toString());
                comment.setCommenter(commenter);
                comment.setPost(post);
                comment.setContent(content);
                comment.setCreatedAt(LocalDateTime.now());
                specializedCommentsService.saveComment(comment);
            }
            session.setAttribute("classId", classId);
            return "redirect:/classroom";
        } catch (Exception e) {
            model.addAttribute("errors", List.of("Failed to add comment: " + e.getMessage()));
            return  "redirect:/classroom";
        }
    }
}
