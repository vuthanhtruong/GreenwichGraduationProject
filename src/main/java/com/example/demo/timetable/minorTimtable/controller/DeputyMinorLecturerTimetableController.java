// src/main/java/com/example/demo/timetable/minorTimetable/controller/DeputyMinorLecturerTimetableController.java

package com.example.demo.timetable.minorTimtable.controller;

import com.example.demo.timetable.majorTimetable.model.Slots;
import com.example.demo.timetable.majorTimetable.service.SlotsService;
import com.example.demo.timetable.minorTimtable.model.MinorTimetable;
import com.example.demo.timetable.minorTimtable.service.MinorTimetableService;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.user.minorLecturer.service.MinorLecturersService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/deputy-staff-timetable")
public class DeputyMinorLecturerTimetableController {

    private final MinorTimetableService minorTimetableService;
    private final MinorLecturersService minorLecturerService;
    private final SlotsService minorSlotsService;

    public DeputyMinorLecturerTimetableController(
            MinorTimetableService minorTimetableService,
            MinorLecturersService minorLecturerService,
            SlotsService minorSlotsService) {
        this.minorTimetableService = minorTimetableService;
        this.minorLecturerService = minorLecturerService;
        this.minorSlotsService = minorSlotsService;
    }

    // Nhận POST từ nút "View Timetable" trong danh sách Minor Lecturer
    @PostMapping("/lecturer")
    public String showMinorLecturerTimetablePost(@RequestParam String lecturerId, HttpSession session) {
        session.setAttribute("view_minor_lecturerId", lecturerId);
        return "redirect:/deputy-staff-timetable/lecturer";
    }

    // Hiển thị thời khóa biểu
    @GetMapping("/lecturer")
    public String showMinorLecturerTimetableGet(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer week,
            Model model,
            HttpSession session) {

        String lecturerIdObj = session.getAttribute("view_minor_lecturerId").toString();
        if (lecturerIdObj == null) {
            model.addAttribute("error", "No minor lecturer selected.");
            return "redirect:/deputy-staff-home/minor-lecturers-list";
        }

        String lecturerId = lecturerIdObj;
        MinorLecturers lecturer = minorLecturerService.getMinorLecturerById(lecturerId);
        if (lecturer == null) {
            model.addAttribute("error", "Minor lecturer not found.");
            return "redirect:/deputy-staff-home/minor-lecturers-list";
        }

        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentWeek = now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

        int targetYear = (year != null && year > 0) ? year : currentYear;
        int targetWeek = (week != null && week > 0 && week <= 53) ? week : currentWeek;

        List<Slots> allSlots = minorSlotsService.getSlots();
        if (allSlots.isEmpty()) {
            model.addAttribute("error", "No slot configuration found for minor lecturers.");
            return "MinorLecturerTimetable"; // Template chung hoặc riêng đều được
        }

        List<MinorTimetable> timetables = minorTimetableService.getMinorTimetablesByMinorLecturer(
                lecturer.getId(), targetWeek, targetYear);

        prepareView(model, timetables, allSlots, targetYear, targetWeek,
                currentYear, currentWeek, lecturer);

        // Đánh dấu đây là Deputy Staff đang xem Minor Lecturer
        model.addAttribute("isDeputyStaffView", true);
        model.addAttribute("list", "/deputy-staff-home/minor-lecturers-list");
        model.addAttribute("home", "/deputy-staff-home");
        model.addAttribute("action", "/deputy-staff-timetable/lecturer");

        return "MinorLecturerTimetable"; // Dùng chung template hoặc tạo riêng cũng được
    }

    private void prepareView(Model model, List<MinorTimetable> timetables, List<Slots> slots,
                             int year, int week, int currentYear, int currentWeek, MinorLecturers lecturer) {

        List<LocalDate> weekDates = getWeekDates(year, week);
        List<String> dayLabels = weekDates.stream()
                .map(d -> d.format(DateTimeFormatter.ofPattern("dd/MM")))
                .collect(Collectors.toList());

        MinorTimetable[][] matrix = new MinorTimetable[7][slots.size()];
        for (MinorTimetable t : timetables) {
            int dayIdx = t.getDayOfWeek().ordinal(); // giả sử enum DayOfWeek
            int slotIdx = slots.indexOf(t.getSlot());
            if (dayIdx < 7 && slotIdx >= 0) {
                matrix[dayIdx][slotIdx] = t;
            }
        }

        List<String> weekLabels = new ArrayList<>();
        for (int w = 1; w <= 53; w++) {
            List<LocalDate> dates = getWeekDates(year, w);
            if (!dates.isEmpty() && dates.get(0).getYear() == year) {
                String label = w + " (" +
                        dates.get(0).format(DateTimeFormatter.ofPattern("dd/MM")) + " - " +
                        dates.get(6).format(DateTimeFormatter.ofPattern("dd/MM")) + ")";
                weekLabels.add(label);
            }
        }

        List<String> dayNames = List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

        model.addAttribute("lecturer", lecturer);
        model.addAttribute("timetables", timetables);
        model.addAttribute("matrix", matrix);
        model.addAttribute("slots", slots);
        model.addAttribute("dayLabels", dayLabels);
        model.addAttribute("dayNames", dayNames);
        model.addAttribute("weekLabels", weekLabels);
        model.addAttribute("year", year);
        model.addAttribute("week", week);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentWeek", currentWeek);
    }

    private List<LocalDate> getWeekDates(int year, int week) {
        return java.util.stream.IntStream.range(0, 7)
                .mapToObj(i -> LocalDate.of(year, 1, 1)
                        .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
                        .with(java.time.DayOfWeek.MONDAY)
                        .plusDays(i))
                .collect(Collectors.toList());
    }
}