package org.example.service.impl;

import org.example.entity.Exam;
import org.example.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    private org.example.mapper.ExamMapper examMapper;

    @Autowired
    private org.example.service.QuestionService questionService;

    @Override
    public Long createExam(Exam exam) {
        if (exam.getName() == null || exam.getName().isBlank()) {
            exam.setName("AI试卷");
        }

        // 保存试卷
        examMapper.insert(exam);

        // 保存题目 + 选项 + 关联
        questionService.saveBatchFromExam(exam.getId(), exam.getQuestions());

        return exam.getId();
    }

    @Override
    public Exam getByIdWithQuestions(Long id) {

        Exam exam = examMapper.selectById(id);

        exam.setQuestions(questionService.getByExamId(id));

        return exam;
    }
}
