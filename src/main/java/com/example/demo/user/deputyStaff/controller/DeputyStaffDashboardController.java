package com.example.demo.user.deputyStaff.controller;

import com.example.demo.classes.minorClasses.service.MinorClassesService;
import com.example.demo.lecturers_Classes.minorLecturers_MinorClasses.service.MinorLecturers_MinorClassesService;
import com.example.demo.subject.minorSubject.service.MinorSubjectsService;
import com.example.demo.timetable.minorTimtable.service.MinorTimetableService;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import com.example.demo.user.minorLecturer.service.MinorLecturersService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.temporal.IsoFields;

@Controller
@RequestMapping("/deputy-staff-home")
public class DeputyStaffDashboardController {

    private final DeputyStaffsService deputyStaffsService;
    private final MinorLecturersService minorLecturersService;
    private final MinorClassesService minorClassesService;
    private final MinorSubjectsService minorSubjectsService;
    private final MinorLecturers_MinorClassesService minorLecturersClassesService;
    private final MinorTimetableService minorTimetableService;

    public DeputyStaffDashboardController(
            DeputyStaffsService deputyStaffsService,
            MinorLecturersService minorLecturersService,
            MinorClassesService minorClassesService,
            MinorSubjectsService minorSubjectsService,
            MinorLecturers_MinorClassesService minorLecturersClassesService,
            MinorTimetableService minorTimetableService) {
        this.deputyStaffsService = deputyStaffsService;
        this.minorLecturersService = minorLecturersService;
        this.minorClassesService = minorClassesService;
        this.minorSubjectsService = minorSubjectsService;
        this.minorLecturersClassesService = minorLecturersClassesService;
        this.minorTimetableService = minorTimetableService;
    }

    @GetMapping("/dashboard")
    public String deputyDashboard(Model model) {
        DeputyStaffs currentDeputy = deputyStaffsService.getDeputyStaff();
        if (currentDeputy == null || currentDeputy.getCampus() == null) {
            model.addAttribute("error", "Unable to load deputy staff information. Please login again.");
            return "error";
        }

        // === BIẾN CẦN THIẾT ===
        String campusId = currentDeputy.getCampus().getCampusId();
        String campusName = currentDeputy.getCampus().getCampusName();

        LocalDate today = LocalDate.now();
        int currentWeek = today.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int currentYear = today.getYear();

        // Đẩy lên model
        model.addAttribute("campusName", campusName);
        model.addAttribute("currentWeek", currentWeek);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("today", today);

        // ==================== 1. MINOR LECTURERS ====================
        model.addAttribute("totalMinorLecturers", minorLecturersService.totalMinorLecturersInMyCampus());
        model.addAttribute("newMinorThisYear", minorLecturersService.newMinorLecturersThisYearInMyCampus());
        model.addAttribute("minorByGender", minorLecturersService.minorLecturersByGenderInMyCampus());
        model.addAttribute("minorByAgeGroup", minorLecturersService.minorLecturersByAgeGroupInMyCampus());
        model.addAttribute("top5SeniorMinor", minorLecturersService.top5MostExperiencedMinorLecturersInMyCampus());

        // ==================== 2. MINOR CLASSES ====================
        model.addAttribute("totalMinorClasses", minorClassesService.totalMinorClassesInMyCampus());
        model.addAttribute("totalSlots", minorClassesService.totalSlotsInMyCampus());
        model.addAttribute("occupiedSlots", minorClassesService.totalOccupiedSlotsInMyCampus());
        model.addAttribute("averageClassSize", String.format("%.1f", minorClassesService.averageClassSizeInMyCampus()));
        model.addAttribute("minorClassesBySubject", minorClassesService.minorClassesBySubjectInMyCampus());
        model.addAttribute("top5LargestMinorClasses", minorClassesService.top5LargestClassesInMyCampus());

        long unscheduledMinor = minorClassesService.unscheduledMinorClassesCount();
        model.addAttribute("unscheduledMinorClasses", unscheduledMinor);
        model.addAttribute("hasUnscheduledMinor", unscheduledMinor > 0);

        // ==================== 3. MINOR SUBJECTS ====================
        model.addAttribute("totalMinorSubjects", minorSubjectsService.totalMinorSubjectsInMyCampus());
        model.addAttribute("minorSubjectsThisSemester", minorSubjectsService.minorSubjectsThisSemester());
        model.addAttribute("minorSubjectsBySemester", minorSubjectsService.minorSubjectsBySemester());
        model.addAttribute("top5MostUsedMinorSubjects", minorSubjectsService.top5MostUsedMinorSubjects());

        // ==================== 4. PHÂN CÔNG GIẢNG VIÊN MINOR ====================
        model.addAttribute("minorLecturersTeachingCount", minorLecturersClassesService.countLecturersTeachingMinorClasses());

        long minorNoLecturer = minorLecturersClassesService.countMinorClassesWithoutLecturer();
        model.addAttribute("minorClassesWithoutLecturer", minorNoLecturer);
        model.addAttribute("hasMinorClassesWithoutLecturer", minorNoLecturer > 0);

        model.addAttribute("minorTop5BusyLecturers", minorLecturersClassesService.getTop5LecturersByMinorClassCount());
        model.addAttribute("minorTop5ClassesMostLecturers", minorLecturersClassesService.getTop5MinorClassesWithMostLecturers());
        model.addAttribute("minorTop5LecturersLightestLoad", minorLecturersClassesService.getTop5LecturersWithFewestMinorClasses());

        // ==================== 5. MINOR TIMETABLE (LỊCH HỌC TUẦN NÀY) ====================
        Object[] minorSummary = minorTimetableService.getDashboardSummaryMinor(campusId, currentWeek, currentYear);
        model.addAttribute("minorTotalClasses", minorSummary[0]);
        model.addAttribute("minorTotalSlots", minorSummary[1]);
        model.addAttribute("minorRoomUtilizationRate", minorSummary[2]);

        model.addAttribute("minorTop5BusyLecturersTimetable", minorTimetableService.getTop5BusyLecturersMinor(campusId, currentWeek, currentYear));
        model.addAttribute("minorSlotsPerDay", minorTimetableService.getSlotsPerDayOfWeekMinor(campusId, currentWeek, currentYear));
        model.addAttribute("minorTop5UsedRooms", minorTimetableService.getTop5UsedRoomsMinor(campusId, currentWeek, currentYear));

        long minorUnscheduledTimetable = minorTimetableService.getUnscheduledMinorClassesCount(campusId, currentWeek, currentYear);
        model.addAttribute("minorUnscheduledTimetable", minorUnscheduledTimetable);
        model.addAttribute("hasMinorUnscheduledTimetable", minorUnscheduledTimetable > 0);

        return "DeputyDashboard"; // File: src/main/resources/templates/DeputyDashboard.html
    }
}