package com.example.demo.timtable.majorTimetable.controller;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.majorClasses.service.MajorClassesService;
import com.example.demo.timtable.majorTimetable.model.MajorTimetable;
import com.example.demo.timtable.majorTimetable.service.MajorTimetableService;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/staff-home/classes-list")
@RequiredArgsConstructor
@SessionAttributes("selectedClassId")
public class MajorTimetableAllController {

    private final MajorTimetableService majorTimetableService;
    private final MajorClassesService majorClassesService;
    private final StaffsService staffsService;

    // === XEM TẤT CẢ LỊCH (POST → GET) ===
    @PostMapping("/major-timetable/view-all")
    public String viewAllSchedulesPost(@RequestParam String classId, RedirectAttributes ra) {
        ra.addFlashAttribute("selectedClassId", classId);
        return "redirect:/staff-home/classes-list/major-timetable-all";
    }

    @GetMapping("/major-timetable-all")
    public String viewAllSchedules(
            @SessionAttribute(value = "selectedClassId", required = false) String classId,
            Model model,
            RedirectAttributes ra,
            HttpSession session) {

        if (classId == null || classId.isBlank()) {
            ra.addFlashAttribute("error", "Please select a class first.");
            return "redirect:/staff-home/classes-list";
        }

        MajorClasses majorClass = majorClassesService.getClassById(classId);
        if (majorClass == null || majorClass.getCreator() == null || majorClass.getCreator().getCampus() == null) {
            ra.addFlashAttribute("error", "Class not found or no campus.");
            return "redirect:/staff-home/classes-list";
        }

        String campusId = majorClass.getCreator().getCampus().getCampusId();
        Staffs currentStaff = staffsService.getStaff();
        if (currentStaff == null || !currentStaff.getCampus().getCampusId().equals(campusId)) {
            ra.addFlashAttribute("error", "Unauthorized access.");
            return "redirect:/staff-home/classes-list";
        }

        // LẤY TẤT CẢ LỊCH (KHÔNG PHÂN TRANG)
        List<MajorTimetable> schedules = majorTimetableService.getAllSchedulesByClass(classId);

        model.addAttribute("classId", classId);
        model.addAttribute("className", majorClass.getNameClass());
        model.addAttribute("campusName", majorClass.getCreator().getCampus().getCampusName());
        model.addAttribute("schedules", schedules);
        model.addAttribute("totalSlots", schedules.size());

        return "MajorTimetableAll";
    }

    // === XÓA LỊCH ===
    @PostMapping("/major-timetable-all/delete")
    public String deleteSchedule(
            @RequestParam String timetableId,
            @RequestParam String classId,
            RedirectAttributes ra) {

        try {
            MajorTimetable tt = majorTimetableService.getById(timetableId);
            if (tt == null || !tt.getClassEntity().getClassId().equals(classId)) {
                ra.addFlashAttribute("error", "Schedule not found or invalid.");
                return redirectToAll(classId);
            }

            MajorClasses majorClass = tt.getClassEntity();
            String campusId = majorClass.getCreator().getCampus().getCampusId();
            Staffs currentStaff = staffsService.getStaff();
            if (!currentStaff.getCampus().getCampusId().equals(campusId)) {
                ra.addFlashAttribute("error", "Unauthorized.");
                return redirectToAll(classId);
            }

            majorTimetableService.delete(tt);
            ra.addFlashAttribute("success", "Schedule deleted successfully.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Delete failed: " + e.getMessage());
        }
        return redirectToAll(classId);
    }

    private String redirectToAll(String classId) {
        return "redirect:/staff-home/classes-list/major-timetable-all?classId=" + classId;
    }
}