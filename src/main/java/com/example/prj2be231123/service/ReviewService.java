package com.example.prj2be231123.service;

import com.example.prj2be231123.domain.Member;
import com.example.prj2be231123.domain.Review;
import com.example.prj2be231123.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewMapper mapper;
    private final MemberService memberService;

    public boolean save(Review review, Member login) {
        review.setWriter(login.getId());

        return mapper.insert(review) == 1;
    }

    public List<Review> list() {
        return mapper.selectAll();
    }

    public Review get(Integer no) {
        return mapper.selectById(no);
    }

    public boolean remove(Integer no) {


        return mapper.deleteById(no) == 1;
    }

    public boolean update(Review review) {
        return mapper.update(review) == 1;
    }

    public boolean validate(Review review) {
        if (review == null) {
            return false;
        }

        if (review.getTitle() == null || review.getTitle().isBlank()) {
            return false;
        }

        if (review.getContent() == null || review.getContent().isBlank()) {
            return false;
        }

        if (review.getRecommend() == null || review.getRecommend().isBlank()) {
            return false;
        }
        return true;
    }

    public boolean hasAccess(Integer no, Member login) {
        if (memberService.isAdmin(login)) {
            return true;
        }

        Review review = mapper.selectById(no);

        return review.getWriter().equals(login.getId());
    }
}
