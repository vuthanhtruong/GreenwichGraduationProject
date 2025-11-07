package com.example.demo;

import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.campus.model.Campuses;
import com.example.demo.curriculum.model.Curriculum;
import com.example.demo.entity.Enums.*;
import com.example.demo.major.model.Majors;
import com.example.demo.specialization.model.Specialization;
import com.example.demo.subject.majorSubject.model.MajorSubjects;
import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import com.example.demo.user.admin.model.Admins;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.user.person.model.Persons;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
public class DemoApplication {

    private static final String DEFAULT_PASSWORD = "Anhnam123";

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(DemoApplication.class, args);
        EntityManagerFactory emf = context.getBean(EntityManagerFactory.class);
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            // 1. Seed Campuses
            seedCampuses(em);

            // 2. Seed admin001 (tự tạo, có campus + creator = chính nó)
            seedAdmin001(em);

            // 3. Seed các admin còn lại
            seedRemainingAdmins(em);

            // 4. Các seed khác
            seedMajors(em);
            seedSpecializations(em);
            seedCurriculums(em);
            seedStaffs(em);
            seedDeputyStaffs(em);
            seedMajorLecturers(em);
            seedMinorLecturers(em);
            seedStudents(em); // admissionYear từ 2025 trở lên
            seedMajorSubjects(em);
            seedMinorSubjects(em);
            seedSpecializedSubjects(em);

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
        LocalDate[] opens = {LocalDate.of(2010, 1, 1), LocalDate.of(2012, 5, 15), LocalDate.of(2015, 3, 20), LocalDate.of(2016, 9, 10),
                LocalDate.of(2018, 11, 25), LocalDate.of(2019, 4, 5), LocalDate.of(2020, 2, 14), LocalDate.of(2021, 7, 1),
                LocalDate.of(2022, 8, 30), LocalDate.of(2023, 10, 12)};

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
        admin.setCreator(admin); // tự tạo

        em.persist(admin);
        createAuth(em, id, admin);

