package com.example.demo.timetable.specializedTimetable.controller;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.classes.specializedClasses.service.SpecializedClassesService;
import com.example.demo.timetable.specializedTimetable.model.SpecializedTimetable;
import com.example.demo.timetable.specializedTimetable.service.SpecializedTimetableService;
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
@RequestMapping("/staff-home/specialized-classes-list")
@RequiredArgsConstructor
@SessionAttributes("selectedSpecializedClassId")
public class SpecializedTimetableAllController {

    private final SpecializedTimetableService timetableService;
    private final SpecializedClassesService classesService;
    private final StaffsService staffsService;

    // === MỞ XEM TẤT CẢ LỊCH (POST → GET) ===
    @PostMapping("/specialized-timetable/view-all")
    public String viewAllSchedulesPost(@RequestParam String classId, RedirectAttributes ra) {
        ra.addFlashAttribute("selectedSpecializedClassId", classId);
        return "redirect:/staff-home/specialized-classes-list/specialized-timetable-all";
    }

    @GetMapping("/specialized-timetable-all")
    public String viewAllSchedules(
            @SessionAttribute(value = "selectedSpecializedClassId", required = false) String classId,
            Model model,
            RedirectAttributes ra,
            HttpSession session) {

        if (classId == null || classId.isBlank()) {
            ra.addFlashAttribute("error", "Vui lòng chọn lớp trước.");
            return "redirect:/staff-home/specialized-classes-list";
        }

        SpecializedClasses clazz = classesService.getClassById(classId);
        if (clazz == null || clazz.getCreator() == null || clazz.getCreator().getCampus() == null) {
            ra.addFlashAttribute("error", "Không tìm thấy lớp hoặc không có campus.");
            return "redirect:/staff-home/specialized-classes-list";
        }

        String campusId = clazz.getCreator().getCampus().getCampusId();
        Staffs currentStaff = staffsService.getStaff();
        if (currentStaff == null || !currentStaff.getCampus().getCampusId().equals(campusId)) {
            ra.addFlashAttribute("error", "Không có quyền truy cập.");
            return "redirect:/staff-home/specialized-classes-list";
        }

        // LẤY TẤT CẢ LỊCH (KHÔNG PHÂN TRANG)
        List<SpecializedTimetable> schedules = timetableService.getAllSchedulesByClass(classId);

        model.addAttribute("classId", classId);
        model.addAttribute("className", clazz.getNameClass());
        model.addAttribute("campusName", clazz.getCreator().getCampus().getCampusName());
        model.addAttribute("schedules", schedules);
        model.addAttribute("totalSlots", schedules.size());

        return "SpecializedTimetableAll";
    }

    // === XÓA LỊCH ===
    @PostMapping("/specialized-timetable-all/delete")
    public String deleteSchedule(
            @RequestParam String timetableId,
            @RequestParam String classId,
            RedirectAttributes ra) {

        try {
            SpecializedTimetable tt = timetableService.getById(timetableId);
            if (tt == null || !tt.getSpecializedClass().getClassId().equals(classId)) {
                ra.addFlashAttribute("error", "Không tìm thấy lịch hoặc không hợp lệ.");
                return redirectToAll(classId);
            }

            SpecializedClasses clazz = tt.getSpecializedClass();
            String campusId = clazz.getCreator().getCampus().getCampusId();
            Staffs currentStaff = staffsService.getStaff();
            if (!currentStaff.getCampus().getCampusId().equals(campusId)) {
                ra.addFlashAttribute("error", "Không có quyền xóa.");
                return redirectToAll(classId);
            }

            timetableService.delete(tt);
            ra.addFlashAttribute("success", "Xóa lịch thành công.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Xóa thất bại: " + e.getMessage());
        }
        return redirectToAll(classId);
    }

    // === ĐÓNG LỊCH XEM (XÓA SESSION) ===
    @GetMapping("/specialized-timetable-all/close")
    public String closeTimetable(HttpSession session) {
        session.removeAttribute("selectedSpecializedClassId");
        return "redirect:/staff-home/specialized-classes-list";
    }

    private String redirectToAll(String classId) {
        return "redirect:/staff-home/specialized-classes-list/specialized-timetable-all";
    }
}