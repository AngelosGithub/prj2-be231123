package com.example.prj2be231123.domain;

import com.example.prj2be231123.util.AppUtil;
import lombok.Data;
import org.apache.ibatis.annotations.Insert;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
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

    private List<String> fileNames;

    public String getAgo() {
        return AppUtil.getAgo(inserted, LocalDateTime.now());
    }
}
