// src/main/java/com/example/demo/timtable/minorTimtable/controller/MinorLecturerTimetableController.java
package com.example.demo.timtable.minorTimtable.controller;

import com.example.demo.timtable.majorTimetable.model.Slots;
import com.example.demo.timtable.minorTimtable.model.MinorTimetable;
import com.example.demo.timtable.minorTimtable.service.MinorTimetableService;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.user.minorLecturer.service.MinorLecturersService;
import com.example.demo.timtable.majorTimetable.service.SlotsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/minor-lecturer-home/minor-timetable")
public class MinorLecturerTimetableController {

    private final MinorTimetableService timetableService;
    private final MinorLecturersService lecturersService;
    private final SlotsService slotsService;

    public MinorLecturerTimetableController(
            MinorTimetableService timetableService,
            MinorLecturersService lecturersService,
            SlotsService slotsService) {
        this.timetableService = timetableService;
        this.lecturersService = lecturersService;
        this.slotsService = slotsService;
    }

    @GetMapping
    public String showLecturerTimetable(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer week,
            Model model,
            HttpSession session) {

        MinorLecturers lecturer = lecturersService.getMinorLecturer(); // từ login
        if (lecturer == null) {
            return "redirect:/login";
        }

        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentWeek = now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

        int targetYear = (year != null && year > 0) ? year : currentYear;
        int targetWeek = (week != null && week > 0 && week <= 53) ? week : currentWeek;

        List<Slots> allSlots = slotsService.getSlots();
        if (allSlots.isEmpty()) {
            model.addAttribute("error", "No slot configuration found.");
            return "MinorLecturerTimetable";
        }

        // DÙNG HÀM ĐÃ CÓ
        List<MinorTimetable> timetables = timetableService.getMinorTimetablesByMinorLecturer(
                lecturer.getId(), targetWeek, targetYear);

        prepareView(model, timetables, allSlots, targetYear, targetWeek, currentYear, currentWeek, lecturer);

        return "MinorLecturerTimetable";
    }

    private void prepareView(Model model, List<MinorTimetable> timetables, List<Slots> slots,
                             int year, int week, int currentYear, int currentWeek, MinorLecturers lecturer) {

        List<LocalDate> weekDates = getWeekDates(year, week);
        List<String> dayLabels = weekDates.stream()
                .map(d -> d.format(DateTimeFormatter.ofPattern("dd/MM")))
                .collect(Collectors.toList());

        MinorTimetable[][] matrix = new MinorTimetable[7][slots.size()];
        for (MinorTimetable t : timetables) {
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