package org.example.entity;

public class Choice {

    private Long id;
    private String content;
    private Long questionId;

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
}