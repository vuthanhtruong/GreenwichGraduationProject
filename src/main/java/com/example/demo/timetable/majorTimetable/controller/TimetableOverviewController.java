// src/main/java/com/example/demo/timetable/majorTimetable/controller/TimetableOverviewController.java
package com.example.demo.timetable.majorTimetable.controller;

import com.example.demo.timetable.majorTimetable.model.Slots;
import com.example.demo.timetable.majorTimetable.model.Timetable;
import com.example.demo.timetable.majorTimetable.service.SlotsService;
import com.example.demo.timetable.majorTimetable.service.TimetableService;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
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
@RequestMapping("/staff-home/timetable-overview")
public class TimetableOverviewController {

    private final TimetableService timetableService;
    private final SlotsService slotsService;
    private final StaffsService staffsService;

    public TimetableOverviewController(TimetableService timetableService,
                                       SlotsService slotsService,
                                       StaffsService staffsService) {
        this.timetableService = timetableService;
        this.slotsService = slotsService;
        this.staffsService = staffsService;
    }

    @GetMapping
    public String showTimetableOverview(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer week,
            Model model) {

        Staffs staff = staffsService.getStaff();
        if (staff == null || staff.getCampus() == null) {
            return "redirect:/login";
        }
        String campusId = staff.getCampus().getCampusId();

        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentWeek = now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

        int targetYear = Optional.ofNullable(year).filter(y -> y > 0).orElse(currentYear);
        int targetWeek = Optional.ofNullable(week).filter(w -> w > 0 && w <= 53).orElse(currentWeek);

        List<Slots> slots = slotsService.getSlots();
        if (slots.isEmpty()) {
            model.addAttribute("error", "No time slots configured.");
            return "TimetableOverview";
        }

        // LẤY TOÀN BỘ MAJOR + SPECIALIZED TRONG TUẦN
        List<Timetable> allTimetables = timetableService.getMajorTimetableAndSpecializedInWeek(
                targetWeek, targetYear, campusId);

        prepareView(model, allTimetables, slots, targetYear, targetWeek, currentYear, currentWeek, staff);

        return "TimetableOverview";
    }

    private void prepareView(Model model, List<Timetable> timetables, List<Slots> slots,
                             int year, int week, int currentYear, int currentWeek, Staffs staff) {

        List<LocalDate> weekDates = getWeekDates(year, week);
        List<String> dayLabels = weekDates.stream()
                .map(d -> d.format(DateTimeFormatter.ofPattern("dd/MM")))
                .collect(Collectors.toList());

        // DÙNG List<Timetable>[][] ĐỂ HIỂN THỊ NHIỀU LỚP TRÙNG SLOT
        List<Timetable>[][] matrix = new ArrayList[7][slots.size()];

        for (Timetable t : timetables) {
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

        model.addAttribute("staff", staff);
        model.addAttribute("campusName", staff.getCampus().getCampusName());
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