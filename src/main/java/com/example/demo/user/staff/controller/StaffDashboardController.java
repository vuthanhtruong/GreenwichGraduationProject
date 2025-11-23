package com.example.demo.user.staff.controller;

import com.example.demo.attendance.majorAttendance.service.MajorAttendanceService;
import com.example.demo.attendance.specializedAttendance.service.SpecializedAttendanceService;
import com.example.demo.classes.majorClasses.service.MajorClassesService;
import com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.service.MajorLecturers_MajorClassesService;
import com.example.demo.lecturers_Classes.majorLecturers_SpecializedClasses.service.MajorLecturers_SpecializedClassesService;
import com.example.demo.subject.majorSubject.service.MajorSubjectsService;
import com.example.demo.subject.specializedSubject.service.SpecializedSubjectsService;
import com.example.demo.timetable.majorTimetable.service.MajorTimetableService;
import com.example.demo.timetable.specializedTimetable.service.SpecializedTimetableService;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.user.student.service.StudentsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.temporal.IsoFields;

@Controller
@RequestMapping("/staff-home")
public class StaffDashboardController {

    private final StudentsService studentsService;
    private final StaffsService staffsService;
    private final MajorLecturersService majorLecturersService;
    private final MajorSubjectsService majorSubjectsService;
    private final SpecializedSubjectsService specializedSubjectsService;
    private final MajorClassesService majorClassesService;
    private final MajorTimetableService majorTimetableService; // Thêm service mới
    private final SpecializedTimetableService specializedTimetableService;
    private final MajorLecturers_MajorClassesService lecturersClassesService;
    private final MajorLecturers_SpecializedClassesService specLecturersClassesService;
    private final SpecializedAttendanceService specializedAttendanceService;
    // Thêm dependency
    private final MajorAttendanceService majorAttendanceService;

