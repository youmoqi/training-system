package com.training.controller;

import com.training.dto.ApiResponse;
import com.training.dto.QuestionDto;
import com.training.dto.QuestionImportDto;
import com.training.dto.QuestionBankResultDto;
import com.training.dto.QuestionAnswerDto;
import com.training.entity.Exam.Question;
import com.training.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;

/**
 * @author YIZ
 */
@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "*")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    // 通用题目查询接口
    @GetMapping
    public ResponseEntity<ApiResponse<Page<QuestionDto>>> getQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long questionBankId) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<QuestionDto> questions = questionService.findQuestions(pageable, keyword, type, questionBankId);
            return ResponseEntity.ok(ApiResponse.success("获取题目成功", questions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 题目管理接口
    @GetMapping("/question-bank/{questionBankId}")
    public ResponseEntity<ApiResponse<List<QuestionDto>>> getQuestionsByQuestionBank(@PathVariable Long questionBankId) {
        try {
            List<QuestionDto> questions = questionService.getQuestionsForExam(questionBankId);
            return ResponseEntity.ok(ApiResponse.success("获取题目成功", questions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Question>> getQuestionById(@PathVariable Long id) {
        return questionService.findById(id)
                .map(question -> ResponseEntity.ok(ApiResponse.success("获取题目成功", question)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Question>> createQuestion(@RequestBody QuestionDto questionDto) {
        try {
            Question savedQuestion = questionService.createQuestion(questionDto);
            return ResponseEntity.ok(ApiResponse.success("创建题目成功", savedQuestion));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Question>> updateQuestion(@PathVariable Long id, @RequestBody QuestionDto questionDto) {
        try {
            Question updatedQuestion = questionService.updateQuestion(id, questionDto);
            return ResponseEntity.ok(ApiResponse.success("更新题目成功", updatedQuestion));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(@PathVariable Long id) {
        try {
            questionService.deleteQuestion(id);
            return ResponseEntity.ok(ApiResponse.success("删除题目成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 答题接口
    @GetMapping("/exam/{questionBankId}")
    public ResponseEntity<ApiResponse<List<QuestionDto>>> getExamQuestions(@PathVariable Long questionBankId) {
        try {
            List<QuestionDto> questions = questionService.getQuestionsForExam(questionBankId);
            return ResponseEntity.ok(ApiResponse.success("获取考试题目成功", questions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 提交题库练习
    @PostMapping("/exam/{questionBankId}/submit")
    public ResponseEntity<ApiResponse<QuestionBankResultDto>> submitQuestionBankExam(
            @PathVariable Long questionBankId,
            @RequestBody List<QuestionAnswerDto> answers,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") Integer timeTaken) {
        try {
            QuestionBankResultDto result = questionService.submitQuestionBankExam(userId, questionBankId, answers, timeTaken);
            return ResponseEntity.ok(ApiResponse.success("提交练习成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 从Word文档导入题目
     */
    @PostMapping("/import/word")
    public ResponseEntity<ApiResponse<List<QuestionDto>>> importQuestionsFromWord(
            @RequestParam("file") MultipartFile file,
            @RequestParam("questionBankId") Long questionBankId) {
        try {
            // 验证文件格式
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.toLowerCase().endsWith(".doc") && !fileName.toLowerCase().endsWith(".docx"))) {
                return ResponseEntity.badRequest().body(ApiResponse.error("请上传.doc或.docx格式的文件"));
            }

            List<QuestionDto> questions = questionService.importQuestionsFromWord(file, questionBankId);
            return ResponseEntity.ok(ApiResponse.success("解析Word文档成功", questions));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("文件读取失败: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 确认导入Word文档中的题目
     */
    @PostMapping("/import/word/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmImportFromWord(@RequestBody QuestionImportDto importDto) {
        try {
            questionService.importQuestions(importDto);
            return ResponseEntity.ok(ApiResponse.success("导入题目成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

        /**
     * 导出题目为Word文档
     */
    @GetMapping("/export/word/{questionBankId}")
    public ResponseEntity<byte[]> exportQuestionsToWord(@PathVariable Long questionBankId) {
        try {
            byte[] wordDocument = questionService.exportQuestionsToWord(questionBankId);

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"questions.docx\"")
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                    .body(wordDocument);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 下载Word模板
     */
    @GetMapping("/template/word")
    public ResponseEntity<byte[]> downloadWordTemplate() {
        try {
            byte[] template = com.training.util.WordTemplateGenerator.generateTemplate();

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"question_template.docx\"")
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                    .body(template);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
