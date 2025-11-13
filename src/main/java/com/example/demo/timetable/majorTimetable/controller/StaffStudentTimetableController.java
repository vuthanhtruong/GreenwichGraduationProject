// src/main/java/com/example/demo/timetable/majorTimetable/controller/StaffStudentTimetableController.java
package com.example.demo.timetable.majorTimetable.controller;

import com.example.demo.timetable.majorTimetable.model.Slots;
import com.example.demo.timetable.majorTimetable.model.Timetable;
import com.example.demo.timetable.majorTimetable.service.SlotsService;
import com.example.demo.timetable.majorTimetable.service.TimetableService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
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
@RequestMapping("/staff-timetable")
public class StaffStudentTimetableController {

    private final TimetableService timetableService;
    private final StudentsService studentsService;
    private final SlotsService slotsService;

    public StaffStudentTimetableController(
            TimetableService timetableService,
            StudentsService studentsService,
            SlotsService slotsService) {
        this.timetableService = timetableService;
        this.studentsService = studentsService;
        this.slotsService = slotsService;
    }

    @PostMapping("/student-view")
    public String showStudentTimetablePost(
            @RequestParam String studentId,
            HttpSession session) {

        session.setAttribute("view_studentId", studentId);

        return "redirect:/staff-timetable/student-view";
    }

    @GetMapping("/student-view")
    public String showStudentTimetableGet(
            Model model,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer week,
            HttpSession session) {

        String studentId = (String) session.getAttribute("view_studentId");

        if (studentId == null) {
            model.addAttribute("error", "No student selected.");
            return "redirect:/staff-home/students-list";
        }

        Students student = studentsService.getStudentById(studentId);
        if (student == null) {
            model.addAttribute("error", "Student not found.");
            return "redirect:/staff-home/students-list";
        }

        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentWeek = now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

        int targetYear = (year != null && year > 0) ? year : currentYear;
        int targetWeek = (week != null && week > 0 && week <= 53) ? week : currentWeek;

        List<Slots> allSlots = slotsService.getSlots();
        if (allSlots.isEmpty()) {
            model.addAttribute("error", "No slot configuration found.");
            return "StudentTimetable";
        }

        List<Timetable> timetables = timetableService.getStudentTimetable(
                student.getId(), targetWeek, targetYear);

        prepareView(model, timetables, allSlots, targetYear, targetWeek, currentYear, currentWeek, student);

        return "StudentTimetable";
    }

    private void prepareView(Model model, List<Timetable> timetables, List<Slots> slots,
                             int year, int week, int currentYear, int currentWeek, Students student) {

        List<LocalDate> weekDates = getWeekDates(year, week);
        List<String> dayLabels = weekDates.stream()
                .map(d -> d.format(DateTimeFormatter.ofPattern("dd/MM")))
                .collect(Collectors.toList());

        Timetable[][] matrix = new Timetable[7][slots.size()];
        for (Timetable t : timetables) {
            int dayIdx = t.getDayOfWeek().ordinal();
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

        model.addAttribute("student", student);
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
        model.addAttribute("list", "/staff-home/students-list");
        model.addAttribute("home", "/staff-home"); // Optional: to hide edit buttons in HTML
        model.addAttribute("action", "/staff-timetable/student-view"); // Optional: to hide edit buttons in HTML
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