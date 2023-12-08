package com.example.prj2be231123.controller;

import com.example.prj2be231123.domain.Member;
import com.example.prj2be231123.domain.Star;
import com.example.prj2be231123.service.StarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/star")
public class StarController {
    private final StarService service;

    @PostMapping
    public void star(@RequestBody Star star) {
        service.update(star);
    }
}
