package com.training.controller;

import com.training.dto.ApiResponse;
import com.training.dto.QuestionBankResultDto;
import com.training.service.QuestionBankResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question-bank-results")
@CrossOrigin(origins = "*")
public class QuestionBankResultController {

    @Autowired
    private QuestionBankResultService questionBankResultService;

    // 获取用户题库练习历史
    @GetMapping("/history/{userId}")
    public ResponseEntity<ApiResponse<Page<QuestionBankResultDto>>> getUserQuestionBankHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isPassed) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(questionBankResultService.getUserQuestionBankHistory(userId, pageable, keyword, isPassed));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取题库练习历史失败: " + e.getMessage()));
        }
    }

    // 获取题库练习结果详情
    @GetMapping("/{resultId}")
    public ResponseEntity<ApiResponse<QuestionBankResultDto>> getQuestionBankResultDetail(@PathVariable Long resultId) {
        try {
            QuestionBankResultDto result = questionBankResultService.getQuestionBankResultById(resultId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取练习结果详情失败: " + e.getMessage()));
        }
    }
} 