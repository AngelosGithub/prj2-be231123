package com.example.prj2be231123.domain;

import com.example.prj2be231123.util.AppUtil;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Review {
    private Integer no;
    private String title;
    private String recommend;
    private String content;
    private String writer;
    private String nickName;
    private Integer restaurantId;
    private LocalDateTime inserted;
    private Integer countComment;
    private Integer starPoint;
    private String place;

    private List<ReviewFile> files;

    public String getAgo() {
        return AppUtil.getAgo(inserted, LocalDateTime.now());
    }
}
