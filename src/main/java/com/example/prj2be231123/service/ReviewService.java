package com.example.prj2be231123.service;

import com.example.prj2be231123.domain.Member;
import com.example.prj2be231123.domain.Review;
import com.example.prj2be231123.mapper.CommentMapper;
import com.example.prj2be231123.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewMapper mapper;
    private final CommentMapper commentMapper;

    public boolean save(Review review, Member login) {
        review.setWriter(login.getId());

        return mapper.insert(review) == 1;
    }

    public List<Review> list(Integer page) {
        int from = (page - 1) * 9;

        return mapper.selectAll(from);
    }

    public Review get(Integer no) {
        return mapper.selectById(no);
    }

    public boolean remove(Integer no) {
        // 1. 게시물에 있는 댓글 지우기
        commentMapper.deleteByReviewId(no);

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
        if (login == null) {
            return false;
        }

        if (login.isAdmin()) {
            return true;
        }

        Review review = mapper.selectById(no);

        return review.getWriter().equals(login.getId());
    }
}
