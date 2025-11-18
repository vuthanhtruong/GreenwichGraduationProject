// src/main/java/com/example/demo/syllabus/minorSyllabus/controller/MinorLecturerClassSyllabusesController.java

package com.example.demo.syllabus.minorSyllabuses.controller;

import com.example.demo.classes.abstractClasses.model.Classes;
import com.example.demo.classes.abstractClasses.service.ClassesService;
import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.syllabus.minorSyllabuses.model.MinorSyllabuses;
import com.example.demo.syllabus.minorSyllabuses.service.MinorSyllabusesService;
import com.example.demo.syllabus.specializationSyllabus.model.SpecializationSyllabuses;
import com.example.demo.syllabus.specializationSyllabus.service.SpecializationSyllabusesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/minor-lecturer-home/classes-list/syllabus")
public class MinorLecturerClassSyllabusesController {

    private final ClassesService classesService;
    private final MinorSyllabusesService minorSyllabusesService;              // Minor

    public MinorLecturerClassSyllabusesController(
            ClassesService classesService,
            MinorSyllabusesService minorSyllabusesService) {

        this.classesService = classesService;
        this.minorSyllabusesService = minorSyllabusesService;
    }

    @GetMapping
    public String showClassSyllabuses(Model model, @RequestParam("classId") String classId) {

        Classes classes = classesService.findClassById(classId);

        // 1. Lớp Minor → lấy đề cương Minor
        if (classes instanceof MinorClasses minorClasses) {
            List<MinorSyllabuses> minorSyllabuses = minorSyllabusesService
                    .getSyllabusesBySubject(minorClasses.getMinorSubject());

            model.addAttribute("Syllabuses", minorSyllabuses);
            model.addAttribute("classType", "Minor");
        }
        model.addAttribute("currentClass", classes);

        return "MinorLecturerClassSyllabuses"; // tên file HTML của Minor
    }
}