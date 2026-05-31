package org.example.vo;

import java.util.List;

public class ExamVo {

    private Long id;
    private String name;
    private List<QuestionImportVo> questions;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<QuestionImportVo> getQuestions() {
        return questions;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuestions(List<QuestionImportVo> questions) {
        this.questions = questions;
    }
}