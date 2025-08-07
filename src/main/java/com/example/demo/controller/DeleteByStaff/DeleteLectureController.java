package com.example.demo.controller.DeleteByStaff;
import com.example.demo.service.LecturesService;
import com.example.demo.service.StaffsService;
import com.example.demo.service.StudentsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff-home/lectures-list")
public class DeleteLectureController {
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final LecturesService lecturesService;

    public DeleteLectureController(StaffsService staffsService, LecturesService lecturesService, StudentsService studentsService) {
        this.staffsService = staffsService;
        this.studentsService=studentsService;
        this.lecturesService = lecturesService;
    }

    @DeleteMapping("/delete-lecture/{id}")
    public String deleteLecture(@PathVariable String id, RedirectAttributes redirectAttributes) {
        lecturesService.deleteLecturer(id);
        redirectAttributes.addFlashAttribute("message", "Delete lecture ID member: " + id);
        return "redirect:/staff-home/lectures-list";
    }
}
