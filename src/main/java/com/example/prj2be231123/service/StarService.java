package com.example.prj2be231123.service;

import com.example.prj2be231123.domain.Star;
import com.example.prj2be231123.mapper.StarMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StarService {
    private final StarMapper mapper;

    public void update(Star star) {
        // 처음 별점을 줄때 : insert
        // 다시 누르면 : edit

    }
}
