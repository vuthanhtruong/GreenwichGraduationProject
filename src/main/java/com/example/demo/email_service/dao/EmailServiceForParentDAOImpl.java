package com.example.demo.email_service.dao;

import com.example.demo.emailTemplates.dto.EmailTemplateDTO;
import com.example.demo.emailTemplates.model.EmailTemplates;
import com.example.demo.emailTemplates.service.EmailTemplatesService;
import com.example.demo.email_service.dto.ParentEmailContext;
import com.example.demo.entity.Enums.EmailTemplateTypes;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.function.Function;

@Repository
public class EmailServiceForParentDAOImpl implements EmailServiceForParentDAO {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailTemplatesService emailTemplatesService;

    @Value("${app.base-url}")
    private String baseUrl;

    // ==================== HELPER METHODS ====================

    private EmailTemplateDTO createDefaultTemplate(EmailTemplateTypes type) {
        EmailTemplateDTO template = new EmailTemplateDTO();

        switch (type) {
            case PARENT_ADD:
                template.setSalutation("Access Your Parent Account");
                template.setGreeting("Welcome to Our University Portal");
                template.setBody("Your parent account has been successfully created. You can now monitor your child's academic progress and stay connected with the university.");
                break;
            case PARENT_EDIT:
                template.setSalutation("Parent Account Updated");
                template.setGreeting("Your Information Has Been Updated");
                template.setBody("Your parent account information has been updated. Please review the details below to ensure everything is correct.");
                break;
            case PARENT_DELETE:
                template.setSalutation("Parent Account Notification");
                template.setGreeting("Account Status Update");
                template.setBody("This email is to notify you about changes to your parent account status.");
                break;
            case PARENT_STUDENT_LINK:
                template.setSalutation("Student Link Notification");
                template.setGreeting("You've Been Linked to a Student");
                template.setBody("Your account has been linked to a student. You can now access their academic information.");
                break;
            default:
                template.setSalutation("Parent Account Information");
                template.setGreeting("Important Update");
                template.setBody("Please review your parent account information below.");
        }

        template.setLinkCta(baseUrl != null ? baseUrl + "/login" : "http://localhost:8080/login");
        template.setSupport("support@university.example.com");
        template.setCampusAddress("123 University Avenue, City, Country");
        template.setCopyrightNotice("University Name. All rights reserved.");
        template.setLinkFacebook("https://www.facebook.com/GreenwichVietnam");
        template.setLinkTiktok("https://www.tiktok.com/@greenwichvietnam");

        return template;
    }

    private void attachTemplateImages(MimeMessageHelper helper, EmailTemplateDTO template) throws MessagingException {
        if (template.getHeaderImage() != null && template.getHeaderImage().length > 0) {
            helper.addInline("headerImage", new ByteArrayResource(template.getHeaderImage()), "image/png");
        }
        if (template.getBannerImage() != null && template.getBannerImage().length > 0) {
            helper.addInline("bannerImage", new ByteArrayResource(template.getBannerImage()), "image/png");
        }
    }

