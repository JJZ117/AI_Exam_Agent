package org.example.mapper;

import org.example.entity.Question;

import java.util.List;

public interface QuestionMapper {

    int insert(Question question);

    Question selectById(Long id);

    List<Question> selectAll();
}