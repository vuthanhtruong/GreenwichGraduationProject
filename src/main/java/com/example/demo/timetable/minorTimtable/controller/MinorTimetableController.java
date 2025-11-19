// src/main/java/com/example/demo/timetable/minorTimetable/controller/MinorTimetableController.java
package com.example.demo.timetable.minorTimtable.controller;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.minorClasses.service.MinorClassesService;
import com.example.demo.entity.Enums.DaysOfWeek;
import com.example.demo.room.model.Rooms;
import com.example.demo.room.service.RoomsService;
import com.example.demo.timetable.majorTimetable.model.Slots;
import com.example.demo.timetable.majorTimetable.service.SlotsService;
import com.example.demo.timetable.minorTimtable.model.MinorTimetable;
import com.example.demo.timetable.minorTimtable.service.MinorTimetableService;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
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
@RequestMapping("/deputy-staff-home/minor-classes-list")
@RequiredArgsConstructor
public class MinorTimetableController {

    private final MinorTimetableService minorTimetableService;
    private final SlotsService slotsService;
    private final DeputyStaffsService deputyStaffsService;
    private final MinorClassesService minorClassesService;
    private final RoomsService roomsService;

    // === MỞ LỊCH TỪ DANH SÁCH LỚP ===
    @PostMapping("/minor-timetable")
    public String openTimetableFromList(
            @RequestParam String classId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer week,
            HttpSession session,
            RedirectAttributes ra) {

        if (classId == null || classId.isBlank()) {
            ra.addFlashAttribute("error", "Please select a class.");
            return "redirect:/deputy-staff-home/minor-classes-list";
        }

        session.setAttribute("selectedMinorClassId", classId);

        String url = "redirect:/deputy-staff-home/minor-classes-list/minor-timetable";
        if (year != null) url += "?year=" + year + (week != null ? "&week=" + week : "");
        else if (week != null) url += "?week=" + week;
        return url;
    }

    // === HIỂN THỊ LỊCH ===
    @GetMapping("/minor-timetable")
    public String showTimetable(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer week,
            HttpSession session,
            Model model,
            RedirectAttributes ra) {

        String classId = (String) session.getAttribute("selectedMinorClassId");
        if (classId == null || classId.isBlank()) {
            ra.addFlashAttribute("error", "Please select a class first.");
            return "redirect:/deputy-staff-home/minor-classes-list";
        }

        return loadTimetable(classId, year, week, model, ra);
    }

    private String loadTimetable(String classId, Integer inputYear, Integer inputWeek, Model model, RedirectAttributes ra) {
        LocalDate now = LocalDate.now();
        LocalDateTime nowTime = LocalDateTime.now();
        int currentYear = now.getYear();
        int currentWeek = now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int year = (inputYear != null) ? inputYear : currentYear;
        int week = (inputWeek != null) ? inputWeek : currentWeek;

        boolean isPast = isPastWeek(year, week, currentYear, currentWeek);

        MinorClasses minorClass = minorClassesService.getClassById(classId);
        if (minorClass == null) {
            ra.addFlashAttribute("error", "Class not found.");
            return "redirect:/deputy-staff-home/minor-classes-list";
        }

        String className = minorClass.getNameClass();
        String campusId = minorClass.getCreator().getCampus().getCampusId();
        if (campusId == null) {
            ra.addFlashAttribute("error", "Class has no campus assigned.");
            return "redirect:/deputy-staff-home/minor-classes-list";
        }

        DeputyStaffs currentStaff = deputyStaffsService.getDeputyStaff();
        if (currentStaff == null || !currentStaff.getCampus().getCampusId().equals(campusId)) {
            ra.addFlashAttribute("error", "You are not authorized to manage this campus.");
            return "redirect:/deputy-staff-home/minor-classes-list";
        }

        List<Slots> slots = slotsService.getSlots();
        if (slots.isEmpty()) {
            model.addAttribute("error", "No time slots configured.");
            return "MinorTimetable";
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

        List<MinorTimetable> allInWeek = minorTimetableService.getMinorTimetablesByWeekInYear(week, year, campusId);

        MinorTimetable[][] bookedMatrix = new MinorTimetable[7][slots.size()];
        List<String>[][] availableRoomsMatrix = new List[7][slots.size()];

        int bookedSlotsInWeek = 0;
        for (MinorTimetable t : allInWeek) {
            if (t.getClassId().equals(classId)) {
                int dayIdx = t.getDayOfWeek().ordinal();
                int slotIdx = slots.indexOf(t.getSlot());
                if (slotIdx >= 0 && dayIdx < 7) {
                    bookedMatrix[dayIdx][slotIdx] = t;
                    bookedSlotsInWeek++;
                }
            }
        }

        int totalRequiredSlots = minorClass.getSlotQuantity() != null ? minorClass.getSlotQuantity() : 0;
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

                List<String> rooms = minorTimetableService
                        .getAvailableRoomsForSlot(classId, campusId, slots.get(slotIdx), day, week, year)
                        .stream()
                        .map(Rooms::getRoomId)
                        .sorted()
                        .toList();
                availableRoomsMatrix[dayIdx][slotIdx] = rooms;
            }
        }

