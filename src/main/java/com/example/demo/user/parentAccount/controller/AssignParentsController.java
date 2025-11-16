package com.example.demo.user.parentAccount.controller;

import com.example.demo.entity.Enums.Gender;
import com.example.demo.user.parentAccount.model.ParentAccounts;
import com.example.demo.user.parentAccount.model.Student_ParentAccounts;
import com.example.demo.user.parentAccount.service.ParentAccountsService;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/staff-home/students-list")
public class AssignParentsController {

    private final StudentsService studentsService;
    private final ParentAccountsService parentAccountsService;
    private final StaffsService staffsService;

    public AssignParentsController(
            StudentsService studentsService,
            ParentAccountsService parentAccountsService,
            StaffsService staffsService) {
        this.studentsService = studentsService;
        this.parentAccountsService = parentAccountsService;
        this.staffsService = staffsService;
    }
    
    @PostMapping("/assign-parents-form")
    public String showAssignParentsForm(
            @RequestParam("id") String studentId,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        Students student = studentsService.getStudentById(studentId);
        if (student == null) {
            redirectAttributes.addFlashAttribute("error", "Student with ID " + studentId + " not found.");
            return "redirect:/staff-home/students-list";
        }

        // Lấy danh sách phụ huynh liên kết
        List<Student_ParentAccounts> parentLinks = parentAccountsService.getParentLinksByStudentId(studentId);
        List<ParentAccounts> parents = parentLinks.stream()
                .map(Student_ParentAccounts::getParent)
                .toList();

        // Dữ liệu cho view
        model.addAttribute("student", student);
        model.addAttribute("parents", parents);
        model.addAttribute("parentLinks", parentLinks); // để lấy relationship, support phone
        model.addAttribute("staffCampus", staffsService.getCampusOfStaff());

        // Mở overlay hoặc trang riêng
        model.addAttribute("openAssignParents", true);

        // Giữ lại trạng thái bảng (page, pageSize)
        Integer pageSize = (Integer) session.getAttribute("pageSize");
        if (pageSize == null) pageSize = 5;
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("currentPage", session.getAttribute("currentPage") != null ? session.getAttribute("currentPage") : 1);

        return "StudentsList"; // trả về cùng trang, mở overlay
    }

    @GetMapping("/assign-parents/{studentId}/avatar/{parentId}")
    public @ResponseBody byte[] getParentAvatar(
            @PathVariable String studentId,
            @PathVariable String parentId) {
        ParentAccounts parent = parentAccountsService.findByEmail(null); // không dùng email
        // Lấy qua link
        Student_ParentAccounts link = parentAccountsService.getParentLinkByStudentId(studentId);
        if (link != null && link.getParent().getId().equals(parentId)) {
            byte[] avatar = link.getParent().getAvatar();
            return avatar != null ? avatar : getDefaultAvatar(link.getParent().getGender());
        }
        return getDefaultAvatar(null);
    }

    private byte[] getDefaultAvatar(Gender gender) {
        String path = (gender == Gender.MALE) ? "/DefaultAvatar/Parent_Male.png" : "/DefaultAvatar/Parent_Female.png";
        try {
            return getClass().getResourceAsStream(path).readAllBytes();
        } catch (Exception e) {
            return new byte[0];
        }
    }
}