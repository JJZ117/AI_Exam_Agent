package org.example.controller;

import org.example.common.Result;
import org.example.service.AiService;
import org.example.vo.AiGenerateRequestVo;
import org.example.vo.QuestionImportVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions/batch")
@CrossOrigin
public class QuestionBatchController {

    @Autowired
    private AiService aiService;

    // ✅ AI生成题目
    @PostMapping("/ai-generate")
    public Result<List<QuestionImportVo>> aiGenerate(@RequestBody AiGenerateRequestVo request) throws Exception {
        return Result.success(aiService.aiGenerateQuestions(request));
    }

    @PostMapping(value = "/ai-generate-by-image", consumes = "multipart/form-data")
    public Result<List<QuestionImportVo>> aiGenerateByImage(@ModelAttribute AiGenerateRequestVo request) throws Exception {
        return Result.success(aiService.aiGenerateSimilarQuestionsByImage(
                request.getImage(),
                request.getTopic(),
                request.getCount(),
                request.getQuestionTypes()
        ));
    }
}
