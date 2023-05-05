package com.sicnu.boot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * description:
 *
 * @author :  胡建华
 * Data:    2023/01/06 17:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {
    private Integer id;
    private Integer questionId;
    private String questionTitle;
    private String name;

    public QuestionDTO(Integer questionId, String questionTitle) {
        this.questionId = questionId;
        this.questionTitle = questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
        this.name = questionTitle;
    }
}
