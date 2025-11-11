// src/main/java/com/example/demo/DemoApplication.java
package com.example.demo;

import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.campus.model.Campuses;
import com.example.demo.curriculum.model.Curriculum;
import com.example.demo.entity.Enums.*;
import com.example.demo.financialHistory.depositHistory.model.DepositHistories;
import com.example.demo.major.model.Majors;
import com.example.demo.room.model.OfflineRooms;
import com.example.demo.room.model.OnlineRooms;
import com.example.demo.specialization.model.Specialization;
import com.example.demo.subject.majorSubject.model.MajorSubjects;
import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import com.example.demo.subject.abstractSubject.model.Subjects;
import com.example.demo.timtable.majorTimetable.model.Slots;
import com.example.demo.tuitionByYear.model.TuitionByYear;
import com.example.demo.tuitionByYear.model.TuitionByYearId;
import com.example.demo.user.admin.model.Admins;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.person.model.Persons;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@SpringBootApplication
public class DemoApplication {

    private static final String DEFAULT_PASSWORD = "Anhnam123";
    private static final double INITIAL_DEPOSIT_AMOUNT = 10000.0;

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(DemoApplication.class, args);
        EntityManagerFactory emf = context.getBean(EntityManagerFactory.class);
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            seedCampuses(em);
            seedAdmin001(em);
            seedRemainingAdmins(em);
            seedMajors(em);
            seedSpecializations(em);
            seedCurriculums(em);
            seedStaffs(em);
            seedDeputyStaffs(em);

            seedMajorLecturers(em);
            seedStudents(em);

            seedMajorSubjects(em);
            seedMinorSubjects(em);
            seedSpecializedSubjects(em);

            seedStudentBalancesAndDepositHistory(em);
            seedTuitionByYear(em);
            seedSlots(em);
            seedRooms(em);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ===================== SEED METHODS =====================

    private static void seedCampuses(EntityManager em) {
        String[] ids = {"CAMP01", "CAMP02", "CAMP03", "CAMP04", "CAMP05", "CAMP06", "CAMP07", "CAMP08", "CAMP09", "CAMP10"};
        String[] names = {"Hà Nội", "TP.HCM", "Đà Nẵng", "Hải Phòng", "Cần Thơ", "Huế", "Quy Nhơn", "Nha Trang", "Vũng Tàu", "Thanh Hóa"};
        LocalDate[] opens = {
                LocalDate.of(2010, 1, 1), LocalDate.of(2012, 5, 15), LocalDate.of(2015, 3, 20), LocalDate.of(2016, 9, 10),
                LocalDate.of(2018, 11, 25), LocalDate.of(2019, 4, 5), LocalDate.of(2020, 2, 14), LocalDate.of(2021, 7, 1),
                LocalDate.of(2022, 8, 30), LocalDate.of(2023, 10, 12)
        };

        for (int i = 0; i < 10; i++) {
            if (exists(em, Campuses.class, "campusId", ids[i])) continue;
            Campuses c = new Campuses();
            c.setCampusId(ids[i]);
            c.setCampusName(names[i] + " Campus");
            c.setOpeningDay(opens[i]);
            c.setDescription("Campus chính tại " + names[i]);
            em.persist(c);
        }
    }

    private static void seedAdmin001(EntityManager em) {
        String id = "admin001";
        if (exists(em, Admins.class, "id", id)) return;

        Admins admin = new Admins();
        admin.setId(id);
        admin.setFirstName("Nguyễn");
        admin.setLastName("Văn A");
        admin.setEmail("admin1@example.com");
        admin.setPhoneNumber("+84912345678");
        admin.setBirthDate(LocalDate.of(1980, 1, 15));
        admin.setGender(Gender.MALE);
        admin.setCountry("Vietnam");
        admin.setProvince("Hà Nội");
        admin.setCity("Hà Nội");
        admin.setDistrict("Cầu Giấy");
        admin.setWard("Dịch Vọng");
        admin.setStreet("123 Trần Duy Hưng");
        admin.setPostalCode("100000");
        admin.setCreatedDate(LocalDateTime.now());

        Campuses campus = find(em, Campuses.class, "campusId", "CAMP01");
        admin.setCampus(campus);
        admin.setCreator(admin);
        em.persist(admin);
        createAuth(em, id, admin);

        for (int i = 0; i < 10; i++) {
            Campuses c = find(em, Campuses.class, "campusId", "CAMP" + String.format("%02d", i + 1));
            if (c != null && c.getCreator() == null) {
                c.setCreator(admin);
                em.merge(c);
            }
        }
    }

