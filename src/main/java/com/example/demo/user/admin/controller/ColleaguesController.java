package com.example.demo.user.admin.controller;

import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.authenticator.service.AuthenticatorsService;
import com.example.demo.user.admin.model.Admins;
import com.example.demo.user.admin.service.AdminsService;
import com.example.demo.user.person.service.PersonsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin-home/colleagues-list")
public class ColleaguesController {

    private static final Logger logger = LoggerFactory.getLogger(ColleaguesController.class);

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
                pageSize = (Integer) session.getAttribute("colleaguePageSize") != null ?
                        (Integer) session.getAttribute("colleaguePageSize") : 20;
            }
            session.setAttribute("colleaguePageSize", pageSize);

            // Search or List
            List<Admins> colleagues;
            long totalColleagues;
            int firstResult = (page - 1) * pageSize;

            if (keyword != null && !keyword.trim().isEmpty() && searchType != null) {
                colleagues = adminsService.searchAdmins(searchType, keyword, firstResult, pageSize);
                totalColleagues = adminsService.countSearchResults(searchType, keyword);
            } else {
                colleagues = adminsService.getPaginatedAdmins(firstResult, pageSize);
                totalColleagues = adminsService.countAdmins();
            }

            // Pagination
            int totalPages = Math.max(1, (int) Math.ceil((double) totalColleagues / pageSize));
            page = Math.max(1, Math.min(page, totalPages));

            // Save to session
            session.setAttribute("colleaguePage", page);
            session.setAttribute("colleagueTotalPages", totalPages);

            // Model
            model.addAttribute("colleagues", colleagues);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalColleagues", totalColleagues);
            model.addAttribute("searchType", searchType);
            model.addAttribute("keyword", keyword);
            model.addAttribute("colleague", new Admins());

            return "ColleaguesList";
        } catch (Exception e) {
            logger.error("Error loading colleagues: {}", e.getMessage(), e);
            model.addAttribute("error", "Error loading colleagues: " + e.getMessage());
            return "ColleaguesList";
        }
    }

    @PostMapping("/search-colleagues")
    public String searchColleagues(
            @RequestParam String searchType,
            @RequestParam String keyword,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("searchType", searchType);
        redirectAttributes.addAttribute("keyword", keyword);
        return "redirect:/admin-home/colleagues-list";
    }

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
                    logger.error("Failed to store avatar temporarily: {}", e.getMessage());
                }
            }
            return "redirect:/admin-home/colleagues-list";
        }

        try {
            // Generate ID
            String id = adminsService.generateAdminId(LocalDate.now());
            colleague.setId(id);

            // Avatar
            if (avatarFile != null && !avatarFile.isEmpty()) {
                colleague.setAvatar(avatarFile.getBytes());
            } else if (session.getAttribute("tempAvatarData") != null) {
                colleague.setAvatar((byte[]) session.getAttribute("tempAvatarData"));
            }

            // Random password
            String randomPassword = adminsService.generateRandomPassword(12);

            // Save Admin
            adminsService.addAdmin(colleague, randomPassword);

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
            logger.error("Failed to add colleague: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Failed to add colleague: " + e.getMessage());
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

    @PostMapping("/delete-colleague/{id}")
    public String deleteColleague(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            adminsService.deleteAdmin(id);
            redirectAttributes.addFlashAttribute("message", "Colleague deleted successfully!");
        } catch (Exception e) {
            logger.error("Failed to delete colleague ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Failed to delete colleague: " + e.getMessage());
        }
        return "redirect:/admin-home/colleagues-list";
    }
}