        // Cập nhật creator cho tất cả campus
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
        LocalDate[] births = {LocalDate.of(1982, 3, 22), LocalDate.of(1978, 7, 10), LocalDate.of(1985, 11, 30),
                LocalDate.of(1981, 5, 18), LocalDate.of(1987, 9, 25), LocalDate.of(1979, 12, 12), LocalDate.of(1983, 4, 8),
                LocalDate.of(1986, 6, 20), LocalDate.of(1984, 8, 14)};

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
        String[] names = {"Quản trị Kinh doanh", "Công nghệ Thông tin", "Thiết kế Đồ họa", "Marketing",
                "Kế toán", "Khoa học Dữ liệu", "Trí tuệ Nhân tạo", "An ninh Mạng", "Tài chính", "Quản trị Nhân sự"};

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
            c.setCurriculumId("CURR01"); c.setName("BTEC"); c.setDescription("Chương trình BTEC");
            c.setCreator(creator); c.setCreatedAt(LocalDateTime.now());
            em.persist(c);
        }
        if (!exists(em, Curriculum.class, "curriculumId", "CURR02")) {
            Curriculum c = new Curriculum();
            c.setCurriculumId("CURR02"); c.setName("3+0"); c.setDescription("Chương trình 3+0");
            c.setCreator(creator); c.setCreatedAt(LocalDateTime.now());
            em.persist(c);
        }
    }

    private static void seedStaffs(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", "admin001");
        Majors[] majors = {
                find(em, Majors.class, "majorId", "GBH"), find(em, Majors.class, "majorId", "GCH"),
                find(em, Majors.class, "majorId", "GDH"), find(em, Majors.class, "majorId", "GKH"),
                find(em, Majors.class, "majorId", "GKT"), find(em, Majors.class, "majorId", "GDT"),
                find(em, Majors.class, "majorId", "GAT"), find(em, Majors.class, "majorId", "GNT"),
                find(em, Majors.class, "majorId", "GFT"), find(em, Majors.class, "majorId", "GHT")
        };
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

    private static void seedMajorLecturers(EntityManager em) {
        Staffs creator = find(em, Staffs.class, "id", "staff001");
        String[] firstNames = {"Hải", "Yến", "Phong", "Thư", "Kiên", "Tâm", "Long", "Huyền", "Quân", "Mai"};
        String[] lastNames = {"Lê", "Phạm", "Hoàng", "Vũ", "Đặng", "Bùi", "Ngô", "Dương", "Nguyễn", "Trần"};

        for (int i = 0; i < 10; i++) {
            String id = "lect" + String.format("%03d", i + 1);
            if (exists(em, MajorLecturers.class, "id", id)) continue;
            MajorLecturers l = new MajorLecturers();
            l.setId(id);
            l.setFirstName(firstNames[i]);
            l.setLastName(lastNames[i]);
            l.setEmail(id + "@lecturer.com");
            l.setPhoneNumber("+8493" + String.format("%08d", 3000000 + i * 11111));
            l.setBirthDate(LocalDate.of(1975 + i % 10, 1 + i % 12, 1 + i % 28));
            l.setGender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE);
            l.setCountry("Vietnam");
            l.setProvince("Đà Nẵng");
            l.setCity("Đà Nẵng");
            l.setDistrict("Hải Châu");
            l.setWard("Hòa Cường");
            l.setStreet("78 Nguyễn Văn Linh");
            l.setPostalCode("550000");
            l.setMajorManagement(find(em, Majors.class, "majorId", i < 5 ? "GCH" : "GBH"));
            l.setCampus(find(em, Campuses.class, "campusId", "CAMP0" + (i % 5 == 0 ? 1 : i % 5 + 1)));
            l.setEmploymentTypes(EmploymentTypes.FULL_TIME);
            l.setCreator(creator);
            em.persist(l);
            createAuth(em, id, l);
        }
    }

    private static void seedMinorLecturers(EntityManager em) {
        DeputyStaffs creator = find(em, DeputyStaffs.class, "id", "deputy001");
        String[] firstNames = {"Tùng", "Hương", "Khoa", "Ngọc", "Đức", "Thảo", "Minh", "Lan", "Hùng", "Mai"};
        String[] lastNames = {"Phạm", "Hoàng", "Vũ", "Đặng", "Bùi", "Ngô", "Dương", "Nguyễn", "Trần", "Lê"};

        for (int i = 0; i < 10; i++) {
            String id = "minlect" + String.format("%03d", i + 1);
            if (exists(em, MinorLecturers.class, "id", id)) continue;
            MinorLecturers l = new MinorLecturers();
            l.setId(id);
            l.setFirstName(firstNames[i]);
            l.setLastName(lastNames[i]);
            l.setEmail(id + "@minor.com");
            l.setPhoneNumber("+8494" + String.format("%08d", 4000000 + i * 22222));
            l.setBirthDate(LocalDate.of(1980 + i % 8, 1 + i % 12, 1 + i % 28));
            l.setGender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE);
            l.setCountry("Vietnam");
            l.setProvince("Hải Phòng");
            l.setCity("Hải Phòng");
            l.setDistrict("Hồng Bàng");
            l.setWard("Hùng Vương");
            l.setStreet("56 Trần Phú");
            l.setPostalCode("180000");
            l.setCampus(find(em, Campuses.class, "campusId", "CAMP0" + (i % 5 == 0 ? 1 : i % 5 + 1)));
            l.setEmploymentTypes(EmploymentTypes.PART_TIME);
            l.setCreator(creator);
            em.persist(l);
            createAuth(em, id, l);
        }
    }

    private static void seedStudents(EntityManager em) {
        Staffs creator = find(em, Staffs.class, "id", "staff001");
        Curriculum curr = find(em, Curriculum.class, "curriculumId", "CURR01");
        String[] firstNames = {"An", "Bình", "Cường", "Duyên", "Đạt", "Hà", "Khánh", "Linh", "Mạnh", "Nhi"};
        String[] lastNames = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Vũ", "Đặng", "Bùi", "Ngô", "Dương"};

        for (int i = 0; i < 10; i++) {
            String id = "stu" + String.format("%03d", i + 1);
            if (exists(em, Students.class, "id", id)) continue;
            Students s = new Students();
            s.setId(id);
            s.setFirstName(firstNames[i]);
            s.setLastName(lastNames[i]);
            s.setEmail(id + "@student.com");
            s.setPhoneNumber("+8495" + String.format("%08d", 5000000 + i * 33333));
            s.setBirthDate(LocalDate.of(2000 + i % 5, 1 + i % 12, 1 + i % 28));
            s.setGender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE);
            s.setCountry("Vietnam");
            s.setProvince("Cần Thơ");
            s.setCity("Cần Thơ");
            s.setDistrict("Ninh Kiều");
            s.setWard("Cái Khế");
            s.setStreet("89 Ninh Kiều");
            s.setPostalCode("900000");

            // admissionYear từ 2025 trở lên
            s.setAdmissionYear(2025 + (i % 3)); // 2025, 2026, 2027

            s.setCreator(creator);
            s.setCampus(find(em, Campuses.class, "campusId", "CAMP0" + (i % 5 == 0 ? 1 : i % 5 + 1)));
            s.setSpecialization(find(em, Specialization.class, "specializationId", "SPEC_IT_SE"));
            s.setCurriculum(curr);
            em.persist(s);
            createAuth(em, id, s);
        }
    }

    // ===================== SUBJECTS SEED =====================

    private static void seedMajorSubjects(EntityManager em) {
        Staffs[] creators = new Staffs[10];
        for (int i = 0; i < 10; i++) creators[i] = find(em, Staffs.class, "id", "staff" + String.format("%03d", i + 1));
        Majors[] majors = {
                find(em, Majors.class, "majorId", "GBH"), find(em, Majors.class, "majorId", "GCH"),
                find(em, Majors.class, "majorId", "GDH"), find(em, Majors.class, "majorId", "GKH"),
                find(em, Majors.class, "majorId", "GKT"), find(em, Majors.class, "majorId", "GDT"),
                find(em, Majors.class, "majorId", "GAT"), find(em, Majors.class, "majorId", "GNT"),
                find(em, Majors.class, "majorId", "GFT"), find(em, Majors.class, "majorId", "GHT")
        };
        Curriculum curr = find(em, Curriculum.class, "curriculumId", "CURR01");
        Admins acceptor = find(em, Admins.class, "id", "admin001");

        String[] names = {"Nhập môn Quản trị", "Lập trình Java", "Thiết kế Cơ bản", "Marketing Căn bản",
                "Kế toán Tài chính", "Phân tích Dữ liệu", "AI Cơ bản", "Mạng Máy tính", "Tài chính Doanh nghiệp", "Quản lý Nhân sự"};

        for (int i = 0; i < 10; i++) {
            String id = "SUB_MAJ_" + String.format("%03d", i + 1);
            if (exists(em, MajorSubjects.class, "subjectId", id)) continue;
            MajorSubjects s = new MajorSubjects();
            s.setSubjectId(id);
            s.setSubjectName(names[i]);
            s.setSemester(i % 8 + 1);
            s.setIsAccepted(i % 3 == 0);
            s.setAcceptor(acceptor);
            s.setCreator(creators[i]);
            s.setMajor(majors[i]);
            s.setCurriculum(curr);
            em.persist(s);
        }
    }

    private static void seedMinorSubjects(EntityManager em) {
        DeputyStaffs[] creators = new DeputyStaffs[10];
        for (int i = 0; i < 10; i++) creators[i] = find(em, DeputyStaffs.class, "id", "deputy" + String.format("%03d", i + 1));
        Admins acceptor = find(em, Admins.class, "id", "admin001");

        String[] names = {"Tiếng Anh Giao tiếp", "Kỹ năng Mềm", "Tư duy Phản biện", "Quản lý Thời gian",
                "Làm việc Nhóm", "Kỹ năng Thuyết trình", "Viết CV", "Phỏng vấn", "Tinh thần Khởi nghiệp", "Sức khỏe Tinh thần"};

        for (int i = 0; i < 10; i++) {
            String id = "SUB_MIN_" + String.format("%03d", i + 1);
            if (exists(em, MinorSubjects.class, "subjectId", id)) continue;
            MinorSubjects s = new MinorSubjects();
            s.setSubjectId(id);
            s.setSubjectName(names[i]);
            s.setSemester(i % 4 + 1);
            s.setIsAccepted(i % 4 == 0);
            s.setAcceptor(acceptor);
            s.setCreator(creators[i]);
            em.persist(s);
        }
    }

    private static void seedSpecializedSubjects(EntityManager em) {
        Staffs[] creators = new Staffs[10];
        for (int i = 0; i < 10; i++) creators[i] = find(em, Staffs.class, "id", "staff" + String.format("%03d", i + 1));
        Specialization[] specs = {
                find(em, Specialization.class, "specializationId", "SPEC_IT_SE"),
                find(em, Specialization.class, "specializationId", "SPEC_IT_AI"),
                find(em, Specialization.class, "specializationId", "SPEC_IT_CS"),
                find(em, Specialization.class, "specializationId", "SPEC_BUS_FIN"),
                find(em, Specialization.class, "specializationId", "SPEC_BUS_HR"),
                find(em, Specialization.class, "specializationId", "SPEC_DES_UI"),
                find(em, Specialization.class, "specializationId", "SPEC_DES_3D"),
                find(em, Specialization.class, "specializationId", "SPEC_MKT_DIG"),
                find(em, Specialization.class, "specializationId", "SPEC_MKT_SM"),
                find(em, Specialization.class, "specializationId", "SPEC_ACC_TAX")
        };
        Curriculum curr = find(em, Curriculum.class, "curriculumId", "CURR01");
        Admins acceptor = find(em, Admins.class, "id", "admin001");

        String[] names = {"Phát triển Web", "Machine Learning", "Penetration Testing", "Ngân hàng Số",
                "Tuyển dụng", "Figma Design", "Blender 3D", "SEO & SEM", "TikTok Marketing", "Kiểm toán"};

        for (int i = 0; i < 10; i++) {
            String id = "SUB_SPEC_" + String.format("%03d", i + 1);
            if (exists(em, SpecializedSubject.class, "subjectId", id)) continue;
            SpecializedSubject s = new SpecializedSubject();
            s.setSubjectId(id);
            s.setSubjectName(names[i]);
            s.setSemester(i % 6 + 3);
            s.setIsAccepted(i % 5 == 0);
            s.setAcceptor(acceptor);
            s.setCreator(creators[i]);
            s.setSpecialization(specs[i]);
            s.setCurriculum(curr);
            em.persist(s);
        }
    }

    // ===================== AUTH & HELPER =====================
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

    private static <T> T find(EntityManager em, Class<T> clazz, String idField, String idValue) {
        try {
            String jpql = "SELECT e FROM " + clazz.getSimpleName() + " e WHERE e." + idField + " = :id";
            return em.createQuery(jpql, clazz).setParameter("id", idValue).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}