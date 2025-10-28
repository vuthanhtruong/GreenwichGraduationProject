package com.example.demo.post.minorClassPosts.controller;

import com.example.demo.classes.abstractClasses.model.Classes;
import com.example.demo.classes.abstractClasses.service.ClassesService;
import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.comment.model.MinorComments;
import com.example.demo.document.service.ClassDocumentsService;
import com.example.demo.post.minorClassPosts.model.MinorClassPosts;
import com.example.demo.post.minorClassPosts.service.MinorClassPostsService;
import com.example.demo.user.employe.model.MinorEmployes;
import com.example.demo.user.employe.service.EmployesService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/classroom")
public class minorClassPostsController {

    private final ClassesService classesService;
    private final MinorClassPostsService minorClassPostsService;
    private final EmployesService employesService;
    private final ClassDocumentsService classDocumentsService;

    public minorClassPostsController(
            ClassesService classesService,
            MinorClassPostsService minorClassPostsService,
            EmployesService employesService,
            ClassDocumentsService classDocumentsService) {
        this.classesService = classesService;
        this.minorClassPostsService = minorClassPostsService;
        this.employesService = employesService;
        this.classDocumentsService = classDocumentsService;
    }

    @PostMapping("/upload-minor-post")
    public String uploadMinorPost(
            @RequestParam("classId") String classId,
            @Valid @ModelAttribute("post") MinorClassPosts post,
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // 1. Kiểm tra class tồn tại
            Classes classes = classesService.findClassById(classId);
            if (classes == null) {
                return handleError(model, post, "Class not found", classId, new ArrayList<>());
            }

            // 2. Kiểm tra class có phải MinorClasses không
            if (!(classes instanceof MinorClasses minorClasses)) {
                return handleError(model, post, "This is not a Minor class", classId, new ArrayList<>());
            }

            // 3. Lấy MinorEmployes (người tạo post)
            MinorEmployes creator = employesService.getMinorEmployee();
            if (creator == null) {
                return handleError(model, post, "No authenticated Minor employee found", classId, new ArrayList<>());
            }
            post.setCreator(creator);

            // 4. Validate nội dung post
            Map<String, String> errors = minorClassPostsService.validatePost(post);
            List<String> errorList = new ArrayList<>(errors.values());

            if (!errorList.isEmpty()) {
                return handleError(model, post, errorList, classId, minorClassPostsService.getClassPostByClass(classId));
            }

            // 5. Gán class + metadata
            post.setMinorClass(minorClasses);
            post.setPostId(minorClassPostsService.generateUniquePostId(classId, LocalDate.now()));
            post.setCreatedAt(LocalDateTime.now());

            // 6. Lưu post
            minorClassPostsService.saveMinorClassPosts(post);

            // 7. Xử lý file (tối đa 5 file, 10MB mỗi file)
            if (files != null && files.length > 0) {
                if (files.length > 5) {
                    return handleError(model, post, List.of("Cannot upload more than 5 files"), classId,
                            minorClassPostsService.getClassPostByClass(classId));
                }

                List<String> fileErrors = classDocumentsService.saveDocuments(post, files);
                if (!fileErrors.isEmpty()) {
                    return handleError(model, post, fileErrors, classId,
                            minorClassPostsService.getClassPostByClass(classId));
                }
            }

            // 8. Thành công
            session.setAttribute("classId", classId);
            redirectAttributes.addFlashAttribute("message", "Minor post created successfully!");
            return "redirect:/classroom";

        } catch (Exception e) {
            return handleError(model, post, List.of("Failed to create minor post: " + e.getMessage()),
                    classId, minorClassPostsService.getClassPostByClass(classId));
        }
    }

    // Helper method: Tránh lặp code khi có lỗi
    private String handleError(Model model, MinorClassPosts post, String errorMsg, String classId, List<?> postsList) {
        return handleError(model, post, List.of(errorMsg), classId, postsList);
    }

    private String handleError(Model model, MinorClassPosts post, List<String> errors, String classId, List<?> postsList) {
        model.addAttribute("errors", errors);
        model.addAttribute("post", post);
        model.addAttribute("classes", classesService.findClassById(classId));
        model.addAttribute("ClassPostsList", postsList);
        model.addAttribute("openPostOverlay", true);
        model.addAttribute("newComment", new MinorComments());
        return "MinorClassroom"; // Trả về template Minor
    }
}