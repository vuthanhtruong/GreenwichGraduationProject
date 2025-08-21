package com.example.demo.majorstaff.controller;
import com.example.demo.majorstaff.service.StaffsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/staff-home")
public class StaffsController {

    private final StaffsService staffsService;

    public StaffsController(StaffsService staffsService) {
        this.staffsService = staffsService;
    }
    @GetMapping("")
    public String getStaffHomeInfo(Model model) {
        model.addAttribute("major", staffsService.getStaffMajor().getMajorName());
        model.addAttribute("staff", staffsService.getStaff());
        return "StaffHome";
    }
}
