package com.csv.controller;

import com.csv.entity.TranslationEntity;
import com.csv.service.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/translations")
public class TranslationController {

    private final TranslationService translationService;

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(value = "size", defaultValue = "10") Integer size,
                                  @RequestParam(value = "page", defaultValue = "0") Integer page) {
        Page<TranslationEntity> translationPage = translationService.getAll(page, size);
        return ResponseEntity.ok(translationPage);
    }
}
