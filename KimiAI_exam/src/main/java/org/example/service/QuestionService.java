package org.example.service;

import org.example.entity.Question;

import java.util.List;

public interface QuestionService {

    void saveBatchFromExam(Long examId, java.util.List<Question> questions);

    java.util.List<Question> getByExamId(Long examId);

    Question getById(Long id);
}