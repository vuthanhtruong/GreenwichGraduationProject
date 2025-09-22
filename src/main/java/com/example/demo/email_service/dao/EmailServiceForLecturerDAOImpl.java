package com.example.demo.email_service.dao;

import com.example.demo.EmailTemplates.dto.EmailTemplateDTO;
import com.example.demo.EmailTemplates.model.EmailTemplates;
import com.example.demo.EmailTemplates.service.EmailTemplatesService;
import com.example.demo.email_service.dto.LecturerEmailContext;
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

@Repository
public class EmailServiceForLecturerDAOImpl implements EmailServiceForLecturerDAO {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailTemplatesService emailTemplatesService;

    @Value("${app.base-url}")
    private String baseUrl;

    private String generateEmailTemplate(
            LecturerEmailContext context,
            EmailTemplateDTO template,
            boolean includeCredentials,
            String lecturerId,
            String rawPassword
    ) {
        java.util.function.Function<String, String> safe = v -> v != null ? v : "N/A";
        java.util.function.Function<java.time.LocalDate, String> safeDate = d -> d != null ? d.toString() : "N/A";
        String year = String.valueOf(java.time.Year.now().getValue());

        // Lấy dữ liệu từ template, fallback về giá trị mặc định nếu null
        String title = template.getSalutation() != null ? template.getSalutation() : "Access Your Lecturer Account";
        String subtitle = template.getGreeting() != null ? template.getGreeting() : "Welcome to Our University";
        String mainMessage = template.getBody() != null ? template.getBody() : "Your lecturer account has been successfully created. Below are your account details and important information.";
        String preheader = template.getGreeting() != null ? template.getGreeting() : "Welcome to our community — your account details and next steps inside.";

        String campus = safe.apply(context.campusName());
        String major = safe.apply(context.majorName());
        String creator = safe.apply(context.creatorName());

        // Sử dụng baseUrl từ cấu hình và template
        String loginUrl = template.getLinkCta() != null ? template.getLinkCta() : "/login";
        String supportEmail = template.getSupport() != null ? template.getSupport() : "support@university.example.com";
        String addressLine = template.getCampusAddress() != null ? template.getCampusAddress() : "123 University Avenue, City, Country";
        String copyrightNotice = template.getCopyrightNotice() != null ? template.getCopyrightNotice() : "University Name. All rights reserved.";

        // Social media links
        String facebookLink = template.getLinkFacebook() != null ? template.getLinkFacebook() : "https://www.facebook.com/GreenwichVietnam";
        String instagramLink = "#"; // Có thể thêm field này vào template nếu cần
        String tiktokLink = template.getLinkTiktok() != null ? template.getLinkTiktok() : "https://www.tiktok.com/@greenwichvietnam";

        // Xử lý ảnh - sử dụng CID hoặc URL mặc định
        boolean hasHeaderImage = template.getHeaderImage() != null && template.getHeaderImage().length > 0;
        boolean hasBannerImage = template.getBannerImage() != null && template.getBannerImage().length > 0;

        String headerImageSrc = hasHeaderImage ? "cid:headerImage" : "https://cms.theuniguide.com/sites/default/files/2022-07/banner-university-of-greenwich-1786x642-2022.png";
        String bannerImageSrc = hasBannerImage ? "cid:bannerImage" : "https://natajsc.com.vn/wp-content/uploads/2024/10/nata-greenwich02.jpg";

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>")
                .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                .append("<title>").append(title).append("</title>")
                .append("<style>@media only screen and (max-width:600px){.container{width:100%!important;margin:0!important;border-radius:0!important}.px{padding-left:16px!important;padding-right:16px!important}.h2{font-size:18px!important}.btn{padding:12px 18px!important;font-size:14px!important}}</style>")
                .append("</head><body style='margin:0;padding:0;background:#f5f7fa;font-family:sans-serif;color:#1f2937;'>")

                // Preheader "ẩn"
                .append("<div style='display:none;max-height:0;overflow:hidden;opacity:0;color:transparent;'>")
                .append(preheader)
                .append("</div>")

                // Container
                .append("<table role='presentation' width='100%' cellpadding='0' cellspacing='0' style='background:#f5f7fa;'>")
                .append("<tr><td align='center' style='padding:24px;'>")

                .append("<table role='presentation' class='container' width='600' cellpadding='0' cellspacing='0' style='width:600px;max-width:600px;background:#ffffff;border-radius:14px;box-shadow:0 6px 18px rgba(0,0,0,0.08);overflow:hidden;'>")

                // Banner đầu (bo góc trên)
                .append("<tr>")
                .append("<td style='padding:0;text-align:center;'>")
                .append("<img src='").append(headerImageSrc).append("' ")
                .append("alt='University Banner' width='100%' ")
                .append("style='display:block;width:100%;max-width:600px;height:auto;border-radius:14px 14px 0 0;'>")
                .append("</td>")
                .append("</tr>")

                // Subtitle ngay dưới banner đầu
                .append("<tr>")
                .append("<td style='text-align:center;padding:18px 24px 0 24px;background:#ffffff;'>")
                .append("<div style='font-size:18px;line-height:1.6;color:#001A4C;font-weight:600;font-family:sans-serif;'>")
                .append(subtitle)
                .append("</div>")
                .append("</td>")
                .append("</tr>")

                // Content
                .append("<tr><td class='px' style='padding:28px 28px 10px 28px;'>")
                .append("<p style='margin:0 0 14px 0;font-size:16px;line-height:1.75;color:#374151;font-family:sans-serif;'>Dear ")
                .append(safe.apply(context.fullName()))
                .append(",</p>");

        if (context.avatarPath() != null) {
            html.append("<div style='text-align:center;margin:18px 0 10px 0;'>")
                    .append("<img src='").append(context.avatarPath()).append("' width='120' alt='Lecturer Avatar' ")
                    .append("style='display:inline-block;border-radius:12px;border:1px solid #eef2f7;max-width:120px;height:auto;'>")
                    .append("</div>");
        }

        html.append("<p style='margin:0 0 18px 0;font-size:16px;line-height:1.8;color:#4b5563;font-family:sans-serif;'>")
                .append(mainMessage)
                .append("</p>");

        // Info table
        html.append("<table class='info-table' role='presentation' width='100%' cellpadding='0' cellspacing='0' style='border:1px solid #e5e7eb;border-radius:10px;overflow:hidden;'>")
                .append("<tr style='background:#f8fafc;'>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;width:40%;font-weight:600;border-bottom:1px solid #e5e7eb;font-family:sans-serif;'>Lecturer ID</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;border-bottom:1px solid #e5e7eb;font-family:sans-serif;'>").append(safe.apply(context.lecturerId())).append("</td>")
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
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;font-weight:600;border-bottom:1px solid #e5e7eb;background:#ffffff;font-family:sans-serif;'>Campus</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;border-bottom:1px solid #e5e7eb;background:#ffffff;font-family:sans-serif;'>").append(campus).append("</td>")
                .append("</tr>")
                .append("<tr style='background:#f8fafc;'>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;font-weight:600;border-bottom:1px solid #e5e7eb;font-family:sans-serif;'>Major</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;border-bottom:1px solid #e5e7eb;font-family:sans-serif;'>").append(major).append("</td>")
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

        // Credentials block (nếu có)
        if (includeCredentials) {
            html.append("<div style='margin:20px 0;padding:16px 16px;border:1px solid #dbeafe;background:#eff6ff;border-radius:10px;'>")
                    .append("<div style='font-weight:700;color:#001A4C;margin-bottom:8px;font-family:sans-serif;'>Your Login Credentials</div>")
                    .append("<div style='font-size:14px;color:#374151;line-height:1.7;font-family:sans-serif;'>")
                    .append("<div><strong>Username:</strong> ").append(safe.apply(lecturerId)).append("</div>")
                    .append("<div><strong>Password:</strong> ").append(safe.apply(rawPassword)).append("</div>")
                    .append("<div style='margin-top:6px;color:#6b7280;font-family:sans-serif;'>For your security, please change your password after the first login.</div>")
                    .append("</div>")
                    .append("</div>");
        }

        // CTA button
        html.append("<div style='text-align:center;margin:22px 0 8px 0;'>")
                .append("<a href='").append(loginUrl).append("' class='btn' ")
                .append("style='display:inline-block;background:#001A4C;color:#ffffff;text-decoration:none;padding:14px 24px;border-radius:10px;font-size:15px;font-weight:600;border:1px solid #001A4C;font-family:sans-serif;'>")
                .append("Access Your Account")
                .append("</a>")
                .append("</div>")

                // Support line
                .append("<p style='margin:6px 0 0 0;font-size:13px;line-height:1.7;color:#6b7280;text-align:center;font-family:sans-serif;'>")
                .append("Need help? Contact our support team at ")
                .append("<a href='mailto:").append(supportEmail).append("' style='color:#001A4C;text-decoration:none;font-family:sans-serif;'>").append(supportEmail).append("</a>.")
                .append("</p>")

                .append("</td></tr>") // end content

                // Footer
                .append("<tr><td style='background:#f8fafc;padding:18px 24px;text-align:center;border-top:1px solid #eef2f7;'>")
                .append("<p style='margin:0 0 6px 0;font-size:12px;color:#9ca3af;font-family:sans-serif;'>&copy; ").append(year).append(" ").append(copyrightNotice).append("</p>")
                .append("<p style='margin:0 0 10px 0;font-size:12px;color:#9ca3af;font-family:sans-serif;'>").append(addressLine).append("</p>")
                .append("<p style='margin:0;font-size:12px;'>")
                .append("<a href='").append(facebookLink).append("' style='color:#001A4C;text-decoration:none;margin:0 6px;font-family:sans-serif;'>Facebook</a>")
                .append("<span style='color:#d1d5db;'>|</span>")
                .append("<a href='").append(instagramLink).append("' style='color:#001A4C;text-decoration:none;margin:0 6px;font-family:sans-serif;'>Instagram</a>")
                .append("<span style='color:#d1d5db;'>|</span>")
                .append("<a href='").append(tiktokLink).append("' style='color:#001A4C;text-decoration:none;margin:0 6px;font-family:sans-serif;'>TikTok</a>")
                .append("</p>")
                .append("</td></tr>")

                // Banner cuối (bo góc dưới)
                .append("<tr>")
                .append("<td style='padding:0;text-align:center;'>")
                .append("<img src='").append(bannerImageSrc).append("' ")
                .append("alt='University Ending Banner' width='100%' ")
                .append("style='display:block;width:100%;max-width:600px;height:auto;border-radius:0 0 14px 14px;'>")
                .append("</td>")
                .append("</tr>")
                .append("</table>") // container end
                .append("</td></tr></table>")
                .append("</body></html>");

        return html.toString();
    }

