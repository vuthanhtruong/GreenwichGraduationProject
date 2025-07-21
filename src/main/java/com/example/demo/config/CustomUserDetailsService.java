package com.example.demo.config;

import com.example.demo.entity.Lecturers;
import com.example.demo.entity.Persons;
import com.example.demo.entity.Staffs;
import com.example.demo.entity.Students;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        // Try to find user in Students table
        try {
            Students student = entityManager.createQuery(
                            "SELECT s FROM Students s WHERE s.id = :id", Students.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return new CustomUserDetails(student, "ROLE_STUDENT");
        } catch (NoResultException e) {
            // Not found in Students, try Staffs
        }

        // Try to find user in Staffs table
        try {
            Staffs staff = entityManager.createQuery(
                            "SELECT s FROM Staffs s WHERE s.id = :id", Staffs.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return new CustomUserDetails(staff, "ROLE_STAFF");
        } catch (NoResultException e) {
            // Not found in Staffs, try Lecturers
        }

        // Try to find user in Lecturers table
        try {
            Lecturers lecturer = entityManager.createQuery(
                            "SELECT l FROM Lecturers l WHERE l.id = :id", Lecturers.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return new CustomUserDetails(lecturer, "ROLE_LECTURER");
        } catch (NoResultException e) {
            throw new UsernameNotFoundException("User not found with id: " + id);
        }
    }
}