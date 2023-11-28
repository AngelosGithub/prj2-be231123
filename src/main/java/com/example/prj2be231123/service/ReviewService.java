package com.example.prj2be231123.service;

import com.example.prj2be231123.domain.Review;
import com.example.prj2be231123.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewMapper mapper;

    public boolean save(Review review) {
        return mapper.insert(review) == 1;
    }
}
