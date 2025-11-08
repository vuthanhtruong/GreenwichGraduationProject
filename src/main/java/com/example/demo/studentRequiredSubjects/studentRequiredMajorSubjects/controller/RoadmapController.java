package com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.controller;

import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import com.example.demo.subject.majorSubject.model.MajorSubjects;
import com.example.demo.studentRequiredSubjects.studentRequiredSpecializedSubjects.service.StudentRequiredSpecializedSubjectsService;
import com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.service.StudentRequiredMajorSubjectsService;
import com.example.demo.subject.minorSubject.model.MinorSubjects;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/student-home")
@PreAuthorize("hasRole('STUDENT')")
public class RoadmapController {

    private final StudentsService StudentService;
    private final StudentRequiredMajorSubjectsService studentRequiredMajorSubjectsService;
    private final StudentRequiredSpecializedSubjectsService studentRequiredSubjectsService;

    public RoadmapController(StudentsService StudentService, StudentRequiredMajorSubjectsService studentRequiredMajorSubjectsService, StudentRequiredSpecializedSubjectsService studentRequiredSubjectsService) {
        this.StudentService = StudentService;
        this.studentRequiredMajorSubjectsService = studentRequiredMajorSubjectsService;
        this.studentRequiredSubjectsService = studentRequiredSubjectsService;
    }

    @GetMapping("/roadmap")
    public String getRoadmap(Model model) {
        try {
            // Lấy thông tin sinh viên đang đăng nhập
            Students student = StudentService.getStudent();
            if (student == null) {
                model.addAttribute("errorMessage", "Unable to retrieve student information. Please log in again.");
                return "Roadmap";
            }

            // Lấy danh sách môn học bắt buộc (MajorSubjects)
            List<MajorSubjects> majorSubjects = studentRequiredMajorSubjectsService.studentMajorRoadmap(student);
            // Lấy danh sách môn học phụ (MinorSubjects)
            List<MinorSubjects> minorSubjects = studentRequiredMajorSubjectsService.studentMinorRoadmap(student);

            List<SpecializedSubject> specializedSubjects=studentRequiredSubjectsService.studentSpecializedRoadmap(student);

            // Thêm dữ liệu vào model để hiển thị trên giao diện
            model.addAttribute("student", student);
            model.addAttribute("majorSubjects", majorSubjects);
            model.addAttribute("minorSubjects", minorSubjects);
            model.addAttribute("specializedSubjects", specializedSubjects);
            model.addAttribute("totalMajorSubjects", majorSubjects.size());
            model.addAttribute("totalMinorSubjects", minorSubjects.size());
            model.addAttribute("totalSpecializedSubjects", specializedSubjects.size());
            model.addAttribute("curriculum", student.getCurriculum() != null ? student.getCurriculum().getName() : "N/A");

            return "Roadmap";
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", "Authentication error: " + e.getMessage());
            model.addAttribute("majorSubjects", List.of());
            model.addAttribute("minorSubjects", List.of());
            model.addAttribute("totalMajorSubjects", 0);
            model.addAttribute("totalMinorSubjects", 0);
            model.addAttribute("curriculum", "N/A");
            return "Roadmap";
        }
    }
}