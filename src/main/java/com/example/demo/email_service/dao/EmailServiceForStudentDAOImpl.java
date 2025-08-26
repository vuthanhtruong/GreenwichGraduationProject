package com.example.demo.email_service.dao;

import com.example.demo.email_service.dto.StudentEmailContext;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

@Repository
public class EmailServiceForStudentDAOImpl implements EmailServiceForStudentDAO {

    @Autowired
    private JavaMailSender mailSender;

    private String generateEmailTemplate(
            StudentEmailContext context,
            String title,
            String subtitle,
            String mainMessage,
            boolean includeCredentials,
            String studentId,
            String rawPassword
    ) {
        // Helpers
        java.util.function.Function<String, String> safe = v -> v != null ? v : "N/A";
        java.util.function.Function<java.time.LocalDate, String> safeDate = d -> d != null ? d.toString() : "N/A";
        String year = String.valueOf(java.time.Year.now().getValue());

        String preheader = "Welcome to our community — your account details and next steps inside.";

        String campus = safe.apply(context.campusName());
        String major  = safe.apply(context.majorName());
        String learn  = safe.apply(context.learningProgramType());
        String creator = safe.apply(context.creatorName());

        String loginUrl = "https://university.example.com/login";
        String supportEmail = "support@university.example.com";
        String addressLine = "123 University Avenue, City, Country";

        // Lưu ý: dùng style inline cho độ tương thích email client
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>")
                .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                // Preheader (ẩn)
                .append("<title>").append(title).append("</title>")
                .append("<style>@media only screen and (max-width:600px){.container{width:100%!important;margin:0!important;border-radius:0!important}.px{padding-left:16px!important;padding-right:16px!important}.h1{font-size:22px!important}.h2{font-size:18px!important}.btn{padding:12px 18px!important;font-size:14px!important}}</style>")
                .append("</head><body style='margin:0;padding:0;background:#f5f7fa;font-family:Helvetica,Arial,sans-serif;color:#1f2937;'>")

                // Preheader “ẩn” bằng display:none; và font-size nhỏ
                .append("<div style='display:none;max-height:0;overflow:hidden;opacity:0;color:transparent;'>")
                .append(preheader)
                .append("</div>")

                // Container
                .append("<table role='presentation' width='100%' cellpadding='0' cellspacing='0' style='background:#f5f7fa;'>")
                .append("<tr><td align='center' style='padding:24px;'>")

                .append("<table role='presentation' class='container' width='600' cellpadding='0' cellspacing='0' style='width:600px;max-width:600px;background:#ffffff;border-radius:14px;box-shadow:0 6px 18px rgba(0,0,0,0.08);overflow:hidden;'>")

                // Header
                .append("<tr><td style='background:linear-gradient(135deg,#0b4be0 0%,#1a75ff 100%);padding:28px 24px;text-align:center;'>")
                .append("<img src='https://university.example.com/logo.png' width='140' height='auto' alt='University Logo' style='display:block;margin:0 auto 10px auto;border:0;outline:none;text-decoration:none;'>")
                .append("<div class='h1' style='font-size:24px;line-height:1.3;color:#ffffff;font-weight:700;letter-spacing:.2px;'>")
                .append("🎓 ").append(title)
                .append("</div>")
                .append("<div class='h2' style='font-size:16px;line-height:1.6;color:#e6f0ff;margin-top:6px;'>")
                .append(subtitle)
                .append("</div>")
                .append("</td></tr>")

                // Content
                .append("<tr><td class='px' style='padding:28px 28px 10px 28px;'>")
                .append("<p style='margin:0 0 14px 0;font-size:16px;line-height:1.75;color:#374151;'>Dear ")
                .append(safe.apply(context.fullName()))
                .append(",</p>");

        if (context.avatarPath() != null) {
            html.append("<div style='text-align:center;margin:18px 0 10px 0;'>")
                    .append("<img src='").append(context.avatarPath()).append("' width='120' alt='Student Avatar' ")
                    .append("style='display:inline-block;border-radius:12px;border:1px solid #eef2f7;max-width:120px;height:auto;'>")
                    .append("</div>");
        }

        html.append("<p style='margin:0 0 18px 0;font-size:16px;line-height:1.8;color:#4b5563;'>")
                .append(mainMessage)
                .append("</p>");

        // Key highlights (icon chips)
        html.append("<table role='presentation' width='100%' cellpadding='0' cellspacing='0' style='margin:6px 0 14px 0;'>")
                .append("<tr>")
                .append("<td style='padding:6px 0;'>")
                .append("<span style='display:inline-block;background:#f1f5ff;color:#0b4be0;border:1px solid #dbe7ff;border-radius:10px;padding:6px 10px;font-size:13px;margin-right:6px;'>🔑 Account Access</span>")
                .append("<span style='display:inline-block;background:#f1fff5;color:#047857;border:1px solid #d8f6df;border-radius:10px;padding:6px 10px;font-size:13px;margin-right:6px;'>📘 Program: ").append(learn).append("</span>")
                .append("<span style='display:inline-block;background:#fffaf1;color:#b45309;border:1px solid #fde7c7;border-radius:10px;padding:6px 10px;font-size:13px;'>📍 Campus: ").append(campus).append("</span>")
                .append("</td>")
                .append("</tr>")
                .append("</table>");

        // Info table (two column)
        html.append("<table class='info-table' role='presentation' width='100%' cellpadding='0' cellspacing='0' style='border:1px solid #e5e7eb;border-radius:10px;overflow:hidden;'>")
                .append("<tr style='background:#f8fafc;'>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;width:40%;font-weight:600;border-bottom:1px solid #e5e7eb;'>Student ID</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;border-bottom:1px solid #e5e7eb;'>").append(safe.apply(context.studentId())).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;width:40%;font-weight:600;border-bottom:1px solid #e5e7eb;background:#ffffff;'>Full Name</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;border-bottom:1px solid #e5e7eb;background:#ffffff;'>").append(safe.apply(context.fullName())).append("</td>")
                .append("</tr>")
                .append("<tr style='background:#f8fafc;'>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;font-weight:600;border-bottom:1px solid #e5e7eb;'>Email</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;border-bottom:1px solid #e5e7eb;'>").append(safe.apply(context.email())).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;font-weight:600;border-bottom:1px solid #e5e7eb;background:#ffffff;'>Phone Number</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;border-bottom:1px solid #e5e7eb;background:#ffffff;'>").append(safe.apply(context.phoneNumber())).append("</td>")
                .append("</tr>")
                .append("<tr style='background:#f8fafc;'>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;font-weight:600;border-bottom:1px solid #e5e7eb;'>Birth Date</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;border-bottom:1px solid #e5e7eb;'>").append(safeDate.apply(context.birthDate())).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;font-weight:600;border-bottom:1px solid #e5e7eb;background:#ffffff;'>Gender</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;border-bottom:1px solid #e5e7eb;background:#ffffff;'>").append(safe.apply(context.gender())).append("</td>")
                .append("</tr>")
                .append("<tr style='background:#f8fafc;'>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;font-weight:600;border-bottom:1px solid #e5e7eb;'>Address</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;border-bottom:1px solid #e5e7eb;'>").append(safe.apply(context.fullAddress())).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;font-weight:600;border-bottom:1px solid #e5e7eb;background:#ffffff;'>Campus</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;border-bottom:1px solid #e5e7eb;background:#ffffff;'>").append(campus).append("</td>")
                .append("</tr>")
                .append("<tr style='background:#f8fafc;'>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;font-weight:600;border-bottom:1px solid #e5e7eb;'>Major</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;border-bottom:1px solid #e5e7eb;'>").append(major).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;font-weight:600;border-bottom:1px solid #e5e7eb;background:#ffffff;'>Admission Year</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;border-bottom:1px solid #e5e7eb;background:#ffffff;'>").append(safeDate.apply(context.admissionYear())).append("</td>")
                .append("</tr>")
                .append("<tr style='background:#f8fafc;'>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;font-weight:600;border-bottom:1px solid #e5e7eb;'>Created Date</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;border-bottom:1px solid #e5e7eb;'>").append(safeDate.apply(context.createdDate())).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;font-weight:600;border-bottom:1px solid #e5e7eb;background:#ffffff;'>Learning Program</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;border-bottom:1px solid #e5e7eb;background:#ffffff;'>").append(learn).append("</td>")
                .append("</tr>")
                .append("<tr style='background:#f8fafc;'>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#1f2937;font-weight:600;'>Created By</td>")
                .append("<td style='padding:12px 14px;font-size:14px;color:#374151;'>").append(creator).append("</td>")
                .append("</tr>")
                .append("</table>");

        // Credentials block (nếu có)
        if (includeCredentials) {
            html.append("<div style='margin:20px 0;padding:16px 16px;border:1px solid #dbeafe;background:#eff6ff;border-radius:10px;'>")
                    .append("<div style='font-weight:700;color:#1d4ed8;margin-bottom:8px;'>🔐 Your Login Credentials</div>")
                    .append("<div style='font-size:14px;color:#374151;line-height:1.7;'>")
                    .append("<div><strong>Username:</strong> ").append(safe.apply(studentId)).append("</div>")
                    .append("<div><strong>Password:</strong> ").append(safe.apply(rawPassword)).append("</div>")
                    .append("<div style='margin-top:6px;color:#6b7280;'>For your security, please change your password after the first login.</div>")
                    .append("</div>")
                    .append("</div>");
        }

        // CTA button
        html.append("<div style='text-align:center;margin:22px 0 8px 0;'>")
                .append("<a href='").append(loginUrl).append("' class='btn' ")
                .append("style='display:inline-block;background:#0b4be0;color:#ffffff;text-decoration:none;padding:14px 24px;border-radius:10px;font-size:15px;font-weight:600;border:1px solid #0940c2;'>")
                .append("Access Your Account")
                .append("</a>")
                .append("</div>")

                // Support line
                .append("<p style='margin:6px 0 0 0;font-size:13px;line-height:1.7;color:#6b7280;text-align:center;'>")
                .append("Need help? Contact our support team at ")
                .append("<a href='mailto:").append(supportEmail).append("' style='color:#0b4be0;text-decoration:none;'>").append(supportEmail).append("</a>.")
                .append("</p>")

                .append("</td></tr>") // end content

                // Footer
                .append("<tr><td style='background:#f8fafc;padding:18px 24px;text-align:center;border-top:1px solid #eef2f7;'>")
                .append("<p style='margin:0 0 6px 0;font-size:12px;color:#9ca3af;'>&copy; ").append(year).append(" University Name. All rights reserved.</p>")
                .append("<p style='margin:0 0 10px 0;font-size:12px;color:#9ca3af;'>").append(addressLine).append("</p>")
                .append("<p style='margin:0;font-size:12px;'>")
                .append("<a href='https://www.facebook.com/GreenwichVietnam' style='color:#0b4be0;text-decoration:none;margin:0 6px;'>Facebook</a>")
                .append("<span style='color:#d1d5db;'>|</span>")
                .append("<a href='https://www.instagram.com/universityofgreenwichvn' style='color:#0b4be0;text-decoration:none;margin:0 6px;'>Instagram</a>")
                .append("<span style='color:#d1d5db;'>|</span>")
                .append("<a href='https://www.tiktok.com/@greenwichvietnam' style='color:#0b4be0;text-decoration:none;margin:0 6px;'>TikTok</a>")
                .append("</p>")
                .append("</td></tr>")

                .append("</table>") // container end
                .append("</td></tr></table>")
                .append("</body></html>");

        return html.toString();
    }


    @Async("emailTaskExecutor")
    @Override
    public void sendEmailToNotifyLoginInformation(String to, String subject, StudentEmailContext context, String rawPassword) throws MessagingException {
        String htmlMessage = generateEmailTemplate(
                context,
                "Access Your Student Account",
                "Welcome to Our University",
                "Your student account has been successfully created. Below are your account details and important information.",
                true,
                context.studentId(),
                rawPassword
        );

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlMessage, true);
        mailSender.send(message);
    }

    @Async("emailTaskExecutor")
    @Override
    public void sendEmailToNotifyInformationAfterEditing(String to, String subject, StudentEmailContext context) throws MessagingException {
        String htmlMessage = generateEmailTemplate(
                context,
                "Student Account Updated",
                "University Notification",
                "Your student account information has been updated. Please review the details below and contact support if any information is incorrect.",
                false,
                "",
                ""
        );

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlMessage, true);
        mailSender.send(message);
    }
}