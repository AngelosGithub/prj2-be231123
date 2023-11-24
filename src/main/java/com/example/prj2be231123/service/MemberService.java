package com.example.prj2be231123.service;

import com.example.prj2be231123.domain.Member;
import com.example.prj2be231123.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberMapper mapper;


    public boolean add(Member member) {
        return mapper.insert(member) == 1;
    }
}
