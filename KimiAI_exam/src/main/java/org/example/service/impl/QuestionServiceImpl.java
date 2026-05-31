package org.example.service.impl;

import org.example.entity.Choice;
import org.example.entity.Question;
import org.example.mapper.ChoiceMapper;
import org.example.mapper.ExamQuestionMapper;
import org.example.mapper.QuestionMapper;
import org.example.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private ChoiceMapper choiceMapper;

    @Autowired
    private ExamQuestionMapper examQuestionMapper;

    @Override
    public void saveBatchFromExam(Long examId, List<Question> questions) {

        if (questions == null || questions.isEmpty()) {
            return;
        }

        for (Question q : questions) {

            // 1️⃣ 保存题目
            questionMapper.insert(q);

            // 2️⃣ 关联试卷
            examQuestionMapper.insert(examId, q.getId());

            // 3️⃣ 保存选项
            if (q.getChoices() != null) {
                for (Choice c : q.getChoices()) {
                    c.setQuestionId(q.getId());
                    choiceMapper.insert(c);
                }
            }
        }
    }

    @Override
    public List<Question> getByExamId(Long examId) {

        List<Long> ids = examQuestionMapper.selectQuestionIdsByExamId(examId);

        List<Question> list = new ArrayList<>();

        for (Long id : ids) {

            Question q = questionMapper.selectById(id);

            // 查选项
            List<Choice> choices = choiceMapper.selectByQuestionId(id);
            q.setChoices(choices);

            list.add(q);
        }

        return list;
    }

    @Override
    public Question getById(Long id) {

        Question q = questionMapper.selectById(id);

        List<Choice> choices = choiceMapper.selectByQuestionId(id);
        q.setChoices(choices);

        return q;
    }
}
