package com.example.demo.comment.controller;

import com.example.demo.comment.model.*;
import com.example.demo.comment.service.*;
import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.post.classPost.service.ClassPostsService;
import com.example.demo.post.majorAssignmentSubmitSlots.model.AssignmentSubmitSlots;
import com.example.demo.post.specializedAssignmentSubmitSlots.model.SpecializedAssignmentSubmitSlots;
import com.example.demo.user.employe.model.MajorEmployes;
import com.example.demo.user.employe.model.MinorEmployes;
import com.example.demo.user.person.service.PersonsService;
import com.example.demo.user.student.model.Students;
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
public class DeleteCommentController {

    private static final Logger log = LoggerFactory.getLogger(DeleteCommentController.class);

    private final ClassPostsService classPostsService;

    private final MajorCommentsService majorCommentsService;
    private final MinorCommentsService minorCommentsService;
    private final StudentCommentsService studentCommentsService;

    private final MajorAssignmentCommentsService majorAssignmentCommentsService;
    private final SpecializedAssignmentCommentsService specializedAssignmentCommentsService;

    private final PersonsService personsService;

    public DeleteCommentController(
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

    @PostMapping("/delete-comment")
    public String deleteComment(
            @RequestParam("commentId") String commentId,
            @RequestParam("classId") String classId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        session.setAttribute("classId", classId);

        Object person = personsService.getPerson(); // keep compatibility with your codebase
        if (person == null) {
            redirectAttributes.addFlashAttribute("errors", java.util.List.of("User not authenticated"));
            return "redirect:/classroom";
        }

        try {
            Optional<MajorComments> majOpt = safeFindMajorComment(commentId);
            if (majOpt.isPresent()) {
                MajorComments comment = majOpt.get();
                majorCommentsService.deleteComment(comment.getCommentId());
                redirectAttributes.addFlashAttribute("message", "Comment deleted successfully");
                return "redirect:/classroom";
            }

            // Minor comment
            Optional<MinorComments> minOpt = safeFindMinorComment(commentId);
            if (minOpt.isPresent()) {
                MinorComments comment = minOpt.get();
                minorCommentsService.deleteComment(comment.getCommentId());
                redirectAttributes.addFlashAttribute("message", "Comment deleted successfully");
                return "redirect:/classroom";
            }

            // Student comment (works on any post type)
            Optional<StudentComments> stuOpt = safeFindStudentComment(commentId);
            if (stuOpt.isPresent()) {
                StudentComments comment = stuOpt.get();
                studentCommentsService.deleteComment(comment.getCommentId());
                redirectAttributes.addFlashAttribute("message", "Comment deleted successfully");
                return "redirect:/classroom";
            }

            // Major assignment comment
            Optional<MajorAssignmentComments> majAsmOpt = safeFindMajorAssignmentComment(commentId);
            if (majAsmOpt.isPresent()) {
                MajorAssignmentComments comment = majAsmOpt.get();
                majorAssignmentCommentsService.deleteComment(comment.getCommentId());
                redirectAttributes.addFlashAttribute("message", "Comment deleted successfully");
                return "redirect:/classroom";
            }

            // Specialized assignment comment
            Optional<SpecializedAssignmentComments> specAsmOpt = safeFindSpecializedAssignmentComment(commentId);
            if (specAsmOpt.isPresent()) {
                SpecializedAssignmentComments comment = specAsmOpt.get();
                specializedAssignmentCommentsService.deleteComment(comment.getCommentId());
                redirectAttributes.addFlashAttribute("message", "Comment deleted successfully");
                return "redirect:/classroom";
            }

            // Not found
            redirectAttributes.addFlashAttribute("errors", java.util.List.of("Comment not found"));
            return "redirect:/classroom";

        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errors", java.util.List.of("Failed to delete comment: " + ex.getMessage()));
            return "redirect:/classroom";
        }
    }

    private Optional<MajorComments> safeFindMajorComment(String commentId) {
        try {
            MajorComments c = majorCommentsService.findCommentById(commentId);
            return Optional.ofNullable(c);
        } catch (NoSuchMethodError | AbstractMethodError e) {
            log.debug("majorCommentsService.findById not present or different signature");
            return Optional.empty();
        } catch (Exception e) {
            log.debug("majorCommentsService.findById exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<MinorComments> safeFindMinorComment(String commentId) {
        try {
            MinorComments c = minorCommentsService.getCommentById(commentId);
            return Optional.ofNullable(c);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<StudentComments> safeFindStudentComment(String commentId) {
        try {
            StudentComments c = studentCommentsService.findCommentById(commentId);
            return Optional.ofNullable(c);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<MajorAssignmentComments> safeFindMajorAssignmentComment(String commentId) {
        try {
            MajorAssignmentComments c = majorAssignmentCommentsService.findCommentById(commentId);
            return Optional.ofNullable(c);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<SpecializedAssignmentComments> safeFindSpecializedAssignmentComment(String commentId) {
        try {
            SpecializedAssignmentComments c = specializedAssignmentCommentsService.findCommentById(commentId);
            return Optional.ofNullable(c);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
