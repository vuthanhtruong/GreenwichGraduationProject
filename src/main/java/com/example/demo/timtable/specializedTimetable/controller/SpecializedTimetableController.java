package com.example.demo.timtable.specializedTimetable.controller;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.classes.specializedClasses.service.SpecializedClassesService;
import com.example.demo.entity.Enums.DaysOfWeek;
import com.example.demo.room.model.Rooms;
import com.example.demo.room.service.RoomsService;
import com.example.demo.timtable.specializedTimetable.model.SpecializedTimetable;
import com.example.demo.timtable.majorTimetable.model.Slots;
import com.example.demo.timtable.majorTimetable.service.SlotsService;
import com.example.demo.timtable.specializedTimetable.service.SpecializedTimetableService;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/staff-home/specialized-classes-list")
@RequiredArgsConstructor
@SessionAttributes("selectedSpecializedClassId")
public class SpecializedTimetableController {

    private final SpecializedTimetableService timetableService;
    private final SlotsService slotsService;
    private final StaffsService staffsService;
    private final SpecializedClassesService classesService;
    private final RoomsService roomsService;

    @PostMapping("/specialized-timetable")
    public String openTimetableFromList(
            @RequestParam String classId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer week,
            RedirectAttributes redirectAttributes) {
        if (classId == null || classId.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Please select a class.");
            return "redirect:/staff-home/specialized-classes-list";
        }
        redirectAttributes.addFlashAttribute("selectedSpecializedClassId", classId);
        String url = "redirect:/staff-home/specialized-classes-list/specialized-timetable";
        if (year != null) {
            url += "?year=" + year + (week != null ? "&week=" + week : "");
        } else if (week != null) {
            url += "?week=" + week;
        }
        return url;
    }

    @GetMapping("/specialized-timetable")
    public String showTimetable(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer week,
            @SessionAttribute(value = "selectedSpecializedClassId", required = false) String classId,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (classId == null || classId.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Please select a class first.");
            return "redirect:/staff-home/specialized-classes-list";
        }
        return loadTimetable(classId, year, week, model, redirectAttributes);
    }

    private String loadTimetable(String classId, Integer inputYear, Integer inputWeek, Model model, RedirectAttributes ra) {
        LocalDate now = LocalDate.now();
        LocalDateTime nowTime = LocalDateTime.now();
        int currentYear = now.getYear();
        int currentWeek = now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int year = (inputYear != null) ? inputYear : currentYear;
        int week = (inputWeek != null) ? inputWeek : currentWeek;

        boolean isPast = isPastWeek(year, week, currentYear, currentWeek);

        SpecializedClasses specializedClass = classesService.getClassById(classId);
        if (specializedClass == null) {
            ra.addFlashAttribute("error", "Class not found.");
            return redirectUrl(year, week);
        }

        String className = specializedClass.getNameClass();
        String campusId = (specializedClass.getCreator() != null && specializedClass.getCreator().getCampus() != null)
                ? specializedClass.getCreator().getCampus().getCampusId() : null;
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
            return "SpecializedTimetable";
        }

        List<LocalDate> dates = getWeekDates(year, week);
        List<String> dayNames = dayNames();
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

        List<SpecializedTimetable> allTimetablesInWeek = timetableService
                .getSpecializedTimetablesByWeekInYear(week, year, campusId);

        SpecializedTimetable[][] bookedMatrix = new SpecializedTimetable[7][slots.size()];
        List<String>[][] availableRoomsMatrix = new List[7][slots.size()];

        int bookedSlotsInWeek = 0;
        for (SpecializedTimetable t : allTimetablesInWeek) {
            if (t.getSpecializedClass().getClassId().equals(classId)) {
                int dayIdx = t.getDayOfWeek().ordinal();
                int slotIdx = slots.indexOf(t.getSlot());
                if (slotIdx >= 0 && dayIdx < 7) {
                    bookedMatrix[dayIdx][slotIdx] = t;
                    bookedSlotsInWeek++;
                }
            }
        }

        int totalRequiredSlots = specializedClass.getSlotQuantity() != null ? specializedClass.getSlotQuantity() : 0;
        int remainingSlots = totalRequiredSlots - bookedSlotsInWeek;
        boolean isFullyScheduled = remainingSlots <= 0;

