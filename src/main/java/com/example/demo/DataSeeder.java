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

    private static final int TOTAL_STUDENTS = 50;
    private static final int MAJOR_CLASSES_TOTAL = 10;
    private static final int MINOR_CLASSES_TOTAL = 5;
    private static final int SPEC_CLASSES_TOTAL = 5;
    private static final int REQUIRED_SUBJECTS_LIMIT = 40;

    private static final int TOTAL_PARENTS = 50;

    private static final String CAMPUS_ID_HANOI = "CAMP01";
    private static final String CAMPUS_ID_HCM = "CAMP02";
    private static final String CAMPUS_ID_DANANG = "CAMP03";

    private static final String CAMPUS_CODE_HANOI = "hn";
    private static final String CAMPUS_CODE_HCM = "hcm";
    private static final String CAMPUS_CODE_DANANG = "dn";

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
        String[] ids = {"GBH", "GCH", "GDH", "GKH", "GKT"};
        String[] names = {
                "Quản trị Kinh doanh",
                "Công nghệ Thông tin",
                "Thiết kế Đồ họa",
                "Marketing",
                "Kế toán"
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
                {"SPEC_MKT_DIG", "Marketing Kỹ thuật số", "GKH"}
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
        Campuses campus = find(em, Campuses.class, "campusId", CAMPUS_ID_HANOI);

        String[] majorIds = {"GBH", "GCH", "GDH", "GKH", "GKT"};
        String[] firstNames = {"Minh", "Lan", "Hùng", "Mai", "Tuấn"};
        String[] lastNames = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng"};

        for (int i = 0; i < majorIds.length; i++) {
            String id = userId3("staff", i + 1);
            if (exists(em, Staffs.class, "id", id)) {
                System.out.println("[STAFF] " + id + " already exists, skip.");
                continue;
            }

            Majors major = find(em, Majors.class, "majorId", majorIds[i]);

            Staffs s = new Staffs();
            s.setId(id);
            s.setFirstName(firstNames[i]);
            s.setLastName(lastNames[i]);
            s.setEmail(id + "@staff.demo.com");
            s.setPhoneNumber("+84101" + String.format("%07d", i + 1));
            s.setBirthDate(LocalDate.of(1985 + i % 3, 1 + i % 12, 1 + i % 28));
            s.setGender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE);
            s.setCountry("Vietnam");
            s.setProvince("Hà Nội");
            s.setCity("Hà Nội");
            s.setDistrict("Cầu Giấy");
            s.setWard("Dịch Vọng");
            s.setStreet("123 Trần Duy Hưng");
            s.setPostalCode("100000");
            s.setMajorManagement(major);
            s.setCampus(campus);
            s.setCreator(creator);

            em.persist(s);
            createAuth(em, id, s);
            System.out.println("[STAFF] Inserted " + id + " - major " + majorIds[i]);
        }
    }

    private static void seedDeputyStaffs(EntityManager em) {
        Admins creator = find(em, Admins.class, "id", mainAdminId());
        Campuses campus = find(em, Campuses.class, "campusId", CAMPUS_ID_HANOI);

        String[] firstNames = {"Anh", "Bình", "Cường"};
        String[] lastNames = {"Trần", "Lê", "Phạm"};

        for (int i = 0; i < firstNames.length; i++) {
            String id = userId3("deputy", i + 1);
            if (exists(em, DeputyStaffs.class, "id", id)) {
                System.out.println("[DEPUTY] " + id + " already exists, skip.");
                continue;
            }

            DeputyStaffs d = new DeputyStaffs();
            d.setId(id);
            d.setFirstName(firstNames[i]);
            d.setLastName(lastNames[i]);
            d.setEmail(id + "@deputy.demo.com");
            d.setPhoneNumber("+84102" + String.format("%07d", i + 1));
            d.setBirthDate(LocalDate.of(1990 + i % 3, 2 + i % 10, 5 + i % 20));
            d.setGender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE);
            d.setCountry("Vietnam");
            d.setProvince("Hà Nội");
            d.setCity("Hà Nội");
            d.setDistrict("Ba Đình");
            d.setWard("Phúc Xá");
            d.setStreet("45 Lê Duẩn");
            d.setPostalCode("100000");
            d.setCampus(campus);
            d.setCreator(creator);

            em.persist(d);
            createAuth(em, id, d);
            System.out.println("[DEPUTY] Inserted " + id);
        }
    }

    private static void seedMajorLecturers(EntityManager em) {
        String[] majorIds = {"GBH", "GCH", "GDH", "GKH", "GKT"};
        String[] firstNames = {"Hải", "Yến", "Phong", "Thư"};
        String[] lastNames = {"Lê", "Phạm", "Hoàng", "Vũ"};

        int lecturerIndex = 1;

        for (String majorId : majorIds) {
            Majors major = find(em, Majors.class, "majorId", majorId);
            if (major == null) continue;

            Staffs creator = findStaffByMajorId(em, majorId);
            if (creator == null) continue;

            Campuses campus = creator.getCampus();

            for (int i = 0; i < 2; i++) {
                String id = userId3("lect", lecturerIndex++);
                if (exists(em, MajorLecturers.class, "id", id)) {
                    System.out.println("[LECTURER] " + id + " already exists, skip.");
                    continue;
                }

                MajorLecturers l = new MajorLecturers();
                l.setId(id);
                l.setFirstName(firstNames[i % firstNames.length]);
                l.setLastName(lastNames[i % lastNames.length]);
                l.setEmail(id + "@lect.demo.com");
                l.setPhoneNumber("+84103" + String.format("%07d", lecturerIndex));
                l.setBirthDate(LocalDate.of(1975 + i, 3 + i % 10, 10 + i % 10));
                l.setGender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE);
                l.setCountry("Vietnam");
                l.setProvince("Hà Nội");
                l.setCity("Hà Nội");
                l.setDistrict("Đống Đa");
                l.setWard("Trung Tự");
                l.setStreet("78 Nguyễn Lương Bằng");
                l.setPostalCode("100000");
                l.setMajorManagement(major);
                l.setCampus(campus);
                l.setEmploymentTypes(EmploymentTypes.FULL_TIME);
                l.setCreator(creator);

                em.persist(l);
                createAuth(em, id, l);
                System.out.println("[LECTURER] Inserted " + id + " for major " + majorId);
            }
        }
    }

    private static void seedMinorLecturers(EntityManager em) {
        List<DeputyStaffs> deputies = em.createQuery("SELECT d FROM DeputyStaffs d", DeputyStaffs.class).getResultList();
        if (deputies.isEmpty()) {
            System.out.println("[MINOR_LECTURER] No deputy found, skip seeding.");
            return;
        }

        String[] firstNames = {"Hảo", "Giang", "Trang"};
        String[] lastNames = {"Nguyễn", "Trần", "Lê"};

        int idx = 1;
        for (int i = 0; i < firstNames.length; i++) {
            String id = userId3("minlect", idx++);
            if (exists(em, MinorLecturers.class, "id", id)) {
                System.out.println("[MINOR_LECTURER] " + id + " already exists, skip.");
                continue;
            }

            DeputyStaffs creator = deputies.get(i % deputies.size());
            Campuses campus = creator.getCampus();

            MinorLecturers ml = new MinorLecturers();
            ml.setId(id);
            ml.setFirstName(firstNames[i]);
            ml.setLastName(lastNames[i]);
            ml.setEmail(id + "@minorlec.demo.com");
            ml.setPhoneNumber("+84104" + String.format("%07d", idx));
            ml.setBirthDate(LocalDate.of(1985 + i, 4 + i, 12 + i));
            ml.setGender(i % 2 == 0 ? Gender.FEMALE : Gender.MALE);
            ml.setCountry("Vietnam");
            ml.setProvince(campus != null && CAMPUS_ID_HCM.equals(campus.getCampusId()) ? "TP. Hồ Chí Minh" :
                    CAMPUS_ID_DANANG.equals(campus.getCampusId()) ? "Đà Nẵng" : "Hà Nội");
            ml.setCity(ml.getProvince());
            ml.setDistrict("Hoàn Kiếm");
            ml.setWard("Hàng Bài");
            ml.setStreet("Đường Minor Lec " + (i + 1));
            ml.setPostalCode("100000");
            ml.setCampus(campus);
            ml.setEmploymentTypes(i % 2 == 0 ? EmploymentTypes.PART_TIME : EmploymentTypes.FULL_TIME);
            ml.setCreator(creator);

            em.persist(ml);
            createAuth(em, id, ml);
            System.out.println("[MINOR_LECTURER] Inserted " + id);
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
        String[] firstNames = {"An", "Bình", "Cường", "Duyên", "Đạt", "Hà", "Khánh", "Linh", "Mạnh", "Nhi"};
        String[] lastNames = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng"};

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

            if (CAMPUS_ID_HANOI.equals(campus.getCampusId())) {
                student.setProvince("Hà Nội");
                student.setCity("Hà Nội");
                student.setDistrict("Đống Đa");
                student.setWard("Láng Hạ");
                student.setStreet("Số " + (10 + i) + " Đường Demo Hà Nội");
            } else if (CAMPUS_ID_HCM.equals(campus.getCampusId())) {
                student.setProvince("TP. Hồ Chí Minh");
                student.setCity("TP. Hồ Chí Minh");
                student.setDistrict("Quận 1");
                student.setWard("Bến Nghé");
                student.setStreet("Số " + (10 + i) + " Đường Demo Sài Gòn");
            } else if (CAMPUS_ID_DANANG.equals(campus.getCampusId())) {
                student.setProvince("Đà Nẵng");
                student.setCity("Đà Nẵng");
                student.setDistrict("Hải Châu");
                student.setWard("Thạch Thang");
                student.setStreet("Số " + (10 + i) + " Đường Demo Đà Nẵng");
            } else {
                student.setProvince("Hà Nội");
                student.setCity("Hà Nội");
                student.setDistrict("Đống Đa");
                student.setWard("Láng Hạ");
                student.setStreet("Số " + (10 + i) + " Đường Demo");
            }

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

        String[] fatherFirstNames = {"Hùng", "Nam", "Thắng", "Dũng", "Quang"};
        String[] motherFirstNames = {"Hoa", "Lan", "Hương", "Trang", "Nhung"};
        String[] lastNames = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng"};

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

        String[] majorIds = {"GBH", "GCH", "GDH", "GKH", "GKT"};
        String[] names = {
                "Nhập môn Quản trị",
                "Lập trình Java",
                "Thiết kế Cơ bản",
                "Marketing Căn bản",
                "Kế toán Tài chính"
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
                "Làm việc Nhóm"
        };

        for (int i = 0; i < names.length; i++) {
            String id = "SUB_MIN_" + String.format("%03d", i + 1);
            if (exists(em, MinorSubjects.class, "subjectId", id)) {
                System.out.println("[SUB_MINOR] " + id + " already exists, skip.");
                continue;
            }

            String deputyId = userId3("deputy", (i % 3) + 1);
            DeputyStaffs creator = find(em, DeputyStaffs.class, "id", deputyId);
            if (creator == null) continue;

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

        String[] specIds = {"SPEC_IT_SE", "SPEC_IT_AI", "SPEC_BUS_FIN", "SPEC_DES_UI", "SPEC_MKT_DIG"};
        String[] names = {
                "Phát triển Web",
                "Machine Learning",
                "Ngân hàng Số",
                "Figma Design",
                "SEO & SEM"
        };

        for (int i = 0; i < specIds.length; i++) {
            String id = "SUB_SPEC_" + String.format("%03d", i + 1);
            if (exists(em, SpecializedSubject.class, "subjectId", id)) {
                System.out.println("[SUB_SPEC] " + id + " already exists, skip.");
                continue;
            }

            Specialization spec = find(em, Specialization.class, "specializationId", specIds[i]);
            if (spec == null) continue;

            Staffs creator = findStaffByMajorId(em, spec.getMajor().getMajorId());
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

        List<MajorSubjects> subjects = em.createQuery("SELECT s FROM MajorSubjects s", MajorSubjects.class).getResultList();
        List<Staffs> staffList = em.createQuery("SELECT s FROM Staffs s", Staffs.class).getResultList();
        if (subjects.isEmpty() || staffList.isEmpty()) {
            System.out.println("[CLASS_MAJOR] No subject or staff, skip.");
            return;
        }

        Sessions[] sessions = Sessions.values();
        Random random = new Random();

        int total = MAJOR_CLASSES_TOTAL;
        int perSubject = Math.max(1, total / subjects.size());
        int index = 1;
        int inserted = 0;

        for (MajorSubjects subj : subjects) {
            for (int i = 0; i < perSubject; i++) {
                if (inserted >= total) break;
                String classId = "CLM-" + String.format("%03d", index++);
                if (exists(em, MajorClasses.class, "classId", classId)) continue;

                Staffs creator = staffList.get(random.nextInt(staffList.size()));

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

        List<MinorSubjects> subjects = em.createQuery("SELECT s FROM MinorSubjects s", MinorSubjects.class).getResultList();
        List<DeputyStaffs> deputyList = em.createQuery("SELECT d FROM DeputyStaffs d", DeputyStaffs.class).getResultList();
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

        List<SpecializedSubject> subjects = em.createQuery("SELECT s FROM SpecializedSubject s", SpecializedSubject.class).getResultList();
        List<Staffs> staffList = em.createQuery("SELECT s FROM Staffs s", Staffs.class).getResultList();
        if (subjects.isEmpty() || staffList.isEmpty()) {
            System.out.println("[CLASS_SPEC] No subject or staff, skip.");
            return;
        }

        Sessions[] sessions = Sessions.values();
        Random random = new Random();

        int total = SPEC_CLASSES_TOTAL;
        int perSubject = Math.max(1, total / subjects.size());
        int index = 1;
        int inserted = 0;

        for (SpecializedSubject subj : subjects) {
            for (int i = 0; i < perSubject; i++) {
                if (inserted >= total) break;
                String classId = "CLS-" + String.format("%03d", index++);
                if (exists(em, SpecializedClasses.class, "classId", classId)) continue;

                Staffs creator = staffList.get(random.nextInt(staffList.size()));

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

    private static void seedStudentRequiredSubjects(EntityManager em) {
        Long count = em.createQuery("SELECT COUNT(s) FROM StudentRequiredSubjects s", Long.class).getSingleResult();
        if (count > 0) {
            System.out.println("[REQ_SUBJECT] Already has data, skip.");
            return;
        }

        List<Students> students = em.createQuery("SELECT s FROM Students s ORDER BY s.id", Students.class).getResultList();
        if (students.isEmpty()) {
            System.out.println("[REQ_SUBJECT] No students, skip.");
            return;
        }

        List<MajorSubjects> majorSubjects = em.createQuery("SELECT s FROM MajorSubjects s", MajorSubjects.class).getResultList();
        List<MinorSubjects> minorSubjects = em.createQuery("SELECT s FROM MinorSubjects s", MinorSubjects.class).getResultList();
        List<SpecializedSubject> specSubjects = em.createQuery("SELECT s FROM SpecializedSubject s", SpecializedSubject.class).getResultList();
        List<Staffs> staffList = em.createQuery("SELECT s FROM Staffs s", Staffs.class).getResultList();
        List<DeputyStaffs> deputyList = em.createQuery("SELECT d FROM DeputyStaffs d", DeputyStaffs.class).getResultList();

        if (majorSubjects.isEmpty() || minorSubjects.isEmpty() || specSubjects.isEmpty()
                || staffList.isEmpty() || deputyList.isEmpty()) {
            System.out.println("[REQ_SUBJECT] Missing subject/staff/deputy, skip.");
            return;
        }

        Random random = new Random();
        int limit = Math.min(REQUIRED_SUBJECTS_LIMIT, students.size());
        int inserted = 0;

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
            rMaj.setNotificationType(YourNotification.NOTIFICATION_014);
            em.persist(rMaj);

            MinorSubjects minSub = minorSubjects.get(i % minorSubjects.size());
            DeputyStaffs assignedDeputy = deputyList.get(random.nextInt(deputyList.size()));
            StudentRequiredMinorSubjects rMin = new StudentRequiredMinorSubjects(
                    stu,
                    minSub,
                    "Bắt buộc học minor subject: " + minSub.getSubjectName(),
                    assignedDeputy
            );
            rMin.setNotificationType(YourNotification.NOTIFICATION_015);
            em.persist(rMin);

            SpecializedSubject specSub = specSubjects.get(i % specSubjects.size());
            Staffs assignedStaff2 = staffList.get(random.nextInt(staffList.size()));
            StudentRequiredSpecializedSubjects rSpec = new StudentRequiredSpecializedSubjects(
                    stu,
                    specSub,
                    "Bắt buộc học specialized subject: " + specSub.getSubjectName(),
                    assignedStaff2
            );
            rSpec.setNotificationType(YourNotification.NOTIFICATION_016);
            em.persist(rSpec);

            inserted += 3;
            System.out.println("[REQ_SUBJECT] Inserted required subjects for " + stu.getId());
        }

        System.out.println("[REQ_SUBJECT] Total rows inserted (3 per student): " + inserted);
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
     * Helper exists() "an toàn tuyệt đối":
     * - Nếu clazz là Persons hoặc subclass của Persons (Admins, Students, Staffs, Lecturers, Parents…)
     *   thì luôn check trên bảng Persons theo ID (tránh trùng PK bất kể discriminator).
     * - Các entity khác (Campuses, Majors, Subjects, …) giữ nguyên hành vi cũ.
     */
    private static <T> boolean exists(EntityManager em, Class<T> clazz, String idField, String idValue) {
        Class<?> targetClass = clazz;
        String field = idField;

        // Nếu là People (Persons hoặc subclass) → luôn check trên bảng Persons theo ID
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
}