    @Async("emailTaskExecutor")
    @Override
    public void sendEmailToNotifyLoginInformation(String to, String subject, LecturerEmailContext context, String rawPassword) throws MessagingException {
        Optional<EmailTemplates> templateOpt = emailTemplatesService.findByType(EmailTemplateTypes.MAJOR_LECTURE_ADD);
        if (templateOpt.isEmpty()) throw new RuntimeException("Template not found");

        EmailTemplateDTO templateDTO = new EmailTemplateDTO(templateOpt.get());
        String htmlMessage = generateEmailTemplate(context, templateDTO, true, context.lecturerId(), rawPassword);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlMessage, true);

        // Attach images as inline resources nếu có
        boolean hasHeaderImage = templateDTO.getHeaderImage() != null && templateDTO.getHeaderImage().length > 0;
        boolean hasBannerImage = templateDTO.getBannerImage() != null && templateDTO.getBannerImage().length > 0;

        if (hasHeaderImage) {
            helper.addInline("headerImage", new ByteArrayResource(templateDTO.getHeaderImage()), "image/png");
        }
        if (hasBannerImage) {
            helper.addInline("bannerImage", new ByteArrayResource(templateDTO.getBannerImage()), "image/png");
        }

        mailSender.send(message);
    }

    @Async("emailTaskExecutor")
    @Override
    public void sendEmailToNotifyInformationAfterEditing(String to, String subject, LecturerEmailContext context) throws MessagingException {
        Optional<EmailTemplates> templateOpt = emailTemplatesService.findByType(EmailTemplateTypes.MAJOR_LECTURE_EDIT);
        if (templateOpt.isEmpty()) throw new RuntimeException("Template not found");

        EmailTemplateDTO templateDTO = new EmailTemplateDTO(templateOpt.get());
        String htmlMessage = generateEmailTemplate(context, templateDTO, false, "", "");

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlMessage, true);

        // Attach images as inline resources nếu có
        boolean hasHeaderImage = templateDTO.getHeaderImage() != null && templateDTO.getHeaderImage().length > 0;
        boolean hasBannerImage = templateDTO.getBannerImage() != null && templateDTO.getBannerImage().length > 0;

        if (hasHeaderImage) {
            helper.addInline("headerImage", new ByteArrayResource(templateDTO.getHeaderImage()), "image/png");
        }
        if (hasBannerImage) {
            helper.addInline("bannerImage", new ByteArrayResource(templateDTO.getBannerImage()), "image/png");
        }

        mailSender.send(message);
    }

    @Async("emailTaskExecutor")
    public void sendEmailToNotifyLecturerDeletion(String to, String subject, LecturerEmailContext context) throws MessagingException {
        Optional<EmailTemplates> templateOpt = emailTemplatesService.findByType(EmailTemplateTypes.MAJOR_LECTURE_DELETE);
        if (templateOpt.isEmpty()) throw new RuntimeException("Template not found");

        EmailTemplateDTO templateDTO = new EmailTemplateDTO(templateOpt.get());
        String htmlMessage = generateEmailTemplate(context, templateDTO, false, "", "");

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlMessage, true);

        // Attach images as inline resources nếu có
        boolean hasHeaderImage = templateDTO.getHeaderImage() != null && templateDTO.getHeaderImage().length > 0;
        boolean hasBannerImage = templateDTO.getBannerImage() != null && templateDTO.getBannerImage().length > 0;

        if (hasHeaderImage) {
            helper.addInline("headerImage", new ByteArrayResource(templateDTO.getHeaderImage()), "image/png");
        }
        if (hasBannerImage) {
            helper.addInline("bannerImage", new ByteArrayResource(templateDTO.getBannerImage()), "image/png");
        }

        mailSender.send(message);
    }
}