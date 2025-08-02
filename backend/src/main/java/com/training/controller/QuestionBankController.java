package com.training.controller;

import com.training.dto.ApiResponse;
import com.training.dto.UserQuestionBankDto;
import com.training.entity.QuestionBank;
import com.training.entity.User;
import com.training.service.QuestionBankService;
import com.training.service.UserService;
import com.training.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/question-banks")
@CrossOrigin(origins = "*")
public class QuestionBankController {

    @Autowired
    private QuestionBankService questionBankService;

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    // 管理员分页获取题库列表（不包含过滤逻辑）
    @GetMapping("/admin/page")
    public ResponseEntity<ApiResponse<Page<QuestionBank>>> getQuestionBanksForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<QuestionBank> questionBanks = questionBankService.findQuestionBanksForAdmin(pageable, keyword);
            return ResponseEntity.ok(ApiResponse.success("获取题库列表成功", questionBanks));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取题库列表失败: " + e.getMessage()));
        }
    }

    // 学员获取我的题库（已购题库）
    @GetMapping("/student/my-question-banks")
    public ResponseEntity<ApiResponse<Page<UserQuestionBankDto>>> getMyQuestionBanks(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<UserQuestionBankDto> userQuestionBanks = questionBankService.findMyQuestionBanks(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success("获取我的题库成功", userQuestionBanks));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取我的题库失败: " + e.getMessage()));
        }
    }

    // 学员获取可购买的题库（未购题库）
    @GetMapping("/student/available-question-banks")
    public ResponseEntity<ApiResponse<Page<QuestionBank>>> getAvailableQuestionBanks(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String userRole) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<QuestionBank> questionBanks = questionBankService.findAvailableQuestionBanks(userId, userRole, pageable);
            return ResponseEntity.ok(ApiResponse.success("获取可购买题库成功", questionBanks));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取可购买题库失败: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<QuestionBank>>> getAllQuestionBanks() {
        List<QuestionBank> questionBanks = questionBankService.findAllQuestionBanks();
        return ResponseEntity.ok(ApiResponse.success("获取题库列表成功", questionBanks));
    }

    @GetMapping("/online")
    public ResponseEntity<ApiResponse<List<QuestionBank>>> getOnlineQuestionBanks(@RequestParam(required = false) String userRole) {
        List<QuestionBank> questionBanks;
        if (userRole != null && !userRole.isEmpty()) {
            questionBanks = questionBankService.findQuestionBanksByRole(userRole);
        } else {
            questionBanks = questionBankService.findOnlineQuestionBanks();
        }
        return ResponseEntity.ok(ApiResponse.success("获取在线题库成功", questionBanks));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionBank>> getQuestionBankById(@PathVariable Long id) {
        return questionBankService.findById(id)
                .map(questionBank -> ResponseEntity.ok(ApiResponse.success("获取题库成功", questionBank)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<QuestionBank>> createQuestionBank(@RequestBody QuestionBank questionBank) {
        try {
            QuestionBank savedQuestionBank = questionBankService.createQuestionBank(questionBank);
            return ResponseEntity.ok(ApiResponse.success("创建题库成功", savedQuestionBank));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionBank>> updateQuestionBank(@PathVariable Long id, @RequestBody QuestionBank questionBank) {
        try {
            questionBank.setId(id);
            QuestionBank updatedQuestionBank = questionBankService.updateQuestionBank(questionBank);
            return ResponseEntity.ok(ApiResponse.success("更新题库成功", updatedQuestionBank));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestionBank(@PathVariable Long id) {
        try {
            questionBankService.deleteQuestionBank(id);
            return ResponseEntity.ok(ApiResponse.success("删除题库成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 当前用户购买题库
     *
     * @param id
     * @param token
     * @return
     */
    @PostMapping("/{id}/purchase")
    public ResponseEntity<ApiResponse<Void>> purchaseQuestionBank(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.getUserIdFromHeader(token);
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            QuestionBank questionBank = questionBankService.findById(id)
                    .orElseThrow(() -> new RuntimeException("题库不存在"));
            questionBankService.purchaseQuestionBank(user, questionBank);
            return ResponseEntity.ok(ApiResponse.success("购买题库成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<UserQuestionBankDto>>> getUserQuestionBanks(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            List<UserQuestionBankDto> userQuestionBanks = questionBankService.getUserQuestionBankDtos(user);
            return ResponseEntity.ok(ApiResponse.success("获取用户题库成功", userQuestionBanks));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取用户已购买的题库列表
     *
     * @param userId
     * @return
     */
    @GetMapping("/purchased")
    public ResponseEntity<ApiResponse<List<QuestionBank>>> getPurchasedQuestionBanks(@RequestParam Long userId) {
        try {
            User user = userService.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.ok(ApiResponse.success("用户不存在", new java.util.ArrayList<>()));
            }
            List<QuestionBank> purchasedQuestionBanks = questionBankService.getPurchasedQuestionBanks(user);
            return ResponseEntity.ok(ApiResponse.success("获取已购买题库成功", purchasedQuestionBanks));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.success("发生异常，返回空列表", new java.util.ArrayList<>()));
        }
    }
}
