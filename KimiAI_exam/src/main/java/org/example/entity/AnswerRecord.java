package org.example.entity;

public class AnswerRecord {

    private Long id;
    private Long userId;
    private Long questionId;
    private String userAnswer;
    private Boolean correct;

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }
}