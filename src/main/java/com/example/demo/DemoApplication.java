package com.example.demo;

import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.campus.model.Campuses;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.curriculum.model.Curriculum;
import com.example.demo.entity.Enums.ContractStatus;
import com.example.demo.entity.Enums.EmploymentTypes;
import com.example.demo.entity.Enums.Gender;
import com.example.demo.entity.Enums.Sessions;
import com.example.demo.entity.Enums.Status;
import com.example.demo.financialHistory.depositHistory.model.DepositHistories;
import com.example.demo.major.model.Majors;
import com.example.demo.room.model.OfflineRooms;
import com.example.demo.room.model.OnlineRooms;
import com.example.demo.specialization.model.Specialization;
import com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.model.StudentRequiredMajorSubjects;
import com.example.demo.studentRequiredSubjects.studentRequiredMinorSubjects.model.StudentRequiredMinorSubjects;
import com.example.demo.studentRequiredSubjects.studentRequiredSpecializedSubjects.model.StudentRequiredSpecializedSubjects;
import com.example.demo.subject.abstractSubject.model.Subjects;
import com.example.demo.subject.majorSubject.model.MajorSubjects;
import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import com.example.demo.timetable.majorTimetable.model.Slots;
import com.example.demo.tuitionByYear.model.TuitionByYear;
import com.example.demo.tuitionByYear.model.TuitionByYearId;
import com.example.demo.user.admin.model.Admins;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.user.person.model.Persons;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
public class DemoApplication {

    private static final String DEFAULT_PASSWORD = "123456";
    private static final double INITIAL_DEPOSIT_AMOUNT = 1000.0; // nhỏ cho đẹp

    // DEMO: ít thôi cho nhanh
    private static final int STUDENTS_PER_CAMPUS = 20;      // nhưng giờ chỉ dùng cho CAMP01
    private static final int MAJOR_CLASSES_TOTAL = 30;
    private static final int MINOR_CLASSES_TOTAL = 15;
    private static final int SPEC_CLASSES_TOTAL = 15;
    private static final int REQUIRED_SUBJECTS_LIMIT = 200; // tối đa 200 SV có required subjects

    // Mapping Campus → code để build ID student: stu{code}{xxxx}
    private static final Map<String, String> CAMPUS_CODE_BY_ID = Map.ofEntries(
            Map.entry("CAMP01", "hn"),
            Map.entry("CAMP02", "hcm"),
            Map.entry("CAMP03", "dn"),
            Map.entry("CAMP04", "hp"),
            Map.entry("CAMP05", "ct"),
            Map.entry("CAMP06", "hue"),
            Map.entry("CAMP07", "qn"),
            Map.entry("CAMP08", "nt"),
            Map.entry("CAMP09", "vt"),
            Map.entry("CAMP10", "th")
    );

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

            seedStaffs(em);           // tất cả Staff ở CAMP01
            seedDeputyStaffs(em);     // tất cả Deputy ở CAMP01
            seedMajorLecturers(em);   // campus lấy từ Staff → CAMP01
            seedMinorLecturers(em);   // chỉ tạo ở CAMP01

            seedStudents(em);         // chỉ tạo SV ở CAMP01

            seedMajorSubjects(em);
            seedMinorSubjects(em);
            seedSpecializedSubjects(em);

            seedStudentBalancesAndDepositHistory(em);
            seedTuitionByYear(em);
            seedSlots(em);
            seedRooms(em);

            seedClasses(em);
            seedStudentRequiredSubjects(em);

            em.getTransaction().commit();
            System.out.println("========== SEED DATA DONE ==========");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ===================== CAMPUS / ADMIN =====================

