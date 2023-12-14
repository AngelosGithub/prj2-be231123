package com.example.prj2be231123.controller;

import com.example.prj2be231123.domain.Member;
import com.example.prj2be231123.domain.Review;
import com.example.prj2be231123.domain.Star;
import com.example.prj2be231123.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewController {
    private final ReviewService service;

    @PostMapping("add")
    public ResponseEntity add(Review review,
                              @RequestParam(value = "no", required = false) Integer no,
                              @RequestParam(value = "point", required = false) Integer point,
                              @RequestParam(value = "uploadFiles[]", required = false) MultipartFile[] files,
                              @SessionAttribute(value = "login", required = false) Member login) throws IOException {
//        파일 요청을 받는지 확인
//        if (files != null) {
//            for (int i = 0; i < files.length; i++) {
//                System.out.println("file = " + files[i].getOriginalFilename());
//                System.out.println("file = " + files[i].getSize());
//            }
//        }

        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 맛집정보 받아올때까지 주석
//        if (!service.validate(review)) {
//            return ResponseEntity.badRequest().build();
//        }

        if (service.save(review, login, files, no, point)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    // /api/review/list?p=1
    // /api/review/list?k=words
    @GetMapping("list")
    public Map<String, Object> list(@RequestParam(value = "p", defaultValue = "1") Integer page,
                                    @RequestParam(value = "k", defaultValue = "") String keyword,
                                    @RequestParam(value = "restaurantNo", defaultValue = "") Integer no) {
        System.out.println("no = " + no);
        // 페이지를 나누기 위한 프로퍼티 입력
        // List<Review> 리스트로 데이터를 넘겼는데 Map으로 변경
        return service.list(page, keyword, no);
    }

    @GetMapping("no/{no}")
    public Review get(@PathVariable Integer no) {
        return service.get(no);
    }

    @DeleteMapping("remove/{no}")
    public ResponseEntity remove(@PathVariable Integer no,
                                 @SessionAttribute(value = "login", required = false) Member login) {
        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!service.hasAccess(no, login)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (service.remove(no)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("edit")
    public ResponseEntity edit(Review review,
                               @RequestParam(value = "point", required = false) Integer point,
                               @RequestParam(value = "removeFileIds[]", required = false) List<Integer> removeFileIds,
                               @RequestParam(value = "uploadFiles[]", required = false) MultipartFile[] uploadFiles,
                               @SessionAttribute(value = "login", required = false) Member login) throws IOException {

        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!service.hasAccess(review.getNo(), login)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (service.validate(review)) {
            if (service.update(review, point, removeFileIds, uploadFiles)) {
                // 별점(point)를 @RequestParam 어노테이션으로 받아서 서비스로 넘김
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.internalServerError().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
