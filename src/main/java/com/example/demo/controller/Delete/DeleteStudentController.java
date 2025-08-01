package com.example.demo.controller.Delete;
import com.example.demo.service.LecturesService;
import com.example.demo.service.StaffsService;
import com.example.demo.service.StudentsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff-home/students-list")
public class DeleteStudentController {
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final LecturesService lecturesService;

    public DeleteStudentController(StaffsService staffsService, LecturesService lecturesService, StudentsService studentsService) {
        this.staffsService = staffsService;
        this.studentsService=studentsService;
        this.lecturesService = lecturesService;
    }

    @DeleteMapping("/delete-student/{id}")
    public String deleteStudent(@PathVariable String id, RedirectAttributes redirectAttributes) {
        studentsService.deleteStudent(id);
        redirectAttributes.addFlashAttribute("message", "Delete student ID member: " + id);
        return "redirect:/staff-home/students-list";
    }
}
