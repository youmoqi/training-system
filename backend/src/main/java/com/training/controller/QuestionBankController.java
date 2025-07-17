package com.training.controller;

import com.training.dto.ApiResponse;
import com.training.dto.UserQuestionBankDto;
import com.training.entity.QuestionBank;
import com.training.entity.User;
import com.training.entity.UserQuestionBank;
import com.training.service.QuestionBankService;
import com.training.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/{id}/purchase")
    public ResponseEntity<ApiResponse<Void>> purchaseQuestionBank(@PathVariable Long id, @RequestParam Long userId) {
        try {
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

    @GetMapping("/purchased")
    public ResponseEntity<ApiResponse<List<QuestionBank>>> getPurchasedQuestionBanks(@RequestParam Long userId) {
        try {
            System.out.println("getPurchasedQuestionBanks called, userId=" + userId);
            User user = userService.findById(userId).orElse(null);
            if (user == null) {
                System.out.println("用户不存在，返回空列表");
                return ResponseEntity.ok(ApiResponse.success("用户不存在", new java.util.ArrayList<>()));
            }
            List<QuestionBank> purchasedQuestionBanks = questionBankService.getPurchasedQuestionBanks(user);
            System.out.println("purchasedQuestionBanks.size=" + purchasedQuestionBanks.size());
            return ResponseEntity.ok(ApiResponse.success("获取已购买题库成功", purchasedQuestionBanks));
        } catch (Exception e) {
            e.printStackTrace();
            // 返回空列表而不是400
            return ResponseEntity.ok(ApiResponse.success("发生异常，返回空列表", new java.util.ArrayList<>()));
        }
    }

    @PutMapping("/{id}/score")
    public ResponseEntity<ApiResponse<Void>> updateScore(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam Integer score) {
        try {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            QuestionBank questionBank = questionBankService.findById(id)
                    .orElseThrow(() -> new RuntimeException("题库不存在"));

            questionBankService.updateQuestionBankScore(user, questionBank, score);
            return ResponseEntity.ok(ApiResponse.success("更新得分成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 