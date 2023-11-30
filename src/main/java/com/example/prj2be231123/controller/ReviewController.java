package com.example.prj2be231123.controller;

import com.example.prj2be231123.domain.Member;
import com.example.prj2be231123.domain.Review;
import com.example.prj2be231123.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewController {
    private final ReviewService service;

    @PostMapping("add")
    public ResponseEntity add(@RequestBody Review review, HttpSession session) {
        Object login = session.getAttribute("login");

        Member loginMember = (Member) login;
        // 맛집정보 받아올때까지 주석
//        if (!service.validate(review)) {
//            return ResponseEntity.badRequest().build();
//        }

        if (service.save(review)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("list")
    public List<Review> list() {
        return service.list();
    }

    @GetMapping("no/{no}")
    public Review get(@PathVariable Integer no) {
        return service.get(no);
    }

    @DeleteMapping("remove/{no}")
    public ResponseEntity remove(@PathVariable Integer no) {
        if (service.remove(no)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("edit")
    public ResponseEntity edit(@RequestBody Review review) {
        if (service.validate(review)) {
            if (service.update(review)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.internalServerError().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
