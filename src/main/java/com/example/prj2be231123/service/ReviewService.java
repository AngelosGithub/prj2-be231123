package com.example.prj2be231123.service;

import com.example.prj2be231123.domain.Review;
import com.example.prj2be231123.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewMapper mapper;

    public boolean save(Review review) {
        return mapper.insert(review) == 1;
    }

    public List<Review> list() {
        return mapper.selectAll();
    }
}
