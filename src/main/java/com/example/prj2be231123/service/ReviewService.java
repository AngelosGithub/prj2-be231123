package com.example.prj2be231123.service;

import com.example.prj2be231123.domain.Member;
import com.example.prj2be231123.domain.Review;
import com.example.prj2be231123.mapper.CommentMapper;
import com.example.prj2be231123.mapper.FileMapper;
import com.example.prj2be231123.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//  Transactional 전체 코드가 실행되어야 함
@Transactional(rollbackFor = Exception.class)
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewMapper mapper;
    private final CommentMapper commentMapper;
    private final FileMapper fileMapper;

    private final S3Client s3;

    // import 를 잘 확인할 것
    @Value("${aws.bucketName}")
    private String bucket;

    public boolean save(Review review, Member login, MultipartFile[] files) throws IOException {
        // 로그인 한 사용자의 아이디를 가져옴
        review.setWriter(login.getId());

        int cnt = mapper.insert(review);

        // reviewFile 테이블에 files 정보 저장(파일의 이름만)
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                // reviewId, name
                fileMapper.insert(review.getNo(), files[i].getOriginalFilename());

                // 실제 파일은 S3 버켓에 업로드
                // 우선 로컬에 저장
                upload(files[i], review.getNo());
            }
        }
        return cnt == 1;
    }

    private void upload(MultipartFile file, Integer reviewId) throws IOException {
        // 기존 로컬에 저장시켰던 코드는 제거함

        // aws 서버에 파일 올리기
        String key = "prj2/review/" + reviewId + "/" + file.getOriginalFilename();
        // 파일 경로 정해주는 key

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        s3.putObject(objectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        // s3 bucket에 파일 업로드 하는 코드
    }

    public Map<String, Object> list(Integer page, String keyword) {
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
        int nextPage = endPageNum;
        // 페이지 이동 버튼 만들때 필요한 계산식

        pageInfo.put("currentPage", page);
        // 현재 페이지 번호

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

        map.put("reviewList", mapper.selectAll(from, "%" + keyword + "%"));
        // 검색기능을 위해 keyword 파라미터 추가
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
