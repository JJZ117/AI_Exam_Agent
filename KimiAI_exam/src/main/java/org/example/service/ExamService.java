package org.example.service;

import org.example.entity.Exam;

public interface ExamService {

    Long createExam(Exam exam);

    Exam getByIdWithQuestions(Long id);
}