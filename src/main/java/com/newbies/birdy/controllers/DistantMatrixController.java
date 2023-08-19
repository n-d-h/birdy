package com.newbies.birdy.controllers;

import com.newbies.birdy.services.GoogleDistantMatrixService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/distant-matrix")
public class DistantMatrixController {

    private final GoogleDistantMatrixService googleDistantMatrixService;

    @GetMapping("/get")
    public ResponseEntity<?> getDistantMatrix(@RequestParam(name = "origin") String origin,
                                              @RequestParam(name = "destination")String destination) throws Exception {
        return ResponseEntity.ok(googleDistantMatrixService.getData(origin, destination));
    }
}
