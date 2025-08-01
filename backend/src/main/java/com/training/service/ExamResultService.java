package com.training.service;

import com.training.dto.ExamResultDto;
import com.training.entity.ExamResult;
import com.training.entity.QuestionResult;
import com.training.entity.User;
import com.training.entity.QuestionBank;
import com.training.entity.Question;
import com.training.repository.ExamResultRepository;
import com.training.repository.QuestionResultRepository;
import com.training.repository.QuestionBankRepository;
import com.training.repository.UserRepository;
import com.training.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExamResultService {
    
    @Autowired
    private ExamResultRepository examResultRepository;
    
    @Autowired
    private QuestionResultRepository questionResultRepository;
    
    @Autowired
    private QuestionBankRepository questionBankRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    // 分页查询考试结果
    public Page<ExamResultDto> findExamResultsWithPagination(Pageable pageable, String keyword, Long userId) {
        Page<ExamResult> examResults;
        
        if (userId != null) {
            // 按用户ID查询
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            examResults = examResultRepository.findByUser(user, pageable);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            // 按关键词搜索
            examResults = examResultRepository.findByQuestionBankTitleContaining(keyword, pageable);
        } else {
            // 查询所有
            examResults = examResultRepository.findAll(pageable);
        }
        
        return examResults.map(this::convertToDto);
    }
    
    public ExamResult saveExamResult(ExamResultDto examResultDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        QuestionBank questionBank = questionBankRepository.findById(examResultDto.getQuestionBankId())
                .orElseThrow(() -> new RuntimeException("题库不存在"));
        
        // 创建考试结果
        ExamResult examResult = new ExamResult();
        examResult.setUser(user);
        examResult.setQuestionBank(questionBank);
        examResult.setTotalQuestions(examResultDto.getTotalQuestions());
        examResult.setCorrectAnswers(examResultDto.getCorrectAnswers());
        examResult.setScore(examResultDto.getScore());
        examResult.setExamTime(LocalDateTime.now());
        
        // 保存考试结果
        ExamResult savedExamResult = examResultRepository.save(examResult);
        
        // 保存题目结果
        if (examResultDto.getQuestionResults() != null) {
            List<QuestionResult> questionResults = examResultDto.getQuestionResults().stream()
                    .map(qrDto -> {
                        QuestionResult qr = new QuestionResult();
                        qr.setExamResult(savedExamResult);
                        qr.setQuestion(questionRepository.findById(qrDto.getQuestionId())
                                .orElseThrow(() -> new RuntimeException("题目不存在")));
                        qr.setUserAnswers(qrDto.getUserAnswers());
                        qr.setCorrectAnswers(qrDto.getCorrectAnswers());
                        qr.setIsCorrect(qrDto.getIsCorrect());
                        qr.setExplanation(qrDto.getExplanation());
                        return qr;
                    })
                    .collect(Collectors.toList());
            
            questionResultRepository.saveAll(questionResults);
            
            // 设置题目结果到考试结果中
            savedExamResult.setQuestionResults(questionResults);
        }
        
        return savedExamResult;
    }
    
    public ExamResultDto getLatestExamResult(Long userId, Long questionBankId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        QuestionBank questionBank = questionBankRepository.findById(questionBankId)
                .orElseThrow(() -> new RuntimeException("题库不存在"));
        
        Optional<ExamResult> examResultOpt = examResultRepository
                .findTopByUserAndQuestionBankOrderByExamTimeDesc(user, questionBank);
        
        if (!examResultOpt.isPresent()) {
            throw new RuntimeException("未找到考试结果");
        }
        
        ExamResult examResult = examResultOpt.get();
        return convertToDto(examResult);
    }
    
    public List<ExamResultDto> getUserExamResults(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        List<ExamResult> examResults = examResultRepository.findByUserOrderByExamTimeDesc(user);
        return examResults.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public ExamResultDto convertToDto(ExamResult examResult) {
        ExamResultDto dto = new ExamResultDto();
        dto.setQuestionBankId(examResult.getQuestionBank().getId());
        dto.setQuestionBankTitle(examResult.getQuestionBank().getTitle());
        dto.setTotalQuestions(examResult.getTotalQuestions());
        dto.setCorrectAnswers(examResult.getCorrectAnswers());
        dto.setScore(examResult.getScore());
        
        // 转换题目结果
        if (examResult.getQuestionResults() != null) {
            List<ExamResultDto.QuestionResultDto> questionResultDtos = examResult.getQuestionResults().stream()
                    .map(qr -> {
                        ExamResultDto.QuestionResultDto qrDto = new ExamResultDto.QuestionResultDto();
                        qrDto.setQuestionId(qr.getQuestion().getId());
                        qrDto.setContent(qr.getQuestion().getContent());
                        qrDto.setType(qr.getQuestion().getType());
                        qrDto.setUserAnswers(qr.getUserAnswers());
                        qrDto.setCorrectAnswers(qr.getCorrectAnswers());
                        qrDto.setIsCorrect(qr.getIsCorrect());
                        qrDto.setExplanation(qr.getExplanation());
                        return qrDto;
                    })
                    .collect(Collectors.toList());
            
            dto.setQuestionResults(questionResultDtos);
        }
        
        return dto;
    }
} 