    private static void seedRemainingAdmins(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", "admin001");
        if (creator == null) throw new IllegalStateException("admin001 must exist!");

        String[] ids = {"admin002", "admin003", "admin004", "admin005", "admin006", "admin007", "admin008", "admin009", "admin010"};
        String[] firstNames = {"Trần", "Lê", "Phạm", "Hoàng", "Vũ", "Đặng", "Bùi", "Ngô", "Dương"};
        String[] lastNames = {"Thị B", "Văn C", "Thị D", "Văn E", "Thị F", "Văn G", "Thị H", "Văn I", "Thị K"};
        String[] emails = {"admin2@example.com", "admin3@example.com", "admin4@example.com", "admin5@example.com", "admin6@example.com",
                "admin7@example.com", "admin8@example.com", "admin9@example.com", "admin10@example.com"};
        String[] phones = {"+84987654321", "+84911223344", "+84955667788", "+84933445566",
                "+84977889900", "+84922334455", "+84966778899", "+84944556677", "+84988990011"};
        LocalDate[] births = {
                LocalDate.of(1982, 3, 22), LocalDate.of(1978, 7, 10), LocalDate.of(1985, 11, 30),
                LocalDate.of(1981, 5, 18), LocalDate.of(1987, 9, 25), LocalDate.of(1979, 12, 12),
                LocalDate.of(1983, 4, 8), LocalDate.of(1986, 6, 20), LocalDate.of(1984, 8, 14)
        };

        for (int i = 0; i < 9; i++) {
            if (exists(em, Admins.class, "id", ids[i])) continue;

            Admins admin = new Admins();
            admin.setId(ids[i]);
            admin.setFirstName(firstNames[i]);
            admin.setLastName(lastNames[i]);
            admin.setEmail(emails[i]);
            admin.setPhoneNumber(phones[i]);
            admin.setBirthDate(births[i]);
            admin.setGender(i % 2 == 0 ? Gender.FEMALE : Gender.MALE);
            admin.setCountry("Vietnam");
            admin.setProvince(i < 4 ? "Hà Nội" : "TP.HCM");
            admin.setCity(i < 4 ? "Hà Nội" : "TP.HCM");
            admin.setDistrict(i % 2 == 0 ? "Cầu Giấy" : "Quận 1");
            admin.setWard(i % 2 == 0 ? "Dịch Vọng" : "Bến Nghé");
            admin.setStreet(i % 2 == 0 ? "123 Trần Duy Hưng" : "45 Lê Lợi");
            admin.setPostalCode(i < 4 ? "100000" : "700000");
            admin.setCreatedDate(LocalDateTime.now().minusDays(300 - i * 10));

            String campusId = "CAMP0" + ((i % 5) + 2);
            Campuses campus = find(em, Campuses.class, "campusId", campusId);
            admin.setCampus(campus);
            admin.setCreator(creator);

            em.persist(admin);
            createAuth(em, ids[i], admin);
        }
    }