    private String generateEmailTemplate(
            ParentEmailContext context,
            EmailTemplateDTO template,
            boolean includeCredentials,
            String parentId,
            String rawPassword,
            String additionalInfo
    ) {
        Function<String, String> safe = v -> v != null ? v : "N/A";
        Function<java.time.LocalDate, String> safeDate = d -> d != null ? d.toString() : "N/A";
        String year = String.valueOf(java.time.Year.now().getValue());

        String title = safe.apply(template.getSalutation());
        String subtitle = safe.apply(template.getGreeting());
        String mainMessage = safe.apply(template.getBody());
        String preheader = safe.apply(template.getGreeting());

        String creator = safe.apply(context.creatorName());
        String loginUrl = template.getLinkCta() != null ? template.getLinkCta() : (baseUrl + "/login");
        String supportEmail = safe.apply(template.getSupport());
        String addressLine = safe.apply(template.getCampusAddress());
        String copyrightNotice = safe.apply(template.getCopyrightNotice());

        String facebookLink = template.getLinkFacebook() != null ? template.getLinkFacebook() : "#";
        String tiktokLink = template.getLinkTiktok() != null ? template.getLinkTiktok() : "#";

        boolean hasHeaderImage = template.getHeaderImage() != null && template.getHeaderImage().length > 0;
        boolean hasBannerImage = template.getBannerImage() != null && template.getBannerImage().length > 0;

        String headerImageSrc = hasHeaderImage ? "cid:headerImage" :
                "https://cms.theuniguide.com/sites/default/files/2022-07/banner-university-of-greenwich-1786x642-2022.png";
        String bannerImageSrc = hasBannerImage ? "cid:bannerImage" :
                "https://scontent.fhan2-3.fna.fbcdn.net/v/t39.30808-6/467750405_985588663602647_1228255341212736818_n.png";

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>")
                .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                .append("<title>").append(title).append("</title>")
                .append("<style>@media only screen and (max-width:600px){.container{width:100%!important;margin:0!important;border-radius:0!important}.px{padding-left:16px!important;padding-right:16px!important}.h2{font-size:18px!important}.btn{padding:12px 18px!important;font-size:14px!important}}</style>")
                .append("</head><body style='margin:0;padding:0;background:#f5f7fa;font-family:sans-serif;color:#1f2937;'>")

                // Preheader
                .append("<div style='display:none;max-height:0;overflow:hidden;opacity:0;color:transparent;'>")
                .append(preheader)
                .append("</div>")

                // Container
                .append("<table role='presentation' width='100%' cellpadding='0' cellspacing='0' style='background:#f5f7fa;'>")
                .append("<tr><td align='center' style='padding:24px;'>")
                .append("<table role='presentation' class='container' width='600' cellpadding='0' cellspacing='0' style='width:600px;max-width:600px;background:#ffffff;border-radius:14px;box-shadow:0 6px 18px rgba(0,0,0,0.08);overflow:hidden;'>")

                // Header Banner
                .append("<tr><td style='padding:0;text-align:center;'>")
                .append("<img src='").append(headerImageSrc).append("' alt='University Banner' width='100%' ")
                .append("style='display:block;width:100%;max-width:600px;height:auto;border-radius:14px 14px 0 0;'>")
                .append("</td></tr>")

                // Subtitle
                .append("<tr><td style='text-align:center;padding:18px 24px 0 24px;background:#ffffff;'>")
                .append("<div style='font-size:18px;line-height:1.6;color:#001A4C;font-weight:600;font-family:sans-serif;'>")
                .append(subtitle)
                .append("</div></td></tr>")

                // Content
                .append("<tr><td class='px' style='padding:28px 28px 10px 28px;'>")
                .append("<p style='margin:0 0 14px 0;font-size:16px;line-height:1.75;color:#374151;font-family:sans-serif;'>Dear ")
                .append(safe.apply(context.fullName()))
                .append(",</p>")
                .append("<p style='margin:0 0 18px 0;font-size:16px;line-height:1.8;color:#4b5563;font-family:sans-serif;'>")
                .append(mainMessage)
                .append("</p>");

        // Additional Info (for student link)
        if (additionalInfo != null && !additionalInfo.isEmpty()) {
            html.append("<div style='margin:20px 0;padding:16px;border:1px solid #dbeafe;background:#eff6ff;border-radius:10px;'>")
                    .append(additionalInfo)
                    .append("</div>");
        }

        // Info Table
        html.append("<table class='info-table' role='presentation' width='100%' cellpadding='0' cellspacing='0' style='border:1px solid #e5e7eb;border-radius:10px;overflow:hidden;'>")
                .append("<tr style='background:#f8fafc;'>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;width:40%;font-weight:600;border-bottom:1px solid #e5e7eb;font-family:sans-serif;'>Parent ID</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;border-bottom:1px solid #e5e7eb;font-family:sans-serif;'>").append(safe.apply(context.parentId())).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;width:40%;font-weight:600;border-bottom:1px solid #e5e7eb;background:#ffffff;font-family:sans-serif;'>Full Name</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;border-bottom:1px solid #e5e7eb;background:#ffffff;font-family:sans-serif;'>").append(safe.apply(context.fullName())).append("</td>")
                .append("</tr>")
                .append("<tr style='background:#f8fafc;'>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;font-weight:600;border-bottom:1px solid #e5e7eb;font-family:sans-serif;'>Email</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;border-bottom:1px solid #e5e7eb;font-family:sans-serif;'>").append(safe.apply(context.email())).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;font-weight:600;border-bottom:1px solid #e5e7eb;background:#ffffff;font-family:sans-serif;'>Phone Number</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;border-bottom:1px solid #e5e7eb;background:#ffffff;font-family:sans-serif;'>").append(safe.apply(context.phoneNumber())).append("</td>")
                .append("</tr>")
                .append("<tr style='background:#f8fafc;'>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;font-weight:600;border-bottom:1px solid #e5e7eb;font-family:sans-serif;'>Birth Date</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;border-bottom:1px solid #e5e7eb;font-family:sans-serif;'>").append(safeDate.apply(context.birthDate())).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;font-weight:600;border-bottom:1px solid #e5e7eb;background:#ffffff;font-family:sans-serif;'>Gender</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;border-bottom:1px solid #e5e7eb;background:#ffffff;font-family:sans-serif;'>").append(safe.apply(context.gender())).append("</td>")
                .append("</tr>")
                .append("<tr style='background:#f8fafc;'>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;font-weight:600;border-bottom:1px solid #e5e7eb;font-family:sans-serif;'>Address</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;border-bottom:1px solid #e5e7eb;font-family:sans-serif;'>").append(safe.apply(context.fullAddress())).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;font-weight:600;border-bottom:1px solid #e5e7eb;background:#ffffff;font-family:sans-serif;'>Created Date</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;border-bottom:1px solid #e5e7eb;background:#ffffff;font-family:sans-serif;'>").append(safeDate.apply(context.createdDate())).append("</td>")
                .append("</tr>")
                .append("<tr style='background:#f8fafc;'>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;font-weight:600;font-family:sans-serif;'>Created By</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;font-family:sans-serif;'>").append(creator).append("</td>")
                .append("</tr>")
                .append("</table>");

        // Credentials Block
        if (includeCredentials) {
            html.append("<div style='margin:20px 0;padding:16px 16px;border:1px solid #dbeafe;background:#eff6ff;border-radius:10px;'>")
                    .append("<div style='font-weight:700;color:#001A4C;margin-bottom:8px;font-family:sans-serif;'>Your Login Credentials</div>")
                    .append("<div style='font-size:14px;color:#374151;line-height:1.7;font-family:sans-serif;'>")
                    .append("<div><strong>Username:</strong> ").append(safe.apply(parentId)).append("</div>")
                    .append("<div><strong>Password:</strong> ").append(safe.apply(rawPassword)).append("</div>")
                    .append("<div style='margin-top:6px;color:#6b7280;font-family:sans-serif;'>For your security, please change your password after the first login.</div>")
                    .append("</div>")
                    .append("</div>");
        }

        // CTA Button
        html.append("<div style='text-align:center;margin:22px 0 8px 0;'>")
                .append("<a href='").append(loginUrl).append("' class='btn' ")
                .append("style='display:inline-block;background:#001A4C;color:#ffffff;text-decoration:none;padding:14px 24px;border-radius:10px;font-size:15px;font-weight:600;border:1px solid #001A4C;font-family:sans-serif;'>")
                .append("Access Your Account")
                .append("</a>")
                .append("</div>")

                // Support
                .append("<p style='margin:6px 0 0 0;font-size:13px;line-height:1.7;color:#6b7280;text-align:center;font-family:sans-serif;'>")
                .append("Need help? Contact our support team at ")
                .append("<a href='mailto:").append(supportEmail).append("' style='color:#001A4C;text-decoration:none;font-family:sans-serif;'>").append(supportEmail).append("</a>.")
                .append("</p>")

                .append("</td></tr>")

                // Footer
                .append("<tr><td style='background:#f8fafc;padding:18px 24px;text-align:center;border-top:1px solid #eef2f7;'>")
                .append("<p style='margin:0 0 6px 0;font-size:12px;color:#9ca3af;font-family:sans-serif;'>&copy; ").append(year).append(" ").append(copyrightNotice).append("</p>")
                .append("<p style='margin:0 0 10px 0;font-size:12px;color:#9ca3af;font-family:sans-serif;'>").append(addressLine).append("</p>")
                .append("<p style='margin:0;font-size:12px;'>")
                .append("<a href='").append(facebookLink).append("' style='color:#001A4C;text-decoration:none;margin:0 6px;font-family:sans-serif;'>Facebook</a>")
                .append("<span style='color:#d1d5db;'>|</span>")
                .append("<a href='#' style='color:#001A4C;text-decoration:none;margin:0 6px;font-family:sans-serif;'>Instagram</a>")
                .append("<span style='color:#d1d5db;'>|</span>")
                .append("<a href='").append(tiktokLink).append("' style='color:#001A4C;text-decoration:none;margin:0 6px;font-family:sans-serif;'>TikTok</a>")
                .append("</p>")
                .append("</td></tr>")

                // Footer Banner
                .append("<tr><td style='padding:0;text-align:center;'>")
                .append("<img src='").append(bannerImageSrc).append("' alt='University Ending Banner' width='100%' ")
                .append("style='display:block;width:100%;max-width:600px;height:auto;border-radius:0 0 14px 14px;'>")
                .append("</td></tr>")

                .append("</table>")
                .append("</td></tr></table>")
                .append("</body></html>");

        return html.toString();
    }

