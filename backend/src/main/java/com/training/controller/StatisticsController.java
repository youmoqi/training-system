package com.training.controller;

import com.training.dto.ApiResponse;
import com.training.dto.QuestionBankStatisticsDto;
import com.training.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/question-bank/{questionBankId}")
    public ResponseEntity<ApiResponse<QuestionBankStatisticsDto>> getQuestionBankStatistics(@PathVariable Long questionBankId) {
        try {
            QuestionBankStatisticsDto statistics = statisticsService.getQuestionBankStatistics(questionBankId);
            return ResponseEntity.ok(ApiResponse.success("获取题库统计成功", statistics));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/system")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemStatistics() {
        try {
            Map<String, Object> statistics = statisticsService.getSystemStatistics();
            return ResponseEntity.ok(ApiResponse.success("获取系统统计成功", statistics));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 