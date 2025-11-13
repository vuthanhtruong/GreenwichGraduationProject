// src/main/java/com/example/demo/timetable/minorTimetable/controller/MinorTimetableAllController.java
package com.example.demo.timetable.minorTimtable.controller;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.minorClasses.service.MinorClassesService;
import com.example.demo.timetable.minorTimtable.model.MinorTimetable;
import com.example.demo.timetable.minorTimtable.service.MinorTimetableService;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/deputy-staff-home/minor-classes-list")
@RequiredArgsConstructor
@SessionAttributes("selectedMinorClassId")
public class MinorTimetableAllController {

    private final MinorTimetableService minorTimetableService;
    private final MinorClassesService minorClassesService;
    private final DeputyStaffsService deputyStaffsService;

    // === XEM TẤT CẢ LỊCH (POST → GET) ===
    @PostMapping("/minor-timetable/view-all")
    public String viewAllSchedulesPost(@RequestParam String classId, RedirectAttributes ra) {
        ra.addFlashAttribute("selectedMinorClassId", classId);
        return "redirect:/deputy-staff-home/minor-classes-list/minor-timetable-all";
    }

    @GetMapping("/minor-timetable-all")
    public String viewAllSchedules(
            @SessionAttribute(value = "selectedMinorClassId", required = false) String classId,
            Model model,
            RedirectAttributes ra,
            HttpSession session) {

        if (classId == null || classId.isBlank()) {
            ra.addFlashAttribute("error", "Please select a class first.");
            return "redirect:/deputy-staff-home/minor-classes-list";
        }

        MinorClasses minorClass = minorClassesService.getClassById(classId);
        if (minorClass == null || minorClass.getCreator() == null || minorClass.getCreator().getCampus() == null) {
            ra.addFlashAttribute("error", "Class not found or no campus.");
            return "redirect:/deputy-staff-home/minor-classes-list";
        }

        String campusId = minorClass.getCreator().getCampus().getCampusId();
        DeputyStaffs currentStaff = deputyStaffsService.getDeputyStaff();
        if (currentStaff == null || !currentStaff.getCampus().getCampusId().equals(campusId)) {
            ra.addFlashAttribute("error", "Unauthorized access.");
            return "redirect:/deputy-staff-home/minor-classes-list";
        }

        // LẤY TẤT CẢ LỊCH (KHÔNG PHÂN TRANG)
        List<MinorTimetable> schedules = minorTimetableService.getAllSchedulesByClass(classId);

        model.addAttribute("classId", classId);
        model.addAttribute("className", minorClass.getNameClass());
        model.addAttribute("campusName", minorClass.getCreator().getCampus().getCampusName());
        model.addAttribute("schedules", schedules);
        model.addAttribute("totalSlots", schedules.size());

        return "MinorTimetableAll";
    }

    // === XÓA LỊCH ===
    @PostMapping("/minor-timetable-all/delete")
    public String deleteSchedule(
            @RequestParam String timetableId,
            @RequestParam String classId,
            RedirectAttributes ra) {

        try {
            MinorTimetable tt = minorTimetableService.getById(timetableId);
            if (tt == null || !tt.getClassId().equals(classId)) {
                ra.addFlashAttribute("error", "Schedule not found or invalid.");
                return redirectToAll(classId);
            }

            MinorClasses minorClass = tt.getMinorClass();
            String campusId = minorClass.getCreator().getCampus().getCampusId();
            DeputyStaffs currentStaff = deputyStaffsService.getDeputyStaff();
            if (!currentStaff.getCampus().getCampusId().equals(campusId)) {
                ra.addFlashAttribute("error", "Unauthorized.");
                return redirectToAll(classId);
            }

            minorTimetableService.delete(tt);
            ra.addFlashAttribute("success", "Schedule deleted successfully.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Delete failed: " + e.getMessage());
        }
        return redirectToAll(classId);
    }

    private String redirectToAll(String classId) {
        return "redirect:/deputy-staff-home/minor-classes-list/minor-timetable-all?classId=" + classId;
    }
}