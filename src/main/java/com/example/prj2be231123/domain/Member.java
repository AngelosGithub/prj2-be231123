package com.example.prj2be231123.domain;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Member {
    private String id;
    private String password;
    private String nickName;
    private String gender;
    private String phone;
    private String email;
    private String birthDate;
    private LocalDateTime inserted;
}
