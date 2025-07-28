package com.example.demo.service.impl;

import com.example.demo.dao.StaffsDAO;
import com.example.demo.dto.LecturersDTO;
import com.example.demo.dto.StaffsDTO;
import com.example.demo.dto.StudentsDTO;
import com.example.demo.entity.*;
import com.example.demo.service.StaffsService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffsServiceImpl implements StaffsService {

    @Override
    public Majors getMajors() {
        return staffsDAO.getMajors();
    }
    private final StaffsDAO staffsDAO;

    public StaffsServiceImpl(StaffsDAO staffsDAO) {
        this.staffsDAO = staffsDAO;
    }
    @Override
    public Staffs getStaffs() {
        return staffsDAO.getStaffs();
    }

    @Override
    public List<Classes> getClasses() {
        return staffsDAO.getClasses();
    }

}
