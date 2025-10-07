package com.example.demo.campus.controller;

import com.example.demo.admin.service.AdminsService;
import com.example.demo.campus.model.Campuses;
import com.example.demo.campus.service.CampusesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin-home/campuses-list")
public class ListCampusesController {

    private static final Logger logger = LoggerFactory.getLogger(ListCampusesController.class);

    private final CampusesService campusesService;

    public ListCampusesController(CampusesService campusesService) {
        this.campusesService = campusesService;
    }

    @GetMapping("")
    public String listCampuses(Model model) {
        List<Campuses> campuses = campusesService.getCampuses();
        Map<String, Map<String, Long>> campusCounts = campusesService.getCampusCounts();

        model.addAttribute("campus", new Campuses());
        model.addAttribute("campuses", campuses);
        model.addAttribute("campusCounts", campusCounts);

        return "ListCampuses";
    }

    @GetMapping("/avatar/{campusId}")
    @ResponseBody
    public ResponseEntity<byte[]> getCampusAvatar(@PathVariable String campusId) {
        Campuses campus = campusesService.getCampusById(campusId);
        if (campus != null && campus.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // Có thể đổi thành campus.getAvatarType() nếu lưu metadata
                    .body(campus.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}
