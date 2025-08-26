package com.example.demo.email_service.dao;

import com.example.demo.student.model.Students;
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

    private String generateEmailTemplate(Students student, String title, String subtitle, String mainMessage, boolean includeCredentials, String studentId, String rawPassword) {
        return String.format(
                "<html>" +
                        "<head>" +
                        "<meta charset='UTF-8'>" +
                        "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                        "<style>" +
                        "* { margin: 0; padding: 0; box-sizing: border-box; }" +
                        "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f5f7fa; line-height: 1.6; color: #333; }" +
                        ".email-container { max-width: 650px; margin: 20px auto; background: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 8px 32px rgba(0,0,0,0.1); }" +
                        ".header { background: linear-gradient(135deg, #1e3c72 0%%, #2a5298 100%%); color: white; padding: 40px 30px; text-align: center; position: relative; }" +
                        ".header::after { content: ''; position: absolute; bottom: 0; left: 0; right: 0; height: 4px; background: linear-gradient(90deg, #ff6b6b, #4ecdc4, #45b7d1, #96ceb4); }" +
                        ".university-logo { width: 60px; height: 60px; background: rgba(255,255,255,0.2); border-radius: 50%%; display: inline-flex; align-items: center; justify-content: center; font-size: 24px; margin-bottom: 15px; }" +
                        ".header h1 { font-size: 28px; font-weight: 600; margin-bottom: 8px; letter-spacing: 0.5px; }" +
                        ".header p { font-size: 16px; opacity: 0.9; font-weight: 300; }" +
                        ".content { padding: 40px 30px; }" +
                        ".welcome-message { text-align: center; margin-bottom: 35px; }" +
                        ".welcome-message h2 { color: #2c3e50; font-size: 24px; margin-bottom: 12px; font-weight: 600; }" +
                        ".welcome-message p { color: #7f8c8d; font-size: 16px; }" +
                        ".credentials-section { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); border-radius: 12px; padding: 25px; margin-bottom: 30px; color: white; }" +
                        ".credentials-title { font-size: 18px; font-weight: 600; margin-bottom: 20px; text-align: center; }" +
                        ".credential-item { display: flex; justify-content: space-between; padding: 12px 0; border-bottom: 1px solid rgba(255,255,255,0.2); }" +
                        ".credential-item:last-child { border-bottom: none; }" +
                        ".credential-label { font-weight: 500; }" +
                        ".credential-value { font-weight: 600; font-family: 'Courier New', monospace; background: rgba(255,255,255,0.2); padding: 4px 8px; border-radius: 4px; }" +
                        ".info-section { display: grid; grid-template-columns: 1fr 1fr; gap: 25px; margin-bottom: 30px; }" +
                        ".info-card { background: #f8f9fc; border: 1px solid #e1e8ed; border-radius: 10px; padding: 20px; }" +
                        ".info-card h3 { color: #2c3e50; font-size: 16px; font-weight: 600; margin-bottom: 15px; padding-bottom: 8px; border-bottom: 2px solid #3498db; }" +
                        ".info-item { display: flex; justify-content: space-between; margin-bottom: 10px; font-size: 14px; }" +
                        ".info-item:last-child { margin-bottom: 0; }" +
                        ".info-label { color: #7f8c8d; font-weight: 500; }" +
                        ".info-value { color: #2c3e50; font-weight: 600; text-align: right; max-width: 60%%; word-wrap: break-word; }" +
                        ".alert-box { background: linear-gradient(135deg, #ffeaa7 0%%, #fab1a0 100%%); border-left: 5px solid #e17055; border-radius: 8px; padding: 20px; margin-bottom: 25px; }" +
                        ".alert-title { color: #d63031; font-weight: 600; margin-bottom: 10px; font-size: 16px; }" +
                        ".alert-content { color: #2d3436; font-size: 14px; }" +
                        ".alert-content ul { margin: 10px 0; padding-left: 20px; }" +
                        ".alert-content li { margin-bottom: 5px; }" +
                        ".next-steps { background: #e8f5e8; border: 1px solid #4caf50; border-radius: 8px; padding: 20px; margin-bottom: 25px; }" +
                        ".next-steps h3 { color: #2e7d32; margin-bottom: 15px; font-size: 16px; }" +
                        ".step-list { list-style: none; }" +
                        ".step-item { display: flex; align-items: center; margin-bottom: 10px; color: #2e7d32; font-size: 14px; }" +
                        ".step-number { background: #4caf50; color: white; width: 24px; height: 24px; border-radius: 50%%; display: flex; align-items: center; justify-content: center; font-size: 12px; font-weight: 600; margin-right: 12px; }" +
                        ".footer { background: #34495e; color: #bdc3c7; padding: 25px 30px; text-align: center; font-size: 13px; }" +
                        ".footer-links { margin-bottom: 15px; }" +
                        ".footer-links a { color: #74b9ff; text-decoration: none; margin: 0 10px; }" +
                        ".footer-links a:hover { text-decoration: underline; }" +
                        "@media (max-width: 768px) {" +
                        ".email-container { margin: 10px; }" +
                        ".content { padding: 25px 20px; }" +
                        ".info-section { grid-template-columns: 1fr; gap: 15px; }" +
                        ".credential-item { flex-direction: column; }" +
                        ".credential-value { margin-top: 5px; align-self: flex-start; }" +
                        ".info-item { flex-direction: column; align-items: flex-start; }" +
                        ".info-value { max-width: 100%%; text-align: left; margin-top: 2px; }" +
                        "}" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<div class='email-container'>" +
                        "<div class='header'>" +
                        "<div class='university-logo'>🎓</div>" +
                        "<h1>%s</h1>" +
                        "<p>%s</p>" +
                        "</div>" +
                        "<div class='content'>" +
                        "<div class='welcome-message'>" +
                        "<h2>Hello %s!</h2>" +
                        "<p>%s</p>" +
                        "</div>" +
                        "%s" + // Credentials section (optional)
                        "<div class='alert-box'>" +
                        "<div class='alert-title'>⚠️ Important %s</div>" +
                        "<div class='alert-content'>" +
                        "<ul>" +
                        "%s" + // Alert items
                        "</ul>" +
                        "</div>" +
                        "</div>" +
                        "%s" + // Next steps (optional)
                        "<div class='info-section'>" +
                        "<div class='info-card'>" +
                        "<h3>👤 Personal Information</h3>" +
                        "<div class='info-item'><span class='info-label'>First Name:</span><span class='info-value'>%s</span></div>" +
                        "<div class='info-item'><span class='info-label'>Last Name:</span><span class='info-value'>%s</span></div>" +
                        "<div class='info-item'><span class='info-label'>Email:</span><span class='info-value'>%s</span></div>" +
                        "<div class='info-item'><span class='info-label'>Phone:</span><span class='info-value'>%s</span></div>" +
                        "<div class='info-item'><span class='info-label'>Birth Date:</span><span class='info-value'>%s</span></div>" +
                        "<div class='info-item'><span class='info-label'>Gender:</span><span class='info-value'>%s</span></div>" +
                        "</div>" +
                        "<div class='info-card'>" +
                        "<h3>🏫 Academic Information</h3>" +
                        "<div class='info-item'><span class='info-label'>MIS ID:</span><span class='info-value'>%s</span></div>" +
                        "<div class='info-item'><span class='info-label'>Campus:</span><span class='info-value'>%s</span></div>" +
                        "<div class='info-item'><span class='info-label'>Major:</span><span class='info-value'>%s</span></div>" +
                        "<div class='info-item'><span class='info-label'>Created Date:</span><span class='info-value'>%s</span></div>" +
                        "</div>" +
                        "</div>" +
                        "<div class='info-section'>" +
                        "<div class='info-card'>" +
                        "<h3>📍 Address Information</h3>" +
                        "<div class='info-item'><span class='info-label'>Country:</span><span class='info-value'>%s</span></div>" +
                        "<div class='info-item'><span class='info-label'>Province:</span><span class='info-value'>%s</span></div>" +
                        "<div class='info-item'><span class='info-label'>City:</span><span class='info-value'>%s</span></div>" +
                        "<div class='info-item'><span class='info-label'>District:</span><span class='info-value'>%s</span></div>" +
                        "<div class='info-item'><span class='info-label'>Ward:</span><span class='info-value'>%s</span></div>" +
                        "<div class='info-item'><span class='info-label'>Street:</span><span class='info-value'>%s</span></div>" +
                        "<div class='info-item'><span class='info-label'>Postal Code:</span><span class='info-value'>%s</span></div>" +
                        "</div>" +
                        "<div class='info-card'>" +
                        "<h3>📞 Support & Contact</h3>" +
                        "<div class='info-item'><span class='info-label'>IT Support:</span><span class='info-value'>it-support@university.edu</span></div>" +
                        "<div class='info-item'><span class='info-label'>Student Services:</span><span class='info-value'>student@university.edu</span></div>" +
                        "<div class='info-item'><span class='info-label'>Phone Support:</span><span class='info-value'>+84-xxx-xxx-xxx</span></div>" +
                        "<div class='info-item'><span class='info-label'>Office Hours:</span><span class='info-value'>Mon-Fri 8:00-17:00</span></div>" +
                        "</div>" +
                        "</div>" +
                        "</div>" +
                        "<div class='footer'>" +
                        "<div class='footer-links'>" +
                        "<a href='#'>Student Portal</a> |" +
                        "<a href='#'>Academic Calendar</a> |" +
                        "<a href='#'>Support Center</a> |" +
                        "<a href='#'>Privacy Policy</a>" +
                        "</div>" +
                        "<p>This is an automated message. Please do not reply to this email.</p>" +
                        "<p>© 2025 University Name. All rights reserved.</p>" +
                        "</div>" +
                        "</div>" +
                        "</body>" +
                        "</html>",
                title,
                subtitle,
                student.getFullName(),
                mainMessage,
                includeCredentials ?
                        "<div class='credentials-section'>" +
                                "<div class='credentials-title'>🔐 Login Credentials</div>" +
                                "<div class='credential-item'>" +
                                "<span class='credential-label'>Student ID:</span>" +
                                "<span class='credential-value'>" + studentId + "</span>" +
                                "</div>" +
                                "<div class='credential-item'>" +
                                "<span class='credential-label'>Temporary Password:</span>" +
                                "<span class='credential-value'>" + rawPassword + "</span>" +
                                "</div>" +
                                "</div>" : "",
                includeCredentials ? "Security Notice" : "Notice",
                includeCredentials ?
                        "<li>Please change your password immediately after your first login</li>" +
                                "<li>Do not share your credentials with anyone</li>" +
                                "<li>Contact IT support if you experience any login issues</li>" :
                        "<li>Verify the updated information below</li>" +
                                "<li>Contact IT support if you did not request these changes</li>" +
                                "<li>Ensure your contact details are up-to-date in the student portal</li>",
                includeCredentials ?
                        "<div class='next-steps'>" +
                                "<h3>📋 Next Steps</h3>" +
                                "<ol class='step-list'>" +
                                "<li class='step-item'><span class='step-number'>1</span>Login to the student portal using your credentials</li>" +
                                "<li class='step-item'><span class='step-number'>2</span>Update your password and verify your contact information</li>" +
                                "<li class='step-item'><span class='step-number'>3</span>Complete your profile and upload required documents</li>" +
                                "</ol>" +
                                "</div>" : "",
                student.getFirstName() != null ? student.getFirstName() : "N/A",
                student.getLastName() != null ? student.getLastName() : "N/A",
                student.getEmail(),
                student.getPhoneNumber() != null ? student.getPhoneNumber() : "N/A",
                student.getBirthDate() != null ? student.getBirthDate().toString() : "N/A",
                student.getGender() != null ? student.getGender().toString() : "N/A",
                student.getCampus() != null ? student.getCampus().getCampusName() : "N/A",
                student.getMajor() != null ? student.getMajor().getMajorName() : "N/A",
                student.getCreatedDate() != null ? student.getCreatedDate().toString() : "N/A",
                student.getCountry() != null ? student.getCountry() : "N/A",
                student.getProvince() != null ? student.getProvince() : "N/A",
                student.getCity() != null ? student.getCity() : "N/A",
                student.getDistrict() != null ? student.getDistrict() : "N/A",
                student.getWard() != null ? student.getWard() : "N/A",
                student.getStreet() != null ? student.getStreet() : "N/A",
                student.getPostalCode() != null ? student.getPostalCode() : "N/A"
        );
    }

    @Async("emailTaskExecutor")
    @Override
    public void sendEmailToNotifyLoginInformation(String to, String subject, Students student, String rawPassword) throws MessagingException {
        String htmlMessage = generateEmailTemplate(
                student,
                "Student Account Created",
                "Welcome to Our University",
                "Your student account has been successfully created. Below are your account details and important information.",
                true,
                student.getId(),
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
    public void sendEmailToNotifyInformationAfterEditing(String to, String subject, Students student) throws MessagingException {
        String htmlMessage = generateEmailTemplate(
                student,
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