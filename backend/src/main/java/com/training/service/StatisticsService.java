package com.training.service;

import com.training.entity.QuestionBank;
import com.training.entity.QuestionBankResult;
import com.training.entity.Question;
import com.training.dto.QuestionBankStatisticsDto;
import com.training.repository.QuestionBankRepository;
import com.training.repository.QuestionBankResultRepository;
import com.training.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    
    @Autowired
    private QuestionBankRepository questionBankRepository;
    
    @Autowired
    private QuestionBankResultRepository questionBankResultRepository;
    
    @Autowired
    private QuestionRepository questionRepository;

    public QuestionBankStatisticsDto getQuestionBankStatistics(Long questionBankId) {
        QuestionBank questionBank = questionBankRepository.findById(questionBankId)
                .orElseThrow(() -> new RuntimeException("题库不存在"));
        
        // 获取题库题目数量
        List<Question> questions = questionRepository.findByQuestionBankId(questionBankId);
        int totalQuestions = questions.size();
        
        // 获取用户答题记录
        List<QuestionBankResult> questionBankResults = questionBankResultRepository.findByQuestionBankIdOrderBySubmitTimeDesc(questionBankId);
        int totalUsers = (int) questionBankResults.stream()
                .map(result -> result.getUser().getId())
                .distinct()
                .count();
        int completedUsers = questionBankResults.size(); // 每个结果代表一次完成
        
        // 计算平均分
        double averageScore = questionBankResults.stream()
                .mapToInt(QuestionBankResult::getScore)
                .average()
                .orElse(0.0);
        
        // 计算完成率 - 由于现在每个结果都是一次完成，完成率就是100%
        double completionRate = 100.0;
        
        // 分数分布统计
        List<QuestionBankStatisticsDto.ScoreDistributionDto> scoreDistribution = calculateScoreDistribution(questionBankResults);
        
        // 题目统计
        List<QuestionBankStatisticsDto.QuestionStatisticsDto> questionStatistics = calculateQuestionStatistics(questions, questionBankResults);
        
        // 构建结果
        QuestionBankStatisticsDto result = new QuestionBankStatisticsDto();
        result.setQuestionBankId(questionBankId);
        result.setQuestionBankTitle(questionBank.getTitle());
        result.setTotalQuestions(totalQuestions);
        result.setTotalUsers(totalUsers);
        result.setCompletedUsers(completedUsers);
        result.setAverageScore(averageScore);
        result.setCompletionRate(completionRate);
        result.setScoreDistribution(scoreDistribution);
        result.setQuestionStatistics(questionStatistics);
        
        return result;
    }

    private List<QuestionBankStatisticsDto.ScoreDistributionDto> calculateScoreDistribution(List<QuestionBankResult> questionBankResults) {
        // 分数区间定义
        String[] ranges = {"0-60", "60-70", "70-80", "80-90", "90-100"};
        int[] boundaries = {0, 60, 70, 80, 90, 100};
        
        List<QuestionBankStatisticsDto.ScoreDistributionDto> distribution = new java.util.ArrayList<>();
        int totalCompleted = questionBankResults.size();
        
        for (int i = 0; i < ranges.length; i++) {
            final int min = boundaries[i];
            final int max = boundaries[i + 1];
            
            int count = (int) questionBankResults.stream()
                    .filter(result -> {
                        double percentage = (double) result.getScore() / result.getTotalScore() * 100;
                        return percentage >= min && percentage < max;
                    })
                    .count();
            
            QuestionBankStatisticsDto.ScoreDistributionDto dto = new QuestionBankStatisticsDto.ScoreDistributionDto();
            dto.setRange(ranges[i]);
            dto.setCount(count);
            dto.setPercentage(totalCompleted > 0 ? (double) count / totalCompleted * 100 : 0.0);
            distribution.add(dto);
        }
        
        return distribution;
    }

    private List<QuestionBankStatisticsDto.QuestionStatisticsDto> calculateQuestionStatistics(
            List<Question> questions, List<QuestionBankResult> questionBankResults) {
        
        // 从题库练习结果中统计每道题的答题情况
        return questions.stream()
                .map(question -> {
                    QuestionBankStatisticsDto.QuestionStatisticsDto dto = new QuestionBankStatisticsDto.QuestionStatisticsDto();
                    dto.setQuestionId(question.getId());
                    dto.setContent(question.getContent());
                    dto.setType(question.getType());
                    
                    // 统计该题目的答题情况
                    long totalAttempts = questionBankResults.stream()
                            .filter(result -> result.getQuestionResults() != null)
                            .flatMap(result -> result.getQuestionResults().stream())
                            .filter(qr -> qr.getQuestion().getId().equals(question.getId()))
                            .count();
                    
                    long correctAttempts = questionBankResults.stream()
                            .filter(result -> result.getQuestionResults() != null)
                            .flatMap(result -> result.getQuestionResults().stream())
                            .filter(qr -> qr.getQuestion().getId().equals(question.getId()))
                            .filter(qr -> qr.getIsCorrect())
                            .count();
                    
                    double correctRate = totalAttempts > 0 ? (double) correctAttempts / totalAttempts * 100 : 0.0;
                    
                    dto.setTotalAttempts((int) totalAttempts);
                    dto.setCorrectAttempts((int) correctAttempts);
                    dto.setCorrectRate(correctRate);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Map<String, Object> getSystemStatistics() {
        // 系统整体统计
        long totalQuestionBanks = questionBankRepository.count();
        long totalUsers = questionBankResultRepository.findAll().stream()
                .map(result -> result.getUser().getId())
                .distinct()
                .count();
        long completedExams = questionBankResultRepository.count();
        
        double averageScore = questionBankResultRepository.findAll().stream()
                .mapToInt(QuestionBankResult::getScore)
                .average()
                .orElse(0.0);
        
        Map<String, Object> statistics = new java.util.HashMap<>();
        statistics.put("totalQuestionBanks", totalQuestionBanks);
        statistics.put("totalUsers", totalUsers);
        statistics.put("completedExams", completedExams);
        statistics.put("averageScore", averageScore);
        
        return statistics;
    }
} 