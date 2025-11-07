package com.example.demo.supportTickets.controller;

import com.example.demo.supportTickets.model.SupportTicketDocuments;
import com.example.demo.supportTickets.model.SupportTickets;
import com.example.demo.supportTickets.service.SupportTicketDocumentsService;
import com.example.demo.supportTickets.service.SupportTicketsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin-home/support-tickets")
public class SupportTicketsController {

    private final SupportTicketsService ticketService;
    private final SupportTicketDocumentsService docService;

    public SupportTicketsController(SupportTicketsService ticketService, SupportTicketDocumentsService docService) {
        this.ticketService = ticketService;
        this.docService = docService;
    }

    @GetMapping
    public String listTickets(Model model, HttpSession session,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(required = false) Integer pageSize,
                              @RequestParam(required = false) String searchType,
                              @RequestParam(required = false) String keyword) {

        if (pageSize == null || pageSize <= 0) pageSize = (Integer) session.getAttribute("ticketPageSize");
        if (pageSize == null) pageSize = 20;
        pageSize = Math.min(pageSize, 100);
        session.setAttribute("ticketPageSize", pageSize);

        int firstResult = (page - 1) * pageSize;

        List<SupportTickets> tickets;
        long total;

        if (keyword != null && !keyword.trim().isEmpty() && searchType != null) {
            tickets = ticketService.searchTickets(searchType, keyword.trim(), firstResult, pageSize);
            total = ticketService.countSearchResults(searchType, keyword.trim());
        } else {
            tickets = ticketService.getPaginatedTickets(firstResult, pageSize);
            total = ticketService.numberOfTickets();
        }

        int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));
        page = Math.max(1, Math.min(page, totalPages));

        session.setAttribute("ticketPage", page);
        session.setAttribute("ticketTotalPages", totalPages);

        // FIX LỖI #numbers.sequence – TÍNH TRƯỚC TRONG JAVA
        int startPage = Math.max(1, page - 2);
        int endPage = Math.min(totalPages, page + 2);
        java.util.List<Integer> pageNumbers = new java.util.ArrayList<>();
        for (int i = startPage; i <= endPage; i++) {
            pageNumbers.add(i);
        }

        model.addAttribute("tickets", tickets);
        model.addAttribute("ticket", new SupportTickets());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalTickets", total);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("docService", docService);
        model.addAttribute("pageNumbers", pageNumbers); // THÊM DÒNG NÀY

        return "SupportTicketsList";
    }

    // ==================== 2. ADD TICKET + UPLOAD FILES ====================
    @PostMapping("/add-ticket")
    public String addTicket(@Valid @ModelAttribute("ticket") SupportTickets ticket,
                            @RequestParam(required = false) List<MultipartFile> files,
                            Model model, RedirectAttributes ra, HttpSession session) {

        Map<String, String> errors = ticketService.validateTicket(ticket);

        if (files != null && !files.isEmpty()) {
            for (MultipartFile f : files) {
                if (f.isEmpty()) continue;
                try {
                    SupportTicketDocuments temp = new SupportTicketDocuments(
                            f.getOriginalFilename(), f.getContentType(), f.getSize(), f.getBytes(), ticket
                    );
                    Map<String, String> fileErrors = docService.validateDocument(temp);
                    if (!fileErrors.isEmpty()) {
                        errors.put("files", fileErrors.values().iterator().next());
                        break;
                    }
                } catch (IOException e) {
                    errors.put("files", "Cannot read file: " + f.getOriginalFilename());
                    break;
                }
            }
        }

        if (!errors.isEmpty()) {
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("ticket", ticket);
            return loadListPage(model, session);
        }

        SupportTickets saved = ticketService.addTicket(ticket);

        if (files != null && !files.isEmpty()) {
            int uploaded = 0;
            for (MultipartFile f : files) {
                if (f.isEmpty()) continue;
                try {
                    SupportTicketDocuments doc = new SupportTicketDocuments(
                            f.getOriginalFilename(), f.getContentType(), f.getSize(), f.getBytes(), saved
                    );
                    docService.saveDocument(doc);
                    uploaded++;
                } catch (Exception e) {
                    ra.addFlashAttribute("error", "Failed: " + f.getOriginalFilename());
                }
            }
            ra.addFlashAttribute("message", "Ticket created + " + uploaded + " file(s) attached!");
        } else {
            ra.addFlashAttribute("message", "Ticket created successfully!");
        }

        return "redirect:/admin-home/support-tickets";
    }

    // ==================== 3. SHOW EDIT FORM ====================
    @PostMapping("/edit")
    public String showEdit(@RequestParam String id, Model model, HttpSession session) {
        SupportTickets ticket = ticketService.getTicketById(id);
        if (ticket == null) {
            return "redirect:/admin-home/support-tickets?error=Not+found";
        }
        session.setAttribute("editingTicketId", id);
        model.addAttribute("ticket", ticket);
        model.addAttribute("documents", docService.getDocumentsByTicketId(id));
        return "EditSupportTicket";
    }

    // ==================== 4. UPDATE TICKET ====================
    @PostMapping("/update")
    public String updateTicket(@Valid @ModelAttribute("ticket") SupportTickets ticket,
                               @RequestParam(required = false) List<MultipartFile> newFiles,
                               HttpSession session, RedirectAttributes ra) {

        String editingId = (String) session.getAttribute("editingTicketId");
        if (editingId == null) {
            ra.addFlashAttribute("error", "No ticket selected!");
            return "redirect:/admin-home/support-tickets";
        }

        Map<String, String> errors = ticketService.validateTicket(ticket);

        if (newFiles != null && !newFiles.isEmpty()) {
            for (MultipartFile f : newFiles) {
                if (f.isEmpty()) continue;
                try {
                    SupportTicketDocuments temp = new SupportTicketDocuments(
                            f.getOriginalFilename(), f.getContentType(), f.getSize(), f.getBytes(), ticket
                    );
                    Map<String, String> fileErrors = docService.validateDocument(temp);
                    if (!fileErrors.isEmpty()) {
                        errors.put("files", fileErrors.values().iterator().next());
                        break;
                    }
                } catch (IOException e) {
                    errors.put("files", "Cannot read file: " + f.getOriginalFilename());
                    break;
                }
            }
        }

        if (!errors.isEmpty()) {
            ra.addFlashAttribute("error", errors.getOrDefault("files", "Invalid data"));
            session.setAttribute("editingTicketId", editingId);
            return "redirect:/admin-home/support-tickets/edit";
        }

        ticketService.updateTicket(editingId, ticket);

        if (newFiles != null && !newFiles.isEmpty()) {
            int count = 0;
            for (MultipartFile f : newFiles) {
                if (f.isEmpty()) continue;
                try {
                    SupportTicketDocuments doc = new SupportTicketDocuments(
                            f.getOriginalFilename(), f.getContentType(), f.getSize(), f.getBytes(), ticket
                    );
                    docService.saveDocument(doc);
                    count++;
                } catch (Exception e) {
                    ra.addFlashAttribute("error", "Failed: " + f.getOriginalFilename());
                }
            }
            ra.addFlashAttribute("message", count + " new file(s) added!");
        } else {
            ra.addFlashAttribute("message", "Ticket updated!");
        }

        session.removeAttribute("editingTicketId");
        return "redirect:/admin-home/support-tickets";
    }

    // ==================== 5. DELETE TICKET ====================
    @PostMapping("/delete")
    public String deleteTicket(@RequestParam String id, RedirectAttributes ra) {
        ticketService.deleteTicket(id);
        ra.addFlashAttribute("message", "Ticket deleted!");
        return "redirect:/admin-home/support-tickets";
    }

    // ==================== 6. VIEW DOCUMENTS (POST + GET) ====================
    @PostMapping("/documents/view")
    public String viewDocumentsPost(@RequestParam String ticketId, Model model, HttpSession session) {
        return viewDocumentsLogic(ticketId, model, session);
    }

    @GetMapping("/documents/view")
    public String viewDocumentsGet(HttpSession session, Model model, RedirectAttributes ra) {
        String ticketId = (String) session.getAttribute("currentTicketId");
        if (ticketId == null) {
            ra.addFlashAttribute("error", "No ticket selected!");
            return "redirect:/admin-home/support-tickets";
        }
        return viewDocumentsLogic(ticketId, model, session);
    }

    private String viewDocumentsLogic(String ticketId, Model model, HttpSession session) {
        SupportTickets ticket = ticketService.getTicketById(ticketId);
        if (ticket == null) {
            return "redirect:/admin-home/support-tickets?error=Not+found";
        }
        session.setAttribute("currentTicketId", ticketId);
        model.addAttribute("ticket", ticket);
        model.addAttribute("documents", docService.getDocumentsByTicketId(ticketId));
        model.addAttribute("totalDocuments", docService.countDocumentsByTicketId(ticketId));
        return "SupportTicketDocumentsList";
    }

    // ==================== 7. UPLOAD DOCUMENTS ====================
    @PostMapping("/documents/upload")
    public String uploadDocuments(@RequestParam String ticketId,
                                  @RequestParam("files") List<MultipartFile> files,
                                  RedirectAttributes ra, HttpSession session) {
        SupportTickets ticket = ticketService.getTicketById(ticketId);
        if (ticket == null) {
            ra.addFlashAttribute("error", "Ticket not found!");
            return "redirect:/admin-home/support-tickets";
        }

        int count = 0;
        for (MultipartFile f : files) {
            if (f.isEmpty()) continue;
            try {
                SupportTicketDocuments doc = new SupportTicketDocuments(
                        f.getOriginalFilename(), f.getContentType(), f.getSize(), f.getBytes(), ticket
                );
                docService.saveDocument(doc);
                count++;
            } catch (Exception e) {
                ra.addFlashAttribute("error", "Upload failed: " + f.getOriginalFilename());
            }
        }

        ra.addFlashAttribute("message", count + " file(s) uploaded!");
        session.setAttribute("currentTicketId", ticketId);
        return "redirect:/admin-home/support-tickets/documents/view";
    }

    // ==================== 8. DELETE DOCUMENT ====================
    @PostMapping("/delete-document")
    public String deleteDocument(@RequestParam Long id,
                                 @RequestParam String ticketId,
                                 RedirectAttributes ra, HttpSession session) {
        SupportTicketDocuments doc = docService.getDocumentById(id);
        if (doc != null) {
            docService.deleteDocument(id);
            ra.addFlashAttribute("message", "Document deleted!");
        } else {
            ra.addFlashAttribute("error", "Document not found!");
        }
        session.setAttribute("currentTicketId", ticketId);
        return "redirect:/admin-home/support-tickets/documents/view";
    }

    // ==================== 9. BACK ====================
    @PostMapping("/back")
    public String back(HttpSession session) {
        session.removeAttribute("currentTicketId");
        session.removeAttribute("editingTicketId");
        return "redirect:/admin-home/support-tickets";
    }

    // ==================== 10. DOWNLOAD DOCUMENT ====================
    @GetMapping("/download-document")
    public ResponseEntity<byte[]> downloadDocument(@RequestParam Long id) {
        SupportTicketDocuments doc = docService.getDocumentById(id);
        if (doc == null || doc.getFileData() == null) {
            return ResponseEntity.notFound().build();
        }
        String fileName = java.net.URLEncoder.encode(doc.getFileName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(fileName).build().toString())
                .body(doc.getFileData());
    }

    // ==================== HELPER ====================
    private String loadListPage(Model model, HttpSession session) {
        int page = (Integer) session.getAttribute("ticketPage") != null ? (Integer) session.getAttribute("ticketPage") : 1;
        int pageSize = (Integer) session.getAttribute("ticketPageSize") != null ? (Integer) session.getAttribute("ticketPageSize") : 20;
        int firstResult = (page - 1) * pageSize;

        model.addAttribute("tickets", ticketService.getPaginatedTickets(firstResult, pageSize));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", session.getAttribute("ticketTotalPages"));
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalTickets", ticketService.numberOfTickets());
        model.addAttribute("docService", docService);
        return "SupportTicketsList";
    }
}