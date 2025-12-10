package com.example.demo.user.admin.controller;

import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.authenticator.service.AuthenticatorsService;
import com.example.demo.campus.model.Campuses;
import com.example.demo.campus.service.CampusesService;
import com.example.demo.user.admin.model.Admins;
import com.example.demo.user.admin.service.AdminsService;
import com.example.demo.user.person.model.Persons;
import com.example.demo.user.person.service.PersonsService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin-home/colleagues-list")
public class ColleaguesController {

    private static final Logger log = LoggerFactory.getLogger(ColleaguesController.class);

    private final AdminsService adminsService;
    private final PersonsService personsService;
    private final AuthenticatorsService authenticatorsService;
    private final CampusesService campusesService;

    public ColleaguesController(AdminsService adminsService,
                                PersonsService personsService,
                                AuthenticatorsService authenticatorsService,
                                CampusesService campusesService) {
        this.adminsService = adminsService;
        this.personsService = personsService;
        this.authenticatorsService = authenticatorsService;
        this.campusesService = campusesService;
    }

    @GetMapping
    public String listColleagues(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            Model model,
            HttpSession session) {

        if (pageSize == null || pageSize < 1) pageSize = 20;
        session.setAttribute("colleaguePageSize", pageSize);
        session.setAttribute("colleagueCurrentPage", page);

        int firstResult = (page - 1) * pageSize;
        List<Admins> colleagues;
        long total;

        if (keyword != null && !keyword.isBlank() && searchType != null) {
            colleagues = adminsService.searchAdmins(searchType, keyword.trim(), firstResult, pageSize);
            total = adminsService.countSearchResults(searchType, keyword.trim());
        } else {
            colleagues = adminsService.getPaginatedAdmins(firstResult, pageSize);
            total = adminsService.countAdmins();
        }

        int totalPages = (int) Math.ceil((double) total / pageSize);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;

        model.addAttribute("colleagues", colleagues);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalColleagues", total);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("colleague", new Admins());
        model.addAttribute("campuses", campusesService.getCampuses());

        return "ColleaguesList";
    }

    @PostMapping("/search-colleagues")
    public String search(@RequestParam String searchType,
                         @RequestParam String keyword,
                         RedirectAttributes ra) {
        ra.addAttribute("searchType", searchType);
        ra.addAttribute("keyword", keyword);
        ra.addAttribute("page", 1);
        return "redirect:/admin-home/colleagues-list";
    }

    @PostMapping("/add-colleague")
    public String addColleague(
            @ModelAttribute("colleague") Admins colleague,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam("campusId") String campusId,
            Model model,
            RedirectAttributes ra,
            HttpSession session) {

        Map<String, String> errors = new HashMap<>();

        // --- Robust campus lookup: trim, null/empty check, log available campus ids if fail ---
        String rawCampusId = campusId;
        if (rawCampusId != null) rawCampusId = rawCampusId.trim();
        if (rawCampusId == null || rawCampusId.isEmpty()) {
            errors.put("campusId", "Please select a valid campus.");
        } else {
            log.debug("Received campusId param: '{}'", rawCampusId);
            Campuses campus = campusesService.getCampusById(rawCampusId);
            // if your service expects numeric id, try a parse attempt (no harm if not needed)
            if (campus == null) {
                try {
                    Long numeric = Long.parseLong(rawCampusId);
                    // try an overload or convert as needed (adjust if your service uses Long)
                    campus = campusesService.getCampusById(String.valueOf(numeric));
                } catch (NumberFormatException nfe) {
                    // ignore parse failure
                }
            }
            if (campus == null) {
                List<Campuses> all = campusesService.getCampuses();
                log.warn("Campus lookup failed for id='{}'. Available campusIds: {}", rawCampusId,
                        all.stream().map(c -> c.getCampusId()).collect(Collectors.toList()));
                errors.put("campusId", "Please select a valid campus.");
            } else {
                colleague.setCampus(campus); // set before validation
            }
        }

        // Now validate (campus is set if valid)
        errors.putAll(adminsService.validateAdmin(colleague, avatarFile));

        if (!errors.isEmpty()) {
            return showFormWithErrors(colleague, avatarFile, campusId, errors, model, session);
        }

        try {
            String adminId = adminsService.generateAdminId(LocalDate.now());
            colleague.setId(adminId);

            if (avatarFile != null && !avatarFile.isEmpty()) {
                colleague.setAvatar(avatarFile.getBytes());
            } else if (session.getAttribute("tempAvatarData") != null) {
                Object tmp = session.getAttribute("tempAvatarData");
                if (tmp instanceof byte[]) {
                    colleague.setAvatar((byte[]) tmp);
                }
            }

            String password = adminsService.generateRandomPassword(12);
            adminsService.addAdmin(colleague, password);

            // Ensure Person exists before creating Authenticator
            Persons person = personsService.getPersonById(adminId);
            if (person == null) {
                log.error("Person record for adminId {} not found after addAdmin", adminId);
                errors.put("general", "Internal error: created admin but person record missing. Contact administrator.");
                return showFormWithErrors(colleague, avatarFile, campusId, errors, model, session);
            }

            Authenticators auth = new Authenticators();
            auth.setPersonId(adminId);
            auth.setPerson(person);
            auth.setPassword(password);
            authenticatorsService.createAuthenticator(auth);

            session.removeAttribute("tempAvatarData");
            session.removeAttribute("tempAvatarName");

            ra.addFlashAttribute("message", "Colleague added successfully!");
            return "redirect:/admin-home/colleagues-list";

        } catch (Exception e) {
            log.error("Add colleague failed", e);
            errors.put("general", "Error: " + (e.getMessage() != null ? e.getMessage() : "Unexpected error"));
            return showFormWithErrors(colleague, avatarFile, campusId, errors, model, session);
        }
    }

