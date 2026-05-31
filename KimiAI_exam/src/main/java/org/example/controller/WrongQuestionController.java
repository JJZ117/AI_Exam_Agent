package org.example.controller;

import org.example.common.Result;
import org.example.service.WrongQuestionService;
import org.example.vo.WrongQuestionFavoriteRequest;
import org.example.vo.WrongQuestionVo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wrong-questions")
@CrossOrigin
public class WrongQuestionController {

    private final WrongQuestionService wrongQuestionService;

    public WrongQuestionController(WrongQuestionService wrongQuestionService) {
        this.wrongQuestionService = wrongQuestionService;
    }

    @GetMapping
    public Result<List<WrongQuestionVo>> list(@RequestParam(required = false) String userKey) throws Exception {
        return Result.success(wrongQuestionService.listByUserKey(userKey));
    }

    @PostMapping("/favorite")
    public Result<List<WrongQuestionVo>> favorite(@RequestBody WrongQuestionFavoriteRequest request) throws Exception {
        return Result.success(wrongQuestionService.favorite(request));
    }

    @DeleteMapping("/{questionId}")
    public Result<List<WrongQuestionVo>> remove(@PathVariable Long questionId,
                                                @RequestParam(required = false) String userKey) throws Exception {
        return Result.success(wrongQuestionService.remove(userKey, questionId));
    }
}
