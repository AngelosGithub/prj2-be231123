package com.example.prj2be231123.controller;

import com.example.prj2be231123.domain.Member;
import com.example.prj2be231123.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService service;

    @PostMapping("signup")
    public void signup(Member member) {
        service.add(member);
        System.out.println("member = " + member);
    }
}
