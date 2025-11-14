package com.example.demo.classes.abstractClasses.service;

import com.example.demo.classes.abstractClasses.model.Classes;

public interface ClassesService {
    Classes findClassById(String classId);

    /** Kiểm tra xem tên lớp đã tồn tại chưa (toàn hệ thống) */
    boolean existsByNameClass(String nameClass);

    /** Dùng khi edit – loại trừ chính lớp đang sửa */
    boolean existsByNameClassExcludingId(String nameClass, String excludeClassId);
}
