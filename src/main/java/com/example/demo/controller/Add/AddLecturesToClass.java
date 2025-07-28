package com.example.demo.controller.Add;

import com.example.demo.entity.Classes;
import com.example.demo.entity.Lecturers;
import com.example.demo.entity.Lecturers_Classes;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
@Controller
@RequestMapping("/staff-home/classes-list")
@PreAuthorize("hasRole('STAFF')")
public class AddLecturesToClass {
    private final Students_ClassesService studentsClassesService;
    private final Lecturers_ClassesService lecturersClassesService;
    private final ClassesService classesService;
    private final StudentsService studentsService;
    private final LecturesService lecturersService;
    private final PersonsService personsService;

    @Autowired
    public AddLecturesToClass(Students_ClassesService studentsClassesService,
                              Lecturers_ClassesService lecturersClassesService,
                              ClassesService classesService,
                              StudentsService studentsService,
                              LecturesService lecturersService, PersonsService personsService) {
        this.studentsClassesService = studentsClassesService;
        this.lecturersClassesService = lecturersClassesService;
        this.classesService = classesService;
        this.studentsService = studentsService;
        this.lecturersService = lecturersService;
        this.personsService = personsService;
    }

    @PostMapping("/add-lecturers-to-class")
    public String addLecturersToClass(@RequestParam("classId") String classId,
                                      @RequestParam(value = "lecturerIds", required = false) List<String> lecturerIds,
                                      RedirectAttributes redirectAttributes) {
        Classes selectedClass = classesService.getClassById(classId);
        if (selectedClass == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Class not found.");
            return "redirect:/staff-home/classes-list/member-arrangement?id=" + classId;
        }

        if (lecturerIds == null || lecturerIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "No lecturers selected.");
            return "redirect:/staff-home/classes-list/member-arrangement?id=" + classId;
        }

        try {
            for (String lecturerId : lecturerIds) {
                Lecturers lecturer = lecturersService.getLecturerById(lecturerId);
                if (lecturer != null) {
                    // Kiểm tra xem giảng viên đã có trong lớp chưa
                    boolean alreadyInClass = lecturersClassesService.listLecturersInClass(selectedClass)
                            .stream()
                            .anyMatch(lc -> lc.getLecturer().getId().equals(lecturerId));
                    if (!alreadyInClass) {
                        Lecturers_Classes lecturerClass = new Lecturers_Classes();
                        lecturerClass.setClassEntity(selectedClass);
                        lecturerClass.setLecturer(lecturer);
                        lecturersClassesService.addLecturerToClass(lecturerClass);
                    }
                }
            }
            redirectAttributes.addFlashAttribute("successMessage", "Lecturers added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add lecturers: " + e.getMessage());
        }

        return "redirect:/staff-home/classes-list/member-arrangement?id=" + classId;
    }
}
