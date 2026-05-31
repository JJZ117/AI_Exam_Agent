package org.example.mapper;

import org.example.entity.Choice;
import java.util.List;

public interface ChoiceMapper {

    // 根据题目查选项
    List<Choice> selectByQuestionId(Long questionId);

    // 批量插入
    int insert(Choice choice);
}