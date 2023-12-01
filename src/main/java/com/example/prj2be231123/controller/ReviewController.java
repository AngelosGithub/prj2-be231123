package com.example.prj2be231123.controller;

import com.example.prj2be231123.domain.Member;
import com.example.prj2be231123.domain.Review;
import com.example.prj2be231123.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewController {
    private final ReviewService service;

    @PostMapping("add")
    public ResponseEntity add(@RequestBody Review review,
                              @SessionAttribute(value = "login", required = false) Member login) {
        System.out.println("login = " + login);
        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 맛집정보 받아올때까지 주석
//        if (!service.validate(review)) {
//            return ResponseEntity.badRequest().build();
//        }

        if (service.save(review, login)) {
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
