package com.training.service;

import com.training.entity.QuestionBank;
import com.training.entity.UserQuestionBank;
import com.training.entity.Question;
import com.training.dto.QuestionBankStatisticsDto;
import com.training.repository.QuestionBankRepository;
import com.training.repository.UserQuestionBankRepository;
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
    private UserQuestionBankRepository userQuestionBankRepository;
    
    @Autowired
    private QuestionRepository questionRepository;

    public QuestionBankStatisticsDto getQuestionBankStatistics(Long questionBankId) {
        QuestionBank questionBank = questionBankRepository.findById(questionBankId)
                .orElseThrow(() -> new RuntimeException("题库不存在"));
        
        // 获取题库题目数量
        List<Question> questions = questionRepository.findByQuestionBank(questionBank);
        int totalQuestions = questions.size();
        
        // 获取用户答题记录
        List<UserQuestionBank> userQuestionBanks = userQuestionBankRepository.findAll().stream()
                .filter(uqb -> uqb.getQuestionBank().getId().equals(questionBankId))
                .collect(Collectors.toList());
        int totalUsers = userQuestionBanks.size();
        int completedUsers = (int) userQuestionBanks.stream()
                .filter(UserQuestionBank::getIsCompleted)
                .count();
        
        // 计算平均分
        double averageScore = userQuestionBanks.stream()
                .filter(UserQuestionBank::getIsCompleted)
                .mapToInt(UserQuestionBank::getScore)
                .average()
                .orElse(0.0);
        
        // 计算完成率
        double completionRate = totalUsers > 0 ? (double) completedUsers / totalUsers * 100 : 0.0;
        
        // 分数分布统计
        List<QuestionBankStatisticsDto.ScoreDistributionDto> scoreDistribution = calculateScoreDistribution(userQuestionBanks);
        
        // 题目统计
        List<QuestionBankStatisticsDto.QuestionStatisticsDto> questionStatistics = calculateQuestionStatistics(questions, userQuestionBanks);
        
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

    private List<QuestionBankStatisticsDto.ScoreDistributionDto> calculateScoreDistribution(List<UserQuestionBank> userQuestionBanks) {
        // 分数区间定义
        String[] ranges = {"0-60", "60-70", "70-80", "80-90", "90-100"};
        int[] boundaries = {0, 60, 70, 80, 90, 100};
        
        List<QuestionBankStatisticsDto.ScoreDistributionDto> distribution = new java.util.ArrayList<>();
        int totalCompleted = (int) userQuestionBanks.stream()
                .filter(UserQuestionBank::getIsCompleted)
                .count();
        
        for (int i = 0; i < ranges.length; i++) {
            final int min = boundaries[i];
            final int max = boundaries[i + 1];
            
            int count = (int) userQuestionBanks.stream()
                    .filter(UserQuestionBank::getIsCompleted)
                    .filter(uqb -> uqb.getScore() >= min && uqb.getScore() < max)
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
            List<Question> questions, List<UserQuestionBank> userQuestionBanks) {
        
        // 这里简化处理，实际应该从答题记录中统计每道题的答题情况
        // 由于当前系统没有详细的答题记录，这里返回基础信息
        return questions.stream()
                .map(question -> {
                    QuestionBankStatisticsDto.QuestionStatisticsDto dto = new QuestionBankStatisticsDto.QuestionStatisticsDto();
                    dto.setQuestionId(question.getId());
                    dto.setContent(question.getContent());
                    dto.setType(question.getType());
                    dto.setTotalAttempts(0); // 需要从答题记录中统计
                    dto.setCorrectAttempts(0); // 需要从答题记录中统计
                    dto.setCorrectRate(0.0); // 需要从答题记录中统计
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Map<String, Object> getSystemStatistics() {
        // 系统整体统计
        long totalQuestionBanks = questionBankRepository.count();
        long totalUsers = userQuestionBankRepository.count();
        long completedExams = userQuestionBankRepository.findAll().stream()
                .filter(UserQuestionBank::getIsCompleted)
                .count();
        
        double averageScore = userQuestionBankRepository.findAll().stream()
                .filter(UserQuestionBank::getIsCompleted)
                .mapToInt(UserQuestionBank::getScore)
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