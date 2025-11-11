package com.example.demo.timtable.controller;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.majorClasses.service.MajorClassesService;
import com.example.demo.entity.Enums.DaysOfWeek;
import com.example.demo.room.model.Rooms;
import com.example.demo.room.service.RoomsService;
import com.example.demo.timtable.model.MajorTimetable;
import com.example.demo.timtable.model.Slots;
import com.example.demo.timtable.service.MajorTimetableService;
import com.example.demo.timtable.service.SlotsService;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/staff-home/classes-list")
@RequiredArgsConstructor
@SessionAttributes("selectedClassId")
public class MajorTimetableController {

    private final MajorTimetableService majorTimetableService;
    private final SlotsService slotsService;
    private final StaffsService staffsService;
    private final MajorClassesService majorClassesService;
    private final RoomsService roomsService;

    @PostMapping("/major-timetable")
    public String openTimetableFromList(
            @RequestParam String classId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer week,
            RedirectAttributes redirectAttributes) {

        if (classId == null || classId.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Please select a class.");
            return "redirect:/staff-home/classes-list";
        }

        redirectAttributes.addFlashAttribute("selectedClassId", classId);

        String url = "redirect:/staff-home/classes-list/major-timetable";
        if (year != null) {
            url += "?year=" + year + (week != null ? "&week=" + week : "");
        } else if (week != null) {
            url += "?week=" + week;
        }
        return url;
    }

    @GetMapping("/major-timetable")
    public String showTimetable(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer week,
            @SessionAttribute(value = "selectedClassId", required = false) String classId,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (classId == null || classId.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Please select a class first.");
            return "redirect:/staff-home/classes-list";
        }

        return loadTimetable(classId, year, week, model, redirectAttributes);
    }

    private String loadTimetable(String classId, Integer inputYear, Integer inputWeek, Model model, RedirectAttributes ra) {
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentWeek = now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

        int year = (inputYear != null) ? inputYear : currentYear;
        int week = (inputWeek != null) ? inputWeek : currentWeek;

        // BLOCK PAST WEEKS
        LocalDate selectedMonday = getWeekDates(year, week).get(0);
        if (selectedMonday.isBefore(now.with(java.time.DayOfWeek.MONDAY))) {
            ra.addFlashAttribute("error", "Cannot arrange timetable for past weeks.");
            return redirectUrl(currentYear, currentWeek);
        }

        MajorClasses majorClass = majorClassesService.getClassById(classId);
        if (majorClass == null) {
            ra.addFlashAttribute("error", "Class not found.");
            return redirectUrl(year, week);
        }

        String className = majorClass.getNameClass();
        String campusId = (majorClass.getCreator() != null && majorClass.getCreator().getCampus() != null)
                ? majorClass.getCreator().getCampus().getCampusId() : null;

        if (campusId == null) {
            ra.addFlashAttribute("error", "Class has no campus assigned.");
            return redirectUrl(year, week);
        }

        Staffs currentStaff = staffsService.getStaff();
        if (currentStaff == null || currentStaff.getCampus() == null ||
                !currentStaff.getCampus().getCampusId().equals(campusId)) {
            ra.addFlashAttribute("error", "You are not authorized to manage this campus.");
            return redirectUrl(year, week);
        }

        List<Slots> slots = slotsService.getSlots();
        if (slots.isEmpty()) {
            model.addAttribute("error", "No time slots configured.");
            return "MajorTimetable";
        }

        List<LocalDate> dates = getWeekDates(year, week);
        List<String> dayNames = dayNames();
        List<String> shortDayNames = List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");

        DateTimeFormatter dayFmt = DateTimeFormatter.ofPattern("dd/MM");
        List<String> dayLabels = dates.stream().map(d -> d.format(dayFmt)).toList();

        List<String> weekLabels = IntStream.rangeClosed(1, 53)
                .mapToObj(w -> {
                    List<LocalDate> wd = getWeekDates(year, w);
                    if (wd.isEmpty() || wd.get(0).getYear() != year) return null;
                    return w + " (" + wd.get(0).format(dayFmt) + " - " + wd.get(6).format(dayFmt) + ")";
                })
                .filter(Objects::nonNull)
                .toList();

        List<MajorTimetable> allTimetablesInWeek = majorTimetableService
                .getMajorTimetablesByWeekInYear(week, year, campusId);

        MajorTimetable[][] bookedMatrix = new MajorTimetable[7][slots.size()];
        List<String>[][] availableRoomsMatrix = new List[7][slots.size()];

        for (MajorTimetable t : allTimetablesInWeek) {
            if (t.getClassEntity().getClassId().equals(classId)) {
                int dayIdx = t.getDayOfWeek().ordinal();
                int slotIdx = slots.indexOf(t.getSlot());
                if (slotIdx >= 0 && dayIdx < 7) {
                    bookedMatrix[dayIdx][slotIdx] = t;
                }
            }
        }

        for (int dayIdx = 0; dayIdx < 7; dayIdx++) {
            DaysOfWeek day = DaysOfWeek.valueOf(dayNames.get(dayIdx));
            for (int slotIdx = 0; slotIdx < slots.size(); slotIdx++) {
                if (bookedMatrix[dayIdx][slotIdx] != null) {
                    availableRoomsMatrix[dayIdx][slotIdx] = List.of();
                    continue;
                }

                Slots slot = slots.get(slotIdx);
                List<String> rooms = majorTimetableService
                        .getAvailableRoomsForSlot(classId, campusId, slot, day, week, year)
                        .stream()
                        .map(Rooms::getRoomId)
                        .sorted()
                        .toList();
                availableRoomsMatrix[dayIdx][slotIdx] = rooms;
            }
        }
        String campusName = (majorClass.getCreator() != null &&
                majorClass.getCreator().getCampus() != null)
                ? majorClass.getCreator().getCampus().getCampusName()
                : null;

        model.addAttribute("classCreatorCampusName", campusName);
        model.addAttribute("classId", classId);
        model.addAttribute("className", className);
        model.addAttribute("year", year);
        model.addAttribute("week", week);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentWeek", currentWeek);
        model.addAttribute("slots", slots);
        model.addAttribute("dayNames", dayNames);
        model.addAttribute("shortDayNames", shortDayNames);
        model.addAttribute("dayLabels", dayLabels);
        model.addAttribute("weekLabels", weekLabels);
        model.addAttribute("bookedTimetableMatrix", bookedMatrix);
        model.addAttribute("availableRoomsMatrix", availableRoomsMatrix);

        return "MajorTimetable";
    }

