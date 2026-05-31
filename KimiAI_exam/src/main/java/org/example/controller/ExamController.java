package org.example.controller;

import org.example.common.Result;
import org.example.entity.Exam;
import org.example.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exam")
@CrossOrigin
public class ExamController {

    @Autowired
    private ExamService examService;

    // ✅ 创建试卷（返回ID）
    @PostMapping("/create")
    public Result<Long> create(@RequestBody Exam exam) {
        return Result.success(examService.createExam(exam));
    }

    // ✅ 根据ID查试卷（带题目+选项）
    @GetMapping("/{id}")
    public Result<Exam> getById(@PathVariable Long id) {
        return Result.success(examService.getByIdWithQuestions(id));
    }
}