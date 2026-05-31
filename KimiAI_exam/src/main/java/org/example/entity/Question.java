package org.example.entity;

import java.util.List;

public class Question {

    private Long id;
    private Long examId;
    private String title;
    private String type;   // CHOICE / JUDGE / TEXT
    private String answer;
    private String analysis;

    // 非数据库字段
    private List<Choice> choices;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Long getExamId() {
        return examId;
    }

    public String getType() {
        return type;
    }

    public String getAnswer() {
        return answer;
    }

    public String getAnalysis() {
        return analysis;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }
}
