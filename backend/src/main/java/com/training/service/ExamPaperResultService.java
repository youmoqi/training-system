package com.training.service;

import com.training.dto.ExamPaperHistoryDto;
import com.training.dto.ExamPaperResultDto;
import com.training.dto.ExamPaperQuestionResultDto;
import com.training.entity.*;
import com.training.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.training.dto.ApiResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExamPaperResultService {

    @Autowired
    private ExamPaperResultRepository examPaperResultRepository;

    @Autowired
    private ExamPaperQuestionResultRepository examPaperQuestionResultRepository;

    // 根据结果ID获取考试结果
    public ExamPaperResultDto getExamResultById(Long resultId) {
        ExamPaperResult examResult = examPaperResultRepository.findById(resultId)
                .orElseThrow(() -> new RuntimeException("未找到考试结果"));
        return convertToDto(examResult);
    }

    private ExamPaperResultDto convertToDto(ExamPaperResult examResult) {
        ExamPaperResultDto dto = new ExamPaperResultDto();
        BeanUtils.copyProperties(examResult, dto);

        // 设置试卷标题
        if (examResult.getExamPaper() != null) {
            dto.setExamPaperTitle(examResult.getExamPaper().getTitle());
        }

        // 设置用户信息
        if (examResult.getUser() != null) {
            dto.setUserName(examResult.getUser().getRealName());
        }

        // 设置考试时间
        dto.setExamTime(examResult.getExamTime());

        // 获取题目答题详情
        List<ExamPaperQuestionResult> questionResults = examPaperQuestionResultRepository
                .findByExamPaperResultOrderById(examResult);

        List<ExamPaperQuestionResultDto> questionResultDtos = questionResults.stream()
                .map(this::convertToQuestionResultDto)
                .collect(Collectors.toList());

        dto.setQuestionResults(questionResultDtos);

        return dto;
    }

    private ExamPaperQuestionResultDto convertToQuestionResultDto(ExamPaperQuestionResult questionResult) {
        ExamPaperQuestionResultDto dto = new ExamPaperQuestionResultDto();
        BeanUtils.copyProperties(questionResult, dto);

        // 设置题目信息
        if (questionResult.getQuestion() != null) {
            Question question = questionResult.getQuestion();
            dto.setQuestionId(question.getId());
            dto.setQuestionContent(question.getContent());
            dto.setQuestionType(question.getType());
            dto.setExplanation(question.getExplanation());

            // 设置选项
            if (question.getOptions() != null && !question.getOptions().isEmpty()) {
                List<String> options = question.getOptions().stream()
                        .map(QuestionOption::getOptionContent)
                        .collect(Collectors.toList());
                dto.setOptions(options);
            }

            // 用户答案和正确答案保持选项标签格式，不转换为选项内容
            // 这样前端显示时只显示选项标签（A、B、C、D）
        }

        return dto;
    }

    // 获取用户考试历史
    public ApiResponse<Page<ExamPaperHistoryDto>> getUserExamHistory(Long userId, Pageable pageable, String keyword, Boolean isPassed) {
        try {
            Page<ExamPaperResult> results;

            if (keyword != null && !keyword.trim().isEmpty()) {
                // 按关键词搜索
                if (isPassed != null) {
                    // 按关键词和通过状态搜索
                    results = examPaperResultRepository.findByUserIdAndExamPaperTitleContainingAndIsPassedOrderByExamTimeDesc(userId, keyword.trim(), isPassed, pageable);
                } else {
                    // 只按关键词搜索
                    results = examPaperResultRepository.findByUserIdAndExamPaperTitleContainingOrderByExamTimeDesc(userId, keyword.trim(), pageable);
                }
            } else {
                // 不按关键词搜索
                if (isPassed != null) {
                    // 只按通过状态搜索
                    results = examPaperResultRepository.findByUserIdAndIsPassedOrderByExamTimeDesc(userId, isPassed, pageable);
                } else {
                    // 不按任何条件搜索
                    results = examPaperResultRepository.findByUserIdOrderByExamTimeDesc(userId, pageable);
                }
            }

            Page<ExamPaperHistoryDto> historyDtos = results.map(this::convertToHistoryDto);
            return ApiResponse.success(historyDtos);
        } catch (Exception e) {
            return ApiResponse.error("获取考试历史失败: " + e.getMessage());
        }
    }

    // 转换历史记录DTO
    private ExamPaperHistoryDto convertToHistoryDto(ExamPaperResult result) {
        ExamPaperHistoryDto dto = new ExamPaperHistoryDto();
        BeanUtils.copyProperties(result, dto);
        
        // 计算考试次数 - 获取该用户对该试卷的所有考试记录，按时间排序，找到当前考试的位置
        List<ExamPaperResult> allResults = examPaperResultRepository.findByUserIdAndExamPaperIdOrderByExamTimeAsc(
                result.getUser().getId(), result.getExamPaper().getId());
        int attemptNumber = 1;
        for (ExamPaperResult examResult : allResults) {
            if (examResult.getId().equals(result.getId())) {
                break;
            }
            attemptNumber++;
        }
        dto.setAttemptNumber(attemptNumber);
        
        // 获取关联信息
        if (result.getExamPaper() != null) {
            dto.setExamPaperTitle(result.getExamPaper().getTitle());
        }
        if (result.getUser() != null) {
            dto.setUserName(result.getUser().getRealName());
        }
        if (result.getExamPaper() != null) {
            dto.setExamPaperId(result.getExamPaper().getId());
        }
        dto.setExamPaperResultId(result.getId());
        return dto;
    }
}