    private static void seedCampuses(EntityManager em) {
        String[] ids = {"CAMP01", "CAMP02", "CAMP03", "CAMP04", "CAMP05", "CAMP06", "CAMP07", "CAMP08", "CAMP09", "CAMP10"};
        String[] names = {"Hà Nội", "TP.HCM", "Đà Nẵng", "Hải Phòng", "Cần Thơ", "Huế", "Quy Nhơn", "Nha Trang", "Vũng Tàu", "Thanh Hóa"};
        LocalDate[] opens = {
                LocalDate.of(2010, 1, 1), LocalDate.of(2012, 5, 15), LocalDate.of(2015, 3, 20), LocalDate.of(2016, 9, 10),
                LocalDate.of(2018, 11, 25), LocalDate.of(2019, 4, 5), LocalDate.of(2020, 2, 14), LocalDate.of(2021, 7, 1),
                LocalDate.of(2022, 8, 30), LocalDate.of(2023, 10, 12)
        };

        for (int i = 0; i < ids.length; i++) {
            if (exists(em, Campuses.class, "campusId", ids[i])) continue;
            Campuses c = new Campuses();
            c.setCampusId(ids[i]);
            c.setCampusName(names[i] + " Campus");
            c.setOpeningDay(opens[i]);
            c.setDescription("Campus chính tại " + names[i]);
            em.persist(c);
            System.out.println("[SEED] Campus inserted: " + c.getCampusId() + " - " + c.getCampusName());
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
        admin.setPhoneNumber("+84912345678"); // unique
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
        System.out.println("[SEED] Admin inserted: " + admin.getId() + " - " + admin.getFullName());
        createAuth(em, id, admin);

        // set creator cho tất cả campus
        for (int i = 0; i < 10; i++) {
            Campuses c = find(em, Campuses.class, "campusId", "CAMP" + String.format("%02d", i + 1));
            if (c != null && c.getCreator() == null) {
                c.setCreator(admin);
                em.merge(c);
                System.out.println("[SEED] Campus creator set: " + c.getCampusId() + " -> " + admin.getId());
            }
        }
    }

    private static void seedRemainingAdmins(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", "admin001");
        if (creator == null) throw new IllegalStateException("admin001 must exist!");

        String[] ids = {"admin002", "admin003", "admin004", "admin005", "admin006"};
        String[] firstNames = {"Trần", "Lê", "Phạm", "Hoàng", "Vũ"};
        String[] lastNames = {"Thị B", "Văn C", "Thị D", "Văn E", "Thị F"};
        String[] emails = {"admin2@example.com", "admin3@example.com", "admin4@example.com", "admin5@example.com", "admin6@example.com"};
        String[] phones = {"+84987654321", "+84911223344", "+84955667788", "+84933445566", "+84977889900"};
        LocalDate[] births = {
                LocalDate.of(1982, 3, 22), LocalDate.of(1978, 7, 10), LocalDate.of(1985, 11, 30),
                LocalDate.of(1981, 5, 18), LocalDate.of(1987, 9, 25)
        };

        Campuses hanoi = find(em, Campuses.class, "campusId", "CAMP01");

        for (int i = 0; i < ids.length; i++) {
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
            admin.setProvince("Hà Nội");
            admin.setCity("Hà Nội");
            admin.setDistrict(i % 2 == 0 ? "Cầu Giấy" : "Quận 1");
            admin.setWard(i % 2 == 0 ? "Dịch Vọng" : "Bến Nghé");
            admin.setStreet(i % 2 == 0 ? "123 Trần Duy Hưng" : "45 Lê Lợi");
            admin.setPostalCode("100000");
            admin.setCreatedDate(LocalDateTime.now().minusDays(100 - i * 5));

            admin.setCampus(hanoi);
            admin.setCreator(creator);

            em.persist(admin);
            System.out.println("[SEED] Admin inserted: " + admin.getId() + " - " + admin.getFullName());
            createAuth(em, ids[i], admin);
        }
    }

    // ===================== MAJORS / SPEC / CURRICULUM =====================

    private static void seedMajors(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", "admin001");
        String[] ids = {"GBH", "GCH", "GDH", "GKH", "GKT", "GDT", "GAT", "GNT", "GFT", "GHT"};
        String[] names = {
                "Quản trị Kinh doanh", "Công nghệ Thông tin", "Thiết kế Đồ họa", "Marketing",
                "Kế toán", "Khoa học Dữ liệu", "Trí tuệ Nhân tạo", "An ninh Mạng", "Tài chính", "Quản trị Nhân sự"
        };

        for (int i = 0; i < ids.length; i++) {
            if (exists(em, Majors.class, "majorId", ids[i])) continue;
            Majors m = new Majors();
            m.setMajorId(ids[i]);
            m.setMajorName(names[i]);
            m.setCreator(creator);
            m.setCreatedDate(LocalDate.now().minusDays(60 - i * 3));
            em.persist(m);
            System.out.println("[SEED] Major inserted: " + m.getMajorId() + " - " + m.getMajorName());
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
            System.out.println("[SEED] Specialization inserted: " + spec.getSpecializationId() + " - " + spec.getSpecializationName());
        }
    }

    private static void seedCurriculums(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", "admin001");
        if (!exists(em, Curriculum.class, "curriculumId", "CURR01")) {
            Curriculum c = new Curriculum();
            c.setCurriculumId("CURR01");
            c.setName("BTEC");
            c.setDescription("Chương trình BTEC demo");
            c.setCreator(creator);
            c.setCreatedAt(LocalDateTime.now());
            em.persist(c);
            System.out.println("[SEED] Curriculum inserted: " + c.getCurriculumId() + " - " + c.getName());
        }
    }

    // ===================== STAFF / DEPUTY / LECTURERS =====================

    private static void seedStaffs(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", "admin001");

        String[] majorIds = {"GBH", "GCH", "GDH", "GKH", "GKT", "GDT", "GAT", "GNT", "GFT", "GHT"};
        Majors[] majors = new Majors[majorIds.length];
        for (int i = 0; i < majorIds.length; i++) {
            majors[i] = find(em, Majors.class, "majorId", majorIds[i]);
        }

        Campuses hanoi = find(em, Campuses.class, "campusId", "CAMP01");

        String[] firstNames = {"Minh", "Lan", "Hùng", "Mai", "Tuấn", "Hương", "Khoa", "Ngọc", "Đức", "Thảo"};
        String[] lastNames = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Vũ", "Đặng", "Bùi", "Ngô", "Dương"};

        for (int i = 0; i < 10; i++) {
            String id = "staff" + String.format("%03d", i + 1);
            if (exists(em, Staffs.class, "id", id)) continue;

            Staffs s = new Staffs();
            s.setId(id);
            s.setFirstName(firstNames[i]);
            s.setLastName(lastNames[i]);
            s.setEmail(id + "@staff.demo.com");
            s.setPhoneNumber("+8471" + String.format("%08d", 10000000 + i)); // unique prefix
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
            s.setCampus(hanoi); // TẤT CẢ STAFF Ở HÀ NỘI
            s.setCreator(creator);
            em.persist(s);
            System.out.println("[SEED] Staff inserted: " + s.getId() + " - " + s.getFullName());
            createAuth(em, id, s);
        }
    }

    private static void seedDeputyStaffs(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", "admin001");
        Campuses hanoi = find(em, Campuses.class, "campusId", "CAMP01");

        String[] firstNames = {"Anh", "Bình", "Cường", "Duyên", "Đạt", "Hà", "Khánh", "Linh", "Mạnh", "Nhi"};
        String[] lastNames = {"Trần", "Lê", "Phạm", "Hoàng", "Vũ", "Đặng", "Bùi", "Ngô", "Dương", "Nguyễn"};

        for (int i = 0; i < 10; i++) {
            String id = "deputy" + String.format("%03d", i + 1);
            if (exists(em, DeputyStaffs.class, "id", id)) continue;

            DeputyStaffs d = new DeputyStaffs();
            d.setId(id);
            d.setFirstName(firstNames[i]);
            d.setLastName(lastNames[i]);
            d.setEmail(id + "@deputy.demo.com");
            d.setPhoneNumber("+8472" + String.format("%08d", 20000000 + i));
            d.setBirthDate(LocalDate.of(1990 + i % 5, 1 + i % 12, 1 + i % 28));
            d.setGender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE);
            d.setCountry("Vietnam");
            d.setProvince("Hà Nội");
            d.setCity("Hà Nội");
            d.setDistrict("Cầu Giấy");
            d.setWard("Dịch Vọng");
            d.setStreet("45 Lê Lợi");
            d.setPostalCode("100000");
            d.setCampus(hanoi);   // TẤT CẢ DEPUTY Ở HÀ NỘI
            d.setCreator(creator);
            em.persist(d);
            System.out.println("[SEED] DeputyStaff inserted: " + d.getId() + " - " + d.getFullName());
            createAuth(em, id, d);
        }
    }

