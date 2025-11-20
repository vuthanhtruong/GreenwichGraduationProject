package com.example.demo.timetable.majorTimetable.controller;

import com.example.demo.timetable.majorTimetable.model.Slots;
import com.example.demo.timetable.majorTimetable.model.Timetable;
import com.example.demo.timetable.majorTimetable.service.SlotsService;
import com.example.demo.timetable.majorTimetable.service.TimetableService;
import com.example.demo.user.parentAccount.service.ParentAccountsService;
import com.example.demo.user.student.model.Students;
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
@RequestMapping("/parent")
public class ParentTimetableController {

    private final ParentAccountsService parentAccountsService;
    private final TimetableService timetableService;
    private final SlotsService slotsService;

    public ParentTimetableController(ParentAccountsService parentAccountsService,
                                     TimetableService timetableService,
                                     SlotsService slotsService) {
        this.parentAccountsService = parentAccountsService;
        this.timetableService = timetableService;
        this.slotsService = slotsService;
    }

    // ===================== STEP 1: Choose child (if parent has multiple) =====================
    @GetMapping("/timetable")
    public String chooseChild(Model model, HttpSession session) {
        var currentParent = parentAccountsService.getParent();
        var children = parentAccountsService.getStudentsByParentId(currentParent.getId());

        if (children.isEmpty()) {
            model.addAttribute("error", "No linked students found.");
            return "parent/NoChildrenTimetable";
        }

        if (children.size() == 1) {
            // Only one child → go directly to timetable
            session.setAttribute("view_childId", children.get(0).getId());
            return "redirect:/parent/timetable/view";
        }

        model.addAttribute("children", children);
        return "parent/SelectChildTimetable"; // Thymeleaf page to select child
    }

    // ===================== STEP 2: Submit selected child =====================
    @PostMapping("/timetable")
    public String selectChild(
            @RequestParam String studentId,
            HttpSession session) {

        // Basic validation
        Students student = parentAccountsService.getStudentsByParentId(
                        parentAccountsService.getParent().getId()).stream()
                .filter(s -> s.getId().equals(studentId))
                .findFirst()
                .orElse(null);

        if (student == null) {
            return "redirect:/parent/timetable?error=invalid";
        }

        session.setAttribute("view_childId", studentId);
        return "redirect:/parent/timetable/view";
    }

    // ===================== STEP 3: View timetable of selected child =====================
    @GetMapping("/timetable/view")
    public String viewChildTimetable(
            Model model,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer week,
            HttpSession session) {

        String childId = (String) session.getAttribute("view_childId");
        if (childId == null) {
            return "redirect:/parent/timetable";
        }

        // Verify parent has access to this child
        boolean hasAccess = parentAccountsService.getStudentsByParentId(
                        parentAccountsService.getParent().getId()).stream()
                .anyMatch(s -> s.getId().equals(childId));

        if (!hasAccess) {
            model.addAttribute("error", "Access denied.");
            return "redirect:/parent/timetable";
        }

        Students child = parentAccountsService.getStudentsByParentId(
                        parentAccountsService.getParent().getId()).stream()
                .filter(s -> s.getId().equals(childId))
                .findFirst()
                .orElse(null);

        if (child == null) {
            return "redirect:/parent/timetable";
        }

        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentWeek = now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

        int targetYear = (year != null && year > 0) ? year : currentYear;
        int targetWeek = (week != null && week > 0 && week <= 53) ? week : currentWeek;

        List<Slots> allSlots = slotsService.getSlots();
        if (allSlots.isEmpty()) {
            model.addAttribute("error", "Timetable configuration not available.");
            return "StudentTimetable";
        }

        List<Timetable> timetables = timetableService.getStudentTimetable(
                child.getId(), targetWeek, targetYear);

        prepareView(model, timetables, allSlots, targetYear, targetWeek,
                currentYear, currentWeek, child);

        // Allow parent to switch child easily
        var siblings = parentAccountsService.getStudentsByParentId(
                parentAccountsService.getParent().getId());
        model.addAttribute("siblings", siblings);
        model.addAttribute("currentChildId", childId);

        return "StudentTimetable"; // Reuse the same beautiful template
    }

    // Reuse the same logic as staff version
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

        // Parent mode: hide edit buttons + correct navigation
        model.addAttribute("parentMode", true);
        model.addAttribute("home", "/parent-home");
        model.addAttribute("action", "/parent/timetable/view");
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