        String campusName = minorClass.getCreator().getCampus().getCampusName();

        int totalBookedSlots = minorTimetableService.countTotalBookedSlots(classId);
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

        return "MinorTimetable";
    }

    @PostMapping("/minor-timetable/save-all")
    public String saveAll(
            @RequestParam Integer year,
            @RequestParam Integer week,
            @RequestParam Map<String, String> allParams,
            HttpSession session,
            RedirectAttributes ra) {

        String classId = (String) session.getAttribute("selectedMinorClassId");
        if (classId == null || classId.isBlank()) {
            ra.addFlashAttribute("error", "No class selected.");
            return "redirect:/deputy-staff-home/minor-classes-list";
        }

        LocalDate now = LocalDate.now();
        LocalDateTime nowTime = LocalDateTime.now();
        int currentYear = now.getYear();
        int currentWeek = now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

        if (isPastWeek(year, week, currentYear, currentWeek)) {
            ra.addFlashAttribute("error", "Cannot save timetable for past weeks.");
            return redirectUrl(year, week);
        }

        try {
            DeputyStaffs creator = deputyStaffsService.getDeputyStaff();
            if (creator == null) {
                ra.addFlashAttribute("error", "Deputy staff not found.");
                return redirectUrl(year, week);
            }

            MinorClasses minorClass = minorClassesService.getClassById(classId);
            if (minorClass == null || minorClass.getCreator() == null || minorClass.getCreator().getCampus() == null) {
                ra.addFlashAttribute("error", "Invalid class or no campus.");
                return redirectUrl(year, week);
            }

            String campusId = minorClass.getCreator().getCampus().getCampusId();
            if (!creator.getCampus().getCampusId().equals(campusId)) {
                ra.addFlashAttribute("error", "You are not authorized to save for this campus.");
                return redirectUrl(year, week);
            }

            // === KIỂM TRA TỔNG SLOT YÊU CẦU VÀ ĐÃ ĐẶT ===
            Integer requiredTotalSlots = minorClass.getSlotQuantity();
            if (requiredTotalSlots == null || requiredTotalSlots <= 0) {
                ra.addFlashAttribute("error", "This minor class has no required slot quantity set.");
                return redirectUrl(year, week);
            }

            int alreadyScheduledTotal = minorTimetableService.countTotalBookedSlots(classId);

            if (alreadyScheduledTotal >= requiredTotalSlots) {
                ra.addFlashAttribute("error",
                        "Class <strong>" + minorClass.getNameClass() + "</strong> is already fully scheduled ("
                                + alreadyScheduledTotal + "/" + requiredTotalSlots + " slots). Cannot add more.");
                return redirectUrl(year, week);
            }

            // Còn được phép thêm bao nhiêu slot nữa?
            int canStillAdd = requiredTotalSlots - alreadyScheduledTotal;

            List<LocalDate> weekDates = getWeekDates(year, week);
            int savedCount = 0;
            List<String> errors = new ArrayList<>();
            Set<String> usedRoomSlotWeek = new HashSet<>();

            List<Slots> slots = slotsService.getSlots();
            if (slots.isEmpty()) {
                ra.addFlashAttribute("error", "No time slots configured.");
                return redirectUrl(year, week);
            }

            List<String> dayNames = dayNames();

            for (int dayIdx = 0; dayIdx < 7; dayIdx++) {
                DaysOfWeek dayOfWeek = DaysOfWeek.valueOf(dayNames.get(dayIdx));
                LocalDate date = weekDates.get(dayIdx);

                for (int slotIdx = 0; slotIdx < slots.size(); slotIdx++) {
                    // Dừng ngay nếu đã đủ tổng slot
                    if (savedCount >= canStillAdd) {
                        errors.add("Stopped: Reached maximum required slots (" + requiredTotalSlots + ").");
                        break;
                    }

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
                        errors.add("Cannot book past slot: " + dayOfWeek.name().substring(0, 3) + " " + slot.getStartTime() + "-" + slot.getEndTime());
                        continue;
                    }

                    String conflictKey = roomId + "|" + slotId + "|" + week + "|" + year;
                    if (usedRoomSlotWeek.contains(conflictKey)) {
                        errors.add("Room " + roomId + " already booked in this week.");
                        continue;
                    }

                    Rooms room = roomsService.getRoomById(roomId);
                    if (room == null || !room.getCampus().getCampusId().equals(campusId)) {
                        errors.add("Invalid room: " + roomId);
                        continue;
                    }

                    if (minorTimetableService.getTimetableByClassSlotDayWeek(classId, campusId, slotId, dayOfWeek, week, year) != null) {
                        errors.add("Slot already booked for this class.");
                        continue;
                    }

                    // === LƯU LỊCH ===
                    MinorTimetable timetable = new MinorTimetable();
                    timetable.setTimetableId(UUID.randomUUID().toString());
                    timetable.setMinorClass(minorClass);
                    timetable.setRoom(room);
                    timetable.setSlot(slot);
                    timetable.setDayOfWeek(dayOfWeek);
                    timetable.setWeekOfYear(week);
                    timetable.setYear(year);
                    timetable.setCreator(creator);

                    minorTimetableService.saveMinorTimetable(timetable, campusId);
                    savedCount++;
                    usedRoomSlotWeek.add(conflictKey);
                }

                if (savedCount >= canStillAdd) break; // dừng vòng lặp ngày nếu đủ
            }

            // === THÔNG BÁO KẾT QUẢ ===
            int newTotalScheduled = alreadyScheduledTotal + savedCount;

            if (savedCount > 0) {
                ra.addFlashAttribute("success",
                        "Successfully saved <strong>" + savedCount + "</strong> slot(s) in week " + week + "/" + year + "!<br>" +
                                "Class <strong>" + minorClass.getNameClass() + "</strong>: " + newTotalScheduled + "/" + requiredTotalSlots + " slots completed.");
            } else if (errors.isEmpty()) {
                ra.addFlashAttribute("info", "No valid rooms selected.");
            }

            if (!errors.isEmpty()) {
                ra.addFlashAttribute("error", String.join("<br>", errors));
            }

            // Nếu vừa lưu xong → đủ slot → gợi ý gửi thông báo
            if (newTotalScheduled >= requiredTotalSlots) {
                ra.addFlashAttribute("success",
                        "Class <strong>" + minorClass.getNameClass() + "</strong> is now FULLY SCHEDULED (" +
                                requiredTotalSlots + "/" + requiredTotalSlots + " slots)! You can now send notification to students.");
            }

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Save failed: " + e.getMessage());
        }

        return redirectUrl(year, week);
    }

    // === XÓA LỊCH ===
    @PostMapping("/minor-timetable/delete")
    public String deleteTimetable(
            @RequestParam String timetableId,
            @RequestParam Integer year,
            @RequestParam Integer week,
            RedirectAttributes ra) {
        try {
            MinorTimetable tt = minorTimetableService.getById(timetableId);
            if (tt == null) {
                ra.addFlashAttribute("error", "Schedule not found.");
                return redirectUrl(year, week);
            }
            MinorClasses minorClass = tt.getMinorClass();
            String campusId = minorClass.getCreator().getCampus().getCampusId();
            DeputyStaffs currentStaff = deputyStaffsService.getDeputyStaff();
            if (!currentStaff.getCampus().getCampusId().equals(campusId)) {
                ra.addFlashAttribute("error", "Unauthorized campus.");
                return redirectUrl(year, week);
            }
            minorTimetableService.delete(tt);
            ra.addFlashAttribute("success", "Schedule deleted successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Delete failed: " + e.getMessage());
        }
        return redirectUrl(year, week);
    }

    // === XÓA SESSION ===
    @GetMapping("/clear-class")
    public String clearClass(HttpSession session) {
        session.removeAttribute("selectedMinorClassId");
        return "redirect:/deputy-staff-home/minor-classes-list";
    }

    // === CHẶN TRUY CẬP TRỰC TIẾP ===
    @GetMapping("/minor-timetable/save-all")
    public String blockDirectSave(RedirectAttributes ra) {
        ra.addFlashAttribute("error", "Direct access not allowed.");
        return "redirect:/deputy-staff-home/minor-classes-list";
    }

    // === HELPER ===
    private String redirectUrl(Integer year, Integer week) {
        String url = "redirect:/deputy-staff-home/minor-classes-list/minor-timetable";
        if (year != null) url += "?year=" + year + (week != null ? "&week=" + week : "");
        else if (week != null) url += "?week=" + week;
        return url;
    }

    private List<String> dayNames() {
        return List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY");
    }

    private List<LocalDate> getWeekDates(int year, int week) {
        LocalDate firstDay = LocalDate.of(year, 1, 1)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
                .with(java.time.DayOfWeek.MONDAY);
        return IntStream.range(0, 7).mapToObj(firstDay::plusDays).toList();
    }

    private boolean isPastWeek(int year, int week, int currentYear, int currentWeek) {
        if (year < currentYear) return true;
        if (year > currentYear) return false;
        return week < currentWeek;
    }

    @PostMapping("/minor-timetable/send-notification")
    public String sendMinorScheduleNotification(
            @RequestParam String classId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer week,
            HttpSession session,
            RedirectAttributes ra) {

        try {
            MinorClasses minorClass = minorClassesService.getClassById(classId);
            if (minorClass == null) {
                ra.addFlashAttribute("error", "Minor class not found.");
                return "redirect:/deputy-staff-home/minor-classes-list/minor-timetable";
            }

            // Kiểm tra quyền campus
            DeputyStaffs currentStaff = deputyStaffsService.getDeputyStaff();
            if (currentStaff == null || !currentStaff.getCampus().getCampusId().equals(minorClass.getCreator().getCampus().getCampusId())) {
                ra.addFlashAttribute("error", "You are not authorized to send notifications for this class.");
                return "redirect:/deputy-staff-home/minor-classes-list/minor-timetable";
            }

            // Kiểm tra đủ slot chưa
            Integer requiredSlots = minorClass.getSlotQuantity();
            if (requiredSlots == null || requiredSlots <= 0) {
                ra.addFlashAttribute("error", "This minor class has no required slot quantity set.");
                return "redirect:/deputy-staff-home/minor-classes-list/minor-timetable";
            }

            int totalScheduled = minorTimetableService.countTotalBookedSlots(classId); // tổng lịch đã xếp

            if (totalScheduled < requiredSlots) {
                ra.addFlashAttribute("error",
                        "Cannot send notification yet! Minor class <strong>" + minorClass.getNameClass() +
                                "</strong> requires <strong>" + requiredSlots + "</strong> slots, " +
                                "but only <strong>" + totalScheduled + "</strong> are scheduled (" +
                                (requiredSlots - totalScheduled) + " missing).");
                return "redirect:/deputy-staff-home/minor-classes-list/minor-timetable";
            }

            // Đủ slot → gửi thông báo
            minorTimetableService.sendScheduleNotification(classId);

            ra.addFlashAttribute("success",
                    "Minor class schedule notification sent successfully!<br>" +
                            "<strong>" + minorClass.getNameClass() + "</strong> (" + totalScheduled + "/" + requiredSlots + " slots completed)");

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to send notification: " + e.getMessage());
        }

        // Giữ nguyên tuần đang xem
        String redirect = "redirect:/deputy-staff-home/minor-classes-list/minor-timetable";
        if (year != null && week != null) {
            redirect += "?year=" + year + "&week=" + week;
        }
        return redirect;
    }
}