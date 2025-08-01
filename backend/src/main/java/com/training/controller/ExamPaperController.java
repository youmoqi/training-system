package com.training.controller;

import com.training.dto.ApiResponse;
import com.training.dto.ExamPaperDto;
import com.training.dto.ExamPaperQuestionDto;
import com.training.dto.PurchaseStatusDto;
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

    // 分页获取所有试卷
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<ExamPaperDto>>> getExamPapersWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isOnline) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ExamPaperDto> examPapers = examPaperService.getExamPapersWithPagination(pageable, keyword, isOnline);
            return ResponseEntity.ok(new ApiResponse<>(true, "获取成功", examPapers));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "获取失败: " + e.getMessage(), null));
        }
    }

    // 获取所有试卷
    @GetMapping
    public ResponseEntity<ApiResponse<List<ExamPaperDto>>> getAllExamPapers() {
        try {
            List<ExamPaperDto> examPapers = examPaperService.getAllExamPapers();
            return ResponseEntity.ok(new ApiResponse<>(true, "获取成功", examPapers));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "获取失败: " + e.getMessage(), null));
        }
    }

    // 根据角色获取试卷
    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<List<ExamPaperDto>>> getExamPapersByRole(@PathVariable String role) {
        try {
            List<ExamPaperDto> examPapers = examPaperService.getExamPapersByRole(role);
            return ResponseEntity.ok(new ApiResponse<>(true, "获取成功", examPapers));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "获取失败: " + e.getMessage(), null));
        }
    }

    // 分页获取用户可用试卷
    @GetMapping("/available/page")
    public ResponseEntity<ApiResponse<Page<ExamPaperDto>>> getAvailableExamPapersWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long userId = jwtUtil.getUserIdFromHeader(authorizationHeader);
            Pageable pageable = PageRequest.of(page, size);
            Page<ExamPaperDto> examPapers = examPaperService.getAvailableExamPapersWithPagination(pageable, keyword, userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "获取成功", examPapers));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "获取失败: " + e.getMessage(), null));
        }
    }

    // 分页获取用户可购买的试卷
    @GetMapping("/purchasable/page")
    public ResponseEntity<ApiResponse<Page<ExamPaperDto>>> getPurchasableExamPapersWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long userId = jwtUtil.getUserIdFromHeader(authorizationHeader);
            Pageable pageable = PageRequest.of(page, size);
            Page<ExamPaperDto> examPapers = examPaperService.getPurchasableExamPapersWithPagination(pageable, keyword, userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "获取成功", examPapers));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "获取失败: " + e.getMessage(), null));
        }
    }

    // 获取试卷详情
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExamPaperDto>> getExamPaperById(@PathVariable Long id) {
        try {
            ExamPaperDto examPaper = examPaperService.getExamPaperById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "获取成功", examPaper));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "获取失败: " + e.getMessage(), null));
        }
    }

    // 获取试卷题目列表
    @GetMapping("/{id}/questions")
    public ResponseEntity<ApiResponse<List<ExamPaperQuestionDto>>> getExamPaperQuestions(@PathVariable Long id) {
        try {
            List<ExamPaperQuestionDto> questions = examPaperService.getExamPaperQuestions(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "获取成功", questions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "获取失败: " + e.getMessage(), null));
        }
    }

    // 创建试卷
    @PostMapping
    public ResponseEntity<ApiResponse<ExamPaperDto>> createExamPaper(@RequestBody ExamPaperDto examPaperDto) {
        try {
            ExamPaperDto createdExamPaper = examPaperService.createExamPaper(examPaperDto);
            return ResponseEntity.ok(new ApiResponse<>(true, "创建成功", createdExamPaper));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "创建失败: " + e.getMessage(), null));
        }
    }

    // 更新试卷
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExamPaperDto>> updateExamPaper(@PathVariable Long id, @RequestBody ExamPaperDto examPaperDto) {
        try {
            ExamPaperDto updatedExamPaper = examPaperService.updateExamPaper(id, examPaperDto);
            return ResponseEntity.ok(new ApiResponse<>(true, "更新成功", updatedExamPaper));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "更新失败: " + e.getMessage(), null));
        }
    }

    // 删除试卷
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExamPaper(@PathVariable Long id) {
        try {
            examPaperService.deleteExamPaper(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "删除失败: " + e.getMessage(), null));
        }
    }

    // 添加题目到试卷
    @PostMapping("/{examPaperId}/questions/{questionId}")
    public ResponseEntity<ApiResponse<Void>> addQuestionToExamPaper(
            @PathVariable Long examPaperId,
            @PathVariable Long questionId,
            @RequestParam Integer score,
            @RequestParam Integer order) {
        try {
            examPaperService.addQuestionToExamPaper(examPaperId, questionId, score, order);
            return ResponseEntity.ok(new ApiResponse<>(true, "添加成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "添加失败: " + e.getMessage(), null));
        }
    }

    // 批量添加题目到试卷
    @PostMapping("/{examPaperId}/questions/batch")
    public ResponseEntity<ApiResponse<Void>> addQuestionsToExamPaper(
            @PathVariable Long examPaperId,
            @RequestBody List<Long> questionIds,
            @RequestParam(defaultValue = "5") Integer defaultScore) {
        try {
            examPaperService.addQuestionsToExamPaper(examPaperId, questionIds, defaultScore);
            return ResponseEntity.ok(new ApiResponse<>(true, "批量添加成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "批量添加失败: " + e.getMessage(), null));
        }
    }

    // 从试卷移除题目
    @DeleteMapping("/{examPaperId}/questions/{questionId}")
    public ResponseEntity<ApiResponse<Void>> removeQuestionFromExamPaper(
            @PathVariable Long examPaperId,
            @PathVariable Long questionId) {
        try {
            examPaperService.removeQuestionFromExamPaper(examPaperId, questionId);
            return ResponseEntity.ok(new ApiResponse<>(true, "移除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "移除失败: " + e.getMessage(), null));
        }
    }

    // 更新题目分值
    @PutMapping("/{examPaperId}/questions/{questionId}/score")
    public ResponseEntity<ApiResponse<Void>> updateQuestionScore(
            @PathVariable Long examPaperId,
            @PathVariable Long questionId,
            @RequestBody Integer score) {
        try {
            examPaperService.updateQuestionScore(examPaperId, questionId, score);
            return ResponseEntity.ok(new ApiResponse<>(true, "更新成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "更新失败: " + e.getMessage(), null));
        }
    }

    // 更新题目顺序
    @PutMapping("/{examPaperId}/questions/order")
    public ResponseEntity<ApiResponse<Void>> updateQuestionOrder(
            @PathVariable Long examPaperId,
            @RequestBody List<Long> questionIds) {
        try {
            examPaperService.updateQuestionOrder(examPaperId, questionIds);
            return ResponseEntity.ok(new ApiResponse<>(true, "更新成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "更新失败: " + e.getMessage(), null));
        }
    }

    // 检查购买状态
    @GetMapping("/{examPaperId}/purchase-status")
    public ResponseEntity<ApiResponse<PurchaseStatusDto>> checkPurchaseStatus(
            @PathVariable Long examPaperId,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long userId = jwtUtil.getUserIdFromHeader(authorizationHeader);
            PurchaseStatusDto status = examPaperService.checkPurchaseStatus(userId, examPaperId);
            return ResponseEntity.ok(new ApiResponse<>(true, "获取成功", status));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "获取失败: " + e.getMessage(), null));
        }
    }

    // 用户购买试卷
    @PostMapping("/{examPaperId}/purchase")
    public ResponseEntity<ApiResponse<Void>> purchaseExamPaper(
            @PathVariable Long examPaperId,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long userId = jwtUtil.getUserIdFromHeader(authorizationHeader);
            examPaperService.purchaseExamPaper(userId, examPaperId);
            return ResponseEntity.ok(new ApiResponse<>(true, "购买成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "购买失败: " + e.getMessage(), null));
        }
    }
}