    // ==================== PUBLIC METHODS ====================

    @Async("emailTaskExecutor")
    @Override
    public void sendEmailToNotifyLoginInformation(String to, String subject,
                                                  ParentEmailContext context,
                                                  String rawPassword) throws MessagingException {
        EmailTemplateDTO templateDTO;

        try {
            Optional<EmailTemplates> templateOpt = emailTemplatesService.findByType(EmailTemplateTypes.PARENT_ADD);
            if (templateOpt.isEmpty()) {
                System.err.println("WARN: Template PARENT_ADD not found. Using default.");
                templateDTO = createDefaultTemplate(EmailTemplateTypes.PARENT_ADD);
            } else {
                templateDTO = new EmailTemplateDTO(templateOpt.get());
            }
        } catch (Exception e) {
            System.err.println("ERROR: Failed to fetch template: " + e.getMessage());
            templateDTO = createDefaultTemplate(EmailTemplateTypes.PARENT_ADD);
        }

        String htmlMessage = generateEmailTemplate(context, templateDTO, true, context.parentId(), rawPassword, null);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlMessage, true);

        attachTemplateImages(helper, templateDTO);
        mailSender.send(message);
    }

    @Async("emailTaskExecutor")
    @Override
    public void sendEmailToNotifyInformationAfterEditing(String to, String subject,
                                                         ParentEmailContext context) throws MessagingException {
        EmailTemplateDTO templateDTO;

        try {
            Optional<EmailTemplates> templateOpt = emailTemplatesService.findByType(EmailTemplateTypes.PARENT_EDIT);
            if (templateOpt.isEmpty()) {
                System.err.println("WARN: Template PARENT_EDIT not found. Using default.");
                templateDTO = createDefaultTemplate(EmailTemplateTypes.PARENT_EDIT);
            } else {
                templateDTO = new EmailTemplateDTO(templateOpt.get());
            }
        } catch (Exception e) {
            System.err.println("ERROR: Failed to fetch template: " + e.getMessage());
            templateDTO = createDefaultTemplate(EmailTemplateTypes.PARENT_EDIT);
        }

        String htmlMessage = generateEmailTemplate(context, templateDTO, false, "", "", null);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlMessage, true);

        attachTemplateImages(helper, templateDTO);
        mailSender.send(message);
    }

    @Async("emailTaskExecutor")
    @Override
    public void sendEmailToNotifyParentDeletion(String to, String subject,
                                                ParentEmailContext context) throws MessagingException {
        EmailTemplateDTO templateDTO;

        try {
            Optional<EmailTemplates> templateOpt = emailTemplatesService.findByType(EmailTemplateTypes.PARENT_DELETE);
            if (templateOpt.isEmpty()) {
                System.err.println("WARN: Template PARENT_DELETE not found. Using default.");
                templateDTO = createDefaultTemplate(EmailTemplateTypes.PARENT_DELETE);
            } else {
                templateDTO = new EmailTemplateDTO(templateOpt.get());
            }
        } catch (Exception e) {
            System.err.println("ERROR: Failed to fetch template: " + e.getMessage());
            templateDTO = createDefaultTemplate(EmailTemplateTypes.PARENT_DELETE);
        }

        String htmlMessage = generateEmailTemplate(context, templateDTO, false, "", "", null);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlMessage, true);

        attachTemplateImages(helper, templateDTO);
        mailSender.send(message);
    }

    @Async("emailTaskExecutor")
    @Override
    public void sendEmailToNotifyStudentLink(String to, String subject,
                                             ParentEmailContext context,
                                             String studentName,
                                             String studentId,
                                             String relationship) throws MessagingException {
        EmailTemplateDTO templateDTO;

        try {
            Optional<EmailTemplates> templateOpt = emailTemplatesService.findByType(EmailTemplateTypes.PARENT_STUDENT_LINK);
            if (templateOpt.isEmpty()) {
                System.err.println("WARN: Template PARENT_STUDENT_LINK not found. Using default.");
                templateDTO = createDefaultTemplate(EmailTemplateTypes.PARENT_STUDENT_LINK);
            } else {
                templateDTO = new EmailTemplateDTO(templateOpt.get());
            }
        } catch (Exception e) {
            System.err.println("ERROR: Failed to fetch template: " + e.getMessage());
            templateDTO = createDefaultTemplate(EmailTemplateTypes.PARENT_STUDENT_LINK);
        }

        // Build additional info for student link
        String additionalInfo = String.format(
                "<div style='font-weight:600;color:#001A4C;margin-bottom:8px;font-family:sans-serif;'>Student Link Information</div>" +
                        "<div style='font-size:14px;color:#374151;line-height:1.7;font-family:sans-serif;'>" +
                        "<div><strong>Student Name:</strong> %s</div>" +
                        "<div><strong>Student ID:</strong> %s</div>" +
                        "<div><strong>Relationship:</strong> %s</div>" +
                        "</div>",
                studentName, studentId, relationship
        );

        String htmlMessage = generateEmailTemplate(context, templateDTO, false, "", "", additionalInfo);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlMessage, true);

        attachTemplateImages(helper, templateDTO);
        mailSender.send(message);
    }
}