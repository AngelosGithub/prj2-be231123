package com.example.prj2be231123.service;

import com.example.prj2be231123.domain.Member;
import com.example.prj2be231123.domain.Review;
import com.example.prj2be231123.mapper.CommentMapper;
import com.example.prj2be231123.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewMapper mapper;
    private final CommentMapper commentMapper;

    public boolean save(Review review, Member login) {
        review.setWriter(login.getId());

        return mapper.insert(review) == 1;
    }

    public Map<String, Object> list(Integer page) {
        // List<Review> 리스트로 데이터를 넘겼는데 Map으로 변경
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> pageInfo = new HashMap<>();

        int from = (page - 1) * 9;
        // 페이지 나누기 위한 코드

        int allPages = mapper.allPages();
        // 마지막 페이지를 구하기위해 모든 글의 갯수를 구한다
        int lastPage = (allPages -1) / 9 + 1;
        // 전체 글의 갯수를 토대로 마지막 페이지를 구하는 계산식

        int startPageNum = (page -1) / 10 * 10 + 1;
        int endPageNum = startPageNum + 10;
        endPageNum = Math.min(endPageNum, lastPage);
        // 페이지의 시작과 끝을 넣을 변수

        int prevPage = startPageNum - 10;
        int nextPage = endPageNum + 1;
        // 페이지 이동 버튼 만들때 필요한 계산식

        pageInfo.put("startPageNum", startPageNum);
        pageInfo.put("endPageNum", endPageNum);
        if (prevPage > 0) {
            // 이전 버튼
            pageInfo.put("prevPage", prevPage);
        }
        if (nextPage < lastPage) {
            // 다음 버튼
            pageInfo.put("nextPage", nextPage);
        }

        map.put("reviewList", mapper.selectAll(from));
        map.put("pageInfo", pageInfo);

        return map;
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
