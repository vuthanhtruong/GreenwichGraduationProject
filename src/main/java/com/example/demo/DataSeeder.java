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
import com.example.demo.entity.Enums.RelationshipToStudent;
import com.example.demo.entity.Enums.Sessions;
import com.example.demo.entity.Enums.Status;
import com.example.demo.entity.Enums.YourNotification;
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
import com.example.demo.user.parentAccount.model.ParentAccounts;
import com.example.demo.user.parentAccount.model.Student_ParentAccounts;
import com.example.demo.user.person.model.Persons;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DataSeeder implements CommandLineRunner {

    private final EntityManagerFactory emf;

    public DataSeeder(EntityManagerFactory emf) {
        this.emf = emf;
    }

    private static final String DEFAULT_PASSWORD = "123456";
    private static final double INITIAL_DEPOSIT_AMOUNT = 1000.0;

    // scale to nhiều user hơn
    private static final int TOTAL_STUDENTS = 200;
    private static final int MAJOR_CLASSES_TOTAL = 40;
    private static final int MINOR_CLASSES_TOTAL = 20;
    private static final int SPEC_CLASSES_TOTAL = 20;

    private static final int TOTAL_PARENTS = 200;

    // số lượng staff/deputy/lecturer theo campus/major
    private static final int STAFF_PER_MAJOR_PER_CAMPUS = 2;
    private static final int DEPUTY_PER_CAMPUS = 3;
    private static final int MAJOR_LECTURERS_PER_MAJOR_PER_CAMPUS = 2;
    private static final int MINOR_LECTURERS_PER_CAMPUS = 3;

    // CAMPUS IDs
    private static final String CAMPUS_ID_HANOI = "CAMP01";
    private static final String CAMPUS_ID_HCM = "CAMP02";
    private static final String CAMPUS_ID_DANANG = "CAMP03";
    private static final String CAMPUS_ID_CANTHO = "CAMP04";
    private static final String CAMPUS_ID_HAIPHONG = "CAMP05";

    private static final String CAMPUS_CODE_HANOI = "hn";
    private static final String CAMPUS_CODE_HCM = "hcm";
    private static final String CAMPUS_CODE_DANANG = "dn";

    // mã campus dùng trong id user (demo, không nhất thiết trùng campus DB)
    private static final String CAMPUS_CODE = CAMPUS_CODE_HANOI;

    @Override
    public void run(String... args) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            System.out.println("===== SEEDING STARTED =====");

            seedCampuses(em);
            seedAdmin001(em);
            seedRemainingAdmins(em);
            seedMajors(em);
            seedSpecializations(em);
            seedCurriculums(em);

            seedStaffs(em);
            seedDeputyStaffs(em);
            seedMajorLecturers(em);
            seedMinorLecturers(em);

            seedStudents(em);
            seedParentAccountsAndRelations(em);

            seedMajorSubjects(em);
            seedMinorSubjects(em);
            seedSpecializedSubjects(em);

            seedStudentBalancesAndDepositHistory(em);
            seedTuitionByYear(em);
            seedSlots(em);
            seedRooms(em);

            seedClasses(em);
            seedStudentRequiredSubjects(em);

            System.out.println("===== SEEDING FINISHED SUCCESSFULLY =====");

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ===================== CAMPUS / ADMIN =====================

    private static void seedCampuses(EntityManager em) {
        seedSingleCampus(
                em,
                CAMPUS_ID_HANOI,
                "Hà Nội Campus",
                LocalDate.of(2010, 1, 1),
                "Campus chính tại Hà Nội"
        );

        seedSingleCampus(
                em,
                CAMPUS_ID_HCM,
                "Hồ Chí Minh Campus",
                LocalDate.of(2015, 9, 1),
                "Campus tại TP. Hồ Chí Minh"
        );

        seedSingleCampus(
                em,
                CAMPUS_ID_DANANG,
                "Đà Nẵng Campus",
                LocalDate.of(2018, 9, 1),
                "Campus tại Đà Nẵng"
        );

        seedSingleCampus(
                em,
                CAMPUS_ID_CANTHO,
                "Cần Thơ Campus",
                LocalDate.of(2019, 9, 1),
                "Campus tại Cần Thơ"
        );

        seedSingleCampus(
                em,
                CAMPUS_ID_HAIPHONG,
                "Hải Phòng Campus",
                LocalDate.of(2020, 9, 1),
                "Campus tại Hải Phòng"
        );
    }

    private static void seedSingleCampus(EntityManager em,
                                         String campusId,
                                         String campusName,
                                         LocalDate openingDay,
                                         String description) {
        if (exists(em, Campuses.class, "campusId", campusId)) {
            System.out.println("[CAMPUS] " + campusId + " already exists, skip.");
            return;
        }

        Campuses c = new Campuses();
        c.setCampusId(campusId);
        c.setCampusName(campusName);
        c.setOpeningDay(openingDay);
        c.setDescription(description);
        em.persist(c);

        System.out.println("[CAMPUS] Inserted " + campusId + " - " + campusName);
    }

    private static void seedAdmin001(EntityManager em) {
        String id = mainAdminId();
        if (exists(em, Admins.class, "id", id)) {
            System.out.println("[ADMIN] " + id + " already exists, skip.");
            return;
        }

        Campuses campus = find(em, Campuses.class, "campusId", CAMPUS_ID_HANOI);
        if (campus == null) {
            throw new IllegalStateException(CAMPUS_ID_HANOI + " must exist before creating " + id);
        }

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
        admin.setCampus(campus);
        admin.setCreator(admin);

        em.persist(admin);
        createAuth(em, id, admin);

        campus.setCreator(admin);
        em.merge(campus);

        System.out.println("[ADMIN] Inserted " + id + " (Hà Nội)");
    }

    private static void seedRemainingAdmins(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", mainAdminId());
        if (creator == null) throw new IllegalStateException(mainAdminId() + " must exist!");

        String[] ids = {userId3("admin", 2), userId3("admin", 3)};
        String[] firstNames = {"Trần", "Lê"};
        String[] lastNames = {"Thị B", "Văn C"};
        String[] emails = {"admin2@example.com", "admin3@example.com"};
        String[] phones = {"+84987654321", "+84911223344"};
        LocalDate[] births = {LocalDate.of(1982, 3, 22), LocalDate.of(1978, 7, 10)};

        Campuses campus = find(em, Campuses.class, "campusId", CAMPUS_ID_HANOI);

        for (int i = 0; i < ids.length; i++) {
            if (exists(em, Admins.class, "id", ids[i])) {
                System.out.println("[ADMIN] " + ids[i] + " already exists, skip.");
                continue;
            }

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
            admin.setDistrict("Cầu Giấy");
            admin.setWard("Dịch Vọng");
            admin.setStreet("123 Trần Duy Hưng");
            admin.setPostalCode("100000");
            admin.setCreatedDate(LocalDateTime.now().minusDays(10 - i * 3));
            admin.setCampus(campus);
            admin.setCreator(creator);

            em.persist(admin);
            createAuth(em, ids[i], admin);

            System.out.println("[ADMIN] Inserted " + ids[i]);
        }
    }

    // ===================== MAJORS / SPEC / CURRICULUM =====================

    private static void seedMajors(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", mainAdminId());

        String[] ids = {
                "GBH", // Quản trị Kinh doanh
                "GCH", // CNTT
                "GDH", // Thiết kế đồ hoạ
                "GKH", // Marketing
                "GKT", // Kế toán
                "GLU", // Luật
                "GQT", // Quản trị Du lịch
                "GNN", // Ngôn ngữ Anh
                "GTC"  // Tài chính
        };
        String[] names = {
                "Quản trị Kinh doanh",
                "Công nghệ Thông tin",
                "Thiết kế Đồ họa",
                "Marketing",
                "Kế toán",
                "Luật",
                "Quản trị Du lịch",
                "Ngôn ngữ Anh",
                "Tài chính"
        };

        for (int i = 0; i < ids.length; i++) {
            if (exists(em, Majors.class, "majorId", ids[i])) {
                System.out.println("[MAJOR] " + ids[i] + " already exists, skip.");
                continue;
            }
            Majors m = new Majors();
            m.setMajorId(ids[i]);
            m.setMajorName(names[i]);
            m.setCreator(creator);
            m.setCreatedDate(LocalDate.now().minusDays(30 - i * 2));
            em.persist(m);
            System.out.println("[MAJOR] Inserted major " + ids[i] + " - " + names[i]);
        }
    }

    private static void seedSpecializations(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", mainAdminId());
        String[][] specs = {
                {"SPEC_IT_SE", "Kỹ thuật Phần mềm", "GCH"},
                {"SPEC_IT_AI", "Trí tuệ Nhân tạo", "GCH"},
                {"SPEC_BUS_FIN", "Tài chính Ngân hàng", "GBH"},
                {"SPEC_DES_UI", "Thiết kế UI/UX", "GDH"},
                {"SPEC_MKT_DIG", "Marketing Kỹ thuật số", "GKH"},
                {"SPEC_FIN_CORP", "Tài chính Doanh nghiệp", "GTC"},
                {"SPEC_ENG_BIZ", "Tiếng Anh Thương mại", "GNN"},
                {"SPEC_TOUR_MICE", "Quản trị Sự kiện MICE", "GQT"}
        };

        for (String[] s : specs) {
            if (exists(em, Specialization.class, "specializationId", s[0])) {
                System.out.println("[SPEC] " + s[0] + " already exists, skip.");
                continue;
            }
            Specialization spec = new Specialization();
            spec.setSpecializationId(s[0]);
            spec.setSpecializationName(s[1]);
            spec.setMajor(find(em, Majors.class, "majorId", s[2]));
            spec.setCreator(creator);
            em.persist(spec);
            System.out.println("[SPEC] Inserted " + s[0] + " - " + s[1]);
        }
    }

    private static void seedCurriculums(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", mainAdminId());
        if (!exists(em, Curriculum.class, "curriculumId", "CURR01")) {
            Curriculum c = new Curriculum();
            c.setCurriculumId("CURR01");
            c.setName("BTEC");
            c.setDescription("Chương trình BTEC demo (multi-campus)");
            c.setCreator(creator);
            c.setCreatedAt(LocalDateTime.now());
            em.persist(c);
            System.out.println("[CURRICULUM] Inserted CURR01 - BTEC");
        } else {
            System.out.println("[CURRICULUM] CURR01 already exists, skip.");
        }
    }

    // ===================== STAFF / DEPUTY / LECTURERS =====================

    private static void seedStaffs(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", mainAdminId());

        List<Campuses> campuses = em.createQuery(
                "SELECT c FROM Campuses c", Campuses.class
        ).getResultList();
        List<Majors> majors = em.createQuery(
                "SELECT m FROM Majors m", Majors.class
        ).getResultList();

        if (campuses.isEmpty() || majors.isEmpty()) {
            System.out.println("[STAFF] No campus or major, skip.");
            return;
        }

        String[] firstNames = {"Minh", "Lan", "Hùng", "Mai", "Tuấn", "Phương", "Hảo", "Giang"};
        String[] lastNames = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Vũ", "Đỗ", "Bùi"};

        int idx = 1;

        for (Campuses campus : campuses) {
            for (Majors major : majors) {
                for (int k = 0; k < STAFF_PER_MAJOR_PER_CAMPUS; k++) {
                    String id = userId3("staff", idx++);
                    if (exists(em, Staffs.class, "id", id)) {
                        System.out.println("[STAFF] " + id + " already exists, skip.");
                        continue;
                    }

                    Staffs s = new Staffs();
                    s.setId(id);
                    s.setFirstName(firstNames[(idx + k) % firstNames.length]);
                    s.setLastName(lastNames[(idx + k) % lastNames.length]);
                    s.setEmail(id + "@staff.demo.com");
                    s.setPhoneNumber("+84101" + String.format("%07d", idx));
                    s.setBirthDate(LocalDate.of(1985 + (idx % 5), 1 + (idx % 12), 1 + (idx % 28)));
                    s.setGender(idx % 2 == 0 ? Gender.MALE : Gender.FEMALE);
                    s.setCountry("Vietnam");

                    // địa chỉ theo campus
                    fillAddressByCampus(s, campus, 100 + idx);

                    s.setMajorManagement(major);
                    s.setCampus(campus);
                    s.setCreator(creator);

                    em.persist(s);
                    createAuth(em, id, s);
                    System.out.println("[STAFF] Inserted " + id + " - major " + major.getMajorId()
                            + " - campus " + campus.getCampusId());
                }
            }
        }
    }

    private static void seedDeputyStaffs(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", mainAdminId());
        List<Campuses> campuses = em.createQuery(
                "SELECT c FROM Campuses c", Campuses.class
        ).getResultList();

        if (campuses.isEmpty()) {
            System.out.println("[DEPUTY] No campus, skip.");
            return;
        }

        String[] firstNames = {"Anh", "Bình", "Cường", "Dũng", "Hải", "Trang"};
        String[] lastNames = {"Trần", "Lê", "Phạm", "Hoàng", "Đặng", "Vũ"};

        int idx = 1;

        for (Campuses campus : campuses) {
            for (int i = 0; i < DEPUTY_PER_CAMPUS; i++) {
                String id = userId3("deputy", idx++);
                if (exists(em, DeputyStaffs.class, "id", id)) {
                    System.out.println("[DEPUTY] " + id + " already exists, skip.");
                    continue;
                }

                DeputyStaffs d = new DeputyStaffs();
                d.setId(id);
                d.setFirstName(firstNames[(idx + i) % firstNames.length]);
                d.setLastName(lastNames[(idx + i) % lastNames.length]);
                d.setEmail(id + "@deputy.demo.com");
                d.setPhoneNumber("+84102" + String.format("%07d", idx));
                d.setBirthDate(LocalDate.of(1990 + (idx % 5), 2 + (idx % 10), 5 + (idx % 20)));
                d.setGender(idx % 2 == 0 ? Gender.MALE : Gender.FEMALE);
                d.setCountry("Vietnam");

                fillAddressByCampus(d, campus, 200 + idx);

                d.setCampus(campus);
                d.setCreator(creator);

                em.persist(d);
                createAuth(em, id, d);
                System.out.println("[DEPUTY] Inserted " + id + " - campus " + campus.getCampusId());
            }
        }
    }

    private static void seedMajorLecturers(EntityManager em) {
        List<Majors> majors = em.createQuery("SELECT m FROM Majors m", Majors.class).getResultList();
        List<Staffs> staffList = em.createQuery("SELECT s FROM Staffs s", Staffs.class).getResultList();
        List<Campuses> campuses = em.createQuery("SELECT c FROM Campuses c", Campuses.class).getResultList();

        if (majors.isEmpty() || staffList.isEmpty() || campuses.isEmpty()) {
            System.out.println("[LECTURER] No major/staff/campus, skip.");
            return;
        }

        // staff theo (majorId + campusId)
        Map<String, List<Staffs>> staffByMajorAndCampus = staffList.stream()
                .filter(st -> st.getMajorManagement() != null
                        && st.getMajorManagement().getMajorId() != null
                        && st.getCampus() != null
                        && st.getCampus().getCampusId() != null)
                .collect(Collectors.groupingBy(st ->
                        st.getMajorManagement().getMajorId() + "#" + st.getCampus().getCampusId()
                ));

        String[] firstNames = {"Hải", "Yến", "Phong", "Thư", "Quân", "Chi"};
        String[] lastNames = {"Lê", "Phạm", "Hoàng", "Vũ", "Đoàn", "Ngô"};

        int lecturerIndex = 1;
        Random random = new Random();

        for (Campuses campus : campuses) {
            for (Majors major : majors) {
                String key = major.getMajorId() + "#" + campus.getCampusId();
                List<Staffs> staffsForCombo = staffByMajorAndCampus.get(key);
                if (staffsForCombo == null || staffsForCombo.isEmpty()) {
                    System.out.println("[LECTURER] No staff for major " + major.getMajorId()
                            + " campus " + campus.getCampusId() + ", skip lecturers.");
                    continue;
                }

                for (int i = 0; i < MAJOR_LECTURERS_PER_MAJOR_PER_CAMPUS; i++) {
                    String id = userId3("lect", lecturerIndex++);
                    if (exists(em, MajorLecturers.class, "id", id)) {
                        System.out.println("[LECTURER] " + id + " already exists, skip.");
                        continue;
                    }

                    Staffs creator = staffsForCombo.get(random.nextInt(staffsForCombo.size()));

                    MajorLecturers l = new MajorLecturers();
                    l.setId(id);
                    l.setFirstName(firstNames[(lecturerIndex + i) % firstNames.length]);
                    l.setLastName(lastNames[(lecturerIndex + i) % lastNames.length]);
                    l.setEmail(id + "@lect.demo.com");
                    l.setPhoneNumber("+84103" + String.format("%07d", lecturerIndex));
                    l.setBirthDate(LocalDate.of(1975 + (lecturerIndex % 5),
                            3 + (lecturerIndex % 10),
                            10 + (lecturerIndex % 10)));
                    l.setGender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE);
                    l.setCountry("Vietnam");

                    fillAddressByCampus(l, campus, 300 + lecturerIndex);

                    l.setMajorManagement(major);
                    l.setCampus(campus);
                    l.setEmploymentTypes(EmploymentTypes.FULL_TIME);
                    l.setCreator(creator);

                    em.persist(l);
                    createAuth(em, id, l);
                    System.out.println("[LECTURER] Inserted " + id + " for major "
                            + major.getMajorId() + " campus " + campus.getCampusId());
                }
            }
        }
    }

    private static void seedMinorLecturers(EntityManager em) {
        List<DeputyStaffs> deputies = em.createQuery("SELECT d FROM DeputyStaffs d", DeputyStaffs.class).getResultList();
        if (deputies.isEmpty()) {
            System.out.println("[MINOR_LECTURER] No deputy found, skip seeding.");
            return;
        }

        List<Campuses> campuses = em.createQuery("SELECT c FROM Campuses c", Campuses.class).getResultList();
        if (campuses.isEmpty()) {
            System.out.println("[MINOR_LECTURER] No campus, skip.");
            return;
        }

        Map<String, List<DeputyStaffs>> deputyByCampus = deputies.stream()
                .filter(d -> d.getCampus() != null && d.getCampus().getCampusId() != null)
                .collect(Collectors.groupingBy(d -> d.getCampus().getCampusId()));

        String[] firstNames = {"Hảo", "Giang", "Trang", "Vy", "Ngọc", "Thu"};
        String[] lastNames = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Đinh"};

        int idx = 1;
        Random random = new Random();

        for (Campuses campus : campuses) {
            List<DeputyStaffs> campusDeputies = deputyByCampus.get(campus.getCampusId());
            if (campusDeputies == null || campusDeputies.isEmpty()) {
                System.out.println("[MINOR_LECTURER] No deputy in campus "
                        + campus.getCampusId() + ", skip.");
                continue;
            }

            for (int i = 0; i < MINOR_LECTURERS_PER_CAMPUS; i++) {
                String id = userId3("minlect", idx++);
                if (exists(em, MinorLecturers.class, "id", id)) {
                    System.out.println("[MINOR_LECTURER] " + id + " already exists, skip.");
                    continue;
                }

                DeputyStaffs creator = campusDeputies.get(random.nextInt(campusDeputies.size()));

                MinorLecturers ml = new MinorLecturers();
                ml.setId(id);
                ml.setFirstName(firstNames[(idx + i) % firstNames.length]);
                ml.setLastName(lastNames[(idx + i) % lastNames.length]);
                ml.setEmail(id + "@minorlec.demo.com");
                ml.setPhoneNumber("+84104" + String.format("%07d", idx));
                ml.setBirthDate(LocalDate.of(1985 + (idx % 5), 4 + (idx % 12), 12 + (idx % 18)));
                ml.setGender(i % 2 == 0 ? Gender.FEMALE : Gender.MALE);
                ml.setCountry("Vietnam");

                fillAddressByCampus(ml, campus, 400 + idx);

                ml.setCampus(campus);
                ml.setEmploymentTypes(i % 2 == 0 ? EmploymentTypes.PART_TIME : EmploymentTypes.FULL_TIME);
                ml.setCreator(creator);

                em.persist(ml);
                createAuth(em, id, ml);
                System.out.println("[MINOR_LECTURER] Inserted " + id + " campus " + campus.getCampusId());
            }
        }
    }

    // ===================== STUDENTS =====================

    private static void seedStudents(EntityManager em) {
        Curriculum curr = find(em, Curriculum.class, "curriculumId", "CURR01");
        if (curr == null) {
            System.out.println("[STUDENT] No curriculum found, skip.");
            return;
        }

        List<Specialization> specs = em.createQuery("SELECT s FROM Specialization s", Specialization.class)
                .getResultList();
        if (specs.isEmpty()) {
            System.out.println("[STUDENT] No specialization found, skip.");
            return;
        }

        List<Campuses> campuses = em.createQuery("SELECT c FROM Campuses c ORDER BY c.campusId", Campuses.class)
                .getResultList();
        if (campuses.isEmpty()) {
            System.out.println("[STUDENT] No campus found, skip.");
            return;
        }

        List<Staffs> staffList = em.createQuery("SELECT s FROM Staffs s", Staffs.class).getResultList();
        if (staffList.isEmpty()) {
            System.out.println("[STUDENT] No staff found, skip.");
            return;
        }

        Staffs defaultCreator = staffList.get(0);
        String[] firstNames = {"An", "Bình", "Cường", "Duyên", "Đạt", "Hà", "Khánh", "Linh", "Mạnh", "Nhi",
                "Oanh", "Phúc", "Quân", "Trang", "Uyên", "Vy"};
        String[] lastNames = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Vũ", "Đỗ", "Bùi"};

        int created = 0;
        for (int i = 1; i <= TOTAL_STUDENTS; i++) {
            Campuses campus = campuses.get((i - 1) % campuses.size());

            String id = userId4("stu", i);
            if (exists(em, Students.class, "id", id)) {
                System.out.println("[STUDENT] " + id + " already exists, skip.");
                continue;
            }

            Students student = new Students();
            student.setId(id);
            student.setFirstName(firstNames[(i - 1) % firstNames.length]);
            student.setLastName(lastNames[(i - 1) % lastNames.length]);
            student.setEmail(id + "@student.demo.com");
            student.setPhoneNumber("+84105" + String.format("%07d", i));
            student.setBirthDate(LocalDate.of(2001 + (i % 3), 1 + (i % 12), 5 + (i % 20)));
            student.setGender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE);
            student.setCountry("Vietnam");

            fillAddressByCampus(student, campus, 10 + i);

            student.setPostalCode("100000");
            student.setAdmissionYear(2025);
            student.setCreator(defaultCreator);
            student.setCampus(campus);
            student.setCurriculum(curr);
            student.setSpecialization(specs.get((i - 1) % specs.size()));

            em.persist(student);
            createAuth(em, id, student);
            created++;
            System.out.println("[STUDENT] Inserted " + id + " (campus " + campus.getCampusId() + ")");
        }

        System.out.println("[STUDENT] Total inserted: " + created);
    }

    // ===================== PARENTS & RELATIONS =====================

    private static void seedParentAccountsAndRelations(EntityManager em) {
        Long relCount = em.createQuery("SELECT COUNT(r) FROM Student_ParentAccounts r", Long.class).getSingleResult();
        if (relCount > 0) {
            System.out.println("[PARENT] Student_ParentAccounts already has data, skip.");
            return;
        }

        List<Students> students = em.createQuery("SELECT s FROM Students s ORDER BY s.id", Students.class)
                .getResultList();
        if (students.isEmpty()) {
            System.out.println("[PARENT] No students, skip.");
            return;
        }

        List<Staffs> staffList = em.createQuery("SELECT s FROM Staffs s", Staffs.class).getResultList();
        Staffs defaultStaffCreator = staffList.isEmpty() ? null : staffList.get(0);
        Random random = new Random();

        String[] fatherFirstNames = {"Hùng", "Nam", "Thắng", "Dũng", "Quang", "Sơn", "Tú"};
        String[] motherFirstNames = {"Hoa", "Lan", "Hương", "Trang", "Nhung", "Thu", "Hà"};
        String[] lastNames = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Vũ", "Đinh"};

        int limit = Math.min(TOTAL_PARENTS, students.size());
        int parentCreated = 0;
        int relationsCreated = 0;

        for (int i = 0; i < limit; i++) {
            Students stu = students.get(i);
            String parentId = userId4("par", i + 1);
            ParentAccounts parent;

            if (exists(em, ParentAccounts.class, "id", parentId)) {
                parent = find(em, ParentAccounts.class, "id", parentId);
                System.out.println("[PARENT] " + parentId + " already exists, reuse.");
            } else {
                parent = new ParentAccounts();
                parent.setId(parentId);

                boolean isFather = (i % 2 == 0);
                if (isFather) {
                    parent.setFirstName(fatherFirstNames[i % fatherFirstNames.length]);
                    parent.setGender(Gender.MALE);
                } else {
                    parent.setFirstName(motherFirstNames[i % motherFirstNames.length]);
                    parent.setGender(Gender.FEMALE);
                }
                parent.setLastName(lastNames[i % lastNames.length]);
                parent.setEmail(parentId + "@parent.demo.com");
                parent.setPhoneNumber("+84106" + String.format("%07d", i + 1));
                parent.setBirthDate(LocalDate.of(1975 + (i % 5), 1 + (i % 12), 10 + (i % 18)));
                parent.setCountry("Vietnam");
                parent.setProvince("Hà Nội");
                parent.setCity("Hà Nội");
                parent.setDistrict("Thanh Xuân");
                parent.setWard("Khương Trung");
                parent.setStreet("Nhà phụ huynh số " + (20 + i));
                parent.setPostalCode("100000");
                parent.setCreator(defaultStaffCreator);

                em.persist(parent);
                createAuth(em, parentId, parent);
                parentCreated++;
                System.out.println("[PARENT] Inserted " + parentId);
            }

            RelationshipToStudent relationship =
                    parent.getGender() == Gender.MALE ? RelationshipToStudent.FATHER : RelationshipToStudent.MOTHER;

            Staffs addedBy = staffList.isEmpty()
                    ? null
                    : staffList.get(random.nextInt(staffList.size()));

            Student_ParentAccounts rel = new Student_ParentAccounts(
                    stu,
                    parent,
                    addedBy,
                    LocalDateTime.now().minusDays(random.nextInt(10)),
                    relationship,
                    parent.getPhoneNumber()
            );

            em.persist(rel);
            relationsCreated++;
            System.out.println("[PARENT] Linked parent " + parent.getId() + " with student " + stu.getId());
        }

        System.out.println("[PARENT] Total parents inserted: " + parentCreated);
        System.out.println("[PARENT] Total student-parent relations inserted: " + relationsCreated);
    }

    // ===================== SUBJECTS =====================

    private static void seedMajorSubjects(EntityManager em) {
        Curriculum curr = find(em, Curriculum.class, "curriculumId", "CURR01");
        Admins acceptor = find(em, Admins.class, "id", mainAdminId());

        // mỗi major 1-2 môn demo
        String[] majorIds = {"GBH", "GCH", "GDH", "GKH", "GKT", "GLU", "GQT", "GNN", "GTC"};
        String[] names = {
                "Nhập môn Quản trị",
                "Lập trình Java",
                "Thiết kế Cơ bản",
                "Marketing Căn bản",
                "Kế toán Tài chính",
                "Luật Đại cương",
                "Quản trị Du lịch Căn bản",
                "Tiếng Anh Tổng quát",
                "Tài chính Doanh nghiệp Căn bản"
        };

        for (int i = 0; i < majorIds.length; i++) {
            String id = "SUB_MAJ_" + String.format("%03d", i + 1);
            if (exists(em, MajorSubjects.class, "subjectId", id)) {
                System.out.println("[SUB_MAJOR] " + id + " already exists, skip.");
                continue;
            }

            Majors major = find(em, Majors.class, "majorId", majorIds[i]);
            Staffs creator = findStaffByMajorId(em, majorIds[i]);
            if (major == null || creator == null) continue;

            MajorSubjects s = new MajorSubjects();
            s.setSubjectId(id);
            s.setSubjectName(names[i]);
            s.setSemester(i + 1);
            s.setIsAccepted(true);
            s.setAcceptor(acceptor);
            s.setCreator(creator);
            s.setMajor(major);
            s.setCurriculum(curr);

            em.persist(s);
            System.out.println("[SUB_MAJOR] Inserted " + id + " - " + names[i]);
        }
    }

    private static void seedMinorSubjects(EntityManager em) {
        Admins acceptor = find(em, Admins.class, "id", mainAdminId());
        String[] names = {
                "Tiếng Anh Giao tiếp",
                "Kỹ năng Mềm",
                "Tư duy Phản biện",
                "Quản lý Thời gian",
                "Làm việc Nhóm",
                "Kỹ năng Thuyết trình"
        };

        for (int i = 0; i < names.length; i++) {
            String id = "SUB_MIN_" + String.format("%03d", i + 1);
            if (exists(em, MinorSubjects.class, "subjectId", id)) {
                System.out.println("[SUB_MINOR] " + id + " already exists, skip.");
                continue;
            }

            // random 1 deputy làm creator
            List<DeputyStaffs> deputies = em.createQuery("SELECT d FROM DeputyStaffs d", DeputyStaffs.class)
                    .getResultList();
            if (deputies.isEmpty()) {
                System.out.println("[SUB_MINOR] No deputy, skip all.");
                return;
            }
            DeputyStaffs creator = deputies.get(i % deputies.size());

            MinorSubjects s = new MinorSubjects();
            s.setSubjectId(id);
            s.setSubjectName(names[i]);
            s.setSemester(i + 1);
            s.setIsAccepted(true);
            s.setAcceptor(acceptor);
            s.setCreator(creator);

            em.persist(s);
            System.out.println("[SUB_MINOR] Inserted " + id + " - " + names[i]);
        }
    }

    private static void seedSpecializedSubjects(EntityManager em) {
        Curriculum curr = find(em, Curriculum.class, "curriculumId", "CURR01");
        Admins acceptor = find(em, Admins.class, "id", mainAdminId());

        String[] specIds = {
                "SPEC_IT_SE",
                "SPEC_IT_AI",
                "SPEC_BUS_FIN",
                "SPEC_DES_UI",
                "SPEC_MKT_DIG",
                "SPEC_FIN_CORP",
                "SPEC_ENG_BIZ",
                "SPEC_TOUR_MICE"
        };
        String[] names = {
                "Phát triển Web",
                "Machine Learning",
                "Ngân hàng Số",
                "Figma Design",
                "SEO & SEM",
                "Phân tích Báo cáo Tài chính",
                "Tiếng Anh Thương mại nâng cao",
                "Tổ chức Sự kiện MICE"
        };

        for (int i = 0; i < specIds.length; i++) {
            String id = "SUB_SPEC_" + String.format("%03d", i + 1);
            if (exists(em, SpecializedSubject.class, "subjectId", id)) {
                System.out.println("[SUB_SPEC] " + id + " already exists, skip.");
                continue;
            }

            Specialization spec = find(em, Specialization.class, "specializationId", specIds[i]);
            if (spec == null) continue;

            Majors major = spec.getMajor();
            Staffs creator = (major != null)
                    ? findStaffByMajorId(em, major.getMajorId())
                    : null;
            if (creator == null) continue;

            SpecializedSubject s = new SpecializedSubject();
            s.setSubjectId(id);
            s.setSubjectName(names[i]);
            s.setSemester(3 + i);
            s.setIsAccepted(true);
            s.setAcceptor(acceptor);
            s.setCreator(creator);
            s.setSpecialization(spec);
            s.setCurriculum(curr);

            em.persist(s);
            System.out.println("[SUB_SPEC] Inserted " + id + " - " + names[i]);
        }
    }

    // ===================== BALANCE / TUITION =====================

    private static void seedStudentBalancesAndDepositHistory(EntityManager em) {
        List<Students> students = em.createQuery("SELECT s FROM Students s", Students.class).getResultList();
        if (students.isEmpty()) {
            System.out.println("[BALANCE] No students, skip.");
            return;
        }

        Set<String> existingBalStudentIds = em.createQuery("SELECT b.studentId FROM AccountBalances b", String.class)
                .getResultStream().collect(Collectors.toSet());

        LocalDateTime now = LocalDateTime.now();
        int created = 0;

        for (Students student : students) {
            String studentId = student.getId();
            if (existingBalStudentIds.contains(studentId)) {
                System.out.println("[BALANCE] Balance already exists for " + studentId + ", skip.");
                continue;
            }

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
            deposit.setCurrentAmount(BigDecimal.valueOf(INITIAL_DEPOSIT_AMOUNT));
            deposit.setCreatedAt(now);
            deposit.setStatus(Status.COMPLETED);
            em.persist(deposit);

            created++;
            System.out.println("[BALANCE] Inserted balance & deposit for " + studentId);
        }

        System.out.println("[BALANCE] Total balances inserted: " + created);
    }

    private static void seedTuitionByYear(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", mainAdminId());

        List<Subjects> subjects = new ArrayList<>();
        subjects.addAll(em.createQuery("SELECT s FROM MajorSubjects s", MajorSubjects.class).getResultList());
        subjects.addAll(em.createQuery("SELECT s FROM MinorSubjects s", MinorSubjects.class).getResultList());
        subjects.addAll(em.createQuery("SELECT s FROM SpecializedSubject s", SpecializedSubject.class).getResultList());

        if (subjects.isEmpty()) {
            System.out.println("[TUITION] No subjects, skip.");
            return;
        }

        List<Campuses> campuses = em.createQuery("SELECT c FROM Campuses c", Campuses.class).getResultList();
        if (campuses.isEmpty()) {
            System.out.println("[TUITION] No campuses, skip.");
            return;
        }

        Integer[] years = {2025, 2026};
        Random rand = new Random();
        int inserted = 0;

        for (Campuses campus : campuses) {
            for (Subjects subject : subjects) {
                for (Integer year : years) {
                    TuitionByYearId id = new TuitionByYearId(subject.getSubjectId(), year, campus.getCampusId());
                    if (existsTuitionByYear(em, id)) {
                        System.out.println("[TUITION] existed " + subject.getSubjectId()
                                + " year " + year + " campus " + campus.getCampusId() + ", skip.");
                        continue;
                    }

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
                    inserted++;
                }
            }
        }

        System.out.println("[TUITION] Total inserted: " + inserted);
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

        int inserted = 0;
        for (String[] data : slotData) {
            if (exists(em, Slots.class, "slotId", data[0])) {
                System.out.println("[SLOT] " + data[0] + " already exists, skip.");
                continue;
            }
            Slots slot = new Slots();
            slot.setSlotId(data[0]);
            slot.setSlotName(data[1]);
            slot.setStartTime(LocalTime.parse(data[2]));
            slot.setEndTime(LocalTime.parse(data[3]));
            em.persist(slot);
            inserted++;
            System.out.println("[SLOT] Inserted " + data[0] + " - " + data[1]);
        }
        System.out.println("[SLOT] Total inserted: " + inserted);
    }

    private static void seedRooms(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", mainAdminId());
        List<Campuses> campuses = em.createQuery("SELECT c FROM Campuses c", Campuses.class).getResultList();
        if (campuses.isEmpty()) {
            System.out.println("[ROOM] No campuses, skip.");
            return;
        }

        String[] physicalIds = {"G101", "G102", "G201"};
        String[] physicalNames = {
                "Phòng G101 - Tầng 1",
                "Phòng G102 - Tầng 1",
                "Phòng G201 - Tầng 2"
        };

        int insertedOffline = 0;
        for (Campuses campus : campuses) {
            for (int i = 0; i < physicalIds.length; i++) {
                String roomId = campus.getCampusId() + "_" + physicalIds[i];
                if (exists(em, OfflineRooms.class, "roomId", roomId)) {
                    System.out.println("[ROOM_OFFLINE] " + roomId + " already exists, skip.");
                    continue;
                }
                OfflineRooms room = new OfflineRooms();
                room.setRoomId(roomId);
                room.setRoomName(physicalNames[i] + " - " + campus.getCampusName());
                room.setCreator(creator);
                room.setCampus(campus);
                room.setFloor((i / 2) + 1);
                em.persist(room);
                insertedOffline++;
                System.out.println("[ROOM_OFFLINE] Inserted " + roomId);
            }
        }

        String[] onlineIds = {"ONLINE01", "ZOOM01", "MEET01"};
        String[] onlineNames = {
                "Phòng Online 01",
                "Zoom Room 01",
                "Google Meet 01"
        };
        String[] links = {
                "https://zoom.us/j/1234567890",
                "https://zoom.us/j/1112223334",
                "https://meet.google.com/abc-defg-hij"
        };

        int insertedOnline = 0;
        for (Campuses campus : campuses) {
            for (int i = 0; i < onlineIds.length; i++) {
                String roomId = campus.getCampusId() + "_" + onlineIds[i];
                if (exists(em, OnlineRooms.class, "roomId", roomId)) {
                    System.out.println("[ROOM_ONLINE] " + roomId + " already exists, skip.");
                    continue;
                }
                OnlineRooms room = new OnlineRooms();
                room.setRoomId(roomId);
                room.setRoomName(onlineNames[i] + " - " + campus.getCampusName());
                room.setCreator(creator);
                room.setCampus(campus);
                room.setLink(links[i]);
                em.persist(room);
                insertedOnline++;
                System.out.println("[ROOM_ONLINE] Inserted " + roomId);
            }
        }

        System.out.println("[ROOM] Offline inserted: " + insertedOffline + ", Online inserted: " + insertedOnline);
    }

    // ===================== CLASSES & REQUIRED SUBJECTS =====================

    private static void seedClasses(EntityManager em) {
        seedMajorClasses(em);
        seedMinorClasses(em);
        seedSpecializedClasses(em);
    }

    private static void seedMajorClasses(EntityManager em) {
        Long count = em.createQuery("SELECT COUNT(c) FROM MajorClasses c", Long.class).getSingleResult();
        if (count > 0) {
            System.out.println("[CLASS_MAJOR] Already has data, skip.");
            return;
        }

        List<MajorSubjects> subjects = em.createQuery("SELECT s FROM MajorSubjects s", MajorSubjects.class)
                .getResultList();
        List<Staffs> staffList = em.createQuery("SELECT s FROM Staffs s", Staffs.class).getResultList();
        if (subjects.isEmpty() || staffList.isEmpty()) {
            System.out.println("[CLASS_MAJOR] No subject or staff, skip.");
            return;
        }

        // staff theo major
        Map<String, List<Staffs>> staffByMajor = staffList.stream()
                .filter(st -> st.getMajorManagement() != null && st.getMajorManagement().getMajorId() != null)
                .collect(Collectors.groupingBy(st -> st.getMajorManagement().getMajorId()));

        Sessions[] sessions = Sessions.values();
        Random random = new Random();

        int total = MAJOR_CLASSES_TOTAL;
        int perSubject = Math.max(1, total / subjects.size());
        int index = 1;
        int inserted = 0;

        for (MajorSubjects subj : subjects) {
            Majors major = subj.getMajor();
            if (major == null) continue;

            List<Staffs> staffForMajor = staffByMajor.get(major.getMajorId());
            if (staffForMajor == null || staffForMajor.isEmpty()) {
                System.out.println("[CLASS_MAJOR] No staff for major " + major.getMajorId() + ", skip classes.");
                continue;
            }

            for (int i = 0; i < perSubject; i++) {
                if (inserted >= total) break;
                String classId = "CLM-" + String.format("%03d", index++);
                if (exists(em, MajorClasses.class, "classId", classId)) continue;

                Staffs creator = staffForMajor.get(random.nextInt(staffForMajor.size()));

                MajorClasses mc = new MajorClasses(
                        classId,
                        "Lớp Major - " + subj.getSubjectName() + " - " + i,
                        30,
                        sessions[random.nextInt(sessions.length)],
                        subj,
                        creator,
                        LocalDateTime.now().minusDays(random.nextInt(5))
                );
                em.persist(mc);
                inserted++;
                System.out.println("[CLASS_MAJOR] Inserted " + classId + " for subject " + subj.getSubjectId());
            }
            if (inserted >= total) break;
        }

        System.out.println("[CLASS_MAJOR] Total inserted: " + inserted);
    }

    private static void seedMinorClasses(EntityManager em) {
        Long count = em.createQuery("SELECT COUNT(c) FROM MinorClasses c", Long.class).getSingleResult();
        if (count > 0) {
            System.out.println("[CLASS_MINOR] Already has data, skip.");
            return;
        }

        List<MinorSubjects> subjects = em.createQuery("SELECT s FROM MinorSubjects s", MinorSubjects.class)
                .getResultList();
        List<DeputyStaffs> deputyList = em.createQuery("SELECT d FROM DeputyStaffs d", DeputyStaffs.class)
                .getResultList();
        if (subjects.isEmpty() || deputyList.isEmpty()) {
            System.out.println("[CLASS_MINOR] No subject or deputy, skip.");
            return;
        }

        Sessions[] sessions = Sessions.values();
        Random random = new Random();

        int total = MINOR_CLASSES_TOTAL;
        int perSubject = Math.max(1, total / subjects.size());
        int index = 1;
        int inserted = 0;

        for (MinorSubjects subj : subjects) {
            for (int i = 0; i < perSubject; i++) {
                if (inserted >= total) break;
                String classId = "CLN-" + String.format("%03d", index++);
                if (exists(em, MinorClasses.class, "classId", classId)) continue;

                DeputyStaffs creator = deputyList.get(random.nextInt(deputyList.size()));

                MinorClasses mc = new MinorClasses(
                        classId,
                        "Lớp Minor - " + subj.getSubjectName() + " - " + i,
                        25,
                        sessions[random.nextInt(sessions.length)],
                        subj,
                        creator,
                        LocalDateTime.now().minusDays(random.nextInt(5))
                );
                em.persist(mc);
                inserted++;
                System.out.println("[CLASS_MINOR] Inserted " + classId + " for subject " + subj.getSubjectId());
            }
            if (inserted >= total) break;
        }

        System.out.println("[CLASS_MINOR] Total inserted: " + inserted);
    }

    private static void seedSpecializedClasses(EntityManager em) {
        Long count = em.createQuery("SELECT COUNT(c) FROM SpecializedClasses c", Long.class).getSingleResult();
        if (count > 0) {
            System.out.println("[CLASS_SPEC] Already has data, skip.");
            return;
        }

        List<SpecializedSubject> subjects = em.createQuery("SELECT s FROM SpecializedSubject s", SpecializedSubject.class)
                .getResultList();
        List<Staffs> staffList = em.createQuery("SELECT s FROM Staffs s", Staffs.class).getResultList();
        if (subjects.isEmpty() || staffList.isEmpty()) {
            System.out.println("[CLASS_SPEC] No subject or staff, skip.");
            return;
        }

        // staff theo major
        Map<String, List<Staffs>> staffByMajor = staffList.stream()
                .filter(st -> st.getMajorManagement() != null && st.getMajorManagement().getMajorId() != null)
                .collect(Collectors.groupingBy(st -> st.getMajorManagement().getMajorId()));

        Sessions[] sessions = Sessions.values();
        Random random = new Random();

        int total = SPEC_CLASSES_TOTAL;
        int perSubject = Math.max(1, total / subjects.size());
        int index = 1;
        int inserted = 0;

        for (SpecializedSubject subj : subjects) {
            Specialization spec = subj.getSpecialization();
            Majors major = (spec != null) ? spec.getMajor() : null;
            if (major == null) continue;

            List<Staffs> staffForMajor = staffByMajor.get(major.getMajorId());
            if (staffForMajor == null || staffForMajor.isEmpty()) {
                System.out.println("[CLASS_SPEC] No staff for major " + major.getMajorId() + ", skip classes.");
                continue;
            }

            for (int i = 0; i < perSubject; i++) {
                if (inserted >= total) break;
                String classId = "CLS-" + String.format("%03d", index++);
                if (exists(em, SpecializedClasses.class, "classId", classId)) continue;

                Staffs creator = staffForMajor.get(random.nextInt(staffForMajor.size()));

                SpecializedClasses sc = new SpecializedClasses(
                        classId,
                        "Lớp Specialized - " + subj.getSubjectName() + " - " + i,
                        20,
                        sessions[random.nextInt(sessions.length)],
                        subj,
                        creator,
                        LocalDateTime.now().minusDays(random.nextInt(5))
                );
                em.persist(sc);
                inserted++;
                System.out.println("[CLASS_SPEC] Inserted " + classId + " for subject " + subj.getSubjectId());
            }
            if (inserted >= total) break;
        }

        System.out.println("[CLASS_SPEC] Total inserted: " + inserted);
    }

    /**
     * Logic mới cho REQUIRED SUBJECTS:
     * - MajorRequired: full tất cả MajorSubjects theo major của student, creator = staff cùng major + campus
     * - SpecRequired: full tất cả SpecializedSubject theo specialization của student, creator = staff cùng major + campus
     * - MinorRequired: full tất cả MinorSubjects, creator = deputy cùng campus
     */
    private static void seedStudentRequiredSubjects(EntityManager em) {
        Long countMaj = em.createQuery(
                "SELECT COUNT(s) FROM StudentRequiredMajorSubjects s", Long.class
        ).getSingleResult();
        Long countMin = em.createQuery(
                "SELECT COUNT(s) FROM StudentRequiredMinorSubjects s", Long.class
        ).getSingleResult();
        Long countSpec = em.createQuery(
                "SELECT COUNT(s) FROM StudentRequiredSpecializedSubjects s", Long.class
        ).getSingleResult();

        if (countMaj + countMin + countSpec > 0) {
            System.out.println("[REQ_SUBJECT] Already has data in required tables, skip.");
            return;
        }

        List<Students> students = em.createQuery("SELECT s FROM Students s ORDER BY s.id", Students.class)
                .getResultList();
        if (students.isEmpty()) {
            System.out.println("[REQ_SUBJECT] No students, skip.");
            return;
        }

        List<MajorSubjects> majorSubjects = em.createQuery(
                "SELECT s FROM MajorSubjects s", MajorSubjects.class
        ).getResultList();
        List<MinorSubjects> minorSubjects = em.createQuery(
                "SELECT s FROM MinorSubjects s", MinorSubjects.class
        ).getResultList();
        List<SpecializedSubject> specSubjects = em.createQuery(
                "SELECT s FROM SpecializedSubject s", SpecializedSubject.class
        ).getResultList();
        List<Staffs> staffList = em.createQuery(
                "SELECT s FROM Staffs s", Staffs.class
        ).getResultList();
        List<DeputyStaffs> deputyList = em.createQuery(
                "SELECT d FROM DeputyStaffs d", DeputyStaffs.class
        ).getResultList();

        if (majorSubjects.isEmpty() || minorSubjects.isEmpty() || specSubjects.isEmpty()
                || staffList.isEmpty() || deputyList.isEmpty()) {
            System.out.println("[REQ_SUBJECT] Missing subject/staff/deputy, skip.");
            return;
        }

        // MajorSubjects theo majorId
        Map<String, List<MajorSubjects>> majorSubjectsByMajor = majorSubjects.stream()
                .filter(ms -> ms.getMajor() != null && ms.getMajor().getMajorId() != null)
                .collect(Collectors.groupingBy(ms -> ms.getMajor().getMajorId()));

        // SpecializedSubject theo specializationId
        Map<String, List<SpecializedSubject>> specSubjectsBySpec = specSubjects.stream()
                .filter(ss -> ss.getSpecialization() != null && ss.getSpecialization().getSpecializationId() != null)
                .collect(Collectors.groupingBy(ss -> ss.getSpecialization().getSpecializationId()));

        // Staffs theo (majorId + campusId)
        Map<String, List<Staffs>> staffByMajorAndCampus = staffList.stream()
                .filter(st -> st.getMajorManagement() != null
                        && st.getMajorManagement().getMajorId() != null
                        && st.getCampus() != null
                        && st.getCampus().getCampusId() != null)
                .collect(Collectors.groupingBy(st ->
                        st.getMajorManagement().getMajorId() + "#" + st.getCampus().getCampusId()
                ));

        // DeputyStaffs theo campusId
        Map<String, List<DeputyStaffs>> deputyByCampus = deputyList.stream()
                .filter(d -> d.getCampus() != null && d.getCampus().getCampusId() != null)
                .collect(Collectors.groupingBy(d -> d.getCampus().getCampusId()));

        Random random = new Random();
        int inserted = 0;

        for (Students stu : students) {
            if (stu.getSpecialization() == null
                    || stu.getSpecialization().getMajor() == null
                    || stu.getSpecialization().getMajor().getMajorId() == null
                    || stu.getCampus() == null
                    || stu.getCampus().getCampusId() == null) {

                System.out.println("[REQ_SUBJECT] Student " + stu.getId()
                        + " missing specialization/major/campus, skip.");
                continue;
            }

            String majorId = stu.getSpecialization().getMajor().getMajorId();
            String specId = stu.getSpecialization().getSpecializationId();
            String campusId = stu.getCampus().getCampusId();

            String staffKey = majorId + "#" + campusId;
            List<Staffs> staffsForStudent = staffByMajorAndCampus.get(staffKey);

            // ====== Major + Specialized: đảm bảo staff đúng ngành + đúng campus ======
            if (staffsForStudent == null || staffsForStudent.isEmpty()) {
                System.out.println("[REQ_SUBJECT] NO Staff for student " + stu.getId()
                        + " (major=" + majorId + ", campus=" + campusId
                        + "), skip major & specialized required.");
            } else {
                Staffs assignedStaff = staffsForStudent.get(random.nextInt(staffsForStudent.size()));

                // Major required = full tất cả MajorSubjects của major đó
                List<MajorSubjects> majorListForStudent =
                        majorSubjectsByMajor.getOrDefault(majorId, Collections.emptyList());
                for (MajorSubjects majSub : majorListForStudent) {
                    StudentRequiredMajorSubjects rMaj = new StudentRequiredMajorSubjects(
                            stu,
                            majSub,
                            "Bắt buộc học major subject: " + majSub.getSubjectName(),
                            assignedStaff
                    );
                    rMaj.setNotificationType(YourNotification.NOTIFICATION_014);
                    em.persist(rMaj);
                    inserted++;
                }

                // Specialized required = full tất cả SpecializedSubject theo specialization của student
                List<SpecializedSubject> specListForStudent =
                        specSubjectsBySpec.getOrDefault(specId, Collections.emptyList());
                for (SpecializedSubject specSub : specListForStudent) {
                    StudentRequiredSpecializedSubjects rSpec = new StudentRequiredSpecializedSubjects(
                            stu,
                            specSub,
                            "Bắt buộc học specialized subject: " + specSub.getSubjectName(),
                            assignedStaff
                    );
                    rSpec.setNotificationType(YourNotification.NOTIFICATION_016);
                    em.persist(rSpec);
                    inserted++;
                }
            }

            // ====== Minor: dùng DeputyStaffs cùng campus ======
            List<DeputyStaffs> deputiesForCampus = deputyByCampus.get(campusId);
            if (deputiesForCampus == null || deputiesForCampus.isEmpty()) {
                System.out.println("[REQ_SUBJECT] NO DeputyStaff for campus "
                        + campusId + " (student " + stu.getId()
                        + "), skip minor required.");
            } else {
                DeputyStaffs assignedDeputy = deputiesForCampus.get(random.nextInt(deputiesForCampus.size()));

                // Minor required = full tất cả MinorSubjects
                for (MinorSubjects minSub : minorSubjects) {
                    StudentRequiredMinorSubjects rMin = new StudentRequiredMinorSubjects(
                            stu,
                            minSub,
                            "Bắt buộc học minor subject: " + minSub.getSubjectName(),
                            assignedDeputy
                    );
                    rMin.setNotificationType(YourNotification.NOTIFICATION_015);
                    em.persist(rMin);
                    inserted++;
                }
            }

            System.out.println("[REQ_SUBJECT] Inserted required subjects for student " + stu.getId());
        }

        System.out.println("[REQ_SUBJECT] Total required rows inserted: " + inserted);
    }

    // ===================== HELPERS =====================

    private static void createAuth(EntityManager em, String personId, Persons person) {
        if (exists(em, Authenticators.class, "personId", personId)) {
            System.out.println("[AUTH] Auth already exists for " + personId + ", skip.");
            return;
        }
        Authenticators auth = new Authenticators();
        auth.setPersonId(personId);
        auth.setPerson(person);
        auth.setPassword(DEFAULT_PASSWORD);
        em.persist(auth);
        System.out.println("[AUTH] Inserted auth for " + personId);
    }

    /**
     * Helper exists():
     * - Nếu clazz là Persons hoặc subclass → luôn check trên Persons theo ID.
     */
    private static <T> boolean exists(EntityManager em, Class<T> clazz, String idField, String idValue) {
        Class<?> targetClass = clazz;
        String field = idField;

        if (Persons.class.isAssignableFrom(clazz)) {
            targetClass = Persons.class;
            field = "id";
        }

        String jpql = "SELECT 1 FROM " + targetClass.getSimpleName() + " e WHERE e." + field + " = :id";
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

    private static String userId3(String prefix, int index) {
        return prefix + CAMPUS_CODE + String.format("%03d", index);
    }

    private static String userId4(String prefix, int index) {
        return prefix + CAMPUS_CODE + String.format("%04d", index);
    }

    private static String mainAdminId() {
        return userId3("admin", 1);
    }

    /**
     * Điền địa chỉ theo campus cho tất cả entity có field address giống nhau.
     */
    private static void fillAddressByCampus(Persons person, Campuses campus, int houseNumber) {
        if (campus == null || campus.getCampusId() == null) {
            person.setProvince("Hà Nội");
            person.setCity("Hà Nội");
            person.setDistrict("Đống Đa");
            person.setWard("Láng Hạ");
            person.setStreet("Số " + houseNumber + " Đường Demo");
            person.setPostalCode("100000");
            return;
        }

        String campusId = campus.getCampusId();
        switch (campusId) {
            case CAMPUS_ID_HCM -> {
                person.setProvince("TP. Hồ Chí Minh");
                person.setCity("TP. Hồ Chí Minh");
                person.setDistrict("Quận 1");
                person.setWard("Bến Nghé");
                person.setStreet("Số " + houseNumber + " Đường Demo Sài Gòn");
            }
            case CAMPUS_ID_DANANG -> {
                person.setProvince("Đà Nẵng");
                person.setCity("Đà Nẵng");
                person.setDistrict("Hải Châu");
                person.setWard("Thạch Thang");
                person.setStreet("Số " + houseNumber + " Đường Demo Đà Nẵng");
            }
            case CAMPUS_ID_CANTHO -> {
                person.setProvince("Cần Thơ");
                person.setCity("Cần Thơ");
                person.setDistrict("Ninh Kiều");
                person.setWard("An Lạc");
                person.setStreet("Số " + houseNumber + " Đường Demo Cần Thơ");
            }
            case CAMPUS_ID_HAIPHONG -> {
                person.setProvince("Hải Phòng");
                person.setCity("Hải Phòng");
                person.setDistrict("Ngô Quyền");
                person.setWard("Máy Tơ");
                person.setStreet("Số " + houseNumber + " Đường Demo Hải Phòng");
            }
            case CAMPUS_ID_HANOI -> {
                    person.setProvince("Hà Nội");
                    person.setCity("Hà Nội");
                    person.setDistrict("Đống Đa");
                    person.setWard("Láng Hạ");
                    person.setStreet("Số " + houseNumber + " Đường Demo Hà Nội");
                }
        }
        person.setPostalCode("100000");
    }
}
