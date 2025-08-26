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

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>")
                .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                .append("<title>").append(title).append("</title>")
                .append("<style>@media only screen and (max-width:600px){.container{width:100%!important;margin:0!important;border-radius:0!important}.px{padding-left:16px!important;padding-right:16px!important}.h2{font-size:18px!important}.btn{padding:12px 18px!important;font-size:14px!important}}</style>")
                .append("</head><body style='margin:0;padding:0;background:#f5f7fa;font-family:Helvetica,Arial,sans-serif;color:#1f2937;'>")

                // Preheader “ẩn”
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
                .append("<img src='https://cms.theuniguide.com/sites/default/files/2022-07/banner-university-of-greenwich-1786x642-2022.png' ")
                .append("alt='Greenwich University Banner' width='100%' ")
                .append("style='display:block;width:100%;max-width:600px;height:auto;border-radius:14px 14px 0 0;'>")
                .append("</td>")
                .append("</tr>")

                // Subtitle ngay dưới banner đầu
                .append("<tr>")
                .append("<td style='text-align:center;padding:18px 24px 0 24px;background:#ffffff;'>")
                .append("<div style='font-size:18px;line-height:1.6;color:#1246a5;font-weight:600;'>")
                .append(subtitle)
                .append("</div>")
                .append("</td>")
                .append("</tr>")

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
        // Info table
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
                    .append("<div style='font-weight:700;color:#1d4ed8;margin-bottom:8px;'>Your Login Credentials</div>")
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
                // Banner cuối (bo góc dưới)
                .append("<tr>")
                .append("<td style='padding:0;text-align:center;'>")
                .append("<img src='https://scontent.fhan2-3.fna.fbcdn.net/v/t39.30808-6/467750405_985588663602647_1228255341212736818_n.png?_nc_cat=108&ccb=1-7&_nc_sid=cc71e4&_nc_eui2=AeHBzUB4JrrplmNOCJ7aka0MU8FaZtam74xTwVpm1qbvjNXz4_Z-Pg8H8BArQCkJhiK00xaUDQre5hBA3hyqJ1Sy&_nc_ohc=JSUhzfOS4RQQ7kNvwE5UR4f&_nc_oc=Admu-O9W3FdifQrvIcY-39qKZRFCPyc2VH3RRePK2rQmNnJIQ8EERdaQrSxj1IOT338&_nc_zt=23&_nc_ht=scontent.fhan2-3.fna&_nc_gid=ciAyH7cwbiuBI9Kd5gPnzA&oh=00_AfVRy5_bt0HVtSG04nrc7NyK-M8imi5klc-J7rMUa0MqVg&oe=68B41159' ")
                .append("alt='Greenwich Vietnam Ending Banner' width='100%' ")
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
