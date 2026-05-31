package org.example.service.impl;

import org.example.entity.Question;
import org.example.service.AnswerService;
import org.example.service.QuestionService;
import org.example.vo.AnswerVo;
import org.example.vo.GradingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class AnswerServiceImpl implements AnswerService {

    @Autowired
    private QuestionService questionService;

    @Override
    public List<GradingResult> gradeAnswers(List<AnswerVo> answers) {
        List<GradingResult> results = new ArrayList<>();
        if (answers == null) {
            return results;
        }

        for (AnswerVo answer : answers) {
            Question q = questionService.getById(answer.getQuestionId());
            if (q == null) {
                continue;
            }

            GradingResult r = new GradingResult();
            r.setQuestionId(q.getId());
            r.setTitle(q.getTitle());
            r.setUserAnswer(answer.getUserAnswer());
            r.setCorrectAnswer(q.getAnswer());
            r.setAnalysis(q.getAnalysis());

            if ("CHOICE".equals(q.getType()) || "JUDGE".equals(q.getType())) {
                r.setCorrect(isCorrect(q.getType(), q.getAnswer(), answer.getUserAnswer()));
            } else {
                r.setCorrect(null);
            }

            results.add(r);
        }

        return results;
    }

    private boolean isCorrect(String type, String standardAnswer, String userAnswer) {
        String expected = normalize(type, standardAnswer);
        String actual = normalize(type, userAnswer);
        return expected.equals(actual);
    }

    private String normalize(String type, String value) {
        String normalized = value == null ? "" : value.trim();
        if (!"JUDGE".equals(type)) {
            return normalized;
        }

        String lower = normalized.toLowerCase(Locale.ROOT);
        if ("true".equals(lower) || "正确".equals(normalized) || "对".equals(normalized)) {
            return "true";
        }
        if ("false".equals(lower) || "错误".equals(normalized) || "错".equals(normalized)) {
            return "false";
        }
        return lower;
    }
}
