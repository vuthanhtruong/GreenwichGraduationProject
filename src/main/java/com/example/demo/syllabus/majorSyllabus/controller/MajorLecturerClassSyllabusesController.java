package com.example.demo.syllabus.majorSyllabus.controller;

import com.example.demo.classes.abstractClasses.model.Classes;
import com.example.demo.classes.abstractClasses.service.ClassesService;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.syllabus.majorSyllabus.model.MajorSyllabuses;
import com.example.demo.syllabus.majorSyllabus.service.SyllabusesService;
import com.example.demo.syllabus.specializationSyllabus.model.SpecializationSyllabuses;
import com.example.demo.syllabus.specializationSyllabus.service.SpecializationSyllabusesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/major-lecturer-home/classes-list/syllabus")
public class MajorLecturerClassSyllabusesController {
    private final ClassesService classesService;
    private final SyllabusesService syllabusesService;
    private final SpecializationSyllabusesService specializationSyllabusesService;

    public MajorLecturerClassSyllabusesController(ClassesService classesService, SyllabusesService syllabusesService, SpecializationSyllabusesService specializationSyllabusesService) {
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
        return "MajorLecturerClassSyllabuses";
    }
}
