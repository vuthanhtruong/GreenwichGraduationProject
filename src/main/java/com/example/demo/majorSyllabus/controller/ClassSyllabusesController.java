package com.example.demo.majorSyllabus.controller;

import com.example.demo.classes.model.Classes;
import com.example.demo.classes.model.MajorClasses;
import com.example.demo.classes.service.ClassesService;
import com.example.demo.classes.service.MajorClassesService;
import com.example.demo.majorSyllabus.model.MajorSyllabuses;
import com.example.demo.majorSyllabus.service.SyllabusesService;
import com.example.demo.specializationSyllabus.model.SpecializationSyllabuses;
import com.example.demo.specializationSyllabus.service.SpecializationSyllabusesService;
import com.example.demo.specializedClasses.model.SpecializedClasses;
import com.example.demo.specializedClasses.service.SpecializedClassesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/student-home/student-classes-list/syllabus")
public class ClassSyllabusesController {
    private final ClassesService classesService;
    private final SyllabusesService syllabusesService;
    private final SpecializationSyllabusesService specializationSyllabusesService;

    public ClassSyllabusesController(ClassesService classesService, SyllabusesService syllabusesService, SpecializationSyllabusesService specializationSyllabusesService) {
        this.classesService = classesService;
        this.syllabusesService = syllabusesService;
        this.specializationSyllabusesService = specializationSyllabusesService;
    }

    @GetMapping()
    public String showClassSyllabuses(Model model, @RequestParam("classId") String classId) {
        Classes classes = classesService.findClassById(classId);
        if(classes instanceof MajorClasses majorClasses){
            List<MajorSyllabuses> majorSyllabuses=syllabusesService.getSyllabusesBySubject(majorClasses.getSubject());
            model.addAttribute("Syllabuses",majorSyllabuses);
        }
        else if(classes instanceof SpecializedClasses specializedClasses){
            List<SpecializationSyllabuses> specializationSyllabuses=specializationSyllabusesService.getSyllabusesBySubject(specializedClasses.getSpecializedSubject());
            model.addAttribute("Syllabuses",specializationSyllabuses);
        }
        return "ClassSyllabuses";
    }
}