    private static void seedMajors(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", "admin001");
        String[] ids = {"GBH", "GCH", "GDH", "GKH", "GKT", "GDT", "GAT", "GNT", "GFT", "GHT"};
        String[] names = {
                "Quản trị Kinh doanh", "Công nghệ Thông tin", "Thiết kế Đồ họa", "Marketing",
                "Kế toán", "Khoa học Dữ liệu", "Trí tuệ Nhân tạo", "An ninh Mạng", "Tài chính", "Quản trị Nhân sự"
        };

        for (int i = 0; i < 10; i++) {
            if (exists(em, Majors.class, "majorId", ids[i])) continue;
            Majors m = new Majors();
            m.setMajorId(ids[i]);
            m.setMajorName(names[i]);
            m.setCreator(creator);
            m.setCreatedDate(LocalDate.now().minusDays(300 - i * 10));
            em.persist(m);
        }
    }

    private static void seedSpecializations(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", "admin001");
        String[][] specs = {
                {"SPEC_IT_SE", "Kỹ thuật Phần mềm", "GCH"},
                {"SPEC_IT_AI", "Trí tuệ Nhân tạo", "GCH"},
                {"SPEC_IT_CS", "An ninh Mạng", "GCH"},
                {"SPEC_BUS_FIN", "Tài chính Ngân hàng", "GBH"},
                {"SPEC_BUS_HR", "Quản trị Nhân sự", "GBH"},
                {"SPEC_DES_UI", "Thiết kế UI/UX", "GDH"},
                {"SPEC_DES_3D", "Hoạt hình 3D", "GDH"},
                {"SPEC_MKT_DIG", "Marketing Kỹ thuật số", "GKH"},
                {"SPEC_MKT_SM", "Social Media", "GKH"},
                {"SPEC_ACC_TAX", "Kế toán Thuế", "GKT"}
        };

        for (String[] s : specs) {
            if (exists(em, Specialization.class, "specializationId", s[0])) continue;
            Specialization spec = new Specialization();
            spec.setSpecializationId(s[0]);
            spec.setSpecializationName(s[1]);
            spec.setMajor(find(em, Majors.class, "majorId", s[2]));
            spec.setCreator(creator);
            em.persist(spec);
        }
    }

    private static void seedCurriculums(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", "admin001");
        if (!exists(em, Curriculum.class, "curriculumId", "CURR01")) {
            Curriculum c = new Curriculum();
            c.setCurriculumId("CURR01");
            c.setName("BTEC");
            c.setDescription("Chương trình BTEC");
            c.setCreator(creator);
            c.setCreatedAt(LocalDateTime.now());
            em.persist(c);
        }
    }

