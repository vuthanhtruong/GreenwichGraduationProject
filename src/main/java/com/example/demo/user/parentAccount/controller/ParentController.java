package com.example.demo.user.parentAccount.controller;
import com.example.demo.user.parentAccount.service.ParentAccountsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/parent-home")
public class ParentController {
    private final ParentAccountsService parentAccountsService;

    public ParentController(ParentAccountsService parentAccountsService) {
        this.parentAccountsService = parentAccountsService;
    }

    @GetMapping("")
    public String getParentHomeInfo(Model model) {
        model.addAttribute("parent", parentAccountsService.getParent());
        model.addAttribute("students", parentAccountsService.getStudentsByParentId(parentAccountsService.getParent().getId()));
        return "ParentHome";
    }
}
