package com.training.controller;

import com.training.dto.ApiResponse;
import com.training.dto.ExamResultDto;
import com.training.entity.ExamResult;
import com.training.service.ExamResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam-results")
@CrossOrigin(origins = "*")
public class ExamResultController {

    @Autowired
    private ExamResultService examResultService;

    @PostMapping
    public ResponseEntity<ApiResponse<ExamResultDto>> saveExamResult(
            @RequestBody ExamResultDto examResultDto,
            @RequestParam Long userId) {
        try {
            examResultService.saveExamResult(examResultDto, userId);
            return ResponseEntity.ok(ApiResponse.success("保存考试结果成功", examResultDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{questionBankId}")
    public ResponseEntity<ApiResponse<ExamResultDto>> getLatestExamResult(
            @PathVariable Long questionBankId,
            @RequestParam Long userId) {
        try {
            ExamResultDto examResult = examResultService.getLatestExamResult(userId, questionBankId);
            return ResponseEntity.ok(ApiResponse.success("获取考试结果成功", examResult));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ExamResultDto>>> getUserExamResults(@PathVariable Long userId) {
        try {
            List<ExamResultDto> examResults = examResultService.getUserExamResults(userId);
            return ResponseEntity.ok(ApiResponse.success("获取用户考试结果成功", examResults));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 