package com.example.prj2be231123.domain;

import com.example.prj2be231123.util.AppUtil;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Integer no;
    private Integer reviewId;
    private String memberId;
    private String memberNickName;
    private String comment;
    private LocalDateTime inserted;

    public String getAgo() {
        return AppUtil.getAgo(inserted, LocalDateTime.now());
    }
}
