package org.example.mapper;

import org.example.entity.Exam;

public interface ExamMapper {

    // 新增试卷
    int insert(Exam exam);

    // 根据ID查询
    Exam selectById(Long id);
}