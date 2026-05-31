package org.example.service;

import org.example.vo.AnswerVo;
import org.example.vo.GradingResult;

import java.util.List;

public interface AnswerService {

    List<GradingResult> gradeAnswers(List<AnswerVo> answers);
}