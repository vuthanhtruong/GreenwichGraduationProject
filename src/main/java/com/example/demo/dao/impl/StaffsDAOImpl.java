package com.example.demo.dao.impl;

import com.example.demo.dao.StaffsDAO;
import com.example.demo.entity.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@Transactional
public class StaffsDAOImpl implements StaffsDAO {

    private final JavaMailSenderImpl mailSender;

    public StaffsDAOImpl(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public List<Students> getPaginatedStudents(int firstResult, int pageSize) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        System.out.println("Username: " + username);
        // Tìm thực thể Persons dựa trên username
        Persons user = entityManager.find(Persons.class, username);
        Staffs staff = (Staffs) user;
        Majors majors=staff.getMajorManagement();

        // Truy vấn danh sách sinh viên theo major của staff
        List<Students> result = entityManager.createQuery(
                        "SELECT s FROM Students s WHERE s.major = :staffmajor", Students.class)
                .setParameter("staffmajor", majors)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
        return result;
    }

    @Override
    public boolean existsByPhoneNumberExcludingId(String phoneNumber, String id) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(s) > 0 FROM Persons s WHERE s.phoneNumber = :phoneNumber AND s.id != :id"
        );
        query.setParameter("phoneNumber", phoneNumber);
        query.setParameter("id", id);
        return (boolean) query.getSingleResult();
    }

    @Override
    public boolean existsByEmailExcludingId(String email, String id) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(s) > 0 FROM Persons s WHERE s.email = :email AND s.id != :id"
        );
        query.setParameter("email", email);
        query.setParameter("id", id);
        return (boolean) query.getSingleResult();
    }

    @Override
    public Students getStudentById(String id) {
        Students student=entityManager.find(Students.class, id);
        return student;
    }

    @Override
    public Lecturers getLecturerById(String id) {
        Lecturers lecturer=entityManager.find(Lecturers.class, id);
        return lecturer;
    }

    @Override
    public void updateLecturer(String id, Lecturers lecturer) {
        lecturer.setId(id); // Đảm bảo ID chính xác
        entityManager.merge(lecturer); // overwrite nếu entity tồn tại
    }

    @Override
    public void updateStudent(String id, Students student) {
        if (student == null) {
            throw new IllegalArgumentException("Student object cannot be null");
        }

        Students existingStudent = entityManager.find(Students.class, id);
        if (existingStudent == null) {
            throw new IllegalArgumentException("Student with ID " + id + " not found");
        }

        // Validate required fields
        if (student.getEmail() == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        if (student.getPhoneNumber() == null) {
            throw new IllegalArgumentException("Phone number cannot be null");
        }

        // Update fields from Persons (only if non-null)
        if (student.getFirstName() != null) {
            existingStudent.setFirstName(student.getFirstName());
        }
        if (student.getLastName() != null) {
            existingStudent.setLastName(student.getLastName());
        }
        existingStudent.setEmail(student.getEmail()); // Required field
        existingStudent.setPhoneNumber(student.getPhoneNumber()); // Required field
        if (student.getBirthDate() != null) {
            existingStudent.setBirthDate(student.getBirthDate());
        }
        if (student.getGender() != null) {
            existingStudent.setGender(student.getGender());
        }
        if (student.getFaceData() != null) {
            existingStudent.setFaceData(student.getFaceData());
        }
        if (student.getVoiceData() != null) {
            existingStudent.setVoiceData(student.getVoiceData());
        }
        if (student.getCountry() != null) {
            existingStudent.setCountry(student.getCountry());
        }
        if (student.getProvince() != null) {
            existingStudent.setProvince(student.getProvince());
        }
        if (student.getCity() != null) {
            existingStudent.setCity(student.getCity());
        }
        if (student.getDistrict() != null) {
            existingStudent.setDistrict(student.getDistrict());
        }
        if (student.getWard() != null) {
            existingStudent.setWard(student.getWard());
        }
        if (student.getStreet() != null) {
            existingStudent.setStreet(student.getStreet());
        }
        if (student.getPostalCode() != null) {
            existingStudent.setPostalCode(student.getPostalCode());
        }

        // Update Students-specific fields (only if non-null)
        if (student.getMisId() != null) {
            existingStudent.setMisId(student.getMisId());
        }
        if (student.getCampus() != null) {
            existingStudent.setCampus(student.getCampus());
        }
        if (student.getMajor() != null) {
            existingStudent.setMajor(student.getMajor());
        }
        if (student.getCreator() != null) {
            existingStudent.setCreator(student.getCreator());
        }
        if (student.getPassword() != null && !student.getPassword().isEmpty()) {
            existingStudent.setPassword(student.getPassword());
        }
        entityManager.merge(existingStudent);
    }

    @Override
    public void deleteStudent(String id) {
        Students student=entityManager.find(Students.class, id);
        entityManager.remove(student);
    }

    @Override
    public void deleteLecturer(String id) {
        Lecturers lecturer=entityManager.find(Lecturers.class, id);
        entityManager.remove(lecturer);
    }

    @Override
    public boolean existsPersonById(String id) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(p) FROM Persons p WHERE p.id = :id");
        query.setParameter("id", id);
        return (Long) query.getSingleResult() > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(p) FROM Persons p WHERE p.email = :email");
        query.setParameter("email", email);
        return (Long) query.getSingleResult() > 0;
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(p) FROM Persons p WHERE p.phoneNumber = :phoneNumber");
        query.setParameter("phoneNumber", phoneNumber);
        return (Long) query.getSingleResult() > 0;
    }
    @Override
    public Majors getMajors() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Staffs staff = entityManager.find(Staffs.class, username);
        return staff.getMajorManagement();
    }

    @Override
    public long numberOfStudents() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Staffs staff = entityManager.find(Staffs.class, username);

        Long totalStudents = (Long) entityManager.createQuery(
                        "SELECT COUNT(s) FROM Students s WHERE s.major.id = :staffmajor")
                .setParameter("staffmajor", staff.getMajorManagement().getMajorId())
                .getSingleResult();
        return totalStudents;
    }

    @Override
    public long numberOfLecturers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Staffs staff = entityManager.find(Staffs.class, username);

        Long totalLecturers = (Long) entityManager.createQuery(
                        "SELECT COUNT(l) FROM Lecturers l WHERE l.majorManagement.id = :staffmajor")
                .setParameter("staffmajor", staff.getMajorManagement().getMajorId())
                .getSingleResult();
        return totalLecturers;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Staffs getStaffs() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("No authenticated user found.");
        }

        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        System.out.println("Username: " + username);

        Persons user = entityManager.find(Persons.class, username);
        if (user == null) {
            throw new IllegalArgumentException("User not found with username: " + username);
        }
        if (!(user instanceof Staffs)) {
            throw new SecurityException("User is not a staff member. Entity type: " + user.getClass().getSimpleName());
        }
        return (Staffs) user;
    }

    @Override
    public Lecturers addLecturers(Lecturers lecturers,String randomPassword) {
        return entityManager.merge(lecturers);
    }

    @Override
    public Students addStudents(Students students, String randomPassword) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        // Query the common superclass (User)
        Persons user = entityManager.find(Persons.class, username);
        if (!(user instanceof Staffs)) {
            throw new SecurityException("Only staff members can add students.");
        }
        Staffs staff = (Staffs) user;
        students.setCampus(staff.getCampus());
        students.setMajor(staff.getMajorManagement());
        students.setCreator(staff);

        // Capture the raw password before encoding
        String rawPassword = randomPassword;

        // Persist the student (password will be encoded by Students.setPassword)
        Students savedStudent = entityManager.merge(students);

        // Send email with full student information
        try {
            String subject = "Your Student Account Information";
            String htmlMessage = String.format(
                    "<html>" +
                            "<head>" +
                            "<style>" +
                            "body { font-family: 'Georgia', 'Times New Roman', serif; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); margin: 0; padding: 20px; color: #333; min-height: 100vh; }" +
                            ".invitation-container { max-width: 1000px; margin: auto; background: #fff; border-radius: 20px; padding: 0; box-shadow: 0 20px 40px rgba(0,0,0,0.15); overflow: hidden; position: relative; }" +

                            // Header với viền trang trí
                            ".header { background: linear-gradient(45deg, #2c3e50, #34495e); color: white; padding: 40px 30px; text-align: center; position: relative; }" +
                            ".header::before { content: ''; position: absolute; top: 0; left: 0; right: 0; height: 5px; background: linear-gradient(90deg, #f39c12, #e74c3c, #9b59b6, #3498db); }" +
                            ".header h1 { margin: 0; font-size: 32px; font-weight: bold; letter-spacing: 2px; text-shadow: 2px 2px 4px rgba(0,0,0,0.3); }" +
                            ".header p { margin: 15px 0 0 0; font-size: 16px; opacity: 0.9; font-style: italic; }" +

                            // Decorative elements
                            ".decorative-border { height: 10px; background: repeating-linear-gradient(90deg, #f39c12 0px, #f39c12 20px, #e74c3c 20px, #e74c3c 40px, #9b59b6 40px, #9b59b6 60px, #3498db 60px, #3498db 80px); }" +

                            ".content { padding: 40px 30px; }" +

                            // Grid layout cho 2 cột
                            ".info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 40px; margin-bottom: 30px; }" +

                            // Style cho từng card
                            ".info-card { background: linear-gradient(145deg, #f8f9fa, #e9ecef); border-radius: 15px; padding: 25px; position: relative; border: 2px solid transparent; background-clip: padding-box; }" +
                            ".info-card::before { content: ''; position: absolute; top: -2px; left: -2px; right: -2px; bottom: -2px; background: linear-gradient(45deg, #667eea, #764ba2); border-radius: 17px; z-index: -1; }" +

                            ".card-title { color: #2c3e50; margin-bottom: 20px; font-size: 22px; font-weight: bold; text-align: center; border-bottom: 2px solid #3498db; padding-bottom: 10px; }" +

                            ".info-list { list-style: none; padding: 0; margin: 0; }" +
                            ".info-item { display: flex; justify-content: space-between; align-items: center; padding: 12px 0; border-bottom: 1px solid #dee2e6; }" +
                            ".info-item:last-child { border-bottom: none; }" +
                            ".info-label { font-weight: 600; color: #495057; flex: 1; }" +
                            ".info-value { color: #2c3e50; font-weight: 500; flex: 1.5; text-align: right; }" +

                            // Important note styling
                            ".important-note { background: linear-gradient(135deg, #fff3cd, #ffeaa7); border-left: 6px solid #f39c12; border-radius: 12px; padding: 25px; margin: 30px 0; position: relative; }" +
                            ".important-note::before { content: '🔒'; position: absolute; top: 15px; right: 20px; font-size: 24px; }" +
                            ".note-title { color: #856404; font-size: 18px; font-weight: bold; margin-bottom: 15px; }" +
                            ".note-content { color: #856404; line-height: 1.6; }" +

                            // Footer
                            ".footer { background: #f8f9fa; text-align: center; padding: 20px; color: #6c757d; font-style: italic; border-top: 1px solid #dee2e6; }" +

                            // Responsive
                            "@media (max-width: 768px) {" +
                            ".info-grid { grid-template-columns: 1fr; gap: 20px; }" +
                            ".header h1 { font-size: 24px; }" +
                            ".invitation-container { margin: 10px; }" +
                            "}" +

                            // Animation
                            ".info-card { transition: transform 0.3s ease, box-shadow 0.3s ease; }" +
                            ".info-card:hover { transform: translateY(-5px); box-shadow: 0 15px 30px rgba(0,0,0,0.1); }" +

                            "</style>" +
                            "</head>" +
                            "<body>" +

                            "<div class='invitation-container'>" +

                            // Header
                            "<div class='header'>" +
                            "<h1>🎓 WELCOME TO OUR UNIVERSITY</h1>" +
                            "<p>Dear %s, your student account has been successfully created!</p>" +
                            "</div>" +

                            "<div class='decorative-border'></div>" +

                            "<div class='content'>" +
                            "<p style='text-align:center; font-size:18px; color:#495057; margin-bottom:30px;'>Please review your account details below and contact administration if any corrections are needed.</p>" +

                            // Grid với 2 cột
                            "<div class='info-grid'>" +

                            // Cột trái - Address Details
                            "<div class='info-card'>" +
                            "<h3 class='card-title'>🏠 Address Information</h3>" +
                            "<ul class='info-list'>" +
                            "<li class='info-item'><span class='info-label'>Country:</span><span class='info-value'>%s</span></li>" +
                            "<li class='info-item'><span class='info-label'>Province:</span><span class='info-value'>%s</span></li>" +
                            "<li class='info-item'><span class='info-label'>City:</span><span class='info-value'>%s</span></li>" +
                            "<li class='info-item'><span class='info-label'>District:</span><span class='info-value'>%s</span></li>" +
                            "<li class='info-item'><span class='info-label'>Ward:</span><span class='info-value'>%s</span></li>" +
                            "<li class='info-item'><span class='info-label'>Street/House:</span><span class='info-value'>%s</span></li>" +
                            "<li class='info-item'><span class='info-label'>Postal Code:</span><span class='info-value'>%s</span></li>" +
                            "</ul>" +
                            "</div>" +

                            // Cột phải - Account Information
                            "<div class='info-card'>" +
                            "<h3 class='card-title'>🔐 Account Details</h3>" +
                            "<ul class='info-list'>" +
                            "<li class='info-item'><span class='info-label'>Student ID:</span><span class='info-value'><strong>%s</strong></span></li>" +
                            "<li class='info-item'><span class='info-label'>Password:</span><span class='info-value'><strong>%s</strong></span></li>" +
                            "<li class='info-item'><span class='info-label'>First Name:</span><span class='info-value'>%s</span></li>" +
                            "<li class='info-item'><span class='info-label'>Last Name:</span><span class='info-value'>%s</span></li>" +
                            "<li class='info-item'><span class='info-label'>Email:</span><span class='info-value'>%s</span></li>" +
                            "<li class='info-item'><span class='info-label'>Phone:</span><span class='info-value'>%s</span></li>" +
                            "<li class='info-item'><span class='info-label'>Birth Date:</span><span class='info-value'>%s</span></li>" +
                            "<li class='info-item'><span class='info-label'>Gender:</span><span class='info-value'>%s</span></li>" +
                            "<li class='info-item'><span class='info-label'>MIS ID:</span><span class='info-value'>%s</span></li>" +
                            "<li class='info-item'><span class='info-label'>Campus:</span><span class='info-value'>%s</span></li>" +
                            "<li class='info-item'><span class='info-label'>Major:</span><span class='info-value'>%s</span></li>" +
                            "<li class='info-item'><span class='info-label'>Created Date:</span><span class='info-value'>%s</span></li>" +
                            "</ul>" +
                            "</div>" +

                            "</div>" + // end info-grid

                            // Important Note
                            "<div class='important-note'>" +
                            "<div class='note-title'>Important Security Information</div>" +
                            "<div class='note-content'>" +
                            "<p><strong>Login Credentials:</strong> Use your Student ID and Password above to access the system.</p>" +
                            "<p><strong>Security Reminder:</strong> Please change your password immediately after your first login for enhanced security.</p>" +
                            "<p><strong>Support:</strong> If any information appears incorrect or you experience login issues, please contact administration immediately.</p>" +
                            "</div>" +
                            "</div>" +

                            "</div>" + // end content

                            "<div class='footer'>" +
                            "This is an automated invitation. Please do not reply directly to this email.<br>" +
                            "Welcome to your academic journey! 🌟" +
                            "</div>" +

                            "</div>" + // end container
                            "</body>" +
                            "</html>",

                    // Parameters
                    students.getFullName(),
                    students.getCountry() != null ? students.getCountry() : "Not provided",
                    students.getProvince() != null ? students.getProvince() : "Not provided",
                    students.getCity() != null ? students.getCity() : "Not provided",
                    students.getDistrict() != null ? students.getDistrict() : "Not provided",
                    students.getWard() != null ? students.getWard() : "Not provided",
                    students.getStreet() != null ? students.getStreet() : "Not provided",
                    students.getPostalCode() != null ? students.getPostalCode() : "Not provided",
                    students.getId(),
                    rawPassword,
                    students.getFirstName() != null ? students.getFirstName() : "Not provided",
                    students.getLastName() != null ? students.getLastName() : "Not provided",
                    students.getEmail(),
                    students.getPhoneNumber(),
                    students.getBirthDate() != null ? students.getBirthDate().toString() : "Not provided",
                    students.getGender() != null ? students.getGender().toString() : "Not provided",
                    students.getMisId() != null ? students.getMisId() : "Not provided",
                    students.getCampus() != null ? students.getCampus().getCampusName() : "Not provided",
                    students.getMajor() != null ? students.getMajor().getMajorName() : "Not provided",
                    students.getCreatedDate().toString()
            );
            sendEmailToNotifyLoginInformation(students.getEmail(), subject, htmlMessage, students);
        } catch (MessagingException e) {
            System.err.println("Failed to send email to " + students.getEmail() + ": " + e.getMessage());
        }

        return savedStudent;
    }
    @Override
    public void sendEmailToNotifyLoginInformation(String recipientEmail, String subject, String htmlMessage, Students student) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(recipientEmail);
        helper.setSubject(subject);
        helper.setText(htmlMessage, true);
        helper.setFrom("vuthanhtruong1280@gmail.com");
        mailSender.send(message);
    }

    @Override
    public List<Students> getAll() {
        List<Students> students=entityManager.createQuery("from Students s", Students.class).getResultList();
        return  students;
    }

    @Override
    public List<Classes> getClasses() {
        return List.of();
    }

    @Override
    public List<Lecturers> getLecturers() {
        List<Lecturers> lecturers=entityManager.createQuery("from Lecturers l", Lecturers.class).getResultList();
        return lecturers;
    }

}
