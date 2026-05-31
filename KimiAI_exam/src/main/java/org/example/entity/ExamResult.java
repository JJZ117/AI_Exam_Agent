package org.example.entity;

public class ExamResult {

    private Long id;
    private Long userId;
    private Long examId;
    private Integer score;

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getExamId() {
        return examId;
    }

    public Integer getScore() {
        return score;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}