    private static void seedMajorLecturers(EntityManager em) {
        String[] majorIds = {"GBH", "GCH", "GDH", "GKH", "GKT", "GDT", "GAT", "GNT", "GFT", "GHT"};
        String[] firstNames = {"Hải", "Yến", "Phong", "Thư", "Kiên", "Tâm", "Long", "Huyền", "Quân", "Mai"};
        String[] lastNames = {"Lê", "Phạm", "Hoàng", "Vũ", "Đặng", "Bùi", "Ngô", "Dương", "Nguyễn", "Trần"};

        int lecturerIndex = 1;
        for (String majorId : majorIds) {
            Majors major = find(em, Majors.class, "majorId", majorId);
            if (major == null) continue;

            Staffs creator = findStaffByMajorId(em, major.getMajorId());
            if (creator == null) continue;

            for (int i = 0; i < 3; i++) { // mỗi major 3 giảng viên → 30
                String id = "lect" + String.format("%03d", lecturerIndex++);
                if (exists(em, MajorLecturers.class, "id", id)) continue;

                MajorLecturers l = new MajorLecturers();
                l.setId(id);
                l.setFirstName(firstNames[i % firstNames.length]);
                l.setLastName(lastNames[i % lastNames.length]);
                l.setEmail(id + "@lect.demo.com");
                l.setPhoneNumber("+8473" + String.format("%08d", 30000000 + lecturerIndex));
                l.setBirthDate(LocalDate.of(1975 + i, 1 + i % 12, 1 + i % 28));
                l.setGender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE);
                l.setCountry("Vietnam");
                l.setProvince("Hà Nội");
                l.setCity("Hà Nội");
                l.setDistrict("Hải Châu");
                l.setWard("Hòa Cường");
                l.setStreet("78 Nguyễn Văn Linh");
                l.setPostalCode("550000");
                l.setMajorManagement(major);
                l.setCampus(creator.getCampus());  // CAMP01
                l.setEmploymentTypes(EmploymentTypes.FULL_TIME);
                l.setCreator(creator);
                em.persist(l);
                System.out.println("[SEED] MajorLecturer inserted: " + l.getId() + " - " + l.getFullName());
                createAuth(em, id, l);
            }
        }
    }

    private static void seedMinorLecturers(EntityManager em) {
        List<DeputyStaffs> deputies = em.createQuery("SELECT d FROM DeputyStaffs d", DeputyStaffs.class).getResultList();
        if (deputies.isEmpty()) return;

        Campuses hanoi = find(em, Campuses.class, "campusId", "CAMP01");
        if (hanoi == null) return;

        String campusCode = CAMPUS_CODE_BY_ID.getOrDefault(hanoi.getCampusId(), "hn");

        String[] firstNames = {"Hảo", "Giang", "Thu", "Lợi", "Trang", "Nhật"};
        String[] lastNames = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Vũ"};

        Random random = new Random();
        int idx = 1;

        // chỉ tạo 4 minor lecturers cho Hà Nội
        for (int i = 0; i < 4; i++) {
            String id = "minlect" + campusCode + String.format("%03d", idx++);
            if (exists(em, MinorLecturers.class, "id", id)) continue;

            MinorLecturers ml = new MinorLecturers();
            ml.setId(id);
            ml.setFirstName(firstNames[i % firstNames.length]);
            ml.setLastName(lastNames[i % lastNames.length]);
            ml.setEmail(id + "@minorlec.demo.com");
            ml.setPhoneNumber("+8474" + String.format("%08d", 40000000 + idx));
            ml.setBirthDate(LocalDate.of(1985 + i % 5, 1 + i % 12, 1 + i % 28));
            ml.setGender(i % 2 == 0 ? Gender.FEMALE : Gender.MALE);
            ml.setCountry("Vietnam");
            ml.setProvince("Hà Nội");
            ml.setCity("Hà Nội");
            ml.setDistrict("Quận " + (i + 1));
            ml.setWard("Phường " + (i + 1));
            ml.setStreet("Đường Minor Lec " + (i + 1));
            ml.setPostalCode("700000");
            ml.setCampus(hanoi); // CHỈ HÀ NỘI
            ml.setEmploymentTypes(i % 2 == 0 ? EmploymentTypes.PART_TIME : EmploymentTypes.FULL_TIME);
            ml.setCreator(deputies.get(random.nextInt(deputies.size())));

            em.persist(ml);
            System.out.println("[SEED] MinorLecturer inserted: " + ml.getId() + " - " + ml.getFullName());
            createAuth(em, id, ml);
        }
    }

    // ===================== STUDENTS =====================

    private static void seedStudents(EntityManager em) {
        Curriculum curr = find(em, Curriculum.class, "curriculumId", "CURR01");
        if (curr == null) return;

        List<Specialization> specs = em.createQuery("SELECT s FROM Specialization s", Specialization.class)
                .getResultList();
        if (specs.isEmpty()) return;

        Map<String, List<Specialization>> specsByMajor = specs.stream()
                .collect(Collectors.groupingBy(s -> s.getMajor().getMajorId()));

        List<Staffs> staffList = em.createQuery("SELECT s FROM Staffs s", Staffs.class).getResultList();
        if (staffList.isEmpty()) return;

        Map<String, Staffs> creatorByCampus = staffList.stream()
                .collect(Collectors.toMap(
                        s -> s.getCampus().getCampusId(),
                        s -> s,
                        (s1, s2) -> s1
                ));

        Campuses hanoi = find(em, Campuses.class, "campusId", "CAMP01");
        if (hanoi == null) return;

        Staffs creator = creatorByCampus.get("CAMP01");
        if (creator == null) return;

        String campusId = hanoi.getCampusId();
        String campusCode = CAMPUS_CODE_BY_ID.getOrDefault(campusId, campusId.toLowerCase());

        String majorId = creator.getMajorManagement().getMajorId();
        List<Specialization> specList = specsByMajor.getOrDefault(majorId, specs);

        String[] firstNames = {"An", "Bình", "Cường", "Duyên", "Đạt", "Hà", "Khánh", "Linh", "Mạnh", "Nhi",
                "Oanh", "Phúc", "Quang", "Rạng", "Sáng", "Tâm", "Uyên", "Vũ", "Xuân", "Yến"};
        String[] lastNames = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Vũ", "Đặng", "Bùi", "Ngô", "Dương"};

        int globalStudentIndex = 1;

        // CHỈ TẠO SV Ở HÀ NỘI
        for (int i = 1; i <= STUDENTS_PER_CAMPUS; i++, globalStudentIndex++) {
            String id = "stu" + campusCode + String.format("%04d", i);
            if (exists(em, Students.class, "id", id)) continue;

            String email = id + "@student.demo.com";
            String phone = "+8475" + String.format("%08d", 50000000 + globalStudentIndex);

            Specialization spec = specList.get((i - 1) % specList.size());

            Students student = new Students();
            student.setId(id);
            student.setFirstName(firstNames[(i - 1) % firstNames.length]);
            student.setLastName(lastNames[(i - 1) % lastNames.length]);
            student.setEmail(email);
            student.setPhoneNumber(phone);
            student.setBirthDate(LocalDate.of(2000 + (i % 5), 1 + (i % 12), 1 + (i % 28)));
            student.setGender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE);
            student.setCountry("Vietnam");
            student.setProvince("Hà Nội");
            student.setCity("Hà Nội");
            student.setDistrict("Quận/Huyện " + (i % 10 + 1));
            student.setWard("Phường/Xã " + (i % 15 + 1));
            student.setStreet("Số " + (10 + i) + " Đường Demo");
            student.setPostalCode("100000");
            student.setAdmissionYear(2025);
            student.setCreator(creator);
            student.setCampus(hanoi);
            student.setSpecialization(spec);
            student.setCurriculum(curr);

            em.persist(student);
            System.out.println("[SEED] Student inserted: " + student.getId() + " - " + student.getFullName());
            createAuth(em, id, student);
        }
    }

    // ===================== SUBJECTS =====================

    private static void seedMajorSubjects(EntityManager em) {
        Curriculum curr = find(em, Curriculum.class, "curriculumId", "CURR01");
        Admins acceptor = find(em, Admins.class, "id", "admin001");
        String[] majorIds = {"GBH", "GCH", "GDH", "GKH", "GKT", "GDT", "GAT", "GNT", "GFT", "GHT"};
        String[] names = {
                "Nhập môn Quản trị", "Lập trình Java", "Thiết kế Cơ bản", "Marketing Căn bản",
                "Kế toán Tài chính", "Phân tích Dữ liệu", "AI Cơ bản", "Mạng Máy tính",
                "Tài chính Doanh nghiệp", "Quản lý Nhân sự"
        };

        for (int i = 0; i < majorIds.length; i++) {
            String id = "SUB_MAJ_" + String.format("%03d", i + 1);
            if (exists(em, MajorSubjects.class, "subjectId", id)) continue;

            Majors major = find(em, Majors.class, "majorId", majorIds[i]);
            if (major == null) continue;

            Staffs creator = findStaffByMajorId(em, majorIds[i]);
            if (creator == null) continue;

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
            System.out.println("[SEED] MajorSubject inserted: " + s.getSubjectId() + " - " + s.getSubjectName());
        }
    }

    private static void seedMinorSubjects(EntityManager em) {
        Admins acceptor = find(em, Admins.class, "id", "admin001");
        String[] names = {
                "Tiếng Anh Giao tiếp", "Kỹ năng Mềm", "Tư duy Phản biện", "Quản lý Thời gian",
                "Làm việc Nhóm", "Kỹ năng Thuyết trình", "Viết CV", "Phỏng vấn", "Tinh thần Khởi nghiệp", "Sức khỏe Tinh thần"
        };

        for (int i = 0; i < names.length; i++) {
            String id = "SUB_MIN_" + String.format("%03d", i + 1);
            if (exists(em, MinorSubjects.class, "subjectId", id)) continue;

            DeputyStaffs creator = find(em, DeputyStaffs.class, "id", "deputy" + String.format("%03d", i + 1));
            if (creator == null) continue;

            MinorSubjects s = new MinorSubjects();
            s.setSubjectId(id);
            s.setSubjectName(names[i]);
            s.setSemester(i % 4 + 1);
            s.setIsAccepted(i % 4 == 0);
            s.setAcceptor(acceptor);
            s.setCreator(creator);
            em.persist(s);
            System.out.println("[SEED] MinorSubject inserted: " + s.getSubjectId() + " - " + s.getSubjectName());
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

        for (int i = 0; i < specIds.length; i++) {
            String id = "SUB_SPEC_" + String.format("%03d", i + 1);
            if (exists(em, SpecializedSubject.class, "subjectId", id)) continue;

            Specialization spec = find(em, Specialization.class, "specializationId", specIds[i]);
            if (spec == null) continue;

            Staffs creator = findStaffByMajorId(em, spec.getMajor().getMajorId());
            if (creator == null) continue;

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
            System.out.println("[SEED] SpecializedSubject inserted: " + s.getSubjectId() + " - " + s.getSubjectName());
        }
    }

    // ===================== BALANCE / TUITION =====================

    private static void seedStudentBalancesAndDepositHistory(EntityManager em) {
        List<Students> students = em.createQuery("SELECT s FROM Students s", Students.class).getResultList();
        if (students.isEmpty()) return;

        Set<String> existingBalStudentIds = em.createQuery("SELECT b.studentId FROM AccountBalances b", String.class)
                .getResultStream().collect(Collectors.toSet());

        LocalDateTime now = LocalDateTime.now();

        for (Students student : students) {
            String studentId = student.getId();
            if (existingBalStudentIds.contains(studentId)) continue;

            AccountBalances balance = new AccountBalances();
            balance.setStudentId(studentId);
            balance.setStudent(student);
            balance.setBalance(INITIAL_DEPOSIT_AMOUNT);
            balance.setLastUpdated(now);
            em.persist(balance);
            System.out.println("[SEED] AccountBalance inserted for student: " + studentId +
                    " | balance=" + INITIAL_DEPOSIT_AMOUNT);

            String historyId = "DEP_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            DepositHistories deposit = new DepositHistories();
            deposit.setHistoryId(historyId);
            deposit.setStudent(student);
            deposit.setAccountBalance(balance);
            deposit.setAmount(INITIAL_DEPOSIT_AMOUNT);
            deposit.setCurrentAmount(BigDecimal.valueOf(INITIAL_DEPOSIT_AMOUNT));
            deposit.setCreatedAt(now);
            deposit.setStatus(Status.COMPLETED);
            deposit.setDescription("Initial deposit of 1000 USD for demo.");
            em.persist(deposit);
            System.out.println("[SEED] DepositHistory inserted: " + historyId +
                    " for student: " + studentId);
        }
    }

    private static void seedTuitionByYear(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", "admin001");
        List<Campuses> campuses = em.createQuery("SELECT c FROM Campuses c", Campuses.class).getResultList();

        List<Subjects> subjects = new ArrayList<>();
        subjects.addAll(em.createQuery("SELECT s FROM MajorSubjects s", MajorSubjects.class).getResultList());
        subjects.addAll(em.createQuery("SELECT s FROM MinorSubjects s", MinorSubjects.class).getResultList());
        subjects.addAll(em.createQuery("SELECT s FROM SpecializedSubject s", SpecializedSubject.class).getResultList());

        Integer[] years = {2025, 2026};
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
                    double base = 10 + (rand.nextDouble() * 5);
                    t.setTuition(roundTo2Decimals(base));
                    t.setReStudyTuition(roundTo2Decimals(base * 0.7));
                    t.setContractStatus(ContractStatus.ACTIVE);
                    t.setCreator(creator);
                    em.persist(t);
                    System.out.println("[SEED] TuitionByYear inserted: subject=" + subject.getSubjectId() +
                            ", year=" + year + ", campus=" + campus.getCampusId());
                }
            }
        }
    }

    private static double roundTo2Decimals(double val) {
        return Math.round(val * 100.0) / 100.0;
    }

    // ===================== SLOTS / ROOMS =====================

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
            System.out.println("[SEED] Slot inserted: " + slot.getSlotId() + " - " + slot.getSlotName());
        }
    }

    private static void seedRooms(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", "admin001");

        String[] physicalIds = {"G101", "G102", "G201", "G202", "G301"};
        String[] physicalNames = {
                "Phòng G101 - Tầng 1", "Phòng G102 - Tầng 1",
                "Phòng G201 - Tầng 2", "Phòng G202 - Tầng 2",
                "Phòng G301 - Tầng 3"
        };

        for (int i = 0; i < physicalIds.length; i++) {
            if (exists(em, OfflineRooms.class, "roomId", physicalIds[i])) continue;
            Campuses campus = find(em, Campuses.class, "campusId", "CAMP" + String.format("%02d", (i % 5) + 1));
            OfflineRooms room = new OfflineRooms();
            room.setRoomId(physicalIds[i]);
            room.setRoomName(physicalNames[i]);
            room.setCreator(creator);
            room.setCampus(campus);
            room.setFloor((i / 2) + 1);
            em.persist(room);
            System.out.println("[SEED] OfflineRoom inserted: " + room.getRoomId() + " - " + room.getRoomName());
        }

        String[] onlineIds = {"ONLINE01", "ZOOM01", "MEET01", "TEAMS01", "WEBEX01"};
        String[] onlineNames = {
                "Phòng Online 01", "Zoom Room 01",
                "Google Meet 01", "Microsoft Teams 01",
                "Cisco Webex 01"
        };
        String[] links = {
                "https://zoom.us/j/1234567890",
                "https://zoom.us/j/1112223334",
                "https://meet.google.com/abc-defg-hij",
                "https://teams.microsoft.com/l/meetup-join/19%3A...",
                "https://webex.com/meet/room1"
        };

        for (int i = 0; i < onlineIds.length; i++) {
            if (exists(em, OnlineRooms.class, "roomId", onlineIds[i])) continue;
            Campuses campus = find(em, Campuses.class, "campusId", "CAMP" + String.format("%02d", (i % 5) + 6));
            OnlineRooms room = new OnlineRooms();
            room.setRoomId(onlineIds[i]);
            room.setRoomName(onlineNames[i]);
            room.setCreator(creator);
            room.setCampus(campus);
            room.setLink(links[i]);
            em.persist(room);
            System.out.println("[SEED] OnlineRoom inserted: " + room.getRoomId() + " - " + room.getRoomName());
        }
    }

    // ===================== CLASSES & REQUIRED SUBJECTS =====================

    private static void seedClasses(EntityManager em) {
        seedMajorClasses(em);
        seedMinorClasses(em);
        seedSpecializedClasses(em);
    }

    private static void seedMajorClasses(EntityManager em) {
        Long count = em.createQuery("SELECT COUNT(c) FROM MajorClasses c", Long.class).getSingleResult();
        if (count > 0) return;

        List<MajorSubjects> subjects = em.createQuery("SELECT s FROM MajorSubjects s", MajorSubjects.class).getResultList();
        if (subjects.isEmpty()) return;

        List<Staffs> staffList = em.createQuery("SELECT s FROM Staffs s", Staffs.class).getResultList();
        if (staffList.isEmpty()) return;

        Sessions[] sessions = Sessions.values();
        Random random = new Random();

        int total = MAJOR_CLASSES_TOTAL;
        int perSubject = Math.max(1, total / subjects.size());
        int index = 1;

        for (MajorSubjects subj : subjects) {
            for (int i = 0; i < perSubject; i++) {
                String classId = "CLM-" + String.format("%03d", index++);
                if (exists(em, MajorClasses.class, "classId", classId)) continue;

                Staffs creator = staffList.get(random.nextInt(staffList.size()));

                MajorClasses mc = new MajorClasses(
                        classId,
                        "Lớp Major - " + subj.getSubjectName() + " - " + i,
                        30 + random.nextInt(5),
                        sessions[random.nextInt(sessions.length)],
                        subj,
                        creator,
                        LocalDateTime.now().minusDays(random.nextInt(10))
                );
                em.persist(mc);
                System.out.println("[SEED] MajorClass inserted: " + mc.getClassId() + " - " + mc.getNameClass());
            }
        }
    }

    private static void seedMinorClasses(EntityManager em) {
        Long count = em.createQuery("SELECT COUNT(c) FROM MinorClasses c", Long.class).getSingleResult();
        if (count > 0) return;

        List<MinorSubjects> subjects = em.createQuery("SELECT s FROM MinorSubjects s", MinorSubjects.class).getResultList();
        if (subjects.isEmpty()) return;

        List<DeputyStaffs> deputyList = em.createQuery("SELECT d FROM DeputyStaffs d", DeputyStaffs.class).getResultList();
        if (deputyList.isEmpty()) return;

        Sessions[] sessions = Sessions.values();
        Random random = new Random();

        int total = MINOR_CLASSES_TOTAL;
        int perSubject = Math.max(1, total / subjects.size());
        int index = 1;

        for (MinorSubjects subj : subjects) {
            for (int i = 0; i < perSubject; i++) {
                String classId = "CLN-" + String.format("%03d", index++);
                if (exists(em, MinorClasses.class, "classId", classId)) continue;

                DeputyStaffs creator = deputyList.get(random.nextInt(deputyList.size()));

                MinorClasses mc = new MinorClasses(
                        classId,
                        "Lớp Minor - " + subj.getSubjectName() + " - " + i,
                        20 + random.nextInt(5),
                        sessions[random.nextInt(sessions.length)],
                        subj,
                        creator,
                        LocalDateTime.now().minusDays(random.nextInt(10))
                );
                em.persist(mc);
                System.out.println("[SEED] MinorClass inserted: " + mc.getClassId() + " - " + mc.getNameClass());
            }
        }
    }

    private static void seedSpecializedClasses(EntityManager em) {
        Long count = em.createQuery("SELECT COUNT(c) FROM SpecializedClasses c", Long.class).getSingleResult();
        if (count > 0) return;

        List<SpecializedSubject> subjects = em.createQuery("SELECT s FROM SpecializedSubject s", SpecializedSubject.class).getResultList();
        if (subjects.isEmpty()) return;

        List<Staffs> staffList = em.createQuery("SELECT s FROM Staffs s", Staffs.class).getResultList();
        if (staffList.isEmpty()) return;

        Sessions[] sessions = Sessions.values();
        Random random = new Random();

        int total = SPEC_CLASSES_TOTAL;
        int perSubject = Math.max(1, total / subjects.size());
        int index = 1;

        for (SpecializedSubject subj : subjects) {
            for (int i = 0; i < perSubject; i++) {
                String classId = "CLS-" + String.format("%03d", index++);
                if (exists(em, SpecializedClasses.class, "classId", classId)) continue;

                Staffs creator = staffList.get(random.nextInt(staffList.size()));

                SpecializedClasses sc = new SpecializedClasses(
                        classId,
                        "Lớp Specialized - " + subj.getSubjectName() + " - " + i,
                        25 + random.nextInt(5),
                        sessions[random.nextInt(sessions.length)],
                        subj,
                        creator,
                        LocalDateTime.now().minusDays(random.nextInt(10))
                );
                em.persist(sc);
                System.out.println("[SEED] SpecializedClass inserted: " + sc.getClassId() + " - " + sc.getNameClass());
            }
        }
    }

    private static void seedStudentRequiredSubjects(EntityManager em) {
        Long count = em.createQuery("SELECT COUNT(s) FROM StudentRequiredSubjects s", Long.class).getSingleResult();
        if (count > 0) return;

        List<Students> students = em.createQuery("SELECT s FROM Students s ORDER BY s.id", Students.class).getResultList();
        if (students.isEmpty()) return;

        List<MajorSubjects> majorSubjects = em.createQuery("SELECT s FROM MajorSubjects s", MajorSubjects.class).getResultList();
        List<MinorSubjects> minorSubjects = em.createQuery("SELECT s FROM MinorSubjects s", MinorSubjects.class).getResultList();
        List<SpecializedSubject> specSubjects = em.createQuery("SELECT s FROM SpecializedSubject s", SpecializedSubject.class).getResultList();
        List<Staffs> staffList = em.createQuery("SELECT s FROM Staffs s", Staffs.class).getResultList();
        List<DeputyStaffs> deputyList = em.createQuery("SELECT d FROM DeputyStaffs d", DeputyStaffs.class).getResultList();

        if (majorSubjects.isEmpty() || minorSubjects.isEmpty() || specSubjects.isEmpty()
                || staffList.isEmpty() || deputyList.isEmpty()) {
            return;
        }

        Random random = new Random();
        int limit = Math.min(REQUIRED_SUBJECTS_LIMIT, students.size());

        for (int i = 0; i < limit; i++) {
            Students stu = students.get(i);

            MajorSubjects majSub = majorSubjects.get(i % majorSubjects.size());
            Staffs assignedStaff = staffList.get(random.nextInt(staffList.size()));
            StudentRequiredMajorSubjects rMaj = new StudentRequiredMajorSubjects(
                    stu,
                    majSub,
                    "Bắt buộc học major subject: " + majSub.getSubjectName(),
                    assignedStaff
            );
            em.persist(rMaj);
            System.out.println("[SEED] StudentRequiredMajor inserted: stu=" + stu.getId() +
                    ", subject=" + majSub.getSubjectId());

            MinorSubjects minSub = minorSubjects.get(i % minorSubjects.size());
            DeputyStaffs assignedDeputy = deputyList.get(random.nextInt(deputyList.size()));
            StudentRequiredMinorSubjects rMin = new StudentRequiredMinorSubjects(
                    stu,
                    minSub,
                    "Bắt buộc học minor subject: " + minSub.getSubjectName(),
                    assignedDeputy
            );
            em.persist(rMin);
            System.out.println("[SEED] StudentRequiredMinor inserted: stu=" + stu.getId() +
                    ", subject=" + minSub.getSubjectId());

            SpecializedSubject specSub = specSubjects.get(i % specSubjects.size());
            Staffs assignedStaff2 = staffList.get(random.nextInt(staffList.size()));
            StudentRequiredSpecializedSubjects rSpec = new StudentRequiredSpecializedSubjects(
                    stu,
                    specSub,
                    "Bắt buộc học specialized subject: " + specSub.getSubjectName(),
                    assignedStaff2
            );
            em.persist(rSpec);
            System.out.println("[SEED] StudentRequiredSpecialized inserted: stu=" + stu.getId() +
                    ", subject=" + specSub.getSubjectId());
        }
    }

    // ===================== HELPERS =====================

    private static void createAuth(EntityManager em, String personId, Persons person) {
        if (exists(em, Authenticators.class, "personId", personId)) return;
        Authenticators auth = new Authenticators();
        auth.setPersonId(personId);
        auth.setPerson(person);
        auth.setPassword(DEFAULT_PASSWORD);
        em.persist(auth);
        System.out.println("[SEED] Authenticator inserted: personId=" + personId);
    }

    private static <T> boolean exists(EntityManager em, Class<T> clazz, String idField, String idValue) {
        String jpql = "SELECT 1 FROM " + clazz.getSimpleName() + " e WHERE e." + idField + " = :id";
        List<Integer> results = em.createQuery(jpql, Integer.class)
                .setParameter("id", idValue)
                .setMaxResults(1)
                .getResultList();
        return !results.isEmpty();
    }

    private static boolean existsTuitionByYear(EntityManager em, TuitionByYearId id) {
        String jpql = "SELECT 1 FROM TuitionByYear t " +
                "WHERE t.id.subjectId = :subjectId " +
                "AND t.id.admissionYear = :admissionYear " +
                "AND t.id.campusId = :campusId";
        List<Integer> results = em.createQuery(jpql, Integer.class)
                .setParameter("subjectId", id.getSubjectId())
                .setParameter("admissionYear", id.getAdmissionYear())
                .setParameter("campusId", id.getCampusId())
                .setMaxResults(1)
                .getResultList();
        return !results.isEmpty();
    }

    private static <T> T find(EntityManager em, Class<T> clazz, String idField, String idValue) {
        String jpql = "SELECT e FROM " + clazz.getSimpleName() + " e WHERE e." + idField + " = :id";
        List<T> results = em.createQuery(jpql, clazz)
                .setParameter("id", idValue)
                .setMaxResults(1)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    private static Staffs findStaffByMajorId(EntityManager em, String majorId) {
        String jpql = "SELECT s FROM Staffs s WHERE s.majorManagement.majorId = :majorId ORDER BY s.id";
        List<Staffs> results = em.createQuery(jpql, Staffs.class)
                .setParameter("majorId", majorId)
                .setMaxResults(1)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
}
