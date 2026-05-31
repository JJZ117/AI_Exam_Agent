package org.example.entity;

public class ExamQuestion {

    private Long id;
    private Long examId;
    private Long questionId;

    public Long getId() {
        return id;
    }

    public Long getExamId() {
        return examId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
}