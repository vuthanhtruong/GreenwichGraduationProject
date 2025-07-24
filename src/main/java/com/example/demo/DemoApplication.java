package com.example.demo;

import com.example.demo.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        // Khởi động ứng dụng và lấy ApplicationContext
        ApplicationContext context = SpringApplication.run(DemoApplication.class, args);

        // Lấy EntityManagerFactory và PasswordEncoder từ context
        EntityManagerFactory entityManagerFactory = context.getBean(EntityManagerFactory.class);
        PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);

        // Tạo EntityManager duy nhất
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            // Thêm các sự kiện mặc định
            entityManager.getTransaction().begin();
            addDefaultEvents(entityManager);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        }

        try {
            // Thêm các slot mặc định
            entityManager.getTransaction().begin();
            addDefaultSlots(entityManager);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        }

        try {
            // Thêm các major mặc định
            entityManager.getTransaction().begin();
            addDefaultMajors(entityManager);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        }

        try {
            // Thêm các tài khoản Staffs mặc định
            entityManager.getTransaction().begin();
            addDefaultStaffs(entityManager, passwordEncoder);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            // Đóng EntityManager
            entityManager.close();
        }
    }

    // Thêm các sự kiện mặc định vào database nếu chưa có
    private static void addDefaultEvents(EntityManager entityManager) {
        List<Notifications> eventsToAdd = new ArrayList<>();

        Notifications event1 = new Notifications();
        event1.setNotificationId("event001");
        event1.setTitle("Default Event");
        event1.setDescription("Default event description");
        event1.setNotificationDate(LocalDateTime.of(2025, 3, 3, 0, 0, 0));
        event1.setNotificationType("DEFAULT");

        Notifications event2 = new Notifications();
        event2.setNotificationId("event002");
        event2.setTitle("New Message");
        event2.setDescription("You have a new message from another user.");
        event2.setNotificationDate(LocalDateTime.of(2025, 3, 3, 20, 57, 47));
        event2.setNotificationType("MESSAGE");

        Notifications event3 = new Notifications();
        event3.setNotificationId("event003");
        event3.setTitle("New Feedback");
        event3.setDescription("You have new feedback from a student.");
        event3.setNotificationDate(LocalDateTime.of(2025, 3, 3, 20, 57, 47));
        event3.setNotificationType("FEEDBACK");

        Notifications event4 = new Notifications();
        event4.setNotificationId("event004");
        event4.setTitle("New Post");
        event4.setDescription("A new post has been made in your classroom.");
        event4.setNotificationDate(LocalDateTime.of(2025, 3, 3, 20, 57, 47));
        event4.setNotificationType("POST");

        Notifications event5 = new Notifications();
        event5.setNotificationId("event005");
        event5.setTitle("New Document");
        event5.setDescription("A new document has been shared with you.");
        event5.setNotificationDate(LocalDateTime.of(2025, 3, 3, 20, 57, 47));
        event5.setNotificationType("DOCUMENT");

        Notifications event6 = new Notifications();
        event6.setNotificationId("event006");
        event6.setTitle("New Comment");
        event6.setDescription("Someone has commented on your post.");
        event6.setNotificationDate(LocalDateTime.of(2025, 3, 3, 20, 57, 47));
        event6.setNotificationType("COMMENT");

        Notifications event7 = new Notifications();
        event7.setNotificationId("event007");
        event7.setTitle("New Blog Post");
        event7.setDescription("A new blog post has been published.");
        event7.setNotificationDate(LocalDateTime.of(2025, 3, 3, 20, 57, 47));
        event7.setNotificationType("BLOG");

        Notifications event8 = new Notifications();
        event8.setNotificationId("event008");
        event8.setTitle("Schedule Notification");
        event8.setDescription("You have a new notification related to your schedule.");
        event8.setNotificationDate(LocalDateTime.of(2025, 3, 3, 20, 57, 47));
        event8.setNotificationType("SCHEDULE_NOTIFICATION");

        Notifications event9 = new Notifications();
        event9.setNotificationId("event009");
        event9.setTitle("Added to Classroom");
        event9.setDescription("You have been added to a new classroom.");
        event9.setNotificationDate(LocalDateTime.of(2025, 3, 3, 20, 57, 47));
        event9.setNotificationType("CLASSROOM_JOIN");

        Notifications event10 = new Notifications();
        event10.setNotificationId("event010");
        event10.setTitle("System Event");
        event10.setDescription("Default system event.");
        event10.setNotificationDate(LocalDateTime.of(2025, 3, 3, 0, 0, 0));
        event10.setNotificationType("SYSTEM_EVENT");

        eventsToAdd.addAll(List.of(event1, event2, event3, event4, event5, event6, event7, event8, event9, event10));

        for (Notifications event : eventsToAdd) {
            try {
                Notifications existingEvent = entityManager.createQuery(
                                "SELECT e FROM Notifications e WHERE e.notificationId = :id", Notifications.class)
                        .setParameter("id", event.getNotificationId())
                        .getSingleResult();
                System.out.println("Sự kiện đã tồn tại: " + event.getTitle());
            } catch (NoResultException e) {
                entityManager.persist(event);
                System.out.println("Đã thêm sự kiện: " + event.getTitle());
            }
        }
    }

    // Thêm các slot mặc định vào database nếu chưa có
    private static void addDefaultSlots(EntityManager entityManager) {
        List<Slots> slotsToAdd = new ArrayList<>();

        Slots slot1 = new Slots();
        slot1.setSlotId("slot001");
        slot1.setSlotName("Slot 1");
        slot1.setStartTime(LocalTime.of(7, 0));
        slot1.setEndTime(LocalTime.of(8, 40));

        Slots slot2 = new Slots();
        slot2.setSlotId("slot002");
        slot2.setSlotName("Slot 2");
        slot2.setStartTime(LocalTime.of(8, 50));
        slot2.setEndTime(LocalTime.of(10, 20));

        Slots slot3 = new Slots();
        slot3.setSlotId("slot003");
        slot3.setSlotName("Slot 3");
        slot3.setStartTime(LocalTime.of(10, 30));
        slot3.setEndTime(LocalTime.of(12, 0));

        Slots slot4 = new Slots();
        slot4.setSlotId("slot004");
        slot4.setSlotName("Slot 4");
        slot4.setStartTime(LocalTime.of(12, 50));
        slot4.setEndTime(LocalTime.of(14, 20));

        Slots slot5 = new Slots();
        slot5.setSlotId("slot005");
        slot5.setSlotName("Slot 5");
        slot5.setStartTime(LocalTime.of(14, 30));
        slot5.setEndTime(LocalTime.of(16, 0));

        Slots slot6 = new Slots();
        slot6.setSlotId("slot006");
        slot6.setSlotName("Slot 6");
        slot6.setStartTime(LocalTime.of(16, 10));
        slot6.setEndTime(LocalTime.of(17, 40));

        slotsToAdd.addAll(List.of(slot1, slot2, slot3, slot4, slot5, slot6));

        for (Slots slot : slotsToAdd) {
            try {
                Slots existingSlot = entityManager.createQuery(
                                "SELECT s FROM Slots s WHERE s.slotId = :id", Slots.class)
                        .setParameter("id", slot.getSlotId())
                        .getSingleResult();
                System.out.println("Slot đã tồn tại: " + slot.getSlotName());
            } catch (NoResultException e) {
                entityManager.persist(slot);
                System.out.println("Đã thêm slot: " + slot.getSlotName());
            }
        }
    }

    // Thêm các major mặc định vào database nếu chưa có
    private static void addDefaultMajors(EntityManager entityManager) {
        List<Majors> majorsToAdd = new ArrayList<>();

        Majors major1 = new Majors();
        major1.setMajorId("major001");
        major1.setMajorName("Business Administration");

        Majors major2 = new Majors();
        major2.setMajorId("major002");
        major2.setMajorName("Information Technology");

        Majors major3 = new Majors();
        major3.setMajorId("major003"); // Fixed: Use major3
        major3.setMajorName("Graphic Design"); // Fixed: Use major3

        Majors major4 = new Majors();
        major4.setMajorId("major004"); // Fixed: Use major4
        major4.setMajorName("Marketing"); // Fixed: Use major4

        majorsToAdd.addAll(List.of(major1, major2, major3, major4));

        for (Majors major : majorsToAdd) {
            try {
                Majors existingMajor = entityManager.createQuery(
                                "SELECT m FROM Majors m WHERE m.majorId = :id", Majors.class)
                        .setParameter("id", major.getMajorId())
                        .getSingleResult();
                System.out.println("Major đã tồn tại: " + major.getMajorName());
            } catch (NoResultException e) {
                entityManager.persist(major);
                System.out.println("Đã thêm Major: " + major.getMajorName());
            }
        }
    }

    // Thêm các tài khoản Staffs mặc định vào database nếu chưa có
    private static void addDefaultStaffs(EntityManager entityManager, PasswordEncoder passwordEncoder) {
        List<Staffs> staffsToAdd = new ArrayList<>();

        // Lấy các Majors từ database để gán
        Majors computerScience = entityManager.createQuery(
                        "SELECT m FROM Majors m WHERE m.majorId = :id", Majors.class)
                .setParameter("id", "major001")
                .getSingleResult();

        Majors informationTechnology = entityManager.createQuery(
                        "SELECT m FROM Majors m WHERE m.majorId = :id", Majors.class)
                .setParameter("id", "major002")
                .getSingleResult();

        Staffs staff1 = new Staffs();
        staff1.setId("staff001");
        staff1.setFirstName("John");
        staff1.setLastName("Doe");
        staff1.setEmail("john.doe@example.com");
        staff1.setPhoneNumber("0391234567");
        staff1.setBirthDate(LocalDate.of(1985, 5, 15));
        staff1.setGender(Gender.MALE);
        staff1.setCountry("Vietnam");
        staff1.setProvince("Hanoi");
        staff1.setCity("Hanoi");
        staff1.setDistrict("Cau Giay");
        staff1.setWard("Dich Vong");
        staff1.setStreet("123 Tran Duy Hung");
        staff1.setPostalCode("100000");
        staff1.setCreatedDate(LocalDate.now());
        staff1.setPassword("Staff123");
        staff1.setMajorManagement(computerScience);

        Staffs staff2 = new Staffs();
        staff2.setId("staff002");
        staff2.setFirstName("Jane");
        staff2.setLastName("Smith");
        staff2.setEmail("jane.smith@example.com");
        staff2.setPhoneNumber("0397654321");
        staff2.setBirthDate(LocalDate.of(1990, 8, 20));
        staff2.setGender(Gender.FEMALE);
        staff2.setCountry("Vietnam");
        staff2.setProvince("Ho Chi Minh");
        staff2.setCity("Ho Chi Minh");
        staff2.setDistrict("District 1");
        staff2.setWard("Ben Nghe");
        staff2.setStreet("45 Le Loi");
        staff2.setPostalCode("700000");
        staff2.setCreatedDate(LocalDate.now());
        staff2.setPassword("Staff123");
        staff2.setMajorManagement(informationTechnology);

        staffsToAdd.addAll(List.of(staff1, staff2));

        for (Staffs staff : staffsToAdd) {
            try {
                Staffs existingStaff = entityManager.createQuery(
                                "SELECT s FROM Staffs s WHERE s.id = :id", Staffs.class)
                        .setParameter("id", staff.getId())
                        .getSingleResult();
                System.out.println("Staff đã tồn tại: " + staff.getId());
            } catch (NoResultException e) {
                entityManager.persist(staff);
                System.out.println("Đã thêm Staff: " + staff.getId());
            }
        }
    }
}