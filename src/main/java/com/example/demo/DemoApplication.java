package com.example.demo;

import com.example.demo.curriculum.model.Curriculum;
import com.example.demo.specialization.model.Specialization;
import com.example.demo.user.admin.model.Admins;
import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.campus.model.Campuses;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.entity.*;
import com.example.demo.entity.Enums.*;
import com.example.demo.major.model.Majors;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.person.model.Persons;
import com.example.demo.subject.majorSubject.model.MajorSubjects;
import com.example.demo.subject.abstractSubject.model.MinorSubjects;
import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import com.example.demo.subject.abstractSubject.model.Subjects;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(DemoApplication.class, args);
        EntityManagerFactory emf = context.getBean(EntityManagerFactory.class);
        EntityManager em = emf.createEntityManager();

        // Seed Admin first
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

        // Seed Specializations
        try {
            em.getTransaction().begin();
            addDefaultSpecializations(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            rollback(em);
            e.printStackTrace();
        }

        // Seed Curriculums
        try {
            em.getTransaction().begin();
            addDefaultCurriculums(em);
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

        // Seed SpecializedSubjects
        try {
            em.getTransaction().begin();
            addDefaultSpecializedSubjects(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            rollback(em);
            e.printStackTrace();
        }

        // Seed Subjects from bulk list
        try {
            em.getTransaction().begin();
            bulkSeedSubjects(em);
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
            auth.setPassword("Admin123");
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

    // ===================== SPECIALIZATIONS =====================
    private static void addDefaultSpecializations(EntityManager em) {
        Admins creator = findAdmin(em, "Admin");
        if (creator == null) {
            System.err.println("Admin not found, cannot create specializations.");
            return;
        }

        Majors gch = findMajor(em, "GCH");
        Majors gbh = findMajor(em, "GBH");
        Majors gdh = findMajor(em, "GDH");
        Majors gkh = findMajor(em, "GKH");

        if (gch == null || gbh == null || gdh == null || gkh == null) {
            System.err.println("One or more majors not found, cannot create specializations.");
            return;
        }

        List<Specialization> specializationsToAdd = new ArrayList<>();

        // IT Specializations
        specializationsToAdd.add(new Specialization("SPEC_IT_SE", "Software Engineering", gch, creator));
        specializationsToAdd.add(new Specialization("SPEC_IT_AI", "Artificial Intelligence", gch, creator));
        specializationsToAdd.add(new Specialization("SPEC_IT_CS", "Cyber Security", gch, creator));
        specializationsToAdd.add(new Specialization("SPEC_IT_DS", "Data Science", gch, creator));
        specializationsToAdd.add(new Specialization("SPEC_IT_CC", "Cloud Computing", gch, creator));
        specializationsToAdd.add(new Specialization("SPEC_IT_IOT", "Internet of Things", gch, creator));
        specializationsToAdd.add(new Specialization("SPEC_IT_BC", "Blockchain Technology", gch, creator));

        // Business Specializations
        specializationsToAdd.add(new Specialization("SPEC_BUS_FIN", "Finance & Banking", gbh, creator));
        specializationsToAdd.add(new Specialization("SPEC_BUS_HR", "Human Resource Management", gbh, creator));
        specializationsToAdd.add(new Specialization("SPEC_BUS_ENT", "Entrepreneurship", gbh, creator));
        specializationsToAdd.add(new Specialization("SPEC_BUS_IB", "International Business", gbh, creator));
        specializationsToAdd.add(new Specialization("SPEC_BUS_OM", "Operations Management", gbh, creator));
        specializationsToAdd.add(new Specialization("SPEC_BUS_SCM", "Supply Chain Management", gbh, creator));

        // Design Specializations
        specializationsToAdd.add(new Specialization("SPEC_DES_UI", "UI/UX Design", gdh, creator));
        specializationsToAdd.add(new Specialization("SPEC_DES_3D", "3D Design & Animation", gdh, creator));
        specializationsToAdd.add(new Specialization("SPEC_DES_GAME", "Game Design", gdh, creator));
        specializationsToAdd.add(new Specialization("SPEC_DES_VFX", "Visual Effects", gdh, creator));
        specializationsToAdd.add(new Specialization("SPEC_DES_MOT", "Motion Graphics", gdh, creator));
        specializationsToAdd.add(new Specialization("SPEC_DES_BRAND", "Brand Identity Design", gdh, creator));

        // Marketing Specializations
        specializationsToAdd.add(new Specialization("SPEC_MKT_DIG", "Digital Marketing", gkh, creator));
        specializationsToAdd.add(new Specialization("SPEC_MKT_SM", "Social Media Marketing", gkh, creator));
        specializationsToAdd.add(new Specialization("SPEC_MKT_SEO", "SEO & Content Marketing", gkh, creator));
        specializationsToAdd.add(new Specialization("SPEC_MKT_ECP", "E-commerce & Performance Marketing", gkh, creator));
        specializationsToAdd.add(new Specialization("SPEC_MKT_BRAND", "Brand Management", gkh, creator));
        specializationsToAdd.add(new Specialization("SPEC_MKT_MR", "Market Research & Analytics", gkh, creator));

        for (Specialization spec : specializationsToAdd) {
            try {
                em.createQuery("SELECT s FROM Specialization s WHERE s.specializationId = :id", Specialization.class)
                        .setParameter("id", spec.getSpecializationId()).getSingleResult();
                System.out.println("Specialization already exists: " + spec.getSpecializationName());
            } catch (NoResultException e) {
                em.persist(spec);
                System.out.println("Added Specialization: " + spec.getSpecializationName());
            }
        }
    }

    // ===================== CURRICULUMS =====================
    private static void addDefaultCurriculums(EntityManager em) {
        Admins creator = findAdmin(em, "Admin");
        if (creator == null) {
            System.err.println("Admin not found, cannot create curriculums.");
            return;
        }

        List<Curriculum> curriculumsToAdd = new ArrayList<>();
        curriculumsToAdd.add(new Curriculum("CURR01", "BTEC", "BTEC curriculum program", creator, LocalDateTime.now()));
        curriculumsToAdd.add(new Curriculum("CURR02", "3+0", "3+0 curriculum program", creator, LocalDateTime.now()));

        for (Curriculum curriculum : curriculumsToAdd) {
            try {
                em.createQuery("SELECT c FROM Curriculum c WHERE c.curriculumId = :id", Curriculum.class)
                        .setParameter("id", curriculum.getCurriculumId()).getSingleResult();
                System.out.println("Curriculum already exists: " + curriculum.getName());
            } catch (NoResultException e) {
                em.persist(curriculum);
                System.out.println("Added Curriculum: " + curriculum.getName());
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
                auth.setPassword("Anhnam123");
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
            auth.setPassword("Anhnam123");
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

        Curriculum btecCurriculum = findCurriculum(em, "CURR01");

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
                subj.setMajor(major);
                subj.setCurriculum(btecCurriculum);
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

    // ===================== SPECIALIZED SUBJECTS =====================
    private static void addDefaultSpecializedSubjects(EntityManager em) {
        Staffs creator = findStaff(em, "vuthanhtruong");
        if (creator == null) {
            System.err.println("Staff not found, cannot create specialized subjects.");
            return;
        }

        // IT Specialization Subjects
        addSpecializedSubjectsForSpec(em, "SPEC_IT_SE", "Software Engineering", creator, new String[]{
                "Advanced Software Architecture", "Design Patterns", "Microservices Architecture",
                "Software Testing & QA", "DevOps Practices", "Agile Development",
                "Software Project Management", "Code Quality & Review"
        });

        addSpecializedSubjectsForSpec(em, "SPEC_IT_AI", "Artificial Intelligence", creator, new String[]{
                "Machine Learning Fundamentals", "Deep Learning", "Natural Language Processing",
                "Computer Vision", "Neural Networks", "AI Ethics",
                "Reinforcement Learning", "AI Applications"
        });

        addSpecializedSubjectsForSpec(em, "SPEC_IT_CS", "Cyber Security", creator, new String[]{
                "Network Security", "Ethical Hacking", "Cryptography",
                "Security Operations", "Incident Response", "Penetration Testing",
                "Security Compliance", "Digital Forensics"
        });

        addSpecializedSubjectsForSpec(em, "SPEC_IT_DS", "Data Science", creator, new String[]{
                "Big Data Analytics", "Data Mining", "Statistical Analysis",
                "Data Visualization", "Predictive Modeling", "Business Intelligence",
                "Data Engineering", "Advanced SQL"
        });

        addSpecializedSubjectsForSpec(em, "SPEC_IT_CC", "Cloud Computing", creator, new String[]{
                "AWS Fundamentals", "Azure Cloud Services", "Cloud Architecture",
                "Cloud Security", "Container Orchestration", "Serverless Computing",
                "Cloud DevOps", "Multi-Cloud Strategy"
        });

        // Business Specialization Subjects
        addSpecializedSubjectsForSpec(em, "SPEC_BUS_FIN", "Finance & Banking", creator, new String[]{
                "Corporate Finance", "Investment Analysis", "Financial Markets",
                "Banking Operations", "Risk Management", "Financial Modeling",
                "Portfolio Management", "Financial Regulations"
        });

        addSpecializedSubjectsForSpec(em, "SPEC_BUS_HR", "Human Resource Management", creator, new String[]{
                "Talent Acquisition", "Performance Management", "Organizational Behavior",
                "HR Analytics", "Compensation & Benefits", "Training & Development",
                "Labor Relations", "Strategic HRM"
        });

        addSpecializedSubjectsForSpec(em, "SPEC_BUS_ENT", "Entrepreneurship", creator, new String[]{
                "Startup Fundamentals", "Business Model Canvas", "Venture Capital",
                "Innovation Management", "Lean Startup", "Entrepreneurial Finance",
                "Growth Hacking", "Social Entrepreneurship"
        });

        // Design Specialization Subjects
        addSpecializedSubjectsForSpec(em, "SPEC_DES_UI", "UI/UX Design", creator, new String[]{
                "User Research Methods", "Interaction Design", "Prototyping & Wireframing",
                "Usability Testing", "Information Architecture", "Design Systems",
                "Mobile UX Design", "Accessibility Design"
        });

        addSpecializedSubjectsForSpec(em, "SPEC_DES_3D", "3D Design & Animation", creator, new String[]{
                "3D Modeling Fundamentals", "Character Animation", "Lighting & Rendering",
                "Texturing & Materials", "Rigging & Skinning", "Motion Capture",
                "VFX Compositing", "3D Printing Design"
        });

        addSpecializedSubjectsForSpec(em, "SPEC_DES_GAME", "Game Design", creator, new String[]{
                "Game Mechanics Design", "Level Design", "Game Art & Assets",
                "Game Programming", "Player Psychology", "Game Monetization",
                "Multiplayer Design", "Game Testing & Balancing"
        });

        addSpecializedSubjectsForSpec(em, "SPEC_DES_VFX", "Visual Effects", creator, new String[]{
                "Particle Systems", "Fluid Simulation", "Destruction Effects",
                "Green Screen Compositing", "Color Grading", "Motion Tracking",
                "VFX Pipeline", "Real-time VFX"
        });

        addSpecializedSubjectsForSpec(em, "SPEC_DES_MOT", "Motion Graphics", creator, new String[]{
                "After Effects Advanced", "Typography Animation", "Kinetic Typography",
                "2D Motion Design", "Broadcast Design", "Motion Graphics Principles",
                "Visual Storytelling", "Motion Graphics Workflow"
        });

        // Marketing Specialization Subjects
        addSpecializedSubjectsForSpec(em, "SPEC_MKT_DIG", "Digital Marketing", creator, new String[]{
                "Digital Marketing Strategy", "Google Ads Mastery", "Facebook Advertising",
                "Email Marketing", "Marketing Automation", "Conversion Optimization",
                "Analytics & Reporting", "Digital Campaign Management"
        });

        addSpecializedSubjectsForSpec(em, "SPEC_MKT_SM", "Social Media Marketing", creator, new String[]{
                "Social Media Strategy", "Content Creation for Social", "Influencer Marketing",
                "Community Management", "Social Media Analytics", "Platform-Specific Marketing",
                "Social Commerce", "Crisis Management"
        });

        addSpecializedSubjectsForSpec(em, "SPEC_MKT_SEO", "SEO & Content Marketing", creator, new String[]{
                "SEO Fundamentals", "Content Strategy", "Keyword Research",
                "On-Page Optimization", "Link Building", "Technical SEO",
                "Content Writing", "SEO Analytics"
        });

        addSpecializedSubjectsForSpec(em, "SPEC_MKT_ECP", "E-commerce & Performance Marketing", creator, new String[]{
                "E-commerce Platforms", "Performance Marketing", "Shopping Ads",
                "Marketplace Marketing", "Retargeting Strategies", "Attribution Modeling",
                "E-commerce Analytics", "Conversion Rate Optimization"
        });

        addSpecializedSubjectsForSpec(em, "SPEC_MKT_BRAND", "Brand Management", creator, new String[]{
                "Brand Strategy", "Brand Identity", "Brand Positioning",
                "Brand Communication", "Brand Equity", "Brand Experience",
                "Rebranding Strategies", "Brand Portfolio Management"
        });

        addSpecializedSubjectsForSpec(em, "SPEC_MKT_MR", "Market Research & Analytics", creator, new String[]{
                "Research Methodology", "Consumer Behavior Analysis", "Survey Design",
                "Data Analysis Tools", "Market Segmentation", "Predictive Analytics",
                "Insights & Reporting", "Marketing Metrics"
        });
    }

    private static void addSpecializedSubjectsForSpec(EntityManager em, String specId, String specName,
                                                      Staffs creator, String[] subjectNames) {
        Specialization spec = findSpecialization(em, specId);
        if (spec == null) {
            System.err.println("Specialization not found: " + specId);
            return;
        }

        Curriculum btecCurriculum = findCurriculum(em, "CURR01");

        for (int i = 0; i < subjectNames.length; i++) {
            String subjectId = specId + "_SUB" + String.format("%02d", i + 1);
            if (existsSubject(em, subjectId)) {
                System.out.println("SpecializedSubject already exists: " + subjectId);
                continue;
            }

            SpecializedSubject subj = new SpecializedSubject();
            subj.setSubjectId(subjectId);
            subj.setSubjectName(subjectNames[i]);
            subj.setSemester(((i / 2) % 6) + 1); // 2 subjects per semester
            subj.setCreator(creator);
            subj.setSpecialization(spec);
            subj.setCurriculum(btecCurriculum);
            em.persist(subj);
            System.out.println("Added SpecializedSubject: " + subjectId + " - " + subjectNames[i]);
        }
    }

    // ===================== BULK SEED from provided list =====================
    private static void bulkSeedSubjects(EntityManager em) {
        // Cache majors and curriculums
        Map<String, Majors> majors = new HashMap<>();
        majors.put("GBH", findMajor(em, "GBH"));
        majors.put("GCH", findMajor(em, "GCH"));
        majors.put("GDH", findMajor(em, "GDH"));
        majors.put("GKH", findMajor(em, "GKH"));

        Curriculum btecCurriculum = findCurriculum(em, "CURR01");
        Curriculum threePlusZeroCurriculum = findCurriculum(em, "CURR02");

        Staffs defaultCreator = findStaff(em, "vuthanhtruong");
        DeputyStaffs deputyCreator = findDeputyStaff(em, "deputy001");

        if (defaultCreator == null || deputyCreator == null) {
            System.err.println("Required creators not found, cannot seed subjects.");
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
            if (existsSubject(em, seed.id)) {
                System.out.println("Subject already exists: " + seed.id);
                continue;
            }

            MajorSubjects subj = new MajorSubjects();
            subj.setSubjectId(seed.id);
            subj.setSubjectName(seed.name);
            subj.setSemester(seed.credits != null && seed.credits > 0 ? Math.max(1, ((seed.credits / 5) % 6)) : 1);
            subj.setCreator(defaultCreator);
            subj.setRequirementType(SubjectTypes.MAJOR_PREPARATION);
            subj.setMajor(guessMajor(majors, seed));
            subj.setCurriculum(btecCurriculum);
            em.persist(subj);
            System.out.println("Added Subject: " + seed.id + " - " + seed.name);
        }
    }

    // ===================== HELPER METHODS =====================
    private static Majors findMajor(EntityManager em, String id) {
        try {
            return em.createQuery("SELECT m FROM Majors m WHERE m.majorId = :id", Majors.class)
                    .setParameter("id", id).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private static Specialization findSpecialization(EntityManager em, String id) {
        try {
            return em.createQuery("SELECT s FROM Specialization s WHERE s.specializationId = :id", Specialization.class)
                    .setParameter("id", id).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private static Curriculum findCurriculum(EntityManager em, String id) {
        try {
            return em.createQuery("SELECT c FROM Curriculum c WHERE c.curriculumId = :id", Curriculum.class)
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

    private static Majors guessMajor(Map<String, Majors> majors, SubjectSeed s) {
        String n = (s.name == null ? "" : s.name.toLowerCase(Locale.ROOT));
        if (n.contains("design") || n.contains("typograph") || n.contains("animation")
                || n.contains("visual") || n.contains("art") || n.contains("drawing")
                || n.contains("sketch") || n.contains("3d")) {
            return majors.get("GDH");
        }
        if (n.contains("marketing") || n.contains("advertis") || n.contains("sales")
                || n.contains("brand") || n.contains("communication") || n.contains("martech")
                || n.contains("content") || n.contains("social media")) {
            return majors.get("GKH");
        }
        if (n.contains("business") || n.contains("entrepreneur") || n.contains("management")
                || n.contains("account") || n.contains("finance") || n.contains("organiz")
                || n.contains("negotiation") || n.contains("international")) {
            return majors.get("GBH");
        }
        if (n.contains("program") || n.contains("java") || n.contains(".net") || n.contains("web")
                || n.contains("software") || n.contains("comput") || n.contains("network")
                || n.contains("database") || n.contains("security") || n.contains("cloud")
                || n.contains("machine learning") || n.contains("ai") || n.contains("algorithm")) {
            return majors.get("GCH");
        }
        return null;
    }

    // Simple holder for subject seed data
    private static class SubjectSeed {
        final String id;
        final String name;
        final Integer credits;
        final Double tuition;

        SubjectSeed(String id, String name, Integer credits, Double tuition) {
            this.id = id;
            this.name = name;
            this.credits = credits;
            this.tuition = tuition;
        }
    }
}