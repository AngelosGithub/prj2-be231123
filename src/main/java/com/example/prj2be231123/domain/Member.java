package com.example.prj2be231123.domain;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private List<Auth> auth;

    public boolean isAdmin(){
        if (auth!=null){
            auth.stream()
                    .map(a->a.getManager())
                    .anyMatch(n->n.equals("admin"));
        }
        return false;
    }
}
