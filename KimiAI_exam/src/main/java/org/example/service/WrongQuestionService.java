package org.example.service;

import org.example.vo.WrongQuestionFavoriteRequest;
import org.example.vo.WrongQuestionVo;

import java.util.List;

public interface WrongQuestionService {

    List<WrongQuestionVo> listByUserKey(String userKey) throws Exception;

    List<WrongQuestionVo> favorite(WrongQuestionFavoriteRequest request) throws Exception;

    List<WrongQuestionVo> remove(String userKey, Long questionId) throws Exception;
}
