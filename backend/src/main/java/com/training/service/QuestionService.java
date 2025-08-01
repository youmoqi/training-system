package com.training.service;

import com.training.entity.Question;
import com.training.entity.QuestionBank;
import com.training.entity.User;
import com.training.entity.UserQuestionBank;
import com.training.dto.QuestionDto;
import com.training.dto.QuestionAnswerDto;
import com.training.dto.ExamResultDto;
import com.training.dto.QuestionImportDto;
import com.training.repository.QuestionRepository;
import com.training.repository.QuestionBankRepository;
import com.training.repository.UserQuestionBankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
public class QuestionService {
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private QuestionBankRepository questionBankRepository;
    
    @Autowired
    private UserQuestionBankRepository userQuestionBankRepository;

    // 分页查询题目
    public Page<QuestionDto> findQuestions(Pageable pageable, String keyword, String type, Long questionBankId) {
        Page<Question> questions;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 按关键词搜索
            if (type != null && !type.trim().isEmpty()) {
                if (questionBankId != null) {
                    questions = questionRepository.findByContentContainingAndTypeAndQuestionBankId(keyword, type, questionBankId, pageable);
                } else {
                    questions = questionRepository.findByContentContainingAndType(keyword, type, pageable);
                }
            } else {
                if (questionBankId != null) {
                    questions = questionRepository.findByContentContainingAndQuestionBankId(keyword, questionBankId, pageable);
                } else {
                    questions = questionRepository.findByContentContaining(keyword, pageable);
                }
            }
        } else {
            // 不按关键词搜索
            if (type != null && !type.trim().isEmpty()) {
                if (questionBankId != null) {
                    questions = questionRepository.findByTypeAndQuestionBankId(type, questionBankId, pageable);
                } else {
                    questions = questionRepository.findByType(type, pageable);
                }
            } else {
                if (questionBankId != null) {
                    questions = questionRepository.findByQuestionBankId(questionBankId, pageable);
                } else {
                    questions = questionRepository.findAll(pageable);
                }
            }
        }
        
