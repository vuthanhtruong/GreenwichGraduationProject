// src/main/java/com/example/demo/timetable/minorTimetable/controller/MinorTimetableOverviewController.java
package com.example.demo.timetable.minorTimtable.controller;

import com.example.demo.timetable.majorTimetable.model.Slots;
import com.example.demo.timetable.majorTimetable.service.SlotsService;
import com.example.demo.timetable.minorTimtable.model.MinorTimetable;
import com.example.demo.timetable.minorTimtable.service.MinorTimetableService;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/deputy-staff-home/minor-timetable-overview")
public class MinorTimetableOverviewController {

    private final MinorTimetableService minorTimetableService;
    private final SlotsService slotsService;
    private final DeputyStaffsService deputyStaffsService;

    public MinorTimetableOverviewController(MinorTimetableService minorTimetableService,
                                            SlotsService slotsService,
                                            DeputyStaffsService deputyStaffsService) {
        this.minorTimetableService = minorTimetableService;
        this.slotsService = slotsService;
        this.deputyStaffsService = deputyStaffsService;
    }

    @GetMapping
    public String showMinorTimetableOverview(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer week,
            Model model) {

        DeputyStaffs deputy = deputyStaffsService.getDeputyStaff();
        if (deputy == null || deputy.getCampus() == null) {
            return "redirect:/login";
        }
        String campusId = deputy.getCampus().getCampusId();

        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentWeek = now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

        int targetYear = Optional.ofNullable(year).filter(y -> y > 0).orElse(currentYear);
        int targetWeek = Optional.ofNullable(week).filter(w -> w > 0 && w <= 53).orElse(currentWeek);

        List<Slots> slots = slotsService.getSlots();
        if (slots.isEmpty()) {
            model.addAttribute("error", "No time slots configured.");
            return "MinorTimetableOverview";
        }

        // LẤY TOÀN BỘ LỊCH MINOR TRONG TUẦN CỦA CAMPUS
        List<MinorTimetable> allMinorTimetables = minorTimetableService.getAllMinorTimetablesInWeek(
                targetWeek, targetYear, campusId);

        prepareView(model, allMinorTimetables, slots, targetYear, targetWeek, currentYear, currentWeek, deputy);

        return "MinorTimetableOverview";
    }

    private void prepareView(Model model, List<MinorTimetable> timetables, List<Slots> slots,
                             int year, int week, int currentYear, int currentWeek, DeputyStaffs deputy) {

        List<LocalDate> weekDates = getWeekDates(year, week);
        List<String> dayLabels = weekDates.stream()
                .map(d -> d.format(DateTimeFormatter.ofPattern("dd/MM")))
                .collect(Collectors.toList());

        // HỖ TRỢ HIỂN THỊ NHIỀU LỚP TRÙNG SLOT (xung đột)
        List<MinorTimetable>[][] matrix = new ArrayList[7][slots.size()];

        for (MinorTimetable t : timetables) {
            int dayIdx = t.getDayOfWeek().ordinal();
            int slotIdx = slots.indexOf(t.getSlot());

            if (dayIdx < 7 && slotIdx >= 0) {
                if (matrix[dayIdx][slotIdx] == null) {
                    matrix[dayIdx][slotIdx] = new ArrayList<>();
                }
                matrix[dayIdx][slotIdx].add(t);
            }
        }

        List<String> weekLabels = IntStream.rangeClosed(1, 53)
                .mapToObj(w -> {
                    List<LocalDate> wd = getWeekDates(year, w);
                    if (wd.isEmpty() || wd.get(0).getYear() != year) return null;
                    return w + " (" + wd.get(0).format(DateTimeFormatter.ofPattern("dd/MM")) +
                            " - " + wd.get(6).format(DateTimeFormatter.ofPattern("dd/MM")) + ")";
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<String> dayNames = List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

        model.addAttribute("deputy", deputy);
        model.addAttribute("campusName", deputy.getCampus().getCampusName());
        model.addAttribute("matrix", matrix);
        model.addAttribute("slots", slots);
        model.addAttribute("dayLabels", dayLabels);
        model.addAttribute("dayNames", dayNames);
        model.addAttribute("weekLabels", weekLabels);
        model.addAttribute("year", year);
        model.addAttribute("week", week);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentWeek", currentWeek);
        model.addAttribute("totalSessions", timetables.size());
    }

    private List<LocalDate> getWeekDates(int year, int week) {
        return IntStream.range(0, 7)
                .mapToObj(i -> LocalDate.of(year, 1, 1)
                        .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
                        .with(java.time.DayOfWeek.MONDAY)
                        .plusDays(i))
                .collect(Collectors.toList());
    }
}