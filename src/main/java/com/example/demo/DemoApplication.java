package com.example.demo;
import com.example.demo.admin.model.Admins;
import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.campus.model.Campuses;
import com.example.demo.deputyStaff.model.DeputyStaffs;
import com.example.demo.entity.*;
import com.example.demo.entity.Enums.*;
import com.example.demo.major.model.Majors;
import com.example.demo.staff.model.Staffs;
import com.example.demo.person.model.Persons;
import com.example.demo.subject.model.MajorSubjects;
import com.example.demo.subject.model.MinorSubjects;
import com.example.demo.subject.model.Subjects;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(DemoApplication.class, args);
        EntityManagerFactory emf = context.getBean(EntityManagerFactory.class);
        EntityManager em = emf.createEntityManager();

        // Seed Admin first to ensure it exists for other entities
        try {
            em.getTransaction().begin();
            ensureDefaultAdmin(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            rollback(em);
            e.printStackTrace();
        }

        // Seed Campuses
        try {
            em.getTransaction().begin();
            addDefaultCampuses(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            rollback(em);
            e.printStackTrace();
        }

        // Seed Slots
        try {
            em.getTransaction().begin();
            addDefaultSlots(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            rollback(em);
            e.printStackTrace();
        }

        // Seed Majors
        try {
            em.getTransaction().begin();
            addDefaultMajors(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            rollback(em);
            e.printStackTrace();
        }

        // Seed Staffs
        try {
            em.getTransaction().begin();
            addDefaultStaffs(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            rollback(em);
            e.printStackTrace();
        }

        // Seed DeputyStaff
        try {
            em.getTransaction().begin();
            addDefaultDeputyStaff(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            rollback(em);
            e.printStackTrace();
        }

        // Seed MajorSubjects
        try {
            em.getTransaction().begin();
            addDefaultMajorSubjects(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            rollback(em);
            e.printStackTrace();
        }

        // Seed MinorSubjects
        try {
            em.getTransaction().begin();
            addDefaultMinorSubjects(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            rollback(em);
            e.printStackTrace();
        }

        // Seed Subjects and TuitionByYear
        try {
            em.getTransaction().begin();
            bulkSeedSubjectsAndTuition(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            rollback(em);
            e.printStackTrace();
        }

        em.close();
    }

    private static void rollback(EntityManager em) {
        if (em.getTransaction().isActive()) em.getTransaction().rollback();
    }

    // ===================== ENSURE DEFAULT ADMIN =====================
    private static void ensureDefaultAdmin(EntityManager em) {
        Admins admin = findAdmin(em, "Admin");
        if (admin == null) {
            admin = new Admins();
            admin.setId("Admin");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@example.com");
            admin.setPhoneNumber("0390000000");
            admin.setBirthDate(LocalDate.of(1980, 1, 1));
            admin.setGender(Gender.OTHER);
            admin.setCountry("Vietnam");
            admin.setProvince("Hanoi");
            admin.setCity("Hanoi");
            admin.setDistrict("Ba Dinh");
            admin.setWard("Ngoc Ha");
            admin.setStreet("10 Ngoc Ha");
            admin.setPostalCode("100000");
            em.persist(admin);

            Authenticators auth = new Authenticators();
            auth.setPersonId(admin.getId());
            auth.setPerson(admin);
            auth.setPassword("Admin123"); // Plain text password
            em.persist(auth);
            System.out.println("Added Admin: " + admin.getId());
        } else {
            System.out.println("Admin already exists: " + admin.getId());
        }
    }

    // ===================== CAMPUSES =====================
    private static void addDefaultCampuses(EntityManager em) {
        Admins creator = findAdmin(em, "Admin");
        if (creator == null) {
            System.err.println("Admin not found, cannot create campuses.");
            return;
        }

        List<Campuses> campusesToAdd = new ArrayList<>();
        campusesToAdd.add(new Campuses("CAMP01", "Hanoi Campus", LocalDate.of(2010, 1, 1), "Main campus in Hanoi", creator));
        campusesToAdd.add(new Campuses("CAMP02", "Ho Chi Minh Campus", LocalDate.of(2012, 5, 15), "Main campus in HCMC", creator));
        campusesToAdd.add(new Campuses("CAMP03", "Da Nang Campus", LocalDate.of(2015, 3, 20), "Central region campus", creator));
        campusesToAdd.add(new Campuses("CAMP04", "Hai Phong Campus", LocalDate.of(2016, 9, 10), "Northern coastal city campus", creator));
        campusesToAdd.add(new Campuses("CAMP05", "Can Tho Campus", LocalDate.of(2018, 11, 25), "Mekong Delta campus", creator));
        campusesToAdd.add(new Campuses("CAMP06", "Hue Campus", LocalDate.of(2019, 4, 5), "Central heritage city campus", creator));
        campusesToAdd.add(new Campuses("CAMP07", "Quy Nhon Campus", LocalDate.of(2020, 2, 14), "Coastal city campus", creator));
        campusesToAdd.add(new Campuses("CAMP08", "Nha Trang Campus", LocalDate.of(2021, 7, 1), "Tourism and marine economy campus", creator));
        campusesToAdd.add(new Campuses("CAMP09", "Vung Tau Campus", LocalDate.of(2022, 8, 30), "Oil & Gas hub campus", creator));
        campusesToAdd.add(new Campuses("CAMP10", "Thanh Hoa Campus", LocalDate.of(2023, 10, 12), "Gateway to northern central region", creator));

        for (Campuses campus : campusesToAdd) {
            try {
                em.createQuery("SELECT c FROM Campuses c WHERE c.campusId = :id", Campuses.class)
                        .setParameter("id", campus.getCampusId()).getSingleResult();
                System.out.println("Campus already exists: " + campus.getCampusName());
            } catch (NoResultException e) {
                em.persist(campus);
                System.out.println("Added Campus: " + campus.getCampusName());
            }
        }
    }

    // ===================== SLOTS =====================
    private static void addDefaultSlots(EntityManager em) {
        List<Slots> slotsToAdd = new ArrayList<>();

        Slots slot1 = new Slots();
        slot1.setSlotId("slot001"); slot1.setSlotName("Slot 1");
        slot1.setStartTime(LocalTime.of(7, 0)); slot1.setEndTime(LocalTime.of(8, 40));

        Slots slot2 = new Slots();
        slot2.setSlotId("slot002"); slot2.setSlotName("Slot 2");
        slot2.setStartTime(LocalTime.of(8, 50)); slot2.setEndTime(LocalTime.of(10, 20));

        Slots slot3 = new Slots();
        slot3.setSlotId("slot003"); slot3.setSlotName("Slot 3");
        slot3.setStartTime(LocalTime.of(10, 30)); slot3.setEndTime(LocalTime.of(12, 0));

        Slots slot4 = new Slots();
        slot4.setSlotId("slot004"); slot4.setSlotName("Slot 4");
        slot4.setStartTime(LocalTime.of(12, 50)); slot4.setEndTime(LocalTime.of(14, 20));

        Slots slot5 = new Slots();
        slot5.setSlotId("slot005"); slot5.setSlotName("Slot 5");
        slot5.setStartTime(LocalTime.of(14, 30)); slot5.setEndTime(LocalTime.of(16, 0));

        Slots slot6 = new Slots();
        slot6.setSlotId("slot006"); slot6.setSlotName("Slot 6");
        slot6.setStartTime(LocalTime.of(16, 10)); slot6.setEndTime(LocalTime.of(17, 40));

        slotsToAdd.addAll(List.of(slot1, slot2, slot3, slot4, slot5, slot6));

        for (Slots slot : slotsToAdd) {
            try {
                em.createQuery("SELECT s FROM Slots s WHERE s.slotId = :id", Slots.class)
                        .setParameter("id", slot.getSlotId()).getSingleResult();
                System.out.println("Slot already exists: " + slot.getSlotName());
            } catch (NoResultException e) {
                em.persist(slot);
                System.out.println("Added slot: " + slot.getSlotName());
            }
        }
    }

    // ===================== MAJORS =====================
    private static void addDefaultMajors(EntityManager em) {
        List<Majors> majorsToAdd = new ArrayList<>();

        Majors major1 = new Majors(); major1.setMajorId("GBH"); major1.setMajorName("Business Administration");
        Majors major2 = new Majors(); major2.setMajorId("GCH"); major2.setMajorName("Information Technology");
        Majors major3 = new Majors(); major3.setMajorId("GDH"); major3.setMajorName("Graphic Design");
        Majors major4 = new Majors(); major4.setMajorId("GKH"); major4.setMajorName("Marketing");

        majorsToAdd.addAll(List.of(major1, major2, major3, major4));

        for (Majors major : majorsToAdd) {
            try {
                em.createQuery("SELECT m FROM Majors m WHERE m.majorId = :id", Majors.class)
                        .setParameter("id", major.getMajorId()).getSingleResult();
                System.out.println("Major already exists: " + major.getMajorName());
            } catch (NoResultException e) {
                em.persist(major);
                System.out.println("Added Major: " + major.getMajorName());
            }
        }
    }

    // ===================== STAFFS + ADMIN =====================
    private static void addDefaultStaffs(EntityManager em) {
        List<Persons> toAdd = new ArrayList<>();
        Majors gbh = findMajor(em, "GBH");
        Majors gch = findMajor(em, "GCH");
        if (gbh == null || gch == null) {
            System.err.println("Error: Required major not found. Ensure majors are added before staffs.");
            return;
        }

        Campuses hanoiCampus = findCampus(em, "CAMP01");
        if (hanoiCampus == null) {
            System.err.println("Error: Hanoi Campus not found. Ensure campuses are added before staffs.");
            return;
        }

        Staffs staff1 = new Staffs();
        staff1.setId("vuthanhtruong");
        staff1.setFirstName("John"); staff1.setLastName("Doe");
        staff1.setEmail("vuthanhtruong1280@gmail.com"); staff1.setPhoneNumber("0391234567");
        staff1.setBirthDate(LocalDate.of(1985, 5, 15)); staff1.setGender(Gender.MALE);
        staff1.setCountry("Vietnam"); staff1.setProvince("Hanoi"); staff1.setCity("Hanoi");
        staff1.setDistrict("Cau Giay"); staff1.setWard("Dich Vong"); staff1.setStreet("123 Tran Duy Hung");
        staff1.setPostalCode("100000"); staff1.setCreatedDate(LocalDate.now());
        staff1.setMajorManagement(gbh);

        Staffs staff2 = new Staffs();
        staff2.setId("staff002");
        staff2.setFirstName("Jane"); staff2.setLastName("Smith");
        staff2.setEmail("jane.smith@example.com"); staff2.setPhoneNumber("0397654321");
        staff2.setBirthDate(LocalDate.of(1990, 8, 20)); staff2.setGender(Gender.FEMALE);
        staff2.setCountry("Vietnam"); staff2.setProvince("Ho Chi Minh"); staff2.setCity("Ho Chi Minh");
        staff2.setDistrict("District 1"); staff2.setWard("Ben Nghe"); staff2.setStreet("45 Le Loi");
        staff2.setPostalCode("700000"); staff2.setCreatedDate(LocalDate.now());
        staff2.setMajorManagement(gch);

        Admins admin = findAdmin(em, "Admin");
        if (admin != null && admin.getCampus() == null) {
            admin.setCampus(hanoiCampus);
            em.merge(admin);
        }

        toAdd.addAll(List.of(staff1, staff2));

        for (Persons p : toAdd) {
            boolean exists;
            try {
                em.createQuery("SELECT p FROM Persons p WHERE p.id = :id", Persons.class)
                        .setParameter("id", p.getId()).getSingleResult();
                exists = true;
            } catch (NoResultException e) {
                exists = false;
            }

            if (!exists) {
                em.persist(p);
                Authenticators auth = new Authenticators();
                auth.setPersonId(p.getId());
                auth.setPerson(p);
                auth.setPassword("Anhnam123"); // Plain text password
                em.persist(auth);
                System.out.println("Added " + p.getRoleType() + ": " + p.getId());
            } else {
                System.out.println("Person already exists: " + p.getId());
            }
        }
    }

    // ===================== DEPUTY STAFF =====================
    private static void addDefaultDeputyStaff(EntityManager em) {
        Admins admin = findAdmin(em, "Admin");
        if (admin == null) {
            System.err.println("Admin not found, cannot create deputy staff.");
            return;
        }

        try {
            em.createQuery("SELECT d FROM DeputyStaffs d WHERE d.id = :id", DeputyStaffs.class)
                    .setParameter("id", "deputy001").getSingleResult();
            System.out.println("DeputyStaff already exists: deputy001");
        } catch (NoResultException e) {
            DeputyStaffs deputy = new DeputyStaffs();
            deputy.setId("deputy001");
            deputy.setFirstName("Trang"); deputy.setLastName("Le");
            deputy.setEmail("deputy001@example.com"); deputy.setPhoneNumber("0380000001");
            deputy.setBirthDate(LocalDate.of(1992, 3, 10)); deputy.setGender(Gender.FEMALE);
            deputy.setCreator(admin);
            em.persist(deputy);

            Authenticators auth = new Authenticators();
            auth.setPersonId(deputy.getId()); auth.setPerson(deputy);
            auth.setPassword("Anhnam123"); // Plain text password
            em.persist(auth);

            System.out.println("Added DeputyStaff: deputy001");
        }
    }

    // ===================== 20 MajorSubjects/major =====================
    private static void addDefaultMajorSubjects(EntityManager em) {
        Staffs creator = findStaff(em, "vuthanhtruong");
        if (creator == null) {
            System.err.println("Staff not found, cannot create major subjects.");
            return;
        }

        List<String> majorIds = Arrays.asList("GBH", "GCH", "GDH", "GKH");
        for (String majorId : majorIds) {
            Majors major = findMajor(em, majorId);
            if (major == null) {
                System.err.println("Major not found: " + majorId + " (skip subjects)");
                continue;
            }

            for (int i = 1; i <= 20; i++) {
                String suffix = String.format("%03d", i);
                String subjectId = majorId + suffix;
                if (existsSubject(em, subjectId)) {
                    System.out.println("MajorSubject already exists: " + subjectId);
                    continue;
                }
                MajorSubjects subj = new MajorSubjects();
                subj.setSubjectId(subjectId);
                subj.setSubjectName(buildMajorSubjectName(majorId, i));
                subj.setSemester(((i - 1) % 6) + 1);
                subj.setCreator(creator);
                subj.setRequirementType(SubjectTypes.MAJOR_PREPARATION);
                subj.setLearningProgramType(LearningProgramTypes.BTEC);
                subj.setMajor(major);
                em.persist(subj);
                System.out.println("Added MajorSubject: " + subjectId);
            }
        }
    }

    private static boolean existsSubject(EntityManager em, String subjectId) {
        try {
            em.createQuery("SELECT s FROM Subjects s WHERE s.subjectId = :id", Subjects.class)
                    .setParameter("id", subjectId).getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    private static String buildMajorSubjectName(String majorId, int idx) {
        switch (majorId) {
            case "GCH": return "IT Core " + idx;
            case "GBH": return "Business Core " + idx;
            case "GDH": return "Design Core " + idx;
            case "GKH": return "Marketing Core " + idx;
            default: return "Major Subject " + idx;
        }
    }

    // ===================== 5 MinorSubjects =====================
    private static void addDefaultMinorSubjects(EntityManager em) {
        DeputyStaffs deputy = findDeputyStaff(em, "deputy001");
        if (deputy == null) {
            System.err.println("DeputyStaff not found, cannot create minor subjects.");
            return;
        }

        for (int i = 1; i <= 5; i++) {
            String subjectId = String.format("MIN%03d", i);
            if (existsSubject(em, subjectId)) {
                System.out.println("MinorSubject already exists: " + subjectId);
                continue;
            }
            MinorSubjects subj = new MinorSubjects();
            subj.setSubjectId(subjectId);
            subj.setSubjectName("Minor Subject " + i);
            subj.setSemester(((i - 1) % 6) + 1);
            subj.setCreator(deputy);
            subj.setRequirementType(SubjectTypes.TOPUP_PREPARATION);
            em.persist(subj);
            System.out.println("Added MinorSubject: " + subjectId);
        }
    }

    // ===================== BULK SEED from provided list =====================
    private static void bulkSeedSubjectsAndTuition(EntityManager em) {
        int currentYear = LocalDate.now().getYear();

        // Cache majors for heuristic mapping
        Map<String, Majors> majors = new HashMap<>();
        majors.put("GBH", findMajor(em, "GBH"));
        majors.put("GCH", findMajor(em, "GCH"));
        majors.put("GDH", findMajor(em, "GDH"));
        majors.put("GKH", findMajor(em, "GKH"));

        Staffs defaultCreator = findStaff(em, "vuthanhtruong");
        Admins creatorAdmin = findAdmin(em, "Admin");
        Campuses defaultCampus = findCampus(em, "CAMP01");

        if (defaultCreator == null || creatorAdmin == null || defaultCampus == null) {
            System.err.println("Required entities (staff, admin, or campus) not found, cannot seed subjects and tuition.");
            return;
        }

        // Build seed list (id, name, credits, tuition)
        List<SubjectSeed> seeds = new ArrayList<>();
        seeds.add(new SubjectSeed("1244", "Business Skills for E-commerce", 15, 6540000.0));
        seeds.add(new SubjectSeed("1251", "Employability and Professional Development", 15, 6540000.0));
        seeds.add(new SubjectSeed("1286", "Website Design", 15, 6540000.0));
        seeds.add(new SubjectSeed("1293", "Procedural Programming", 15, 6540000.0));
        seeds.add(new SubjectSeed("1295", "Object Oriented Programming", 15, 6540000.0));
        seeds.add(new SubjectSeed("1456", "Data Structures and Algorithms", 15, 6540000.0));
        seeds.add(new SubjectSeed("1510", "Web Application Development", 15, 6540000.0));
        seeds.add(new SubjectSeed("1525", "Distributed Software Applications", 15, 6540000.0));
        seeds.add(new SubjectSeed("1528", "Java Programming", 15, 6540000.0));
        seeds.add(new SubjectSeed("1537", "Programming in .net", 15, 6540000.0));
        seeds.add(new SubjectSeed("1618", "Programming", 15, 6540000.0));
        seeds.add(new SubjectSeed("1619", "Networking", 15, 6540000.0));
        seeds.add(new SubjectSeed("1619A", "Networking", 0, 4905000.0));
        seeds.add(new SubjectSeed("1620", "Professional Practice", 15, 6540000.0));
        seeds.add(new SubjectSeed("1622", "Database Design & Development", 15, 6540000.0));
        seeds.add(new SubjectSeed("1623", "Security", 15, 6540000.0));
        seeds.add(new SubjectSeed("1625", "Managing a Successful Computing Project", 15, 6540000.0));
        seeds.add(new SubjectSeed("1631", "Software Development Life Cycles", 15, 6540000.0));
        seeds.add(new SubjectSeed("1633", "Website Design & Development", 15, 6540000.0));
        seeds.add(new SubjectSeed("1639", "Computing Research Project (2 parts)", 30, 13080000.0));
        seeds.add(new SubjectSeed("1641", "Business Intelligence", 15, 6540000.0));
        seeds.add(new SubjectSeed("1644", "Cloud Computing", 15, 6540000.0));
        seeds.add(new SubjectSeed("1649", "Data Structures & Algorithms", 15, 6540000.0));
        seeds.add(new SubjectSeed("1649A", "Data Structure & Algorithms", 0, 4905000.0));
        seeds.add(new SubjectSeed("1651", "Advanced Programming", 15, 6540000.0));
        seeds.add(new SubjectSeed("1670", "Application Development", 15, 6540000.0));
        seeds.add(new SubjectSeed("1690", "Internet of Things", 15, 6540000.0));
        seeds.add(new SubjectSeed("1987", "Quality Systems in IT", 15, 6540000.0));
        seeds.add(new SubjectSeed("1991", "Data Analysis & Design", 15, 6540000.0));
        seeds.add(new SubjectSeed("2003", "Computer Systems Architecture", 10, 6540000.0));
        seeds.add(new SubjectSeed("3512", "Professional Development", 15, 6540000.0));
        seeds.add(new SubjectSeed("3513", "Contextual Studies", 15, 6540000.0));
        seeds.add(new SubjectSeed("3514", "Individual Project", 15, 6540000.0));
        seeds.add(new SubjectSeed("3515", "Techniques and Processes", 15, 6540000.0));
        seeds.add(new SubjectSeed("3524", "Typography", 15, 6540000.0));
        seeds.add(new SubjectSeed("3525", "Graphic Design Practices", 15, 6540000.0));
        seeds.add(new SubjectSeed("3532", "Printmaking", 15, 6540000.0));
        seeds.add(new SubjectSeed("3541", "Visual Narratives", 15, 6540000.0));
        seeds.add(new SubjectSeed("3542", "Professional Practice", 15, 6540000.0));
        seeds.add(new SubjectSeed("3544", "Applied Practice - Collaborative Project", 30, 13080000.0));
        seeds.add(new SubjectSeed("3550", "Advanced Graphic Design Studies", 30, 13080000.0));
        seeds.add(new SubjectSeed("3559", "Branding and Identity", 15, 6540000.0));
        seeds.add(new SubjectSeed("3562", "Art Direction", 15, 6540000.0));
        seeds.add(new SubjectSeed("3596", "Digital Animation", 15, 6540000.0));
        seeds.add(new SubjectSeed("446", "Computer Systems", 15, 6540000.0));
        seeds.add(new SubjectSeed("447", "Database Design Concepts", 15, 6540000.0));
        seeds.add(new SubjectSeed("485", "Business and the Business Environment", 15, 6540000.0));
        seeds.add(new SubjectSeed("486", "Marketing Essentials", 15, 6540000.0));
        seeds.add(new SubjectSeed("487", "Human Resource Management", 15, 6540000.0));
        seeds.add(new SubjectSeed("488", "Management and Operations", 15, 6540000.0));
        seeds.add(new SubjectSeed("489", "Management Accounting", 15, 6540000.0));
        seeds.add(new SubjectSeed("4902", "Applied Programming and Design Principles", 15, 13080000.0));
        seeds.add(new SubjectSeed("491", "Managing a Successful Business Project (Pearson-set)", 15, 6540000.0));
        seeds.add(new SubjectSeed("492", "Business Law", 15, 6540000.0));
        seeds.add(new SubjectSeed("495", "Entrepreneurship and Small Business", 15, 6540000.0));
        seeds.add(new SubjectSeed("5032", "Business and the Business Environment", 15, 6540000.0));
        seeds.add(new SubjectSeed("5033", "Marketing Processes and Planning", 15, 6540000.0));
        seeds.add(new SubjectSeed("5035", "Human Resource Management", 15, 6540000.0));
        seeds.add(new SubjectSeed("5036", "Leadership and Management", 15, 6540000.0));
        seeds.add(new SubjectSeed("5038", "Accounting Principles", 15, 6540000.0));
        seeds.add(new SubjectSeed("5039", "Managing a Successful Business Project (Pearson-set)", 15, 6540000.0));
        seeds.add(new SubjectSeed("5047", "Entrepreneurial Ventures", 15, 6540000.0));
        seeds.add(new SubjectSeed("5060", "Research Project (Pearson-set)", 30, 13080000.0));
        seeds.add(new SubjectSeed("5064", "Organizational Behavior", 15, 6540000.0));
        seeds.add(new SubjectSeed("5075", "Understanding and Leading Change", 15, 6540000.0));
        seeds.add(new SubjectSeed("5076", "Global Business Environment", 15, 6540000.0));
        seeds.add(new SubjectSeed("5078", "Principles of Operations Management", 15, 6540000.0));
        seeds.add(new SubjectSeed("5120", "Marketing Insights and Analytics", 15, 6540000.0));
        seeds.add(new SubjectSeed("5121", "Digital Marketing", 15, 6540000.0));
        seeds.add(new SubjectSeed("5123", "Integrated Marketing Communications", 15, 6540000.0));
        seeds.add(new SubjectSeed("5131", "Sales Management", 15, 6540000.0));
        seeds.add(new SubjectSeed("522", "Research Project (Pearson-set)", 30, 13080000.0));
        seeds.add(new SubjectSeed("525", "Organizational Behaviour", 15, 6540000.0));
        seeds.add(new SubjectSeed("528", "Operations and Project Management", 15, 6540000.0));
        seeds.add(new SubjectSeed("529", "Understanding and Leading Change", 15, 6540000.0));
        seeds.add(new SubjectSeed("530", "Global Business Environment", 15, 6540000.0));
        seeds.add(new SubjectSeed("534", "Product and Service Development", 15, 6540000.0));
        seeds.add(new SubjectSeed("535", "Integrated Marketing Communications", 15, 6540000.0));
        seeds.add(new SubjectSeed("538", "Digital Marketing", 15, 6540000.0));
        seeds.add(new SubjectSeed("5403", "Ideas Generation and Development in Art and Design", 15, 6540000.0));
        seeds.add(new SubjectSeed("5414", "Design Principles", 15, 6540000.0));
        seeds.add(new SubjectSeed("546", "Business Environment", 15, 6540000.0));
        seeds.add(new SubjectSeed("570", "Statistics for management", 15, 6540000.0));
        seeds.add(new SubjectSeed("574", "Business Strategy", 15, 6540000.0));
        seeds.add(new SubjectSeed("6374", "Visual Communication in Art and Design", 15, 6540000.0));
        seeds.add(new SubjectSeed("6378", "Contextual and Cultural Referencing in Art and Design", 15, 6540000.0));
        seeds.add(new SubjectSeed("736", "Business Law", 15, 6540000.0));
        seeds.add(new SubjectSeed("7388", "Programming", 15, 6540000.0));
        seeds.add(new SubjectSeed("7393", "Networking", 15, 6540000.0));
        seeds.add(new SubjectSeed("7398", "Professional Practice", 15, 6540000.0));
        seeds.add(new SubjectSeed("7400", "Database Design & Development", 15, 6540000.0));
        seeds.add(new SubjectSeed("7406", "Security", 15, 6540000.0));
        seeds.add(new SubjectSeed("7407", "Planning a Computing Project (Pearson-set)", 15, 6540000.0));
        seeds.add(new SubjectSeed("7408", "Software Development Lifecycles", 15, 6540000.0));
        seeds.add(new SubjectSeed("7419", "Website Design & Development", 15, 6540000.0));
        seeds.add(new SubjectSeed("7425", "Computing Research Project (Pearson-set)", 30, 13080000.0));
        seeds.add(new SubjectSeed("7428", "Business Process Support", 15, 6540000.0));
        seeds.add(new SubjectSeed("7430", "Data Structures & Algorithms", 15, 6540000.0));
        seeds.add(new SubjectSeed("7436", "Application Development", 15, 6540000.0));
        seeds.add(new SubjectSeed("7442", "Cloud Computing", 15, 6540000.0));
        seeds.add(new SubjectSeed("7481", "Internet of Things", 15, 6540000.0));
        seeds.add(new SubjectSeed("995", "Project Design Implementation and Evaluation", 20, 6540000.0));
        seeds.add(new SubjectSeed("ACP", "Aptis Checkpoint", 0, null));
        seeds.add(new SubjectSeed("AE111", "Academic Reading Skills", 0, 5232000.0));
        seeds.add(new SubjectSeed("AE112", "Academic Writing Skills", 0, 7848000.0));
        seeds.add(new SubjectSeed("AEG113", "Academic English 1", 0, 6540000.0));
        seeds.add(new SubjectSeed("AEG114", "Academic English 2", 0, 4905000.0));
        seeds.add(new SubjectSeed("AEG115", "Academic English for non-business", 0, 3270000.0));
        seeds.add(new SubjectSeed("AEG116", "Academic English 1", 0, 5000000.0));
        seeds.add(new SubjectSeed("AEG117", "Academic English 2", 0, 6540000.0));
        seeds.add(new SubjectSeed("AIGW201", "Introduction to Artificial Intelligence", 0, null));
        seeds.add(new SubjectSeed("AMD201", "Advanced Microservices Development and Deployment", 0, 4905000.0));
        seeds.add(new SubjectSeed("BUSI0011", "Dissertation", 30, 0.0));
        seeds.add(new SubjectSeed("BUSI0011.1", "Dissertation 1", 30, 0.0));
        seeds.add(new SubjectSeed("BUSI0011.2", "Dissertation 2", 30, 0.0));
        seeds.add(new SubjectSeed("BUSI0011.3", "Dissertation 3", 30, 0.0));
        seeds.add(new SubjectSeed("BUSI1204", "Personal and Professional Development", 15, null));
        seeds.add(new SubjectSeed("BUSI1214", "Contemporary Issues in Events Management", 30, null));
        seeds.add(new SubjectSeed("BUSI1314", "Business Ethics", 15, null));
        seeds.add(new SubjectSeed("BUSI1315", "Management Practice 2", 15, null));
        seeds.add(new SubjectSeed("BUSI1323", "Leadership in Organisations", 15, 0.0));
        seeds.add(new SubjectSeed("BUSI1326", "Fundamentals of Entrepreneurship", 15, null));
        seeds.add(new SubjectSeed("BUSI1327", "Innovation in Competitive Environments", 15, null));
        seeds.add(new SubjectSeed("BUSI1334", "Career & Professional Practice", 15, 0.0));
        seeds.add(new SubjectSeed("BUSI1475", "Management in Critical Context", 15, 0.0));
        seeds.add(new SubjectSeed("BUSI1628", "Managing Organisations and Individuals", 30, null));
        seeds.add(new SubjectSeed("BUSI1630", "Cross Cultural Management and Diversity Management", 30, null));
        seeds.add(new SubjectSeed("BUSI1632", "Negotiations", 15, null));
        seeds.add(new SubjectSeed("BUSI1633", "Strategy for Managers", 15, 0.0));
        seeds.add(new SubjectSeed("BUSI1637", "Discover Project Management", 15, null));
        seeds.add(new SubjectSeed("BUSI1695", "International Business Environment", 15, null));
        seeds.add(new SubjectSeed("BUSI1700", "International Management and Organisational Functions (Collabs)", 30, null));
        seeds.add(new SubjectSeed("BUSI1701", "Personal and Professional Development", 15, null));
        seeds.add(new SubjectSeed("BUSI1702", "Organisational Decision Making", 15, null));
        seeds.add(new SubjectSeed("BUSI1714", "International Entrepreneurship Project", 30, null));
        seeds.add(new SubjectSeed("BUSI1714.1", "International Entrepreneurship Project Part 1", 30, null));
        seeds.add(new SubjectSeed("BUSI1714.2", "International Entrepreneurship Project Part 2", 30, null));
        seeds.add(new SubjectSeed("BUSI1715", "Organisational Analysis & Performance", 15, null));
        seeds.add(new SubjectSeed("BUSI1763", "Dynamics of Global Business and Practices", 15, null));
        seeds.add(new SubjectSeed("BUSI1764", "Global Entrepreneurship and Innovation", 15, null));
        seeds.add(new SubjectSeed("BUSI1765", "Simulated Learning Project", 15, null));
        seeds.add(new SubjectSeed("BUSI1767_Part1", "Business Consultancy Project (Part 1)", 15, null));
        seeds.add(new SubjectSeed("BUSI1767_Part2", "Business Consultancy Project (Part 2)", 15, null));
        seeds.add(new SubjectSeed("BUSI1769", "Global Supply Chains", 15, null));
        seeds.add(new SubjectSeed("BUSI1772", "Ethics and Global Corporate Citizenship", 15, null));
        seeds.add(new SubjectSeed("BUSI1774", "Strategic Management and Leadership", 15, null));
        seeds.add(new SubjectSeed("BUSI1815", "Leading and Managing Change", 15, null));
        seeds.add(new SubjectSeed("COG111", "Chess 1", 0, 3270000.0));
        seeds.add(new SubjectSeed("COG121", "Chess 2", 0, 3270000.0));
        seeds.add(new SubjectSeed("COG131", "Chess 3", 0, 3270000.0));
        seeds.add(new SubjectSeed("COMP1108", "Project", 30, 0.0));
        seeds.add(new SubjectSeed("COMP1551", "Application Development", 15, null));
        seeds.add(new SubjectSeed("COMP1589", "Computer Systems and Internet Technologies", 15, null));
        seeds.add(new SubjectSeed("COMP1639", "Database Engineering", 30, 0.0));
        seeds.add(new SubjectSeed("COMP1640", "Enterprise Web Software Development", 15, 0.0));
        seeds.add(new SubjectSeed("COMP1643", "Information and Content Management", 15, null));
        seeds.add(new SubjectSeed("COMP1648", "Development Frameworks and Methods", 15, 0.0));
        seeds.add(new SubjectSeed("COMP1649", "Human Computer Interaction and Design", 15, 0.0));
        seeds.add(new SubjectSeed("COMP1661", "Application Development for Mobile Devices", 15, 0.0));
        seeds.add(new SubjectSeed("COMP1682", "Project", 30, 0.0));
        seeds.add(new SubjectSeed("COMP1714", "Software Engineering Management", 15, 0.0));
        seeds.add(new SubjectSeed("COMP1752", "Object Oriented Programming", 15, null));
        seeds.add(new SubjectSeed("COMP1753", "Programming Foundations", 15, null));
        seeds.add(new SubjectSeed("COMP1770", "Professional Project Management", 15, null));
        seeds.add(new SubjectSeed("COMP1773", "User Interface Design", 15, null));
        seeds.add(new SubjectSeed("COMP1786", "Mobile Application Design And Development", 15, 0.0));
        seeds.add(new SubjectSeed("COMP1787", "Requirements Management", 15, 0.0));
        seeds.add(new SubjectSeed("COMP1807", "Agile Development with SCRUM", 15, null));
        seeds.add(new SubjectSeed("COMP1809", "Introduction to Computer Science and its Applications", 15, null));
        seeds.add(new SubjectSeed("COMP1810", "Data and Web Analytics", 15, null));
        seeds.add(new SubjectSeed("COMP1821", "Principles of Software Engineering", 15, null));
        seeds.add(new SubjectSeed("COMP1841", "Web Programming 1", 15, null));
        seeds.add(new SubjectSeed("COMP1842", "Web Programming 2", 15, null));
        seeds.add(new SubjectSeed("COMP1843", "Principles of Security", 15, null));
        seeds.add(new SubjectSeed("COMP1844", "Information Analysis and Visualisation", 15, null));
        seeds.add(new SubjectSeed("COMP1845", "Systems Development", 15, null));
        seeds.add(new SubjectSeed("COMP1856", "Software Engineering", 15, null));
        seeds.add(new SubjectSeed("COMP1857", "Introduction to Data Science", 15, null));
        seeds.add(new SubjectSeed("COMP1858", "Data Structures and Algorithms", 15, null));
        seeds.add(new SubjectSeed("COMP1859", "Information Retrieval", 15, null));
        seeds.add(new SubjectSeed("COMP1861", "Machine Learning", 15, null));
        seeds.add(new SubjectSeed("COMP1891", "Applications in AI and Data Science", 15, null));
        seeds.add(new SubjectSeed("COMP1913", "International Data Analytics", 15, null));
        seeds.add(new SubjectSeed("CV", "Company Visit", 0, null));
        seeds.add(new SubjectSeed("DESI 1219", "Design Research Project", 60, 0.0));
        seeds.add(new SubjectSeed("DESI 1219.1", "Design Research Project 1", 60, 0.0));
        seeds.add(new SubjectSeed("DESI 1219.2", "Design Research Project 2", 60, 0.0));
        seeds.add(new SubjectSeed("DESI 1219.3", "Design Research Project 3", 60, 0.0));
        seeds.add(new SubjectSeed("DESI 1221", "Professional Practice & Portfolio", 15, 0.0));
        seeds.add(new SubjectSeed("DESI 1222", "Interdisciplinary Design", 15, 0.0));
        seeds.add(new SubjectSeed("DESI 1222.1", "Interdisciplinary Design 1", 0, null));
        seeds.add(new SubjectSeed("DESI 1226", "Experience Design", 30, 0.0));
        seeds.add(new SubjectSeed("DESI 1226.2", "Experience Design 2", 30, 0.0));
        seeds.add(new SubjectSeed("DESI1198.1", "Graphic Design Principles Part 1", 30, null));
        seeds.add(new SubjectSeed("DESI1198.2", "Graphic Design Principles Part 2", 30, null));
        seeds.add(new SubjectSeed("DESI1200.1", "Typographic Studies Part 1", 30, null));
        seeds.add(new SubjectSeed("DESI1200.2", "Typographic Studies Part 2", 30, null));
        seeds.add(new SubjectSeed("DESI1213.1", "Experimental Studio Practices Part 1", 30, null));
        seeds.add(new SubjectSeed("DESI1213.2", "Experimental Studio Practices Part 2", 30, null));
        seeds.add(new SubjectSeed("DESI1214.1", "Design Thinking Part 1", 30, null));
        seeds.add(new SubjectSeed("DESI1214.2", "Design Thinking Part 2", 30, null));
        seeds.add(new SubjectSeed("DESI1215.1", "Branding and Advertising Part 1", 30, null));
        seeds.add(new SubjectSeed("DESI1215.2", "Branding and Advertising Part 2", 30, null));
        seeds.add(new SubjectSeed("DESI1217", "Narrative and Sequence", 15, null));
        seeds.add(new SubjectSeed("DESI1218", "Creative Professional Practice", 15, null));
        seeds.add(new SubjectSeed("DESI1219 Exhibition", "Design Research Project", 60, null));
        seeds.add(new SubjectSeed("DESI1219.1", "Design Research Project Part 1", 30, null));
        seeds.add(new SubjectSeed("DESI1219.2", "Design Research Project Part 2", 30, null));
        seeds.add(new SubjectSeed("DESI1237.1", "Interdisciplinary Design Part 1", 30, null));
        seeds.add(new SubjectSeed("DESI1237.2", "Interdisciplinary Design Part 2", 30, null));
        seeds.add(new SubjectSeed("DESI1238.1", "Design Investigations Part 1", 30, null));
        seeds.add(new SubjectSeed("DESI1238.2", "Design Investigations Part 2", 30, null));
        seeds.add(new SubjectSeed("DESI1239.1", "Art and Design in Context Part 1", 30, null));
        seeds.add(new SubjectSeed("DESI1239.2", "Art and Design in Context Part 2", 30, null));
        seeds.add(new SubjectSeed("DESI1240.1", "Professional Practice and Portfolio Part 1", 30, null));
        seeds.add(new SubjectSeed("DESI1240.2", "Professional Practice and Portfolio Part 2", 30, null));
        seeds.add(new SubjectSeed("DESI1254.1", "Interdisciplinary Spaces Part 1", 30, null));
        seeds.add(new SubjectSeed("DESI1254.2", "Interdisciplinary Spaces Part 2", 30, null));
        seeds.add(new SubjectSeed("DESI1255.1", "Design Engagement Part 1", 30, null));
        seeds.add(new SubjectSeed("DESI1255.2", "Design Engagement Part 2", 30, null));
        seeds.add(new SubjectSeed("DPLG101", "Deep Learning", 0, null));
        seeds.add(new SubjectSeed("DTGG102", "Visual Design tool", 0, 4905000.0));
        seeds.add(new SubjectSeed("ENI501", "English level 5", 0, 11300000.0));
        seeds.add(new SubjectSeed("ENR001", "English level 0", 0, 7850000.0));
        seeds.add(new SubjectSeed("ENR003", "English level 0", 0, 11950000.0));
        seeds.add(new SubjectSeed("ENR101", "English level 1", 0, 11300000.0));
        seeds.add(new SubjectSeed("ENR102", "English level 1", 0, 11800000.0));
        seeds.add(new SubjectSeed("ENR103", "English level 1", 0, 11950000.0));
        seeds.add(new SubjectSeed("ENR201", "English level 2", 0, 11300000.0));
        seeds.add(new SubjectSeed("ENR202", "English level 2", 0, 11800000.0));
        seeds.add(new SubjectSeed("ENR203", "English level 2", 0, 11950000.0));
        seeds.add(new SubjectSeed("ENR301", "English level 3", 0, 11300000.0));
        seeds.add(new SubjectSeed("ENR302", "English level 3", 0, 11800000.0));
        seeds.add(new SubjectSeed("ENR303", "English level 3", 0, 11950000.0));
        seeds.add(new SubjectSeed("ENR401", "English level 4", 0, 11300000.0));
        seeds.add(new SubjectSeed("ENR402", "English level 4", 0, 11800000.0));
        seeds.add(new SubjectSeed("ENR403", "English level 4", 0, 11950000.0));
        seeds.add(new SubjectSeed("ENR502", "English level 5", 0, 11800000.0));
        seeds.add(new SubjectSeed("ENR503", "English level 5", 0, 11950000.0));
        seeds.add(new SubjectSeed("ENT002", "English 1 - Topnotch Fundamental", 0, 11300000.0));
        seeds.add(new SubjectSeed("ENT101", "English 2 - Topnotch 1", 0, 11300000.0));
        seeds.add(new SubjectSeed("ENT102", "English 2 - Topnotch 1", 0, 11300000.0));
        seeds.add(new SubjectSeed("ENT201", "English 3 - Top Notch 2", 0, 11300000.0));
        seeds.add(new SubjectSeed("ENT202", "English 3 - Top Notch 2", 0, 11300000.0));
        seeds.add(new SubjectSeed("ENT301", "English 4 - Top Notch 3", 0, 11300000.0));
        seeds.add(new SubjectSeed("ENT302", "English 4 - Top Notch 3", 0, 11300000.0));
        seeds.add(new SubjectSeed("ENT401", "English 5 - Summit 1", 0, 11300000.0));
        seeds.add(new SubjectSeed("ENT402", "English 5 - Summit 1", 0, 11300000.0));
        seeds.add(new SubjectSeed("ENT501", "English 6 - Summit 2", 0, 11300000.0));
        seeds.add(new SubjectSeed("ENT502", "English 6 - Summit 2", 0, 11300000.0));
        seeds.add(new SubjectSeed("ENT502-PTE", "English 6 - Summit 2 - PTE", 0, 11300000.0));
        seeds.add(new SubjectSeed("ENT503-PTE", "English 6 - Summit 2 - PTE", 0, 11300000.0));
        seeds.add(new SubjectSeed("EVNG201", "Events Management", 0, 6540000.0));
        seeds.add(new SubjectSeed("Exam", "Examination", 0, null));
        seeds.add(new SubjectSeed("FINA1149", "Finance for non-Finance Managers", 15, null));
        seeds.add(new SubjectSeed("FINA1161", "Introduction to Finance for Business", 15, null));
        seeds.add(new SubjectSeed("FINA1163", "Financial Resource Management", 15, null));
        seeds.add(new SubjectSeed("GAIM101", "AI for leaders", 15, null));
        seeds.add(new SubjectSeed("GDPG201", "Applied Practice Project", 0, 3270000.0));
        seeds.add(new SubjectSeed("GDQPAN", "Military & Security Education", 0, 1860000.0));
        seeds.add(new SubjectSeed("GUIDE", "Guide", 0, null));
        seeds.add(new SubjectSeed("IBG201", "International Business", 0, 6540000.0));
        seeds.add(new SubjectSeed("INDU1106", "Management Practice 1", 15, null));
        seeds.add(new SubjectSeed("INDU1107", "Teams in Organisations", 15, null));
        seeds.add(new SubjectSeed("INDU1130", "International Human Resource Management", 30, 0.0));
        seeds.add(new SubjectSeed("INDU1166", "Future Paths", 15, null));
        seeds.add(new SubjectSeed("LIB", "Library Guide", 0, null));
        seeds.add(new SubjectSeed("LS01", "Live lecture", 0, null));
        seeds.add(new SubjectSeed("LS02", "Live lecture", 0, null));
        seeds.add(new SubjectSeed("MACG101", "Advanced math for Computer Science", 0, null));
        seeds.add(new SubjectSeed("MARK 1051", "Contemporary Issues in Marketing", 30, 0.0));
        seeds.add(new SubjectSeed("MARK1051", "Contemporary Issues in Marketing", 30, null));
        seeds.add(new SubjectSeed("MARK1107", "Principles and Practice of Marketing", 30, null));
        seeds.add(new SubjectSeed("MARK1234", "Social Media and Analytics and Critical", 15, null));
        seeds.add(new SubjectSeed("MARK1249", "Critical Approaches to Advertising", 15, null));
        seeds.add(new SubjectSeed("MARK1266", "Principles of Marketing in a Global Context", 15, null));
        seeds.add(new SubjectSeed("MARK1286", "Marketing and Sales in the Future Economy", 15, null));
        seeds.add(new SubjectSeed("MARK1289", "Creative Toolbox", 15, null));
        seeds.add(new SubjectSeed("MARK1290", "Digital Marketing 101", 15, null));
        seeds.add(new SubjectSeed("MARK1295", "Fundamentals of Marketing", 15, null));
        seeds.add(new SubjectSeed("MATH1179", "Mathematics for Computer Science", 15, null));
        seeds.add(new SubjectSeed("MEDS1159", "Corporate Communications", 15, null));
        seeds.add(new SubjectSeed("MESD1249", "Corporate Communications", 30, null));
        seeds.add(new SubjectSeed("MKTG209", "Content Marketing", 0, 6540000.0));
        seeds.add(new SubjectSeed("MKTG301", "MarTech", 0, 6540000.0));
        seeds.add(new SubjectSeed("OC", "Opening Ceremony", 0, null));
        seeds.add(new SubjectSeed("OJT", "On the job training", 0, 18000000.0));
        seeds.add(new SubjectSeed("OR", "Orientation", 0, null));
        seeds.add(new SubjectSeed("PDP", "Personal development plan", 0, null));
        seeds.add(new SubjectSeed("PRCG201", "Public Relations & Communication", 0, 6540000.0));
        seeds.add(new SubjectSeed("PRO101", "Procedural Programming", 0, 4905000.0));
        seeds.add(new SubjectSeed("PROG102", "Procedural Programming", 0, 6540000.0));
        seeds.add(new SubjectSeed("PROG191", "Java Programming", 0, 6540000.0));
        seeds.add(new SubjectSeed("RESE1170", "Business Research Methods", 15, null));
        seeds.add(new SubjectSeed("RV101", "VSTEP Training", 0, null));
        seeds.add(new SubjectSeed("SALG301", "Selling and sales management", 0, 6540000.0));
        seeds.add(new SubjectSeed("SCMG201", "Operations and Supply Chain Management", 0, 6540000.0));
        seeds.add(new SubjectSeed("SIT1", "Advanced Algorithm", 0, null));
        seeds.add(new SubjectSeed("SIT2", "Machine Learning", 0, null));
        seeds.add(new SubjectSeed("SS101", "Seminar", 0, null));
        seeds.add(new SubjectSeed("SSC101", "Business Communication", 0, 2452500.0));
        seeds.add(new SubjectSeed("SSD101", "Basic Drawing - Sketching", 0, 4905000.0));
        seeds.add(new SubjectSeed("SSDG101", "Basic Drawing and Sketching", 0, 4905000.0));
        seeds.add(new SubjectSeed("SSDG102", "Basic Drawing and Sketching", 0, 6540000.0));
        seeds.add(new SubjectSeed("SSDG102.1", "Basic Drawing and Sketching Part 1", 0, null));
        seeds.add(new SubjectSeed("SSDG102.2", "Basic Drawing and Sketching Part 2", 0, null));
        seeds.add(new SubjectSeed("SSG101", "Working in Groups", 0, 2452500.0));
        seeds.add(new SubjectSeed("SSGG101", "Working in group", 0, 2452500.0));
        seeds.add(new SubjectSeed("SSGG102", "Working in group", 0, 1890000.0));
        seeds.add(new SubjectSeed("SSGG103", "Teamwork in Global Environment", 0, 1890000.0));
        seeds.add(new SubjectSeed("SSLG102", "Study skills for University success", 0, 3270000.0));
        seeds.add(new SubjectSeed("SSM201", "Management Skills", 0, 2452500.0));
        seeds.add(new SubjectSeed("SSMG201", "Management Skills", 0, 2452500.0));
        seeds.add(new SubjectSeed("SSN301", "Negotiation Skills", 0, 2452500.0));
        seeds.add(new SubjectSeed("SSNG301", "Negotiation Skill", 0, 2452500.0));
        seeds.add(new SubjectSeed("SSNG302", "Negotiation Skill", 0, 1890000.0));
        seeds.add(new SubjectSeed("TB", "Teambuilding", 0, null));
        seeds.add(new SubjectSeed("TE", "Training EOS", 0, null));
        seeds.add(new SubjectSeed("VIE 1014", "Politics", 0, 1308000.0));
        seeds.add(new SubjectSeed("VIE 1024", "Law", 0, 1308000.0));
        seeds.add(new SubjectSeed("VIE 1053", "Fundamental in IT", 0, 1308000.0));
        seeds.add(new SubjectSeed("VIE1054", "IT fundamentals", 0, 3270000.0));
        seeds.add(new SubjectSeed("VOG111", "Vovinam 1", 0, 3270000.0));
        seeds.add(new SubjectSeed("VOG112", "Vovinam 1", 0, 3270000.0));
        seeds.add(new SubjectSeed("VOG121", "Vovinam 2", 0, 3270000.0));
        seeds.add(new SubjectSeed("VOG122", "Vovinam 2", 0, 3270000.0));
        seeds.add(new SubjectSeed("VOG131", "Vovinam 3", 0, 3270000.0));
        seeds.add(new SubjectSeed("VOG132", "Vovinam 3", 0, 3270000.0));
        seeds.add(new SubjectSeed("VOV111", "Vovinam 1", 0, 3815000.0));
        seeds.add(new SubjectSeed("VOV121", "Vovinam 2", 0, 3815000.0));
        seeds.add(new SubjectSeed("VOV131", "Vovinam 3", 0, 3815000.0));
        seeds.add(new SubjectSeed("WEBG301", "Project Web", 0, 6540000.0));
        seeds.add(new SubjectSeed("WS", "Workshop", 0, null));

        // Deduplicate by subjectId (keep first occurrence)
        Map<String, SubjectSeed> byId = new LinkedHashMap<>();
        for (SubjectSeed s : seeds) {
            byId.putIfAbsent(s.id, s);
        }
        List<SubjectSeed> uniqueSeeds = new ArrayList<>(byId.values());

        for (SubjectSeed seed : uniqueSeeds) {
            // Create/ensure Subject
            boolean exists = existsSubject(em, seed.id);
            MajorSubjects subj;
            if (exists) {
                subj = em.find(MajorSubjects.class, seed.id);
                if (subj == null) {
                    Subjects base = em.find(Subjects.class, seed.id);
                    if (base instanceof MajorSubjects) {
                        subj = (MajorSubjects) base;
                    }
                }
                if (subj != null) {
                    if (seed.name != null && (subj.getSubjectName() == null || !subj.getSubjectName().equals(seed.name))) {
                        subj.setSubjectName(seed.name);
                        em.merge(subj);
                    }
                } else {
                    System.out.println("Subject exists (not MajorSubjects), skip create: " + seed.id);
                    continue;
                }
            } else {
                subj = new MajorSubjects();
                subj.setSubjectId(seed.id);
                subj.setSubjectName(seed.name);
                subj.setSemester(seed.credits != null && seed.credits > 0 ? Math.max(1, ((seed.credits / 5) % 6)) : 1);
                subj.setCreator(defaultCreator);
                subj.setRequirementType(SubjectTypes.MAJOR_PREPARATION);
                subj.setLearningProgramType(LearningProgramTypes.BTEC);
                subj.setMajor(guessMajor(em, majors, seed));
                em.persist(subj);
                System.out.println("Added Subject: " + seed.id + " - " + seed.name);
            }

        }
    }

    private static Majors findMajor(EntityManager em, String id) {
        try {
            return em.createQuery("SELECT m FROM Majors m WHERE m.majorId = :id", Majors.class)
                    .setParameter("id", id).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private static Staffs findStaff(EntityManager em, String id) {
        try {
            return em.createQuery("SELECT s FROM Staffs s WHERE s.id = :id", Staffs.class)
                    .setParameter("id", id).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private static Admins findAdmin(EntityManager em, String id) {
        try {
            return em.createQuery("SELECT a FROM Admins a WHERE a.id = :id", Admins.class)
                    .setParameter("id", id).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private static DeputyStaffs findDeputyStaff(EntityManager em, String id) {
        try {
            return em.createQuery("SELECT d FROM DeputyStaffs d WHERE d.id = :id", DeputyStaffs.class)
                    .setParameter("id", id).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private static Campuses findCampus(EntityManager em, String id) {
        try {
            return em.createQuery("SELECT c FROM Campuses c WHERE c.campusId = :id", Campuses.class)
                    .setParameter("id", id).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private static Majors guessMajor(EntityManager em, Map<String, Majors> majors, SubjectSeed s) {
        String n = (s.name == null ? "" : s.name.toLowerCase(Locale.ROOT));
        if (n.contains("design") || n.contains("typograph") || n.contains("animation")
                || n.contains("visual") || n.contains("art")) {
            return majors.get("GDH");
        }
        if (n.contains("marketing") || n.contains("advertis") || n.contains("sales")
                || n.contains("brand") || n.contains("communication") || n.contains("martech")) {
            return majors.get("GKH");
        }
        if (n.contains("business") || n.contains("entrepreneur") || n.contains("management")
                || n.contains("account") || n.contains("finance") || n.contains("organiz")) {
            return majors.get("GBH");
        }
        if (n.contains("program") || n.contains("java") || n.contains(".net") || n.contains("web")
                || n.contains("software") || n.contains("comput") || n.contains("network")
                || n.contains("database") || n.contains("security") || n.contains("cloud")
                || n.contains("machine learning") || n.contains("ai")) {
            return majors.get("GCH");
        }
        return null; // not mandatory
    }

    // Simple holder
    private static class SubjectSeed {
        final String id;
        final String name;
        final Integer credits; // not persisted (Subjects has no field), but used to scatter semester
        final Double tuition;

        SubjectSeed(String id, String name, Integer credits, Double tuition) {
            this.id = id;
            this.name = name;
            this.credits = credits;
            this.tuition = tuition;
        }
    }
}