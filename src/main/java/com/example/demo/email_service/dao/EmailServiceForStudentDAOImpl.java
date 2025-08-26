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

    private String generateEmailTemplate(StudentEmailContext context, String title, String subtitle, String mainMessage, boolean includeCredentials, String studentId, String rawPassword) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang='en'>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }");
        html.append(".container { max-width: 600px; margin: 20px auto; background: #ffffff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
        html.append(".header { background: #004aad; color: #ffffff; padding: 20px; text-align: center; border-top-left-radius: 8px; border-top-right-radius: 8px; }");
        html.append(".header h1 { margin: 0; font-size: 24px; }");
        html.append(".content { padding: 20px; }");
        html.append(".content h2 { color: #333333; font-size: 20px; margin-top: 0; }");
        html.append(".content p { color: #666666; line-height: 1.6; }");
        html.append(".info-table { width: 100%; border-collapse: collapse; margin: 20px 0; }");
        html.append(".info-table th, .info-table td { padding: 10px; border: 1px solid #dddddd; text-align: left; }");
        html.append(".info-table th { background: #f9f9f9; color: #333333; }");
        html.append(".credentials { background: #e8f0fe; padding: 15px; border-radius: 5px; margin: 20px 0; }");
        html.append(".credentials p { margin: 5px 0; }");
        html.append(".footer { text-align: center; padding: 20px; color: #999999; font-size: 12px; }");
        html.append(".button { display: inline-block; padding: 10px 20px; margin: 20px 0; background: #004aad; color: #ffffff; text-decoration: none; border-radius: 5px; }");
        html.append("@media only screen and (max-width: 600px) { .container { margin: 10px; } .header h1 { font-size: 20px; } .content h2 { font-size: 18px; } }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='container'>");
        html.append("<div class='header'>");
        html.append("<h1>").append(title).append("</h1>");
        html.append("</div>");
        html.append("<div class='content'>");
        html.append("<h2>").append(subtitle).append("</h2>");
        html.append("<p>Dear ").append(context.fullName()).append(",</p>");
        if (context.avatarPath() != null) {
            html.append("<p><img src='").append(context.avatarPath()).append("' alt='Student Avatar' style='max-width: 100px; border-radius: 5px;'/></p>");
        }
        html.append("<p>").append(mainMessage).append("</p>");
        html.append("<table class='info-table'>");
        html.append("<tr><th>Student ID</th><td>").append(context.studentId() != null ? context.studentId() : "N/A").append("</td></tr>");
        html.append("<tr><th>Full Name</th><td>").append(context.fullName()).append("</td></tr>");
        html.append("<tr><th>Admission Year</th><td>").append(context.admissionYear() != null ? context.admissionYear().toString() : "N/A").append("</td></tr>");
        html.append("<tr><th>Created Date</th><td>").append(context.createdDate() != null ? context.createdDate().toString() : "N/A").append("</td></tr>");
        html.append("<tr><th>Major</th><td>").append(context.majorName() != null ? context.majorName() : "N/A").append("</td></tr>");
        html.append("<tr><th>Campus</th><td>").append(context.campusName() != null ? context.campusName() : "N/A").append("</td></tr>");
        html.append("<tr><th>Learning Program</th><td>").append(context.learningProgramType() != null ? context.learningProgramType() : "N/A").append("</td></tr>");
        html.append("<tr><th>Created By</th><td>").append(context.creatorName() != null ? context.creatorName() : "N/A").append("</td></tr>");
        html.append("</table>");
        if (includeCredentials) {
            html.append("<div class='credentials'>");
            html.append("<p><strong>Your Login Credentials:</strong></p>");
            html.append("<p><strong>Student ID:</strong> ").append(studentId).append("</p>");
            html.append("<p><strong>Password:</strong> ").append(rawPassword).append("</p>");
            html.append("<p>Please change your password after your first login for security purposes.</p>");
            html.append("</div>");
        }
        html.append("<p><a href='https://university.example.com/login' class='button'>Access Your Account</a></p>");
        html.append("<p>If you have any questions, please contact our support team at <a href='mailto:support@university.example.com'>support@university.example.com</a>.</p>");
        html.append("</div>");
        html.append("<div class='footer'>");
        html.append("<p>&copy; ").append(java.time.Year.now().getValue()).append(" University Name. All rights reserved.</p>");
        html.append("<p>123 University Avenue, City, Country</p>");
        html.append("</div>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }

    @Async("emailTaskExecutor")
    @Override
    public void sendEmailToNotifyLoginInformation(String to, String subject, StudentEmailContext context, String rawPassword) throws MessagingException {
        String htmlMessage = generateEmailTemplate(
                context,
                "Student Account Created",
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