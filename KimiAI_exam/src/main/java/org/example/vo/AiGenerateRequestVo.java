package org.example.vo;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class AiGenerateRequestVo {

    private String topic;
    private Integer count;
    private List<String> questionTypes;
    private MultipartFile image;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<String> getQuestionTypes() {
        return questionTypes;
    }

    public void setQuestionTypes(List<String> questionTypes) {
        this.questionTypes = questionTypes;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
