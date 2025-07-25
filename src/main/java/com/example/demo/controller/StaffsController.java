package com.example.demo.controller;
import com.example.demo.entity.Staffs;
import com.example.demo.service.StaffsService;
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
        model.addAttribute("major", staffsService.getMajors().getMajorName());
        model.addAttribute("staff", staffsService.getStaffs());
        return "StaffHome";
    }
}
