package com.sicnu.boot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * description:
 *
 * @author :  胡建华
 * Data:    2023/01/06 17:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoDTO {
    private Integer id;
    private Integer videoId;
    private String name;
    private String introduction;

    public VideoDTO(Integer videoId, String name, String introduction) {
        this.videoId = videoId;
        this.name = name;
        this.introduction = introduction;
    }
}
