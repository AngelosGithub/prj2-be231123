package com.example.prj2be231123.service;

import com.example.prj2be231123.domain.Comment;
import com.example.prj2be231123.domain.Member;
import com.example.prj2be231123.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper mapper;

    public boolean add(Comment comment, Member login) {
        // 로그인 데이터 중 아이디를 받아옴
        comment.setMemberId(login.getId());
        return mapper.insert(comment) == 1;
    }

    public boolean validate(Comment comment) {
        if (comment == null) {
            return false;
        }

        if (comment.getReviewId() == null || comment.getReviewId() < 1) {
            // 리뷰 아이디가 없거나 1보다 작을때 에러
            return false;
        }

        if (comment.getComment() == null || comment.getComment().isBlank()) {
            // 코멘트를 적지 않았을때 에러
            return false;
        }

        return true;
    }

    public List<Comment> list(Integer reviewId) {
        return mapper.selectByReviewId(reviewId);
    }

    public boolean remove(Integer no) {
        return mapper.deleteById(no) == 1;
    }

    public boolean hasAccess(Integer no, Member login) {
        Comment comment = mapper.selectById(no);

        return comment.getMemberId().equals(login.getId());
    }

    public boolean update(Comment comment) {
        return mapper.update(comment) == 1;
    }

    public boolean editValidate(Comment comment) {
        if (comment == null) {
            return false;
        }

        if (comment.getNo() == null) {
            return false;
        }

        if (comment.getComment() == null || comment.getComment().isBlank()) {
            return false;
        }

        return true;
    }
}
