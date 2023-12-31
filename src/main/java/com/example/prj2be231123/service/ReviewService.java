package com.example.prj2be231123.service;

import com.example.prj2be231123.domain.*;
import com.example.prj2be231123.mapper.CommentMapper;
import com.example.prj2be231123.mapper.FileMapper;
import com.example.prj2be231123.mapper.ReviewMapper;
import com.example.prj2be231123.mapper.StarMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//  Transactional 전체 코드가 실행되어야 함
@Transactional(rollbackFor = Exception.class)
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewMapper mapper;
    private final CommentMapper commentMapper;
    private final FileMapper fileMapper;
    private final StarMapper starMapper;

    private final S3Client s3;

    // import 를 잘 확인할 것
    @Value("${image.file.prefix}")
    private String urlPrefix;
    @Value("${aws.bucketName}")
    private String bucket;

    public boolean save(Review review, Member login, MultipartFile[] files, Integer no, Integer point) throws IOException {
        // 로그인 한 사용자의 아이디를 가져옴
        review.setWriter(login.getId());
        // 레스토랑 id를 받아서 글 작성시 필요한 RestaurantId 에 값을 넣는다
        review.setRestaurantId(no);

        int cnt = mapper.insert(review);

        int star = starMapper.insert(review.getNo(), login.getId(), point);

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

    public HashMap<String, Object> list(Integer page, String keyword, String category, Integer no) {
        // List<Review> 리스트로 데이터를 넘겼는데 Map으로 변경
        HashMap<String, Object> map = new HashMap<>();
        HashMap<String, Object> pageInfo = new HashMap<>();

        List<Review> reviewList = new ArrayList<>();

        int from = (page - 1) * 6;
        // 페이지 나누기 위한 코드
        int countAll =0;
        // 마지막 페이지를 구하기위해 모든 글의 갯수를 구한다

        if (no == null) {
            countAll = mapper.allPages("%" + keyword + "%", category);
            reviewList = mapper.selectAll(from, "%" + keyword + "%", category);
            map.put("reviewList", reviewList);
            // 검색기능을 위해 keyword 파라미터 추가
        }

        if (no != null) {
            // 리뷰 더보기를 눌렀을 때 페이지 지정
            reviewList = mapper.selectByRestaurantNo(from, no);
            map.put("reviewList", reviewList);
            countAll = reviewList.size();
        }

        int lastPage = (countAll -1) / 6 + 1;
        // 전체 글의 갯수를 토대로 마지막 페이지를 구하는 계산식

        int startPageNum = (page -1) / 5 * 5 + 1;
        int endPageNum = startPageNum + 4;
        endPageNum = Math.min(endPageNum, lastPage);
        // 페이지의 시작과 끝을 넣을 변수

        int prevPage = startPageNum - 5;
        int nextPage = endPageNum;
        // 페이지 이동 버튼 만들때 필요한 계산식
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

        for (Review review : reviewList) {
            List<ReviewFile> files = fileMapper.selectFilesById(review.getNo());

            for (ReviewFile file : files) {
                String url = urlPrefix + "prj2/review/" + review.getNo() + "/" + file.getFileName();
                file.setUrl(url);
            }
            review.setFiles(files);
        }
        // 리뷰 리스트에 사진 나오도록 수정중

        map.put("pageInfo", pageInfo);
//        map.put("reviewList", reviewList);

        return map;
    }

    public Review get(Integer no) {
        Review review = mapper.selectById(no);

        List<ReviewFile> reviewFiles = fileMapper.selectFilesByReviewId(no);

        for (ReviewFile reviewFile : reviewFiles) {
            String url = urlPrefix + "prj2/review/" + no + "/" + reviewFile.getFileName();
            reviewFile.setUrl(url);
        }

        review.setFiles(reviewFiles);

        return review;
    }

    public boolean remove(Integer no) {

        // 1. 게시물에 있는 댓글 지우기
        commentMapper.deleteByReviewId(no);

        // 2. 별점 지우기
        starMapper.deleteByReviewId(no);

        // 파일 지우기
        deleteFile(no);

        return mapper.deleteById(no) == 1;
    }

    private void deleteFile(Integer no) {
        // 파일명 조회
        List<ReviewFile> reviewFiles = fileMapper.selectFilesByReviewId(no);

        // s3 버킷의 오브젝트 지우기
        for (ReviewFile file : reviewFiles) {
            String key = "prj2/review/" + no + "/" + file.getFileName();

            DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3.deleteObject(objectRequest);
        }

        // 게시물에 있는 파일의 레코드 지우기
        fileMapper.deleteByReviewId(no);
    }

    public boolean update(Review review, Integer point,
                          List<Integer> removeFileIds,
                          MultipartFile[] files) throws IOException {
        // 파일 수정은 데이터베이스의 값을 변경하는것이 아니라서
        // aws에 있는 파일을 지우고 새로운 파일을 추가하는 방식으로 사용
        // 파일 지우기
        if (removeFileIds != null) {
            for (Integer no : removeFileIds) {
                // s3에서 지우기
                ReviewFile file = fileMapper.selectById(no);

                String key = "prj2/review/" + review.getNo() + "/" + file.getFileName();
                DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build();
                s3.deleteObject(objectRequest);

                // db에서 지우기
                fileMapper.deleteById(no);
            }
        }
        // 파일 추가하기
        if (files != null) {
            for (MultipartFile file : files) {
                // s3에서 추가하기
                upload(file, review.getNo());

                // db에서 추가하기
                fileMapper.insert(review.getNo(), file.getOriginalFilename());
            }
        }
        starMapper.update(point, review.getNo());
        // 별점을 게시물 번호와 함께 mapper로 넘김

        return mapper.update(review) == 1;
    }

    public boolean validate(Review review, MultipartFile[] files) {
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

        if (review.getNo() == null && files == null) {
            return false;
        }


        return true;
    }

    public boolean hasAccess(Integer no, Member login) {
        if (login == null) {
            return false;
        }

        if (!login.isAdmin()) {
            return true;
        }

        Review review = mapper.selectById(no);

        return review.getWriter().equals(login.getId());
    }

    public boolean valiFileNumber(List<Integer> removeFileIds, MultipartFile[] files, Review review) {
        // 파일 갯수 조회해서 removerFileIds 랑 같은 상황에서  files가 null 인경우

        Integer fileCount = mapper.selectFileCount(review.getNo());
        // 레스토랑 파일 이미지 count 조회
        System.out.println("fileCount = " + fileCount);
        System.out.println("removeFileIds = " + removeFileIds);

        if (removeFileIds != null){
            if(removeFileIds.size() == fileCount && files==null){
                return false;
            }
        }

        return true;
    }
}
