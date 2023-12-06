package com.example.prj2be231123.domain;

import lombok.Data;
import org.apache.ibatis.annotations.Insert;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;

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

    public String getAgo() {
        LocalDateTime now = LocalDateTime.now();

        if (inserted.isBefore(now.minusYears(1))) {
            Period between = Period.between(inserted.toLocalDate(), now.toLocalDate());
            return between.get(ChronoUnit.YEARS) + "년 전";
        } else if (inserted.isBefore(now.minusMonths(1))) {
            Period between = Period.between(inserted.toLocalDate(), now.toLocalDate());
            return between.get(ChronoUnit.MONTHS) + "달 전";
        } else if (inserted.isBefore(now.minusDays(1))) {
            Period between = Period.between(inserted.toLocalDate(), now.toLocalDate());
            return between.get(ChronoUnit.DAYS) + "일 전";
        } else if (inserted.isBefore(now.minusHours(1))) {
            Duration between = Duration.between(inserted, now);
            return (between.getSeconds() / 60 / 60) + "시간 전";
        } else if (inserted.isBefore(now.minusMinutes(1))) {
            Duration between = Duration.between(inserted, now);
            return (between.getSeconds() / 60) + "분 전";
        } else {
            Duration between = Duration.between(inserted, now);
            return between.getSeconds() + "초 전";
        }
    }
}