    @PostMapping("/major-timetable/save-all")
    public String saveAllMajorTimetable(
            @RequestParam Integer year,
            @RequestParam Integer week,
            @RequestParam Map<String, String> allParams,
            @SessionAttribute("selectedClassId") String classId,
            RedirectAttributes redirectAttributes) {

        int savedCount = 0;
        List<String> errors = new ArrayList<>();
        Set<String> usedRoomSlotWeek = new HashSet<>();

        try {
            Staffs creator = staffsService.getStaff();
            if (creator == null) {
                redirectAttributes.addFlashAttribute("error", "Staff not found.");
                return redirectUrl(year, week);
            }

            MajorClasses majorClass = majorClassesService.getClassById(classId);
            if (majorClass == null || majorClass.getCreator() == null || majorClass.getCreator().getCampus() == null) {
                redirectAttributes.addFlashAttribute("error", "Invalid class or no campus.");
                return redirectUrl(year, week);
            }

            String campusId = majorClass.getCreator().getCampus().getCampusId();
            if (!creator.getCampus().getCampusId().equals(campusId)) {
                redirectAttributes.addFlashAttribute("error", "You are not authorized to save for this campus.");
                return redirectUrl(year, week);
            }

            List<Slots> slots = slotsService.getSlots();
            if (slots.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No time slots configured.");
                return redirectUrl(year, week);
            }

            List<String> dayNames = dayNames();

            for (int dayIdx = 0; dayIdx < 7; dayIdx++) {
                DaysOfWeek dayOfWeek = DaysOfWeek.valueOf(dayNames.get(dayIdx));

                for (int slotIdx = 0; slotIdx < slots.size(); slotIdx++) {
                    String roomKey = "room_" + dayIdx + "_" + slotIdx;
                    String roomId = allParams.get(roomKey);
                    if (roomId == null || roomId.trim().isEmpty() || roomId.equals("-- Select Room --")) {
                        continue;
                    }

                    String slotIdKey = "slotId_" + dayIdx + "_" + slotIdx;
                    String slotId = allParams.get(slotIdKey);
                    if (slotId == null || slotId.isEmpty()) continue;

                    Slots slot = slotsService.getSlotById(slotId);
                    if (slot == null) {
                        errors.add("Invalid slot ID: " + slotId);
                        continue;
                    }

                    String conflictKey = roomId + "|" + slotId + "|" + week + "|" + year;
                    if (usedRoomSlotWeek.contains(conflictKey)) {
                        errors.add("Room " + roomId + " already booked in week " + week + "/" + year);
                        continue;
                    }

                    Rooms room = roomsService.getRoomById(roomId);
                    if (room == null || !room.getCampus().getCampusId().equals(campusId)) {
                        errors.add("Room not found or not in campus: " + roomId);
                        continue;
                    }

                    if (majorTimetableService.getTimetableByClassSlotDayWeek(classId, campusId, slotId, dayOfWeek, week, year) != null) {
                        errors.add("Class already has schedule at: " + roomId);
                        continue;
                    }

                    MajorTimetable timetable = new MajorTimetable();
                    timetable.setTimetableId(UUID.randomUUID().toString());
                    timetable.setClassEntity(majorClass);
                    timetable.setRoom(room);
                    timetable.setSlot(slot);
                    timetable.setDayOfWeek(dayOfWeek);
                    timetable.setWeekOfYear(week);
                    timetable.setYear(year);
                    timetable.setCreator(creator);

                    majorTimetableService.SaveMajorTimetable(timetable, campusId);
                    savedCount++;
                    usedRoomSlotWeek.add(conflictKey);
                }
            }

            if (savedCount > 0) {
                redirectAttributes.addFlashAttribute("success",
                        "Saved " + savedCount + " room(s) in week " + week + "/" + year);
            } else if (errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("info", "No rooms selected.");
            }

            if (!errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", String.join("<br>", errors));
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return redirectUrl(year, week);
    }

    // === ADD DELETE ENDPOINT ===
    @PostMapping("/major-timetable/delete")
    public String deleteTimetable(
            @RequestParam String timetableId,
            @RequestParam Integer year,
            @RequestParam Integer week,
            @RequestParam String classId,
            RedirectAttributes ra) {

        try {
            MajorTimetable tt = majorTimetableService.getById(timetableId);
            if (tt == null) {
                ra.addFlashAttribute("error", "Schedule not found.");
                return redirectUrl(year, week);
            }

            MajorClasses majorClass = tt.getClassEntity();
            String campusId = majorClass.getCreator().getCampus().getCampusId();
            Staffs currentStaff = staffsService.getStaff();

            if (!currentStaff.getCampus().getCampusId().equals(campusId)) {
                ra.addFlashAttribute("error", "Unauthorized campus.");
                return redirectUrl(year, week);
            }

            majorTimetableService.delete(tt);
            ra.addFlashAttribute("success", "Schedule deleted successfully.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Delete failed: " + e.getMessage());
        }

        return redirectUrl(year, week);
    }

    @GetMapping("/clear-class")
    public String clearClass(HttpSession session) {
        session.removeAttribute("selectedClassId");
        return "redirect:/staff-home/classes-list";
    }

    @GetMapping("/major-timetable/save-all")
    public String blockDirectSave(RedirectAttributes ra) {
        ra.addFlashAttribute("error", "Direct access not allowed.");
        return "redirect:/staff-home/classes-list";
    }

    private String redirectUrl(Integer year, Integer week) {
        String url = "redirect:/staff-home/classes-list/major-timetable";
        if (year != null) {
            url += "?year=" + year + (week != null ? "&week=" + week : "");
        } else if (week != null) {
            url += "?week=" + week;
        }
        return url;
    }

    private List<String> dayNames() {
        return List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY");
    }

    private List<LocalDate> getWeekDates(int year, int week) {
        LocalDate firstDay = LocalDate.of(year, 1, 1)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
                .with(java.time.DayOfWeek.MONDAY);
        return IntStream.range(0, 7)
                .mapToObj(i -> firstDay.plusDays(i))
                .toList();
    }
}