    private String showFormWithErrors(Admins colleague,
                                      MultipartFile avatarFile,
                                      String campusId,
                                      Map<String, String> errors,
                                      Model model,
                                      HttpSession session) {

        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                session.setAttribute("tempAvatarData", avatarFile.getBytes());
                session.setAttribute("tempAvatarName", avatarFile.getOriginalFilename());
            } catch (IOException ignored) {}
        }

        model.addAttribute("openAddOverlay", true);
        model.addAttribute("errors", errors != null ? errors : new HashMap<>());
        model.addAttribute("colleague", colleague != null ? colleague : new Admins());
        model.addAttribute("campusId", campusId);
        model.addAttribute("campuses", campusesService.getCampuses());

        Integer sessionPage = (Integer) session.getAttribute("colleagueCurrentPage");
        Integer sessionSize = (Integer) session.getAttribute("colleaguePageSize");
        int page = sessionPage != null ? sessionPage : 1;
        int size = (sessionSize != null && sessionSize > 0) ? sessionSize : 20;

        model.addAttribute("colleagues", adminsService.getPaginatedAdmins((page - 1) * size, size));
        model.addAttribute("currentPage", page);
        long total = adminsService.countAdmins();
        model.addAttribute("totalPages", Math.max(1, (int) Math.ceil(total / (double) size)));
        model.addAttribute("pageSize", size);
        model.addAttribute("totalColleagues", total);

        return "ColleaguesList";
    }

    @GetMapping("/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> avatar(@PathVariable String id) {
        Admins a = adminsService.getAdminById(id);
        if (a != null) {
            byte[] data = a.getAvatar();
            if (data == null || data.length == 0) {
                // try loaded bytes from helper (Admins#getAvatarBytes)
                try {
                    data = a.getAvatarBytes();
                } catch (Exception e) {
                    log.warn("Failed reading avatar bytes for id {}: {}", id, e.getMessage());
                }
            }
            if (data != null && data.length > 0) {
                MediaType mt = detectImageMediaType(data);
                return ResponseEntity.ok().contentType(mt).body(data);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/delete-colleague")
    public String delete(@RequestParam String partnerId, RedirectAttributes ra) {
        try {
            adminsService.deleteAdmin(partnerId);
            ra.addFlashAttribute("message", "Colleague deleted!");
        } catch (Exception e) {
            log.error("Delete failed for id {}: {}", partnerId, e.getMessage(), e);
            ra.addFlashAttribute("error", "Delete failed: " + e.getMessage());
        }
        return "redirect:/admin-home/colleagues-list";
    }

    // --- helper: quick detection of jpeg/png/gif from byte signature ---
    private MediaType detectImageMediaType(byte[] bytes) {
        if (bytes == null || bytes.length < 8) return MediaType.APPLICATION_OCTET_STREAM;
        // PNG: 89 50 4E 47 0D 0A 1A 0A
        if ((bytes[0] & 0xFF) == 0x89 && (bytes[1] & 0xFF) == 0x50 && (bytes[2] & 0xFF) == 0x4E) {
            return MediaType.IMAGE_PNG;
        }
        // JPEG: FF D8 FF
        if ((bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8 && (bytes[2] & 0xFF) == 0xFF) {
            return MediaType.IMAGE_JPEG;
        }
        // GIF: 47 49 46 38
        if ((bytes[0] & 0xFF) == 0x47 && (bytes[1] & 0xFF) == 0x49 && (bytes[2] & 0xFF) == 0x46) {
            return MediaType.IMAGE_GIF;
        }
        // fallback
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