        for (int dayIdx = 0; dayIdx < 7; dayIdx++) {
            DaysOfWeek day = DaysOfWeek.valueOf(dayNames.get(dayIdx));
            LocalDate date = dates.get(dayIdx);

            for (int slotIdx = 0; slotIdx < slots.size(); slotIdx++) {
                if (bookedMatrix[dayIdx][slotIdx] != null) {
                    availableRoomsMatrix[dayIdx][slotIdx] = List.of();
                    continue;
                }

                if (isPast) {
                    availableRoomsMatrix[dayIdx][slotIdx] = List.of();
                    continue;
                }

                LocalDateTime slotEnd = date.atTime(slots.get(slotIdx).getEndTime());
                if (slotEnd.isBefore(nowTime)) {
                    availableRoomsMatrix[dayIdx][slotIdx] = List.of();
                    continue;
                }

                List<String> rooms = timetableService
                        .getAvailableRoomsForSlot(classId, campusId, slots.get(slotIdx), day, week, year)
                        .stream()
                        .map(Rooms::getRoomId)
                        .sorted()
                        .toList();
                availableRoomsMatrix[dayIdx][slotIdx] = rooms;
            }
        }

        String campusName = (specializedClass.getCreator() != null && specializedClass.getCreator().getCampus() != null)
                ? specializedClass.getCreator().getCampus().getCampusName() : "Unknown Campus";

        int totalBookedSlots = timetableService.countTotalBookedSlots(classId);
        int remainingTotalSlots = totalRequiredSlots - totalBookedSlots;

        model.addAttribute("totalRequiredSlots", totalRequiredSlots);
        model.addAttribute("totalBookedSlots", totalBookedSlots);
        model.addAttribute("remainingTotalSlots", remainingTotalSlots);

        model.addAttribute("classCreatorCampusName", campusName);
        model.addAttribute("classId", classId);
        model.addAttribute("className", className);
        model.addAttribute("year", year);
        model.addAttribute("week", week);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentWeek", currentWeek);
        model.addAttribute("isPast", isPast);
        model.addAttribute("slots", slots);
        model.addAttribute("dayNames", dayNames);
        model.addAttribute("dayLabels", dayLabels);
        model.addAttribute("weekLabels", weekLabels);
        model.addAttribute("bookedTimetableMatrix", bookedMatrix);
        model.addAttribute("availableRoomsMatrix", availableRoomsMatrix);
        model.addAttribute("bookedSlotsInWeek", bookedSlotsInWeek);
        model.addAttribute("remainingSlots", remainingSlots);
        model.addAttribute("isFullyScheduled", isFullyScheduled);

