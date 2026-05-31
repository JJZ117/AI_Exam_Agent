package org.example.service;

import org.example.vo.AiGenerateRequestVo;
import org.example.vo.QuestionImportVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AiService {

    List<QuestionImportVo> aiGenerateQuestions(AiGenerateRequestVo request) throws Exception;

    List<QuestionImportVo> aiGenerateSimilarQuestionsByImage(MultipartFile image, String topic, Integer count, List<String> questionTypes) throws Exception;
}
