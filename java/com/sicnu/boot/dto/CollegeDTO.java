package com.sicnu.boot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * description:
 *
 * @author :  胡建华
 * Data:    2023/01/06 17:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollegeDTO {
    private Integer id;
    private Integer collegeId;
    private String name;
    private String introduction;

    public CollegeDTO(Integer collegeId, String name, String introduction) {
        this.collegeId = collegeId;
        this.name = name;
        this.introduction = introduction;
    }
}
