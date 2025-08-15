package com.example.demo;

import com.example.demo.entity.*;
import com.example.demo.entity.Enums.Gender;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        // Start the application and get ApplicationContext
        ApplicationContext context = SpringApplication.run(DemoApplication.class, args);

        // Get EntityManagerFactory and PasswordEncoder from context
        EntityManagerFactory entityManagerFactory = context.getBean(EntityManagerFactory.class);
        PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);

        // Create a single EntityManager
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            // Add default slots
            entityManager.getTransaction().begin();
            addDefaultSlots(entityManager);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Failed to add default slots: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            // Add default majors
            entityManager.getTransaction().begin();
            addDefaultMajors(entityManager);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Failed to add default majors: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            // Add default staff accounts
            entityManager.getTransaction().begin();
            addDefaultStaffs(entityManager, passwordEncoder);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Failed to add default staffs: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close EntityManager
            entityManager.close();
        }
    }

    // Add default slots to the database if they don't exist
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
                System.out.println("Slot already exists: " + slot.getSlotName());
            } catch (NoResultException e) {
                entityManager.persist(slot);
                System.out.println("Added slot: " + slot.getSlotName());
            }
        }
    }

    // Add default majors to the database if they don't exist
    private static void addDefaultMajors(EntityManager entityManager) {
        List<Majors> majorsToAdd = new ArrayList<>();

        Majors major1 = new Majors();
        major1.setMajorId("GBH");
        major1.setMajorName("Business Administration");

        Majors major2 = new Majors();
        major2.setMajorId("GCH");
        major2.setMajorName("Information Technology");

        Majors major3 = new Majors();
        major3.setMajorId("GDH");
        major3.setMajorName("Graphic Design");

        Majors major4 = new Majors();
        major4.setMajorId("GKH");
        major4.setMajorName("Marketing");

        majorsToAdd.addAll(List.of(major1, major2, major3, major4));

        for (Majors major : majorsToAdd) {
            try {
                Majors existingMajor = entityManager.createQuery(
                                "SELECT m FROM Majors m WHERE m.majorId = :id", Majors.class)
                        .setParameter("id", major.getMajorId())
                        .getSingleResult();
                System.out.println("Major already exists: " + major.getMajorName());
            } catch (NoResultException e) {
                entityManager.persist(major);
                System.out.println("Added Major: " + major.getMajorName());
            }
        }
    }

    // Add default staff accounts to the database if they don't exist
    private static void addDefaultStaffs(EntityManager entityManager, PasswordEncoder passwordEncoder) {
        List<Staffs> staffsToAdd = new ArrayList<>();

        // Retrieve Majors from the database with error handling
        Majors businessAdministration = null;
        Majors informationTechnology = null;
        try {
            businessAdministration = entityManager.createQuery(
                            "SELECT m FROM Majors m WHERE m.majorId = :id", Majors.class)
                    .setParameter("id", "GBH")
                    .getSingleResult();
            informationTechnology = entityManager.createQuery(
                            "SELECT m FROM Majors m WHERE m.majorId = :id", Majors.class)
                    .setParameter("id", "GCH")
                    .getSingleResult();
        } catch (NoResultException e) {
            System.err.println("Error: Required major not found. Ensure majors are added before staffs.");
            return; // Exit if majors are not found
        }

        Staffs staff1 = new Staffs();
        staff1.setId("vuthanhtruong");
        staff1.setFirstName("John");
        staff1.setLastName("Doe");
        staff1.setEmail("vuthanhtruong1280@gmail.com");
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
        staff1.setMajorManagement(businessAdministration);

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
        staff2.setMajorManagement(informationTechnology);

        staffsToAdd.addAll(List.of(staff1, staff2));

        for (Staffs staff : staffsToAdd) {
            try {
                Staffs existingStaff = entityManager.createQuery(
                                "SELECT s FROM Staffs s WHERE s.id = :id", Staffs.class)
                        .setParameter("id", staff.getId())
                        .getSingleResult();
                System.out.println("Staff already exists: " + staff.getId());
            } catch (NoResultException e) {
                try {
                    entityManager.persist(staff);
                    // Create and save Authenticators entity for the staff
                    Authenticators authenticators = new Authenticators();
                    authenticators.setPersonId(staff.getId());
                    authenticators.setPerson(staff);
                    authenticators.setPassword("Anhnam123"); // Password will be encoded by Authenticators' setPassword
                    entityManager.persist(authenticators);
                    System.out.println("Added Staff: " + staff.getId());
                } catch (Exception ex) {
                    System.err.println("Failed to add staff " + staff.getId() + ": " + ex.getMessage());
                    throw ex; // Re-throw to trigger transaction rollback
                }
            }
        }
    }
}