    public StaffDashboardController(StudentsService studentsService,
                                    StaffsService staffsService,
                                    MajorLecturersService majorLecturersService,
                                    MajorSubjectsService majorSubjectsService,
                                    SpecializedSubjectsService specializedSubjectsService,
                                    MajorClassesService majorClassesService,
                                    MajorTimetableService majorTimetableService, SpecializedTimetableService specializedTimetableService, MajorLecturers_MajorClassesService lecturersClassesService, MajorLecturers_SpecializedClassesService specLecturersClassesService, SpecializedAttendanceService specializedAttendanceService, MajorAttendanceService majorAttendanceService) {
        this.studentsService = studentsService;
        this.staffsService = staffsService;
        this.majorLecturersService = majorLecturersService;
        this.majorSubjectsService = majorSubjectsService;
        this.specializedSubjectsService = specializedSubjectsService;
        this.majorClassesService = majorClassesService;
        this.majorTimetableService = majorTimetableService;
        this.specializedTimetableService = specializedTimetableService;
        this.lecturersClassesService = lecturersClassesService;
        this.specLecturersClassesService = specLecturersClassesService;
        this.specializedAttendanceService = specializedAttendanceService;
        this.majorAttendanceService = majorAttendanceService;
    }
    @GetMapping("/dashboard")
    public String staffDashboard(Model model) {
        Staffs currentStaff = staffsService.getStaff();

        String campusId = currentStaff.getCampus().getCampusId();
        String campusName = currentStaff.getCampus().getCampusName();
        String majorName = currentStaff.getMajorManagement().getMajorName();

        // Lấy tuần và năm hiện tại (hôm nay: 19/11/2025 → tuần 47 năm 2025)
        LocalDate today = LocalDate.now();
        int currentWeek = today.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int currentYear = today.getYear();

        // Thông tin cơ bản
        model.addAttribute("userRole", "STAFF");
        model.addAttribute("campusName", campusName);
        model.addAttribute("majorName", majorName);
        model.addAttribute("currentWeek", currentWeek);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("today", today);

        // ===================================================================
        // 1. THỐNG KÊ SINH VIÊN
        // ===================================================================
        model.addAttribute("totalStudents", studentsService.totalStudentsForCurrentStaff());
        model.addAttribute("newStudents30Days", studentsService.countNewStudentsLast30DaysForCurrentStaff());
        model.addAttribute("monthlyIntake", studentsService.monthlyStudentIntakeThisYearForCurrentStaff());
        model.addAttribute("studentIntakeByYear", studentsService.countStudentsByAdmissionYearForCurrentStaff());
        model.addAttribute("byGender", studentsService.countStudentsByGenderForCurrentStaff());
        model.addAttribute("top5Specializations", studentsService.top5SpecializationsForCurrentStaff());

        // ===================================================================
        // 2. THỐNG KÊ GIẢNG VIÊN CHÍNH NGÀNH
        // ===================================================================
        model.addAttribute("totalMajorLecturers", majorLecturersService.totalMajorLecturersInMyMajor());
        model.addAttribute("newLecturersThisYear", majorLecturersService.newMajorLecturersThisYearInMyMajor());
        model.addAttribute("lecturersByGender", majorLecturersService.majorLecturersByGenderInMyMajor());
        model.addAttribute("lecturersByAgeGroup", majorLecturersService.majorLecturersByAgeGroupInMyMajor());
        model.addAttribute("top5ExperiencedLecturers", majorLecturersService.top5MostExperiencedLecturersInMyMajor());

        // ===================================================================
        // 3. MÔN HỌC CHUNG (MajorSubjects)
        // ===================================================================
        model.addAttribute("totalSubjects", majorSubjectsService.totalSubjectsInMyMajor());
        model.addAttribute("subjectsBySemester", majorSubjectsService.subjectsBySemesterInMyMajor());
        model.addAttribute("subjectsInCurrentSemester", majorSubjectsService.subjectsInCurrentSemesterInMyMajor());
        model.addAttribute("subjectsWithoutCurriculum", majorSubjectsService.subjectsWithoutCurriculumInMyMajor());

        // ===================================================================
        // 4. MÔN HỌC CHUYÊN NGÀNH (SpecializedSubjects)
        // ===================================================================
        model.addAttribute("totalSpecializedSubjects", specializedSubjectsService.totalSpecializedSubjectsInMyMajor());
        model.addAttribute("specializedBySemester", specializedSubjectsService.specializedSubjectsBySemesterInMyMajor());

        // ===================================================================
        // 5. LỚP HỌC CHÍNH NGÀNH (MajorClasses)
        // ===================================================================
        model.addAttribute("totalMajorClasses", majorClassesService.totalMajorClassesInMyMajor());
        model.addAttribute("totalSlots", majorClassesService.totalSlotsInMyMajor());
        model.addAttribute("occupiedSlots", majorClassesService.totalOccupiedSlotsInMyMajor());
        model.addAttribute("averageClassSize", String.format("%.1f", majorClassesService.averageClassSizeInMyMajor()));
        model.addAttribute("fillRate", majorClassesService.totalSlotsInMyMajor() > 0
                ? Math.round((double) majorClassesService.totalOccupiedSlotsInMyMajor() / majorClassesService.totalSlotsInMyMajor() * 100)
                : 0);
        model.addAttribute("top5LargestClasses", majorClassesService.top5LargestClassesInMyMajor());

        // ===================================================================
        // 6. THỐNG KÊ LỊCH HỌC TUẦN HIỆN TẠI (MAJOR TIMETABLE) - SIÊU THỰC TẾ
        // ===================================================================
        Object[] summary = majorTimetableService.getDashboardSummary(campusId, currentWeek, currentYear);
        model.addAttribute("timetableTotalClasses", summary[0]);     // Số lớp có lịch tuần này
        model.addAttribute("timetableTotalSlots", summary[1]);       // Tổng tiết đã xếp
        model.addAttribute("roomUtilizationRate", summary[2]);      // % phòng đang dùng
        model.addAttribute("timetableWeek", summary[3]);
        model.addAttribute("timetableYear", summary[4]);

        // Top 5 giảng viên dạy nhiều nhất tuần này
        model.addAttribute("top5BusyLecturers", majorTimetableService.getTop5BusyLecturers(campusId, currentWeek, currentYear));

        // Biểu đồ cột: số tiết theo ngày (Thứ 2 → Chủ Nhật)
        model.addAttribute("slotsPerDay", majorTimetableService.getSlotsPerDayOfWeek(campusId, currentWeek, currentYear));

        // Top 5 phòng dùng nhiều nhất
        model.addAttribute("top5UsedRooms", majorTimetableService.getTop5UsedRooms(campusId, currentWeek, currentYear));

        // CẢNH BÁO: Số lớp CHƯA xếp lịch trong tuần này → hiển thị đỏ trên dashboard
        int unscheduled = majorTimetableService.getUnscheduledClassesCount(campusId, currentWeek, currentYear);
        model.addAttribute("unscheduledClassesCount", unscheduled);
        model.addAttribute("hasUnscheduled", unscheduled > 0); // để Thymeleaf hiện badge đỏ

        Object[] specSummary = specializedTimetableService.getDashboardSummarySpecialized(campusId, currentWeek, currentYear);
        model.addAttribute("specTotalClasses", specSummary[0]);
        model.addAttribute("specTotalSlots", specSummary[1]);
        model.addAttribute("specRoomUtilizationRate", specSummary[2]); // dùng chung % phòng

        model.addAttribute("specTop5BusyLecturers", specializedTimetableService.getTop5BusyLecturersSpecialized(campusId, currentWeek, currentYear));
        model.addAttribute("specSlotsPerDay", specializedTimetableService.getSlotsPerDayOfWeekSpecialized(campusId, currentWeek, currentYear));
        model.addAttribute("specTop5UsedRooms", specializedTimetableService.getTop5UsedRoomsSpecialized(campusId, currentWeek, currentYear));

        int specUnscheduled = specializedTimetableService.getUnscheduledSpecializedClassesCount(campusId, currentWeek, currentYear);
        model.addAttribute("specUnscheduledClassesCount", specUnscheduled);
        model.addAttribute("hasSpecUnscheduled", specUnscheduled > 0);

        model.addAttribute("lecturersTeachingCount", lecturersClassesService.countLecturersTeachingAtLeastOneClass());
        model.addAttribute("top5LecturersByLoad", lecturersClassesService.getTop5LecturersByClassCount());
        model.addAttribute("classesWithoutLecturer", lecturersClassesService.countMajorClassesWithoutAnyLecturer());
        model.addAttribute("hasClassesWithoutLecturer", lecturersClassesService.countMajorClassesWithoutAnyLecturer() > 0);

        model.addAttribute("top5ClassesMostLecturers", lecturersClassesService.getTop5ClassesWithMostLecturers());
        model.addAttribute("top5LecturersLightestLoad", lecturersClassesService.getTop5LecturersWithFewestClasses()); // Gợi ý phân công

        model.addAttribute("specLecturersTeachingCount", specLecturersClassesService.countLecturersTeachingSpecializedClasses());
        model.addAttribute("specTop5BusyLecturers", specLecturersClassesService.getTop5LecturersBySpecializedClassCount());

        int specNoLecturer = (int) specLecturersClassesService.countSpecializedClassesWithoutLecturer();
        model.addAttribute("specClassesWithoutLecturer", specNoLecturer);
        model.addAttribute("hasSpecClassesWithoutLecturer", specNoLecturer > 0); // badge đỏ

        model.addAttribute("specTop5ClassesMostLecturers", specLecturersClassesService.getTop5SpecializedClassesWithMostLecturers());
        model.addAttribute("specTop5LecturersLightestLoad", specLecturersClassesService.getTop5LecturersWithFewestSpecializedClasses());

        model.addAttribute("majorAttendanceSessions",
                majorAttendanceService.countAttendanceSessionsThisWeek(campusId, currentWeek, currentYear));

        double avgRate = majorAttendanceService.getAverageAttendanceRateThisWeek(campusId, currentWeek, currentYear);
        model.addAttribute("majorAvgAttendanceRate", String.format("%.1f", avgRate) + "%");
        model.addAttribute("majorAttendanceRateColor", avgRate >= 90 ? "success" : avgRate >= 80 ? "warning" : "danger");

        model.addAttribute("majorLowAttendanceClasses",
                majorAttendanceService.getTop5ClassesLowestAttendanceThisWeek(campusId, currentWeek, currentYear));

        long highAbsent = majorAttendanceService.countStudentsWithManyAbsencesThisWeek(campusId, currentWeek, currentYear);
        model.addAttribute("majorHighAbsentStudents", highAbsent);
        model.addAttribute("hasMajorHighAbsent", highAbsent > 0);

        model.addAttribute("specAttendanceSessions",
                specializedAttendanceService.countAttendanceSessionsThisWeek(campusId, currentWeek, currentYear));

        double specAvgRate = specializedAttendanceService.getAverageAttendanceRateThisWeek(campusId, currentWeek, currentYear);
        model.addAttribute("specAvgAttendanceRate", String.format("%.1f", specAvgRate) + "%");
        model.addAttribute("specAttendanceRateColor", specAvgRate >= 90 ? "success" : specAvgRate >= 80 ? "warning" : "danger");

        model.addAttribute("specLowAttendanceClasses",
                specializedAttendanceService.getTop5ClassesLowestAttendanceThisWeek(campusId, currentWeek, currentYear));

        long specHighAbsent = specializedAttendanceService.countStudentsWithManyAbsencesThisWeek(campusId, currentWeek, currentYear);
        model.addAttribute("specHighAbsentStudents", specHighAbsent);
        model.addAttribute("hasSpecHighAbsent", specHighAbsent > 0);

        return "Dashboard"; // hoặc "Dashboard" tùy tên file .html của bạn
    }
}