        return "SpecializedTimetable";
    }

    @PostMapping("/specialized-timetable/save-all")
    public String saveAllTimetable(
            @RequestParam Integer year,
            @RequestParam Integer week,
            @RequestParam Map<String, String> allParams,
            @SessionAttribute("selectedSpecializedClassId") String classId,
            RedirectAttributes redirectAttributes) {

        LocalDate now = LocalDate.now();
        LocalDateTime nowTime = LocalDateTime.now();
        int currentYear = now.getYear();
        int currentWeek = now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

        if (isPastWeek(year, week, currentYear, currentWeek)) {
            redirectAttributes.addFlashAttribute("error", "Cannot save timetable for past weeks.");
            return redirectUrl(year, week);
        }

        List<LocalDate> weekDates = getWeekDates(year, week);
        int savedCount = 0;
        List<String> errors = new ArrayList<>();
        Set<String> usedRoomSlotWeek = new HashSet<>();

        try {
            Staffs creator = staffsService.getStaff();
            if (creator == null) {
                redirectAttributes.addFlashAttribute("error", "Staff not found.");
                return redirectUrl(year, week);
            }

            SpecializedClasses specializedClass = classesService.getClassById(classId);
            if (specializedClass == null || specializedClass.getCreator() == null || specializedClass.getCreator().getCampus() == null) {
                redirectAttributes.addFlashAttribute("error", "Invalid class or no campus.");
                return redirectUrl(year, week);
            }

            String campusId = specializedClass.getCreator().getCampus().getCampusId();
            if (!creator.getCampus().getCampusId().equals(campusId)) {
                redirectAttributes.addFlashAttribute("error", "You are not authorized to save for this campus.");
                return redirectUrl(year, week);
            }

            int totalRequired = specializedClass.getSlotQuantity() != null ? specializedClass.getSlotQuantity() : 0;
            int currentBooked = timetableService.countBookedSlotsInWeek(classId, week, year, campusId);

            if (currentBooked >= totalRequired) {
                redirectAttributes.addFlashAttribute("error",
                        "Class is fully scheduled (" + currentBooked + "/" + totalRequired + "). Cannot add more.");
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
                LocalDate date = weekDates.get(dayIdx);

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

                    LocalDateTime slotEndTime = date.atTime(slot.getEndTime());
                    if (slotEndTime.isBefore(nowTime)) {
                        String dayShort = dayOfWeek.name().substring(0, 3).toUpperCase();
                        String timeRange = slot.getStartTime() + " - " + slot.getEndTime();
                        errors.add("Cannot book past slot: " + dayShort + " " + timeRange);
                        continue;
                    }

                    String conflictKey = roomId + "|" + slotId + "|" + week + "|" + year;
                    if (usedRoomSlotWeek.contains(conflictKey)) {
                        errors.add("Room " + roomId + " already booked in this week");
                        continue;
                    }

                    Rooms room = roomsService.getRoomById(roomId);
                    if (room == null || !room.getCampus().getCampusId().equals(campusId)) {
                        errors.add("Room not found or not in campus: " + roomId);
                        continue;
                    }

                    if (timetableService.getTimetableByClassSlotDayWeek(classId, campusId, slotId, dayOfWeek, week, year) != null) {
                        errors.add("Class already has schedule in this slot");
                        continue;
                    }

                    SpecializedTimetable timetable = new SpecializedTimetable();
                    timetable.setTimetableId(UUID.randomUUID().toString());
                    timetable.setSpecializedClass(specializedClass);
                    timetable.setRoom(room);
                    timetable.setSlot(slot);
                    timetable.setDayOfWeek(dayOfWeek);
                    timetable.setWeekOfYear(week);
                    timetable.setYear(year);
                    timetable.setCreator(creator);
                    timetableService.saveSpecializedTimetable(timetable, campusId);
                    savedCount++;
                    usedRoomSlotWeek.add(conflictKey);
                }
            }

            if (savedCount > 0) {
                redirectAttributes.addFlashAttribute("success",
                        "Successfully saved " + savedCount + " room(s) in week " + week + "/" + year);
            } else if (errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("info", "No valid rooms selected.");
            }
            if (!errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", String.join("<br>", errors));
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return redirectUrl(year, week);
    }

    @PostMapping("/specialized-timetable/delete")
    public String deleteTimetable(
            @RequestParam String timetableId,
            @RequestParam Integer year,
            @RequestParam Integer week,
            @RequestParam String classId,
            RedirectAttributes ra) {
        try {
            SpecializedTimetable tt = timetableService.getById(timetableId);
            if (tt == null) {
                ra.addFlashAttribute("error", "Schedule not found.");
                return redirectUrl(year, week);
            }
            SpecializedClasses specializedClass = tt.getSpecializedClass();
            String campusId = specializedClass.getCreator().getCampus().getCampusId();
            Staffs currentStaff = staffsService.getStaff();
            if (!currentStaff.getCampus().getCampusId().equals(campusId)) {
                ra.addFlashAttribute("error", "Unauthorized campus.");
                return redirectUrl(year, week);
            }
            timetableService.delete(tt);
            ra.addFlashAttribute("success", "Schedule deleted successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Delete failed: " + e.getMessage());
        }
        return redirectUrl(year, week);
    }

    @GetMapping("/clear-class")
    public String clearClass(HttpSession session) {
        session.removeAttribute("selectedSpecializedClassId");
        return "redirect:/staff-home/specialized-classes-list";
    }

    @GetMapping("/specialized-timetable/save-all")
    public String blockDirectSave(RedirectAttributes ra) {
        ra.addFlashAttribute("error", "Direct access not allowed.");
        return "redirect:/staff-home/specialized-classes-list";
    }

    private String redirectUrl(Integer year, Integer week) {
        String url = "redirect:/staff-home/specialized-classes-list/specialized-timetable";
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

    private boolean isPastWeek(int year, int week, int currentYear, int currentWeek) {
        if (year < currentYear) return true;
        if (year > currentYear) return false;
        return week < currentWeek;
    }
}