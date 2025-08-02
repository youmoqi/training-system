package com.training.controller;

import com.training.dto.*;
import com.training.service.ExamPaperService;
import com.training.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam-papers")
@CrossOrigin(origins = "*")
public class ExamPaperController {

    @Autowired
    private ExamPaperService examPaperService;
    @Autowired
    private JwtUtil jwtUtil;

    // 分页查询试卷
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<ExamPaperDto>>> getExamPapersWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isOnline) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ExamPaperDto> examPapers = examPaperService.getExamPapersWithPagination(pageable, keyword, isOnline);
        return ResponseEntity.ok(ApiResponse.success(examPapers));
    }

    // 获取所有试卷
    @GetMapping
    public ResponseEntity<ApiResponse<List<ExamPaperDto>>> getAllExamPapers() {
        List<ExamPaperDto> examPapers = examPaperService.getAllExamPapers();
        return ResponseEntity.ok(ApiResponse.success(examPapers));
    }

    // 根据角色获取试卷
    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<List<ExamPaperDto>>> getExamPapersByRole(@PathVariable String role) {
        List<ExamPaperDto> examPapers = examPaperService.getExamPapersByRole(role);
        return ResponseEntity.ok(ApiResponse.success(examPapers));
    }

    // 分页查询用户已购买的试卷
    @GetMapping("/available/page")
    public ResponseEntity<ApiResponse<Page<ExamPaperDto>>> getAvailableExamPapersWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam Long userId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ExamPaperDto> examPapers = examPaperService.getAvailableExamPapersWithPagination(pageable, keyword, userId);
        return ResponseEntity.ok(ApiResponse.success(examPapers));
    }

    // 分页查询用户可购买的试卷
    @GetMapping("/purchasable/page")
    public ResponseEntity<ApiResponse<Page<ExamPaperDto>>> getPurchasableExamPapersWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam Long userId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ExamPaperDto> examPapers = examPaperService.getPurchasableExamPapersWithPagination(pageable, keyword, userId);
        return ResponseEntity.ok(ApiResponse.success(examPapers));
    }

    // 获取试卷详情
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExamPaperDto>> getExamPaperById(@PathVariable Long id) {
        ExamPaperDto examPaper = examPaperService.getExamPaperById(id);
        return ResponseEntity.ok(ApiResponse.success(examPaper));
    }

    // 获取试卷题目列表
    @GetMapping("/{id}/questions")
    public ResponseEntity<ApiResponse<List<ExamPaperQuestionDto>>> getExamPaperQuestions(@PathVariable Long id) {
        List<ExamPaperQuestionDto> questions = examPaperService.getExamPaperQuestions(id);
        return ResponseEntity.ok(ApiResponse.success(questions));
    }

    // 删除试卷
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteExamPaper(@PathVariable Long id) {
        examPaperService.deleteExamPaper(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功"));
    }

    // 添加题目到试卷
    @PostMapping("/{examPaperId}/questions/{questionId}")
    public ResponseEntity<ApiResponse<String>> addQuestionToExamPaper(
            @PathVariable Long examPaperId,
            @PathVariable Long questionId,
            @RequestParam(defaultValue = "5") Integer score,
            @RequestParam(required = false) Integer order) {

        int questionOrder = order != null ? order : examPaperService.getExamPaperQuestions(examPaperId).size() + 1;
        examPaperService.addQuestionToExamPaper(examPaperId, questionId, score, questionOrder);
        return ResponseEntity.ok(ApiResponse.success("添加成功"));
    }

    // 批量添加题目到试卷
    @PostMapping("/{examPaperId}/questions/batch")
    public ResponseEntity<ApiResponse<String>> addQuestionsToExamPaper(
            @PathVariable Long examPaperId,
            @RequestBody List<Long> questionIds,
            @RequestParam(defaultValue = "5") Integer defaultScore) {

        examPaperService.addQuestionsToExamPaper(examPaperId, questionIds, defaultScore);
        return ResponseEntity.ok(ApiResponse.success("批量添加成功"));
    }

    // 从试卷移除题目
    @DeleteMapping("/{examPaperId}/questions/{questionId}")
    public ResponseEntity<ApiResponse<String>> removeQuestionFromExamPaper(
            @PathVariable Long examPaperId,
            @PathVariable Long questionId) {

        examPaperService.removeQuestionFromExamPaper(examPaperId, questionId);
        return ResponseEntity.ok(ApiResponse.success("移除成功"));
    }

    // 更新题目分值
    @PutMapping("/{examPaperId}/questions/{questionId}/score")
    public ResponseEntity<ApiResponse<String>> updateQuestionScore(
            @PathVariable Long examPaperId,
            @PathVariable Long questionId,
            @RequestParam Integer score) {
        examPaperService.updateQuestionScore(examPaperId, questionId, score);
        return ResponseEntity.ok(ApiResponse.success("更新成功"));
    }

    // 更新题目顺序
    @PutMapping("/{examPaperId}/questions/order")
    public ResponseEntity<ApiResponse<String>> updateQuestionOrder(
            @PathVariable Long examPaperId,
            @RequestBody List<Long> questionIds) {
        examPaperService.updateQuestionOrder(examPaperId, questionIds);
        return ResponseEntity.ok(ApiResponse.success("更新成功"));
    }

    // 检查用户购买状态
    @GetMapping("/{examPaperId}/purchase-status")
    public ResponseEntity<ApiResponse<PurchaseStatusDto>> checkPurchaseStatus(
            @PathVariable Long examPaperId,
            @RequestParam Long userId) {

        PurchaseStatusDto status = examPaperService.checkPurchaseStatus(userId, examPaperId);
        return ResponseEntity.ok(ApiResponse.success(status));
    }

    // 用户购买试卷
    @PostMapping("/{examPaperId}/purchase")
    public ResponseEntity<ApiResponse<String>> purchaseExamPaper(@PathVariable Long examPaperId, @RequestHeader("Authorization") String authorization) {
        Long userId = jwtUtil.getUserIdFromHeader(authorization);
        examPaperService.purchaseExamPaper(userId, examPaperId);
        return ResponseEntity.ok(ApiResponse.success("购买成功"));
    }

    // 提交试卷考试
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<ExamPaperResultDto>> submitExamPaper(@RequestBody ExamPaperSubmitDto submitDto, @RequestHeader("Authorization") String authorization) {
        Long userId = jwtUtil.getUserIdFromHeader(authorization);
        ApiResponse<ExamPaperResultDto> response = examPaperService.submitExamPaper(userId, submitDto.getExamPaperId(), submitDto.getAnswers(), submitDto.getTimeTaken());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ApiResponse<ExamPaperDto> createExamPaper(@RequestBody ExamPaperDto examPaperDto) {
        return examPaperService.createExamPaper(examPaperDto);
    }

    @PutMapping("/{id}")
    public ApiResponse<ExamPaperDto> updateExamPaper(@PathVariable Long id,
                                                     @RequestBody ExamPaperDto examPaperDto) {
        return examPaperService.updateExamPaper(id, examPaperDto);
    }

    @PostMapping("/{id}/auto-generate")
    public ApiResponse<String> autoGenerateExamPaper(@PathVariable Long id) {
        return examPaperService.autoGenerateExamPaper(id);
    }

    @GetMapping("/category/{category}")
    public ApiResponse<List<ExamPaperDto>> getExamPapersByCategory(@PathVariable String category) {
        return examPaperService.getExamPapersByCategory(category);
    }

    @GetMapping("/{examPaperId}/can-retake/{userId}")
    public ApiResponse<Boolean> canUserRetakeExam(@PathVariable Long examPaperId, @PathVariable Long userId) {
        return examPaperService.canUserRetakeExam(userId, examPaperId);
    }

    @GetMapping("/{examPaperId}/next-attempt/{userId}")
    public ApiResponse<Integer> getUserNextAttemptNumber(@PathVariable Long examPaperId, @PathVariable Long userId) {
        return examPaperService.getUserNextAttemptNumber(userId, examPaperId);
    }

    // 自动组卷规则管理
    @GetMapping("/{examPaperId}/auto-rules")
    public ApiResponse<List<ExamPaperAutoRuleDto>> getAutoRules(@PathVariable Long examPaperId) {
        return examPaperService.getAutoRules(examPaperId);
    }

    @PostMapping("/{examPaperId}/auto-rules")
    public ApiResponse<ExamPaperAutoRuleDto> createAutoRule(@PathVariable Long examPaperId,
                                                            @RequestBody ExamPaperAutoRuleDto ruleDto) {
        return examPaperService.createAutoRule(examPaperId, ruleDto);
    }

    @PutMapping("/{examPaperId}/auto-rules/{ruleId}")
    public ApiResponse<ExamPaperAutoRuleDto> updateAutoRule(@PathVariable Long examPaperId,
                                                            @PathVariable Long ruleId,
                                                            @RequestBody ExamPaperAutoRuleDto ruleDto) {
        return examPaperService.updateAutoRule(examPaperId, ruleId, ruleDto);
    }

    @DeleteMapping("/{examPaperId}/auto-rules/{ruleId}")
    public ApiResponse<String> deleteAutoRule(@PathVariable Long examPaperId, @PathVariable Long ruleId) {
        return examPaperService.deleteAutoRule(examPaperId, ruleId);
    }
}
