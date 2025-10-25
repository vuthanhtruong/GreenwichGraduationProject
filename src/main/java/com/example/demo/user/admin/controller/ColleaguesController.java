// File: ColleaguesController.java
package com.example.demo.user.admin.controller;

import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.authenticator.service.AuthenticatorsService;
import com.example.demo.user.admin.model.Admins;
import com.example.demo.user.admin.service.AdminsService;
import com.example.demo.user.person.service.PersonsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/admin-home/colleagues-list")
public class ColleaguesController {

    private final AdminsService adminsService;
    private final PersonsService personsService;
    private final AuthenticatorsService authenticatorsService;

    public ColleaguesController(AdminsService adminsService,
                                PersonsService personsService,
                                AuthenticatorsService authenticatorsService) {
        this.adminsService = adminsService;
        this.personsService = personsService;
        this.authenticatorsService = authenticatorsService;
    }

    // =============================
    // 1. LIST + PAGINATION + SEARCH
    // =============================
    @GetMapping
    public String listColleagues(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword) {

        try {
            // Page Size
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("colleaguePageSize");
                if (pageSize == null) pageSize = 20;
            }
            session.setAttribute("colleaguePageSize", pageSize);

            // Search or List
            List<Admins> colleagues = new ArrayList<>();
            int totalColleagues = 0;

            if (keyword != null && !keyword.trim().isEmpty() && searchType != null) {
                switch (searchType) {
                    case "id" -> {
                        Admins admin = adminsService.getAdminById(keyword.trim());
                        if (admin != null) colleagues.add(admin);
                    }
                    case "name" -> colleagues = searchByName(keyword.trim());
                }
                totalColleagues = colleagues.size();
            } else {
                colleagues = adminsService.getAdmins();
                totalColleagues = colleagues.size();
            }

            // Pagination
            int totalPages = Math.max(1, (int) Math.ceil((double) totalColleagues / pageSize));
            page = Math.max(1, Math.min(page, totalPages));
            int from = (page - 1) * pageSize;
            int to = Math.min(from + pageSize, totalColleagues);
            List<Admins> paginated = colleagues.subList(from, to);

            // Save to session
            session.setAttribute("colleaguePage", page);
            session.setAttribute("colleagueTotalPages", totalPages);

            // Model
            model.addAttribute("colleagues", paginated);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalColleagues", totalColleagues);
            model.addAttribute("searchType", searchType);
            model.addAttribute("keyword", keyword);
            model.addAttribute("colleague",new Admins());

            return "ColleaguesList";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading colleagues: " + e.getMessage());
            return "ColleaguesList";
        }
    }

    private List<Admins> searchByName(String name) {
        return adminsService.getAdmins().stream()
                .filter(a -> (a.getFirstName() + " " + a.getLastName()).toLowerCase()
                        .contains(name.toLowerCase()))
                .toList();
    }

    // =============================
    // 2. SEARCH POST
    // =============================
    @PostMapping("/search-colleagues")
    public String searchColleagues(
            @RequestParam String searchType,
            @RequestParam String keyword,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("searchType", searchType);
        redirectAttributes.addAttribute("keyword", keyword);
        return "redirect:/admin-home/colleagues-list";
    }

    // =============================
    // 3. ADD COLLEAGUE
    // =============================
    @PostMapping("/add-colleague")
    public String addColleague(
            @Valid @ModelAttribute("colleague") Admins colleague,
            BindingResult result,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        Map<String, String> errors = adminsService.validateAdmin(colleague, avatarFile);
        if (!errors.isEmpty() || result.hasErrors()) {
            errors.forEach((k, v) -> result.rejectValue(k, "", v));
            redirectAttributes.addFlashAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("colleague", colleague);
            redirectAttributes.addFlashAttribute("openAddOverlay", true);
            if (avatarFile != null && !avatarFile.isEmpty()) {
                try {
                    session.setAttribute("tempAvatarData", avatarFile.getBytes());
                    session.setAttribute("tempAvatarName", avatarFile.getOriginalFilename());
                } catch (IOException e) {
                    // ignore
                }
            }
            return "redirect:/admin-home/colleagues-list";
        }

        try {
            // Generate ID
            String id = generateAdminId( LocalDate.now());
            colleague.setId(id);

            // Avatar
            if (avatarFile != null && !avatarFile.isEmpty()) {
                colleague.setAvatar(avatarFile.getBytes());
            }

            // Random password
            String randomPassword = generateRandomPassword(12);

            // Save Admin
            adminsService.addAdmin(colleague, randomPassword);  // You need this method in service

            // Create Authenticator
            Authenticators auth = new Authenticators();
            auth.setPersonId(id);
            auth.setPerson(personsService.getPersonById(id));
            auth.setPassword(randomPassword);
            authenticatorsService.createAuthenticator(auth);

            // Clean session
            session.removeAttribute("tempAvatarData");
            session.removeAttribute("tempAvatarName");

            redirectAttributes.addFlashAttribute("message", "Colleague added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add colleague: " + e.getMessage());
        }
        return "redirect:/admin-home/colleagues-list";
    }

    // =============================
    // 4. EDIT FORM
    // =============================
    @PostMapping("/edit-colleague-form")
    public String showEditForm(@RequestParam String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Admins colleague = adminsService.getAdminById(id);
            if (colleague == null) {
                redirectAttributes.addFlashAttribute("error", "Colleague not found");
                return "redirect:/admin-home/colleagues-list";
            }
            model.addAttribute("editColleague", colleague);
            model.addAttribute("openEditOverlay", true);
            return "redirect:/admin-home/colleagues-list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/admin-home/colleagues-list";
        }
    }

    // =============================
    // 5. UPDATE COLLEAGUE
    // =============================
    @PostMapping("/update-colleague")
    public String updateColleague(
            @Valid @ModelAttribute("editColleague") Admins colleague,
            BindingResult result,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        Map<String, String> errors = adminsService.validateAdmin(colleague, avatarFile);
        if (!errors.isEmpty() || result.hasErrors()) {
            errors.forEach((k, v) -> result.rejectValue(k, "", v));
            redirectAttributes.addFlashAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("editColleague", colleague);
            redirectAttributes.addFlashAttribute("openEditOverlay", true);
            return "redirect:/admin-home/colleagues-list";
        }

        try {
            adminsService.editAdmin(colleague, avatarFile);
            redirectAttributes.addFlashAttribute("message", "Colleague updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update: " + e.getMessage());
        }
        return "redirect:/admin-home/colleagues-list";
    }

    @GetMapping("/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getAvatar(@PathVariable String id) {
        Admins admin = adminsService.getAdminById(id);
        if (admin != null && admin.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(admin.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }

    // =============================
    // UTILS
    // =============================
    private String generateAdminId(LocalDate date) {
        String prefix = "ADM" + date.getYear() % 100;
        int count = (int) adminsService.getAdmins().stream()
                .filter(a -> a.getId().startsWith(prefix))
                .count() + 1;
        return prefix + String.format("%04d", count);
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}