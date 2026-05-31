package org.example.entity;

import java.util.List;

public class Exam {

    private Long id;
    private String name;

    // 非数据库字段（前端用）
    private List<Question> questions;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}