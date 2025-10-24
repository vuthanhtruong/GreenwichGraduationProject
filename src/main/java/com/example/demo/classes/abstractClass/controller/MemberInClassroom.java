package com.example.demo.classes.abstractClass.controller;

import com.example.demo.classes.abstractClass.model.Classes;
import com.example.demo.classes.abstractClass.service.ClassesService;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.service.MajorLecturers_MajorClassesService;
import com.example.demo.lecturers_Classes.majorLecturers_SpecializedClasses.service.MajorLecturers_SpecializedClassesService;
import com.example.demo.students_Classes.students_MajorClass.service.StudentsMajorClassesService;
import com.example.demo.students_Classes.students_SpecializedClasses.service.StudentsSpecializedClassesService;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.person.model.Persons;
import com.example.demo.user.person.service.PersonsService;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.student.model.Students;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/classroom")
public class MemberInClassroom {

    private final StudentsSpecializedClassesService studentsSpecializedClassesService;
    private final StudentsMajorClassesService studentsMajorClassesService;
    private final MajorLecturers_MajorClassesService majorLecturersMajorClassesService;
    private final MajorLecturers_SpecializedClassesService majorLecturersSpecializedClassesService;
    private final ClassesService classesService;
    private final PersonsService personsService;

    public MemberInClassroom(
            StudentsSpecializedClassesService studentsSpecializedClassesService,
            StudentsMajorClassesService studentsMajorClassesService,
            MajorLecturers_MajorClassesService majorLecturersMajorClassesService,
            MajorLecturers_SpecializedClassesService majorLecturersSpecializedClassesService,
            ClassesService classesService, PersonsService personsService) {
        this.studentsSpecializedClassesService = studentsSpecializedClassesService;
        this.studentsMajorClassesService = studentsMajorClassesService;
        this.majorLecturersMajorClassesService = majorLecturersMajorClassesService;
        this.majorLecturersSpecializedClassesService = majorLecturersSpecializedClassesService;
        this.classesService = classesService;
        this.personsService = personsService;
    }

    @PostMapping("/members")
    public String listClassMembers(
            @RequestParam("classId") String classId,
            Model model,
            HttpSession session) {

        session.setAttribute("classId", classId); // ← THÊM DÒNG NÀY

        try {
            if(personsService.getPerson() instanceof MajorLecturers){
                model.addAttribute("home", "/major-lecturer-home");
            } else if (personsService.getPerson() instanceof Staffs) {
                model.addAttribute("home", "/staff-home");
            } else if (personsService.getPerson() instanceof Students) {
                model.addAttribute("home", "/student-home");
            }
            Classes classEntity = classesService.findClassById(classId);
            if (classEntity == null) {
                return prepareErrorModel(model, classId, "Class not found: " + classId);
            }

            List<Persons> members = new ArrayList<>();

            if (classEntity instanceof MajorClasses majorClasses) {
                List<Students> students = studentsMajorClassesService.getStudentsByClass(majorClasses);
                List<MajorLecturers> lecturers = majorLecturersMajorClassesService.listLecturersInClass(majorClasses);
                members.addAll(students);
                members.addAll(lecturers);
            } else if (classEntity instanceof SpecializedClasses specializedClasses) {
                List<Students> students = studentsSpecializedClassesService.getStudentsByClass(specializedClasses);
                List<MajorLecturers> lecturers = majorLecturersSpecializedClassesService.listLecturersInClass(specializedClasses);
                members.addAll(students);
                members.addAll(lecturers);
            } else {
                return prepareErrorModel(model, classId, "Unsupported class type.");
            }

            model.addAttribute("classId", classId);
            model.addAttribute("className", classEntity.getNameClass());
            model.addAttribute("members", members);
            model.addAttribute("studentsCount", members.stream().filter(p -> p instanceof Students).count());
            model.addAttribute("lecturersCount", members.stream().filter(p -> p instanceof MajorLecturers).count());

            return "MemberInClassroom";

        } catch (Exception e) {
            return prepareErrorModel(model, classId, "Error: " + e.getMessage());
        }
    }

    private String prepareErrorModel(Model model, String classId, String errorMsg) {
        model.addAttribute("errors", List.of(errorMsg));
        model.addAttribute("classId", classId);
        model.addAttribute("className", "Unknown Class");
        model.addAttribute("members", new ArrayList<>());
        model.addAttribute("eligibleStudents", new ArrayList<>());
        model.addAttribute("studentsCount", 0);
        model.addAttribute("lecturersCount", 0);
        return "MemberInClassroom";
    }
}