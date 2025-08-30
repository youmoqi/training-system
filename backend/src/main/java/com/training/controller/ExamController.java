package com.training.controller;

import com.training.dto.*;
import com.training.service.ExamService;
import com.training.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 14798
 */
@RestController
@RequestMapping("/api/exams")
@CrossOrigin(origins = "*")
public class ExamController {

    @Autowired
    private ExamService examService;
    @Autowired
    private JwtUtil jwtUtil;

    // 分页查询试卷
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<ExamDto>>> getExamsWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isOnline) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ExamDto> exams = examService.getExamsWithPagination(pageable, keyword, isOnline);
        return ResponseEntity.ok(ApiResponse.success(exams));
    }

    // 获取所有试卷
    @GetMapping
    public ResponseEntity<ApiResponse<List<ExamDto>>> getAllExams() {
        List<ExamDto> exams = examService.getAllExams();
        return ResponseEntity.ok(ApiResponse.success(exams));
    }

    // 分页查询用户已购买的试卷
    @GetMapping("/available/page")
    public ResponseEntity<ApiResponse<Page<ExamDto>>> getAvailableExamsWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam Long userId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ExamDto> exams = examService.getAvailableExamsWithPagination(pageable, keyword, userId);
        return ResponseEntity.ok(ApiResponse.success(exams));
    }

    // 分页查询用户可购买的试卷
    @GetMapping("/purchasable/page")
    public ResponseEntity<ApiResponse<Page<ExamDto>>> getPurchasableExamsWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam Long userId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ExamDto> exams = examService.getPurchasableExamsWithPagination(pageable, keyword, userId);
        return ResponseEntity.ok(ApiResponse.success(exams));
    }

    // 获取试卷详情
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExamDto>> getExamById(@PathVariable Long id) {
        ExamDto exam = examService.getExamById(id);
        return ResponseEntity.ok(ApiResponse.success(exam));
    }

    // 获取试卷题目列表
    @GetMapping("/{id}/questions")
    public ResponseEntity<ApiResponse<List<ExamQuestionDto>>> getExamQuestions(@PathVariable Long id) {
        List<ExamQuestionDto> questions = examService.getExamQuestions(id);
        return ResponseEntity.ok(ApiResponse.success(questions));
    }

    // 删除试卷
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功"));
    }

    // 添加题目到试卷
    @PostMapping("/{examId}/questions/{questionId}")
    public ResponseEntity<ApiResponse<String>> addQuestionToExam(
            @PathVariable Long examId,
            @PathVariable Long questionId,
            @RequestParam(defaultValue = "5") Integer score,
            @RequestParam(required = false) Integer order) {

        int questionOrder = order != null ? order : examService.getExamQuestions(examId).size() + 1;
        examService.addQuestionToExam(examId, questionId, score, questionOrder);
        return ResponseEntity.ok(ApiResponse.success("添加成功"));
    }

    // 批量添加题目到试卷
    @PostMapping("/{examId}/questions/batch")
    public ResponseEntity<ApiResponse<String>> addQuestionsToExam(
            @PathVariable Long examId,
            @RequestBody List<Long> questionIds,
            @RequestParam(defaultValue = "5") Integer defaultScore) {

        examService.addQuestionsToExam(examId, questionIds, defaultScore);
        return ResponseEntity.ok(ApiResponse.success("批量添加成功"));
    }

    // 从试卷移除题目
    @DeleteMapping("/{examId}/questions/{questionId}")
    public ResponseEntity<ApiResponse<String>> removeQuestionFromExam(
            @PathVariable Long examId,
            @PathVariable Long questionId) {

        examService.removeQuestionFromExam(examId, questionId);
        return ResponseEntity.ok(ApiResponse.success("移除成功"));
    }

    // 更新题目分值
    @PutMapping("/{examId}/questions/{questionId}/score")
    public ResponseEntity<ApiResponse<String>> updateQuestionScore(
            @PathVariable Long examId,
            @PathVariable Long questionId,
            @RequestParam Integer score) {
        examService.updateQuestionScore(examId, questionId, score);
        return ResponseEntity.ok(ApiResponse.success("更新成功"));
    }

    // 更新题目顺序
    @PutMapping("/{examId}/questions/order")
    public ResponseEntity<ApiResponse<String>> updateQuestionOrder(
            @PathVariable Long examId,
            @RequestBody List<Long> questionIds) {
        examService.updateQuestionOrder(examId, questionIds);
        return ResponseEntity.ok(ApiResponse.success("更新成功"));
    }

    // 检查用户购买状态
    @GetMapping("/{examId}/purchase-status")
    public ResponseEntity<ApiResponse<PurchaseStatusDto>> checkPurchaseStatus(
            @PathVariable Long examId,
            @RequestParam Long userId) {

        PurchaseStatusDto status = examService.checkPurchaseStatus(userId, examId);
        return ResponseEntity.ok(ApiResponse.success(status));
    }

    // 用户购买试卷
    @PostMapping("/{examId}/purchase")
    public ResponseEntity<ApiResponse<String>> purchaseExam(@PathVariable Long examId, @RequestHeader("Authorization") String authorization) {
        Long userId = jwtUtil.getUserIdFromHeader(authorization);
        examService.purchaseExam(userId, examId);
        return ResponseEntity.ok(ApiResponse.success("购买成功"));
    }

    // 提交试卷考试
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<ExamResultDto>> submitExam(@RequestBody ExamSubmitDto submitDto, @RequestHeader("Authorization") String authorization) {
        Long userId = jwtUtil.getUserIdFromHeader(authorization);
        ApiResponse<ExamResultDto> response = examService.submitExam(userId, submitDto.getExamId(), submitDto.getAnswers(), submitDto.getTimeTaken());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ApiResponse<ExamDto> createExam(@RequestBody ExamDto examDto) {
        return examService.createExam(examDto);
    }

    @PutMapping("/{id}")
    public ApiResponse<ExamDto> updateExam(@PathVariable Long id,
                                                @RequestBody ExamDto examDto) {
        return examService.updateExam(id, examDto);
    }

    @PostMapping("/{id}/auto-generate")
    public ApiResponse<String> autoGenerateExam(@PathVariable Long id) {
        return examService.autoGenerateExam(id);
    }

    @GetMapping("/category/{category}")
    public ApiResponse<List<ExamDto>> getExamsByCategory(@PathVariable String category) {
        return examService.getExamsByCategory(category);
    }

    @GetMapping("/{examId}/can-retake/{userId}")
    public ApiResponse<Boolean> canUserRetakeExam(@PathVariable Long examId, @PathVariable Long userId) {
        return examService.canUserRetakeExam(userId, examId);
    }

    @GetMapping("/{examId}/next-attempt/{userId}")
    public ApiResponse<Integer> getUserNextAttemptNumber(@PathVariable Long examId, @PathVariable Long userId) {
        return examService.getUserNextAttemptNumber(userId, examId);
    }

    // 自动组卷规则管理
    @GetMapping("/{examId}/auto-rules")
    public ApiResponse<List<ExamAutoRuleDto>> getAutoRules(@PathVariable Long examId) {
        return examService.getAutoRules(examId);
    }

    @PostMapping("/{examId}/auto-rules")
    public ApiResponse<ExamAutoRuleDto> createAutoRule(@PathVariable Long examId,
                                                       @RequestBody ExamAutoRuleDto ruleDto) {
        return examService.createAutoRule(examId, ruleDto);
    }

    @PutMapping("/{examId}/auto-rules/{ruleId}")
    public ApiResponse<ExamAutoRuleDto> updateAutoRule(@PathVariable Long examId,
                                                       @PathVariable Long ruleId,
                                                       @RequestBody ExamAutoRuleDto ruleDto) {
        return examService.updateAutoRule(examId, ruleId, ruleDto);
    }

    @DeleteMapping("/{examId}/auto-rules/{ruleId}")
    public ApiResponse<String> deleteAutoRule(@PathVariable Long examId, @PathVariable Long ruleId) {
        return examService.deleteAutoRule(examId, ruleId);
    }
}