        return questions.map(this::convertToDto);
    }

    // 题目管理功能
    public Question createQuestion(QuestionDto questionDto) {
        QuestionBank questionBank = questionBankRepository.findById(questionDto.getQuestionBankId())
                .orElseThrow(() -> new RuntimeException("题库不存在"));
        
        Question question = new Question();
        question.setQuestionBank(questionBank);
        question.setContent(questionDto.getContent());
        question.setType(questionDto.getType());
        question.setExplanation(questionDto.getExplanation());
        question.setOptions(questionDto.getOptions());
        question.setAnswers(questionDto.getAnswers());
        
        return questionRepository.save(question);
    }

    public Optional<Question> findById(Long id) {
        return questionRepository.findById(id);
    }

    public List<Question> findByQuestionBank(Long questionBankId) {
        QuestionBank questionBank = questionBankRepository.findById(questionBankId)
                .orElseThrow(() -> new RuntimeException("题库不存在"));
        return questionRepository.findByQuestionBankOrderById(questionBank);
    }

    public Question updateQuestion(Long id, QuestionDto questionDto) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("题目不存在"));
        
        QuestionBank questionBank = questionBankRepository.findById(questionDto.getQuestionBankId())
                .orElseThrow(() -> new RuntimeException("题库不存在"));
        
        question.setQuestionBank(questionBank);
        question.setContent(questionDto.getContent());
        question.setType(questionDto.getType());
        question.setExplanation(questionDto.getExplanation());
        question.setOptions(questionDto.getOptions());
        question.setAnswers(questionDto.getAnswers());
        
        return questionRepository.save(question);
    }

    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    // 答题功能
    public List<QuestionDto> getQuestionsForExam(Long questionBankId) {
        List<Question> questions = findByQuestionBank(questionBankId);
        return questions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ExamResultDto submitExam(Long questionBankId, Long userId, List<QuestionAnswerDto> answers) {
        // 验证用户是否购买了题库
        User user = new User();
        user.setId(userId);
        QuestionBank questionBank = questionBankRepository.findById(questionBankId)
                .orElseThrow(() -> new RuntimeException("题库不存在"));
        
        Optional<UserQuestionBank> userQuestionBankOpt = userQuestionBankRepository.findByUserAndQuestionBank(user, questionBank);
        if (!userQuestionBankOpt.isPresent()) {
            throw new RuntimeException("您尚未购买此题库");
        }

        // 获取题库所有题目
        List<Question> questions = questionRepository.findByQuestionBankOrderById(questionBank);
        
        // 计算得分
        int totalQuestions = questions.size();
        AtomicInteger correctAnswers = new AtomicInteger(0);
        List<ExamResultDto.QuestionResultDto> questionResults = questions.stream()
                .map(question -> {
                    ExamResultDto.QuestionResultDto result = new ExamResultDto.QuestionResultDto();
                    result.setQuestionId(question.getId());
                    result.setContent(question.getContent());
                    result.setType(question.getType());
                    result.setCorrectAnswers(question.getAnswers());
                    result.setExplanation(question.getExplanation());
                    
                    // 查找用户答案
                    Optional<QuestionAnswerDto> userAnswer = answers.stream()
                            .filter(a -> a.getQuestionId().equals(question.getId()))
                            .findFirst();
                    
                    if (userAnswer.isPresent()) {
                        result.setUserAnswers(userAnswer.get().getUserAnswers());
                        boolean isCorrect = checkAnswer(question, userAnswer.get().getUserAnswers());
                        result.setIsCorrect(isCorrect);
                        if (isCorrect) {
                            correctAnswers.incrementAndGet();
                        }
                    } else {
                        result.setUserAnswers(Arrays.asList());
                        result.setIsCorrect(false);
                    }
                    
                    return result;
                })
                .collect(Collectors.toList());
        
        // 计算分数（百分制）
        int score = totalQuestions > 0 ? (correctAnswers.get() * 100) / totalQuestions : 0;
        
        // 更新用户题库记录
        UserQuestionBank userQuestionBank = userQuestionBankOpt.get();
        userQuestionBank.setScore(score);
        userQuestionBank.setIsCompleted(true);
        userQuestionBank.setCompleteTime(LocalDateTime.now());
        userQuestionBankRepository.save(userQuestionBank);
        
        // 构建结果
        ExamResultDto result = new ExamResultDto();
        result.setQuestionBankId(questionBankId);
        result.setQuestionBankTitle(questionBank.getTitle());
        result.setTotalQuestions(totalQuestions);
        result.setCorrectAnswers(correctAnswers.get());
        result.setScore(score);
        result.setQuestionResults(questionResults);
        
        return result;
    }

    private boolean checkAnswer(Question question, List<String> userAnswers) {
        if (userAnswers == null || userAnswers.isEmpty()) {
            return false;
        }
        
        List<String> correctAnswers = question.getAnswers();
        if (correctAnswers == null || correctAnswers.isEmpty()) {
            return false;
        }
        
        // 单选题和多选题的判分逻辑
        if ("SINGLE_CHOICE".equals(question.getType()) || "MULTIPLE_CHOICE".equals(question.getType())) {
            return userAnswers.size() == correctAnswers.size() && 
                   userAnswers.containsAll(correctAnswers) && 
                   correctAnswers.containsAll(userAnswers);
        }
        
        // 主观题需要人工判分，这里暂时返回false
        return false;
    }

    // 题目导入导出功能
    public List<QuestionDto> exportQuestions(Long questionBankId) {
        List<Question> questions = findByQuestionBank(questionBankId);
        return questions.stream()
                .map(this::convertToDtoWithAnswers)
                .collect(Collectors.toList());
    }

    public void importQuestions(QuestionImportDto importDto) {
        QuestionBank questionBank = questionBankRepository.findById(importDto.getQuestionBankId())
                .orElseThrow(() -> new RuntimeException("题库不存在"));
        
        for (QuestionDto questionDto : importDto.getQuestions()) {
            Question question = new Question();
            question.setQuestionBank(questionBank);
            question.setContent(questionDto.getContent());
            question.setType(questionDto.getType());
            question.setExplanation(questionDto.getExplanation());
            question.setOptions(questionDto.getOptions());
            question.setAnswers(questionDto.getAnswers());
            
            questionRepository.save(question);
        }
    }

    private QuestionDto convertToDtoWithAnswers(Question question) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setQuestionBankId(question.getQuestionBank().getId());
        dto.setQuestionBankTitle(question.getQuestionBank().getTitle());
        dto.setContent(question.getContent());
        dto.setType(question.getType());
        dto.setExplanation(question.getExplanation());
        dto.setOptions(question.getOptions());
        dto.setAnswers(question.getAnswers());
        return dto;
    }

    private QuestionDto convertToDto(Question question) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setQuestionBankId(question.getQuestionBank().getId());
        dto.setQuestionBankTitle(question.getQuestionBank().getTitle());
        dto.setContent(question.getContent());
        dto.setType(question.getType());
        dto.setExplanation(question.getExplanation());
        dto.setOptions(question.getOptions());
        // 答题时不返回答案
        dto.setAnswers(null);
        return dto;
    }
} 