package com.training.service;

import com.training.dto.ApiResponse;
import com.training.dto.QuestionBankResultDto;
import com.training.entity.Exam.QuestionBankResult;
import com.training.entity.Exam.QuestionOption;
import com.training.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author YIZ
 */
@Service
@Transactional
public class QuestionBankResultService {

    @Autowired
    private QuestionBankResultRepository questionBankResultRepository;

    // 获取用户题库练习历史（分页）
    public ApiResponse<Page<QuestionBankResultDto>> getUserQuestionBankHistory(Long userId, Pageable pageable, String keyword, Boolean isPassed) {
        try {
            Page<QuestionBankResult> results;

            if (keyword != null && !keyword.trim().isEmpty()) {
                if (isPassed != null) {
                    results = questionBankResultRepository.findByUserIdAndQuestionBankTitleContainingAndIsPassedOrderBySubmitTimeDesc(userId, keyword.trim(), isPassed, pageable);
                } else {
                    results = questionBankResultRepository.findByUserIdAndQuestionBankTitleContainingOrderBySubmitTimeDesc(userId, keyword.trim(), pageable);
                }
            } else {
                if (isPassed != null) {
                    results = questionBankResultRepository.findByUserIdAndIsPassedOrderBySubmitTimeDesc(userId, isPassed, pageable);
                } else {
                    results = questionBankResultRepository.findByUserIdOrderBySubmitTimeDesc(userId, pageable);
                }
            }

            Page<QuestionBankResultDto> historyDtos = results.map(this::convertToDto);
            return ApiResponse.success(historyDtos);
        } catch (Exception e) {
            return ApiResponse.error("获取题库练习历史失败: " + e.getMessage());
        }
    }

    // 根据结果ID获取题库练习结果
    public QuestionBankResultDto getQuestionBankResultById(Long resultId) {
        QuestionBankResult questionBankResult = questionBankResultRepository.findById(resultId)
                .orElseThrow(() -> new RuntimeException("未找到题库练习结果"));
        return convertToDto(questionBankResult);
    }

    // 转换QuestionBankResult为QuestionBankResultDto
    private QuestionBankResultDto convertToDto(QuestionBankResult questionBankResult) {
        QuestionBankResultDto dto = new QuestionBankResultDto();
        BeanUtils.copyProperties(questionBankResult, dto);

        // 设置用户信息
        if (questionBankResult.getUser() != null) {
            dto.setUserId(questionBankResult.getUser().getId());
            dto.setUserName(questionBankResult.getUser().getRealName());
        }

        // 设置题库信息
        if (questionBankResult.getQuestionBank() != null) {
            dto.setQuestionBankId(questionBankResult.getQuestionBank().getId());
            dto.setQuestionBankTitle(questionBankResult.getQuestionBank().getTitle());
        }

        // 计算练习次数 - 获取该用户对该题库的练习次数
        List<QuestionBankResult> allResults = null;
        if (questionBankResult.getUser() != null && questionBankResult.getQuestionBank() != null) {
            allResults = questionBankResultRepository.findByUserIdAndQuestionBankIdOrderBySubmitTimeAsc(
                    questionBankResult.getUser().getId(), questionBankResult.getQuestionBank().getId());
        }

        // 找到当前结果在列表中的位置（从1开始）
        int attemptNumber = 1;
        if (allResults != null) {
            for (QuestionBankResult result : allResults) {
                if (result.getId().equals(questionBankResult.getId())) {
                    break;
                }
                attemptNumber++;
            }
        }
        dto.setAttemptNumber(attemptNumber);

        // 设置题目结果
        if (questionBankResult.getQuestionResults() != null) {
            List<QuestionBankResultDto.QuestionResult> questionResults = questionBankResult.getQuestionResults().stream()
                    .map(qr -> {
                        QuestionBankResultDto.QuestionResult qrDto = new QuestionBankResultDto.QuestionResult();
                        qrDto.setQuestionId(qr.getQuestion().getId());
                        qrDto.setQuestionContent(qr.getQuestion().getContent());
                        qrDto.setQuestionType(qr.getQuestion().getType());
                        qrDto.setUserAnswer(qr.getUserAnswer());
                        qrDto.setCorrectAnswer(qr.getCorrectAnswer());
                        qrDto.setIsCorrect(qr.getIsCorrect());
                        qrDto.setScore(qr.getScore());
                        qrDto.setMaxScore(qr.getMaxScore());
                        qrDto.setExplanation(qr.getExplanation());

                        // 设置选项
                        if (qr.getQuestion().getOptions() != null && !qr.getQuestion().getOptions().isEmpty()) {
                            List<String> options = qr.getQuestion().getOptions().stream()
                                    .map(QuestionOption::getOptionContent)
                                    .collect(Collectors.toList());
                            qrDto.setOptions(options);
                        }

                        return qrDto;
                    })
                    .collect(Collectors.toList());
            dto.setQuestionResults(questionResults);
        }

        return dto;
    }
}
