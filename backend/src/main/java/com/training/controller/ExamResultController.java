package com.training.controller;

import com.training.dto.ApiResponse;
import com.training.dto.ExamResultDto;
import com.training.service.ExamResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
/**
 * @author YIZ
 */
@RestController
@RequestMapping("/api/exam-results")
@CrossOrigin(origins = "*")
public class ExamResultController {

    @Autowired
    private ExamResultService examResultService;

    // 获取考试结果详情
    @GetMapping("/{resultId}")
    public ResponseEntity<ApiResponse<ExamResultDto>> getExamResultDetail(@PathVariable Long resultId) {
        try {
            ExamResultDto result = examResultService.getExamResultById(resultId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取考试结果详情失败: " + e.getMessage()));
        }
    }

    // 获取用户考试历史
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<ExamResultDto>>> getUserExamHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isPassed) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(examResultService.getUserExamHistory(userId, pageable, keyword, isPassed));
    }

    @GetMapping("/export-exam-data")
    public ResponseEntity<InputStreamResource> exportExamData(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam List<Long> exportRoleIds) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endDate, formatter);

        // 生成 Excel 文件
        ByteArrayInputStream inputStream = examResultService.exportExamDataToExcel(start, end, exportRoleIds);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=exam_results.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(inputStream));
    }
}
