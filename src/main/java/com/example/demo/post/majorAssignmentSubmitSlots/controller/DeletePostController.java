package com.example.demo.post.majorAssignmentSubmitSlots.controller;

import com.example.demo.post.classPost.service.ClassPostsService;
import com.example.demo.post.majorAssignmentSubmitSlots.model.AssignmentSubmitSlots;
import com.example.demo.post.majorAssignmentSubmitSlots.service.AssignmentSubmitSlotsService;
import com.example.demo.post.majorClassPosts.model.MajorClassPosts;
import com.example.demo.post.majorClassPosts.service.MajorClassPostsService;
import com.example.demo.post.minorClassPosts.model.MinorClassPosts;
import com.example.demo.post.minorClassPosts.service.MinorClassPostsService;
import com.example.demo.post.specializedAssignmentSubmitSlots.model.SpecializedAssignmentSubmitSlots;
import com.example.demo.post.specializedAssignmentSubmitSlots.service.SpecializedAssignmentSubmitSlotsService;
import com.example.demo.post.specializedClassPosts.model.SpecializedClassPosts;
import com.example.demo.post.specializedClassPosts.service.SpecializedClassPostsService;
import com.example.demo.user.person.service.PersonsService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/classroom")
public class DeletePostController {

    private static final Logger log = LoggerFactory.getLogger(DeletePostController.class);

    private final AssignmentSubmitSlotsService AssignmentSubmitSlotsService;
    private final SpecializedAssignmentSubmitSlotsService specializedAssignmentSubmitSlotsService;
    private final MinorClassPostsService minorClassPostsService;
    private final MajorClassPostsService majorClassPostsService;
    private final PersonsService personsService;
    private final SpecializedClassPostsService specializedClassPostsService;

    public DeletePostController(
            AssignmentSubmitSlotsService AssignmentSubmitSlotsService,
            SpecializedAssignmentSubmitSlotsService specializedAssignmentSubmitSlotsService, MinorClassPostsService minorClassPostsService, MajorClassPostsService majorClassPostsService,
            PersonsService personsService, SpecializedClassPostsService specializedClassPostsService) {
        this.AssignmentSubmitSlotsService = AssignmentSubmitSlotsService;
        this.specializedAssignmentSubmitSlotsService = specializedAssignmentSubmitSlotsService;
        this.minorClassPostsService = minorClassPostsService;
        this.majorClassPostsService = majorClassPostsService;
        this.personsService = personsService;
        this.specializedClassPostsService = specializedClassPostsService;
    }

    @PostMapping("/delete-post")
    public String deletePost(@RequestParam("postId") String postId,
                             @RequestParam("classId") String classId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        // Lưu classId vào session để redirect
        session.setAttribute("classId", classId);

        Object person = personsService.getPerson();
        if (person == null) {
            redirectAttributes.addFlashAttribute("errors", java.util.List.of("User not authenticated"));
            return "redirect:/classroom";
        }

        try {
            Optional<SpecializedClassPosts> specializedPostOpt = safeFindSpecializedClassPost(postId);
            if (specializedPostOpt.isPresent()) {
                specializedClassPostsService.deleteSpecializedClassPost(postId);
                redirectAttributes.addFlashAttribute("message", "Specialized post deleted successfully");
                return "redirect:/classroom";
            }
            // 1. Minor ClassPosts
            Optional<MinorClassPosts> minorPostOpt = safeFindMinorPost(postId);
            if (minorPostOpt.isPresent()) {
                MinorClassPosts post = minorPostOpt.get();
                minorClassPostsService.deleteMinorClassPost(post.getPostId());
                redirectAttributes.addFlashAttribute("message", "Minor post deleted successfully");
                return "redirect:/classroom";
            }

            // 2. Major ClassPosts
            Optional<MajorClassPosts> majorPostOpt = safeFindMajorClassPost(postId);
            if (majorPostOpt.isPresent()) {
                MajorClassPosts post = majorPostOpt.get();
                majorClassPostsService.deleteMajorClassPost(post.getPostId());
                redirectAttributes.addFlashAttribute("message", "Major post deleted successfully");
                return "redirect:/classroom";
            }

            Optional<AssignmentSubmitSlots> majorAssignmentOpt = safeFindMajorAssignment(postId);
            if (majorAssignmentOpt.isPresent()) {
                AssignmentSubmitSlots post = majorAssignmentOpt.get();
                AssignmentSubmitSlotsService.deleteByPostId(post.getPostId());
                redirectAttributes.addFlashAttribute("message", "Post deleted successfully");
                return "redirect:/classroom";
            }

            // 3. Specialized Assignment
            Optional<SpecializedAssignmentSubmitSlots> specializedAssignmentOpt = safeFindSpecializedPost(postId);
            if (specializedAssignmentOpt.isPresent()) {
                SpecializedAssignmentSubmitSlots post = specializedAssignmentOpt.get();
                specializedAssignmentSubmitSlotsService.deleteByPostId(post.getPostId());
                redirectAttributes.addFlashAttribute("message", "Post deleted successfully");
                return "redirect:/classroom";
            }

            // Không tìm thấy post
            redirectAttributes.addFlashAttribute("errors", java.util.List.of("Post not found"));
            return "redirect:/classroom";

        } catch (Exception ex) {
            log.error("Failed to delete post: {}", ex.getMessage(), ex);
            redirectAttributes.addFlashAttribute("errors", java.util.List.of("Failed to delete post: " + ex.getMessage()));
            return "redirect:/classroom";
        }
    }

    private Optional<AssignmentSubmitSlots> safeFindMajorAssignment(String postId) {
        try {
            return Optional.ofNullable(AssignmentSubmitSlotsService.findByPostId(postId));
        } catch (Exception e) {
            log.debug("AssignmentSubmitSlotsService.findByPostId exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<SpecializedAssignmentSubmitSlots> safeFindSpecializedPost(String postId) {
        try {
            return Optional.ofNullable(specializedAssignmentSubmitSlotsService.findByPostId(postId));
        } catch (Exception e) {
            log.debug("specializedAssignmentSubmitSlotsService.findByPostId exception: {}", e.getMessage());
            return Optional.empty();
        }
    }
    private Optional<MinorClassPosts> safeFindMinorPost(String postId) {
        try {
            return Optional.ofNullable(minorClassPostsService.getMinorClassPost(postId));
        } catch (Exception e) {
            log.debug("minorClassPostsService.findPostById exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<MajorClassPosts> safeFindMajorClassPost(String postId) {
        try {
            return Optional.ofNullable(majorClassPostsService.getMajorClassPost(postId));
        } catch (Exception e) {
            log.debug("majorClassPostsService.findPostById exception: {}", e.getMessage());
            return Optional.empty();
        }
    }
    private Optional<SpecializedClassPosts> safeFindSpecializedClassPost(String postId) {
        try {
            return Optional.ofNullable(specializedClassPostsService.getSpecializedClassPost(postId));
        } catch (Exception e) {
            log.debug("specializedClassPostsService.getSpecializedClassPost exception: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