    private static void seedStaffs(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", "admin001");
        Majors[] majors = new Majors[10];
        String[] majorIds = {"GBH", "GCH", "GDH", "GKH", "GKT", "GDT", "GAT", "GNT", "GFT", "GHT"};
        for (int i = 0; i < 10; i++) majors[i] = find(em, Majors.class, "majorId", majorIds[i]);

        Campuses[] campuses = new Campuses[10];
        for (int i = 0; i < 10; i++) campuses[i] = find(em, Campuses.class, "campusId", "CAMP" + String.format("%02d", i + 1));

        String[] firstNames = {"Minh", "Lan", "Hùng", "Mai", "Tuấn", "Hương", "Khoa", "Ngọc", "Đức", "Thảo"};
        String[] lastNames = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Vũ", "Đặng", "Bùi", "Ngô", "Dương"};

        for (int i = 0; i < 10; i++) {
            String id = "staff" + String.format("%03d", i + 1);
            if (exists(em, Staffs.class, "id", id)) continue;
            Staffs s = new Staffs();
            s.setId(id);
            s.setFirstName(firstNames[i]);
            s.setLastName(lastNames[i]);
            s.setEmail(id + "@staff.com");
            s.setPhoneNumber("+8491" + String.format("%08d", 1000000 + i * 12345));
            s.setBirthDate(LocalDate.of(1985 + i % 5, 1 + i % 12, 1 + i % 28));
            s.setGender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE);
            s.setCountry("Vietnam");
            s.setProvince("Hà Nội");
            s.setCity("Hà Nội");
            s.setDistrict("Cầu Giấy");
            s.setWard("Dịch Vọng");
            s.setStreet("123 Trần Duy Hưng");
            s.setPostalCode("100000");
            s.setMajorManagement(majors[i]);
            s.setCampus(campuses[i]);
            s.setCreator(creator);
            em.persist(s);
            createAuth(em, id, s);
        }
    }

    private static void seedDeputyStaffs(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", "admin001");
        String[] firstNames = {"Anh", "Bình", "Cường", "Duyên", "Đạt", "Hà", "Khánh", "Linh", "Mạnh", "Nhi"};
        String[] lastNames = {"Trần", "Lê", "Phạm", "Hoàng", "Vũ", "Đặng", "Bùi", "Ngô", "Dương", "Nguyễn"};

        for (int i = 0; i < 10; i++) {
            String id = "deputy" + String.format("%03d", i + 1);
            if (exists(em, DeputyStaffs.class, "id", id)) continue;
            DeputyStaffs d = new DeputyStaffs();
            d.setId(id);
            d.setFirstName(firstNames[i]);
            d.setLastName(lastNames[i]);
            d.setEmail(id + "@deputy.com");
            d.setPhoneNumber("+8492" + String.format("%08d", 2000000 + i * 54321));
            d.setBirthDate(LocalDate.of(1990 + i % 5, 1 + i % 12, 1 + i % 28));
            d.setGender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE);
            d.setCountry("Vietnam");
            d.setProvince("TP.HCM");
            d.setCity("TP.HCM");
            d.setDistrict("Quận 1");
            d.setWard("Bến Nghé");
            d.setStreet("45 Lê Lợi");
            d.setPostalCode("700000");
            d.setCampus(find(em, Campuses.class, "campusId", "CAMP0" + (i % 5 == 0 ? 1 : i % 5 + 1)));
            d.setCreator(creator);
            em.persist(d);
            createAuth(em, id, d);
        }
    }

    // 100 MAJOR LECTURERS: 10 per Major → CHỈ STAFF CÙNG MAJOR + CAMPUS MỚI TẠO
    private static void seedMajorLecturers(EntityManager em) {
        String[] majorIds = {"GBH", "GCH", "GDH", "GKH", "GKT", "GDT", "GAT", "GNT", "GFT", "GHT"};
        String[] firstNames = {"Hải", "Yến", "Phong", "Thư", "Kiên", "Tâm", "Long", "Huyền", "Quân", "Mai",
                "Khánh", "Linh", "Minh", "Ngọc", "Phương", "Quỳnh", "Sơn", "Tùng", "Uyên", "Vân"};
        String[] lastNames = {"Lê", "Phạm", "Hoàng", "Vũ", "Đặng", "Bùi", "Ngô", "Dương", "Nguyễn", "Trần"};

        int lecturerIndex = 1;
        for (int m = 0; m < 10; m++) {
            Majors major = find(em, Majors.class, "majorId", majorIds[m]);
            Staffs creator = findStaffByMajorId(em, major.getMajorId());
            Campuses campus = creator.getCampus(); // LẤY CAMPUS TỪ STAFF → BẢO MẬT

            for (int i = 0; i < 10; i++) {
                String id = "lect" + String.format("%03d", lecturerIndex++);
                if (exists(em, MajorLecturers.class, "id", id)) continue;

                MajorLecturers l = new MajorLecturers();
                l.setId(id);
                l.setFirstName(firstNames[i]);
                l.setLastName(lastNames[i % 10]);
                l.setEmail(id + "@lecturer.com");
                l.setPhoneNumber("+8493" + String.format("%08d", 3000000 + lecturerIndex * 111));
                l.setBirthDate(LocalDate.of(1975 + i, 1 + i % 12, 1 + i % 28));
                l.setGender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE);
                l.setCountry("Vietnam");
                l.setProvince("Đà Nẵng");
                l.setCity("Đà Nẵng");
                l.setDistrict("Hải Châu");
                l.setWard("Hòa Cường");
                l.setStreet("78 Nguyễn Văn Linh");
                l.setPostalCode("550000");
                l.setMajorManagement(major);
                l.setCampus(campus);
                l.setEmploymentTypes(EmploymentTypes.FULL_TIME);
                l.setCreator(creator);
                em.persist(l);
                createAuth(em, id, l);
            }
        }
    }

    // 100 STUDENTS: 10 per Specialization → CHỈ STAFF CÙNG MAJOR + CAMPUS MỚI TẠO
    private static void seedStudents(EntityManager em) {
        Curriculum curr = find(em, Curriculum.class, "curriculumId", "CURR01");

        String[] specIds = {"SPEC_IT_SE", "SPEC_IT_AI", "SPEC_IT_CS", "SPEC_BUS_FIN", "SPEC_BUS_HR",
                "SPEC_DES_UI", "SPEC_DES_3D", "SPEC_MKT_DIG", "SPEC_MKT_SM", "SPEC_ACC_TAX"};
        String[] firstNames = {"An", "Bình", "Cường", "Duyên", "Đạt", "Hà", "Khánh", "Linh", "Mạnh", "Nhi",
                "Oanh", "Phúc", "Quang", "Rạng", "Sáng", "Tâm", "Uyên", "Vũ", "Xuân", "Yến"};
        String[] lastNames = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Vũ", "Đặng", "Bùi", "Ngô", "Dương"};

        int studentIndex = 1;
        for (int s = 0; s < 10; s++) {
            Specialization spec = find(em, Specialization.class, "specializationId", specIds[s]);
            Staffs creator = findStaffByMajorId(em, spec.getMajor().getMajorId());
            Campuses campus = creator.getCampus();

            for (int i = 0; i < 10; i++) {
                String id = "stu" + String.format("%03d", studentIndex++);
                if (exists(em, Students.class, "id", id)) continue;

                Students student = new Students();
                student.setId(id);
                student.setFirstName(firstNames[i]);
                student.setLastName(lastNames[i % 10]);
                student.setEmail(id + "@student.com");
                student.setPhoneNumber("+8495" + String.format("%08d", 5000000 + studentIndex * 333));
                student.setBirthDate(LocalDate.of(2000 + i % 5, 1 + i % 12, 1 + i % 28));
                student.setGender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE);
                student.setCountry("Vietnam");
                student.setProvince("Cần Thơ");
                student.setCity("Cần Thơ");
                student.setDistrict("Ninh Kiều");
                student.setWard("Cái Khế");
                student.setStreet("89 Ninh Kiều");
                student.setPostalCode("900000");
                student.setAdmissionYear(2025);
                student.setCreator(creator);
                student.setCampus(campus);
                student.setSpecialization(spec);
                student.setCurriculum(curr);
                em.persist(student);
                createAuth(em, id, student);
            }
        }
    }

    private static void seedMajorSubjects(EntityManager em) {
        Curriculum curr = find(em, Curriculum.class, "curriculumId", "CURR01");
        Admins acceptor = find(em, Admins.class, "id", "admin001");
        String[] majorIds = {"GBH", "GCH", "GDH", "GKH", "GKT", "GDT", "GAT", "GNT", "GFT", "GHT"};
        String[] names = {
                "Nhập môn Quản trị", "Lập trình Java", "Thiết kế Cơ bản", "Marketing Căn bản",
                "Kế toán Tài chính", "Phân tích Dữ liệu", "AI Cơ bản", "Mạng Máy tính",
                "Tài chính Doanh nghiệp", "Quản lý Nhân sự"
        };

        for (int i = 0; i < 10; i++) {
            String id = "SUB_MAJ_" + String.format("%03d", i + 1);
            if (exists(em, MajorSubjects.class, "subjectId", id)) continue;

            Majors major = find(em, Majors.class, "majorId", majorIds[i]);
            Staffs creator = findStaffByMajorId(em, majorIds[i]);

            MajorSubjects s = new MajorSubjects();
            s.setSubjectId(id);
            s.setSubjectName(names[i]);
            s.setSemester(i % 8 + 1);
            s.setIsAccepted(i % 3 == 0);
            s.setAcceptor(acceptor);
            s.setCreator(creator);
            s.setMajor(major);
            s.setCurriculum(curr);
            em.persist(s);
        }
    }

    private static void seedMinorSubjects(EntityManager em) {
        Admins acceptor = find(em, Admins.class, "id", "admin001");
        String[] names = {
                "Tiếng Anh Giao tiếp", "Kỹ năng Mềm", "Tư duy Phản biện", "Quản lý Thời gian",
                "Làm việc Nhóm", "Kỹ năng Thuyết trình", "Viết CV", "Phỏng vấn", "Tinh thần Khởi nghiệp", "Sức khỏe Tinh thần"
        };

        for (int i = 0; i < 10; i++) {
            String id = "SUB_MIN_" + String.format("%03d", i + 1);
            if (exists(em, MinorSubjects.class, "subjectId", id)) continue;

            DeputyStaffs creator = find(em, DeputyStaffs.class, "id", "deputy" + String.format("%03d", i + 1));

            MinorSubjects s = new MinorSubjects();
            s.setSubjectId(id);
            s.setSubjectName(names[i]);
            s.setSemester(i % 4 + 1);
            s.setIsAccepted(i % 4 == 0);
            s.setAcceptor(acceptor);
            s.setCreator(creator);
            em.persist(s);
        }
    }

    private static void seedSpecializedSubjects(EntityManager em) {
        Curriculum curr = find(em, Curriculum.class, "curriculumId", "CURR01");
        Admins acceptor = find(em, Admins.class, "id", "admin001");
        String[] specIds = {"SPEC_IT_SE", "SPEC_IT_AI", "SPEC_IT_CS", "SPEC_BUS_FIN", "SPEC_BUS_HR",
                "SPEC_DES_UI", "SPEC_DES_3D", "SPEC_MKT_DIG", "SPEC_MKT_SM", "SPEC_ACC_TAX"};
        String[] names = {
                "Phát triển Web", "Machine Learning", "Penetration Testing", "Ngân hàng Số",
                "Tuyển dụng", "Figma Design", "Blender 3D", "SEO & SEM", "TikTok Marketing", "Kiểm toán"
        };

        for (int i = 0; i < 10; i++) {
            String id = "SUB_SPEC_" + String.format("%03d", i + 1);
            if (exists(em, SpecializedSubject.class, "subjectId", id)) continue;

            Specialization spec = find(em, Specialization.class, "specializationId", specIds[i]);
            Staffs creator = findStaffByMajorId(em, spec.getMajor().getMajorId());

            SpecializedSubject s = new SpecializedSubject();
            s.setSubjectId(id);
            s.setSubjectName(names[i]);
            s.setSemester(i % 6 + 3);
            s.setIsAccepted(i % 5 == 0);
            s.setAcceptor(acceptor);
            s.setCreator(creator);
            s.setSpecialization(spec);
            s.setCurriculum(curr);
            em.persist(s);
        }
    }

    private static void seedStudentBalancesAndDepositHistory(EntityManager em) {
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 100; i++) {
            String studentId = "stu" + String.format("%03d", i + 1);
            Students student = find(em, Students.class, "id", studentId);
            if (student == null || exists(em, AccountBalances.class, "studentId", studentId)) continue;

            AccountBalances balance = new AccountBalances();
            balance.setStudentId(studentId);
            balance.setStudent(student);
            balance.setBalance(INITIAL_DEPOSIT_AMOUNT);
            balance.setLastUpdated(now);
            em.persist(balance);

            String historyId = "DEP_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            DepositHistories deposit = new DepositHistories();
            deposit.setHistoryId(historyId);
            deposit.setStudent(student);
            deposit.setAccountBalance(balance);
            deposit.setAmount(INITIAL_DEPOSIT_AMOUNT);
            deposit.setDepositTime(now);
            deposit.setCurrentAmount(BigDecimal.valueOf(INITIAL_DEPOSIT_AMOUNT));
            deposit.setCreatedAt(now);
            deposit.setStatus(Status.COMPLETED);
            deposit.setDescription("Initial deposit of 1000 USD for new student account.");
            em.persist(deposit);
        }
    }

    private static void seedTuitionByYear(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", "admin001");
        List<Campuses> campuses = em.createQuery("SELECT c FROM Campuses c", Campuses.class).getResultList();
        List<Subjects> subjects = new ArrayList<>();
        subjects.addAll(em.createQuery("SELECT s FROM MajorSubjects s", MajorSubjects.class).getResultList());
        subjects.addAll(em.createQuery("SELECT s FROM MinorSubjects s", MinorSubjects.class).getResultList());
        subjects.addAll(em.createQuery("SELECT s FROM SpecializedSubject s", SpecializedSubject.class).getResultList());

        Integer[] years = {2025, 2026, 2027};
        Random rand = new Random();

        for (Subjects subject : subjects) {
            for (Integer year : years) {
                for (Campuses campus : campuses) {
                    TuitionByYearId id = new TuitionByYearId(subject.getSubjectId(), year, campus.getCampusId());
                    if (existsTuitionByYear(em, id)) continue;

                    TuitionByYear t = new TuitionByYear();
                    t.setId(id);
                    t.setSubject(subject);
                    t.setCampus(campus);
                    t.setAdmissionYear(year);
                    double base = 10 + (rand.nextDouble() * 10);
                    t.setTuition(roundTo2Decimals(base));
                    t.setReStudyTuition(roundTo2Decimals(base * 0.7));
                    t.setContractStatus(ContractStatus.ACTIVE);
                    t.setCreator(creator);
                    em.persist(t);
                }
            }
        }
    }

    private static double roundTo2Decimals(double val) {
        return Math.round(val * 100.0) / 100.0;
    }

    private static void seedSlots(EntityManager em) {
        String[][] slotData = {
                {"SLOT01", "Slot 1", "07:10", "08:40"},
                {"SLOT02", "Slot 2", "08:50", "10:20"},
                {"SLOT03", "Slot 3", "10:30", "12:00"},
                {"SLOT04", "Slot 4", "12:50", "14:20"},
                {"SLOT05", "Slot 5", "14:30", "16:00"},
                {"SLOT06", "Slot 6", "16:10", "17:40"}
        };

        for (String[] data : slotData) {
            if (exists(em, Slots.class, "slotId", data[0])) continue;
            Slots slot = new Slots();
            slot.setSlotId(data[0]);
            slot.setSlotName(data[1]);
            slot.setStartTime(LocalTime.parse(data[2]));
            slot.setEndTime(LocalTime.parse(data[3]));
            em.persist(slot);
        }
    }

    private static void seedRooms(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", "admin001");

        // 10 Offline Rooms
        String[] physicalIds = {"G101", "G102", "G201", "G202", "G301", "G302", "G401", "G402", "G501", "G502"};
        String[] physicalNames = {
                "Phòng G101 - Tầng 1", "Phòng G102 - Tầng 1",
                "Phòng G201 - Tầng 2", "Phòng G202 - Tầng 2",
                "Phòng G301 - Tầng 3", "Phòng G302 - Tầng 3",
                "Phòng G401 - Tầng 4", "Phòng G402 - Tầng 4",
                "Phòng G501 - Tầng 5", "Phòng G502 - Tầng 5"
        };

        for (int i = 0; i < 10; i++) {
            if (exists(em, OfflineRooms.class, "roomId", physicalIds[i])) continue;
            Campuses campus = find(em, Campuses.class, "campusId", "CAMP" + String.format("%02d", (i % 5) + 1));
            OfflineRooms room = new OfflineRooms();
            room.setRoomId(physicalIds[i]);
            room.setRoomName(physicalNames[i]);
            room.setCreator(creator);
            room.setCampus(campus);
            room.setFloor((i / 2) + 1);
            em.persist(room);
        }

        // 10 Online Rooms
        String[] onlineIds = {"ONLINE01", "ONLINE02", "ZOOM01", "ZOOM02", "MEET01", "MEET02", "TEAMS01", "TEAMS02", "WEBEX01", "WEBEX02"};
        String[] onlineNames = {
                "Phòng Online 01", "Phòng Online 02",
                "Zoom Room 01", "Zoom Room 02",
                "Google Meet 01", "Google Meet 02",
                "Microsoft Teams 01", "Microsoft Teams 02",
                "Cisco Webex 01", "Cisco Webex 02"
        };
        String[] links = {
                "https://zoom.us/j/1234567890", "https://zoom.us/j/9876543210",
                "https://zoom.us/j/1112223334", "https://zoom.us/j/4445556667",
                "https://meet.google.com/abc-defg-hij", "https://meet.google.com/xyz-abcd-efg",
                "https://teams.microsoft.com/l/meetup-join/19%3A...", "https://teams.microsoft.com/l/meetup-join/19%3A...",
                "https://webex.com/meet/room1", "https://webex.com/meet/room2"
        };

        for (int i = 0; i < 10; i++) {
            if (exists(em, OnlineRooms.class, "roomId", onlineIds[i])) continue;
            Campuses campus = find(em, Campuses.class, "campusId", "CAMP" + String.format("%02d", (i % 5) + 6));
            OnlineRooms room = new OnlineRooms();
            room.setRoomId(onlineIds[i]);
            room.setRoomName(onlineNames[i]);
            room.setCreator(creator);
            room.setCampus(campus);
            room.setLink(links[i]);
            em.persist(room);
        }
    }

    // ===================== HELPER METHODS =====================

    private static void createAuth(EntityManager em, String personId, Persons person) {
        if (exists(em, Authenticators.class, "personId", personId)) return;
        Authenticators auth = new Authenticators();
        auth.setPersonId(personId);
        auth.setPerson(person);
        auth.setPassword(DEFAULT_PASSWORD);
        em.persist(auth);
    }

    private static <T> boolean exists(EntityManager em, Class<T> clazz, String idField, String idValue) {
        try {
            String jpql = "SELECT 1 FROM " + clazz.getSimpleName() + " e WHERE e." + idField + " = :id";
            em.createQuery(jpql, Integer.class).setParameter("id", idValue).getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    private static boolean existsTuitionByYear(EntityManager em, TuitionByYearId id) {
        try {
            String jpql = "SELECT 1 FROM TuitionByYear t WHERE t.id.subjectId = :subjectId AND t.id.admissionYear = :admissionYear AND t.id.campusId = :campusId";
            em.createQuery(jpql, Integer.class)
                    .setParameter("subjectId", id.getSubjectId())
                    .setParameter("admissionYear", id.getAdmissionYear())
                    .setParameter("campusId", id.getCampusId())
                    .getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    private static <T> T find(EntityManager em, Class<T> clazz, String idField, String idValue) {
        try {
            String jpql = "SELECT e FROM " + clazz.getSimpleName() + " e WHERE e." + idField + " = :id";
            return em.createQuery(jpql, clazz).setParameter("id", idValue).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private static Staffs findStaffByMajorId(EntityManager em, String majorId) {
        try {
            String jpql = "SELECT s FROM Staffs s WHERE s.majorManagement.majorId = :majorId";
            return em.createQuery(jpql, Staffs.class)
                    .setParameter("majorId", majorId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}