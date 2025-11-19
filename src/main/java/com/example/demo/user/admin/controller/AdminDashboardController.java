package com.example.demo.user.admin.controller;

import com.example.demo.accountBalance.dao.AccountBalancesDAO;
import com.example.demo.accountBalance.service.AccountBalancesService;
import com.example.demo.user.admin.service.AdminsService;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
import com.example.demo.user.minorLecturer.service.MinorLecturersService;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.user.student.service.StudentsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.temporal.IsoFields;

@Controller
@RequestMapping("/admin-home")
public class AdminDashboardController {

    private final AdminsService adminsService;
    private final DeputyStaffsService deputyStaffsService;
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final MajorLecturersService majorLecturersService;
    private final MinorLecturersService minorLecturersService;
    private final AccountBalancesService accountBalancesService;

    public AdminDashboardController(AdminsService adminsService,
                                    DeputyStaffsService deputyStaffsService,
                                    StaffsService staffsService, StudentsService studentsService, MajorLecturersService majorLecturersService, MinorLecturersService minorLecturersService, AccountBalancesService accountBalancesService) {
        this.adminsService = adminsService;
        this.deputyStaffsService = deputyStaffsService;
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.majorLecturersService = majorLecturersService;
        this.minorLecturersService = minorLecturersService;
        this.accountBalancesService = accountBalancesService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {

        // Security check
        if (adminsService.getAdmin() == null) {
            return "redirect:/login";
        }

        LocalDate today = LocalDate.now();
        int currentWeek = today.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int currentYear = today.getYear();

        model.addAttribute("today", today);
        model.addAttribute("currentWeek", currentWeek);
        model.addAttribute("currentYear", currentYear);

        // ==================== DEPUTY STAFF (Minor Program) ====================
        model.addAttribute("totalDeputyStaffs", deputyStaffsService.totalDeputyStaffs());
        model.addAttribute("newDeputyThisYear", deputyStaffsService.newDeputyStaffsThisYear());
        model.addAttribute("deputyByCampus", deputyStaffsService.deputyStaffsByCampus());
        model.addAttribute("deputyByGender", deputyStaffsService.deputyStaffsByGender());
        model.addAttribute("deputyByAgeGroup", deputyStaffsService.deputyStaffsByAgeGroup());
        model.addAttribute("top5NewestDeputies", deputyStaffsService.top5NewestDeputyStaffs());

        long campusesWithoutDeputy = deputyStaffsService.countCampusesWithoutDeputyStaff();
        model.addAttribute("campusesWithoutDeputy", campusesWithoutDeputy);
        model.addAttribute("hasCampusWithoutDeputy", campusesWithoutDeputy > 0);

        // ==================== MAJOR STAFF (Regular Program Management) ====================
        model.addAttribute("totalStaffs", staffsService.totalStaffsAllCampus());
        model.addAttribute("newStaffThisYear", staffsService.newStaffsThisYearAllCampus());
        model.addAttribute("staffByCampus", staffsService.staffsByCampus());
        model.addAttribute("staffByMajor", staffsService.staffsByMajor());
        model.addAttribute("staffByGender", staffsService.staffsByGender());
        model.addAttribute("staffByAgeGroup", staffsService.staffsByAgeGroup());
        model.addAttribute("top5NewestStaffs", staffsService.top5NewestStaffs());

        long majorsWithoutStaff = staffsService.countMajorsWithoutStaff();
        model.addAttribute("majorsWithoutStaff", majorsWithoutStaff);
        model.addAttribute("hasMajorWithoutStaff", majorsWithoutStaff > 0);

        model.addAttribute("totalStudents", studentsService.totalStudentsAllCampus());
        model.addAttribute("newStudentsThisYear", studentsService.newStudentsThisYearAllCampus());

        model.addAttribute("studentsByCampus", studentsService.studentsByCampus());
        model.addAttribute("studentsByMajor", studentsService.studentsByMajor());
        model.addAttribute("studentsBySpecialization", studentsService.studentsBySpecialization());
        model.addAttribute("studentsByGender", studentsService.studentsByGender());
        model.addAttribute("studentsByAdmissionYear", studentsService.studentsByAdmissionYear());
        model.addAttribute("studentsByAgeGroup", studentsService.studentsByAgeGroup());
        model.addAttribute("top10NewestStudents", studentsService.top10NewestStudents());

        long campusesWithoutStudents = studentsService.countCampusesWithoutStudents();
        model.addAttribute("campusesWithoutStudents", campusesWithoutStudents);
        model.addAttribute("hasCampusWithoutStudents", campusesWithoutStudents > 0);

        model.addAttribute("totalMajorLecturers", majorLecturersService.totalMajorLecturersAllCampus());
        model.addAttribute("newMajorLecturersThisYear", majorLecturersService.newMajorLecturersThisYearAllCampus());

        model.addAttribute("majorLecturersByCampus", majorLecturersService.majorLecturersByCampus());
        model.addAttribute("majorLecturersByMajor", majorLecturersService.majorLecturersByMajor());
        model.addAttribute("majorLecturersByGender", majorLecturersService.majorLecturersByGender());
        model.addAttribute("majorLecturersByAgeGroup", majorLecturersService.majorLecturersByAgeGroup());
        model.addAttribute("top5NewestLecturers", majorLecturersService.top5NewestMajorLecturers());
        model.addAttribute("top5SeniorLecturers", majorLecturersService.top5MostSeniorMajorLecturers());

        long majorsWithoutLecturer = majorLecturersService.countMajorsWithoutMajorLecturer();
        model.addAttribute("majorsWithoutLecturer", majorsWithoutLecturer);
        model.addAttribute("hasMajorWithoutLecturer", majorsWithoutLecturer > 0); // red badge

        // ==================== MINOR LECTURERS (Part-time / Visiting) ====================
        model.addAttribute("totalMinorLecturers", minorLecturersService.totalMinorLecturersAllCampus());
        model.addAttribute("newMinorLecturersThisYear", minorLecturersService.newMinorLecturersThisYearAllCampus());

        model.addAttribute("minorLecturersByCampus", minorLecturersService.minorLecturersByCampus());
        model.addAttribute("minorLecturersByGender", minorLecturersService.minorLecturersByGender());
        model.addAttribute("minorLecturersByAgeGroup", minorLecturersService.minorLecturersByAgeGroup());
        model.addAttribute("top5NewestMinorLecturers", minorLecturersService.top5NewestMinorLecturers());
        model.addAttribute("top5SeniorMinorLecturers", minorLecturersService.top5MostSeniorMinorLecturers());

        long campusesWithoutMinor = minorLecturersService.countCampusesWithoutMinorLecturer();
        model.addAttribute("campusesWithoutMinorLecturer", campusesWithoutMinor);
        model.addAttribute("hasCampusWithoutMinorLecturer", campusesWithoutMinor > 0);

        // ==================== ACCOUNT BALANCE OVERVIEW ====================
        model.addAttribute("totalStudentsWithAccount", accountBalancesService.totalStudentsWithAccount());
        model.addAttribute("totalBalanceAll", accountBalancesService.totalBalanceAllStudents());
        model.addAttribute("averageBalance", String.format("%,.0f VND", accountBalancesService.averageBalance()));

        model.addAttribute("studentsWithZeroBalance", accountBalancesService.countStudentsWithZeroBalance());
        model.addAttribute("studentsWithNegativeBalance", accountBalancesService.countStudentsWithNegativeBalance());
        model.addAttribute("hasNegativeBalance", accountBalancesService.countStudentsWithNegativeBalance() > 0);

        model.addAttribute("balanceDistribution", accountBalancesService.balanceDistribution());
        model.addAttribute("top10HighestBalance", accountBalancesService.top10HighestBalanceStudents());
        model.addAttribute("top10LowestBalance", accountBalancesService.top10LowestBalanceStudents());// red badge

        return "AdminDashboard";
    }
}