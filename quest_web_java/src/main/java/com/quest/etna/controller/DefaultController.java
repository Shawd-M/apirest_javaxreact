package com.quest.etna.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultController {
    @GetMapping("/testSuccess")
    public ResponseEntity<String> testSuccess() {
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    @GetMapping("/testNotFound")
    public ResponseEntity<String> testNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found");
    }

    @GetMapping("/testError")
    public ResponseEntity<String> testError() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
    }
}

