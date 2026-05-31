package org.example.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExamQuestionMapper {

    int insert(@Param("examId") Long examId, @Param("questionId") Long questionId);

    List<Long> selectQuestionIdsByExamId(@Param("examId") Long examId);
}
