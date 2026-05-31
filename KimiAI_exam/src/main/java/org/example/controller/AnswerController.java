package org.example.controller;

import org.example.common.Result;
import org.example.service.AnswerService;
import org.example.vo.AnswerVo;
import org.example.vo.GradingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    // ✅ 提交答案
    @PostMapping("/submit")
    public Result<List<GradingResult>> submit(@RequestBody List<AnswerVo> answers) throws Exception {
        return Result.success(answerService.gradeAnswers(answers));
    }
}