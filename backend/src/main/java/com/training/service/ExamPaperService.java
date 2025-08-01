package com.training.service;

import com.training.dto.ExamPaperDto;
import com.training.dto.ExamPaperQuestionDto;
import com.training.dto.ExamPaperResultDto;
import com.training.dto.PurchaseStatusDto;
import com.training.entity.*;
import com.training.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamPaperService {

    @Autowired
    private ExamPaperRepository examPaperRepository;

    @Autowired
    private ExamPaperQuestionRepository examPaperQuestionRepository;

    @Autowired
    private UserExamPaperRepository userExamPaperRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ExamPaperResultRepository examPaperResultRepository;

    // 分页查询试卷
    public Page<ExamPaperDto> getExamPapersWithPagination(Pageable pageable, String keyword, Boolean isOnline) {
        Page<ExamPaper> examPapers;

        if (keyword != null && !keyword.trim().isEmpty()) {
            // 按关键词搜索
            if (isOnline != null) {
                // 按上线状态筛选
                examPapers = examPaperRepository.findByTitleContainingAndIsOnline(keyword, isOnline, pageable);
            } else {
                // 不按上线状态筛选
                examPapers = examPaperRepository.findByTitleContaining(keyword, pageable);
            }
        } else {
            // 不按关键词搜索
            if (isOnline != null) {
                // 按上线状态筛选
                examPapers = examPaperRepository.findByIsOnline(isOnline, pageable);
            } else {
                // 不按上线状态筛选，显示所有试卷
                examPapers = examPaperRepository.findAll(pageable);
            }
        }

        return examPapers.map(this::convertToDto);
    }

    // 分页查询用户已购买的试卷
    public Page<ExamPaperDto> getAvailableExamPapersWithPagination(Pageable pageable, String keyword, Long userId) {
        Page<UserExamPaper> userExamPapers;
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 按关键词搜索用户已购买的试卷
            userExamPapers = userExamPaperRepository.findByUserIdAndExamPaperTitleContaining(userId, keyword, pageable);
        } else {
            // 不按关键词搜索，获取用户所有已购买的试卷
            userExamPapers = userExamPaperRepository.findByUserId(userId, pageable);
        }
        return userExamPapers.map(uep -> convertToDto(uep.getExamPaper()));
    }

    // 分页查询用户可购买的试卷（未购买的试卷）
    public Page<ExamPaperDto> getPurchasableExamPapersWithPagination(Pageable pageable, String keyword, Long userId) {
        Page<ExamPaper> examPapers;
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 按关键词搜索可购买的试卷
            examPapers = userExamPaperRepository.findPurchasableByUserIdAndTitleContaining(userId, keyword, pageable);
        } else {
            // 不按关键词搜索，获取所有可购买的试卷
            examPapers = userExamPaperRepository.findPurchasableByUserId(userId, pageable);
        }
        return examPapers.map(this::convertToDto);
    }

    // 获取所有试卷
    public List<ExamPaperDto> getAllExamPapers() {
        return examPaperRepository.findByIsOnlineTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 根据角色获取试卷
    public List<ExamPaperDto> getExamPapersByRole(String role) {
        return examPaperRepository.findByRoleAndIsOnlineTrue(role).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 获取试卷详情
    public ExamPaperDto getExamPaperById(Long id) {
        ExamPaper examPaper = examPaperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("试卷不存在"));
        return convertToDto(examPaper);
    }

    // 获取试卷题目列表
    public List<ExamPaperQuestionDto> getExamPaperQuestions(Long examPaperId) {
        List<ExamPaperQuestion> questions = examPaperQuestionRepository.findQuestionsByExamPaperId(examPaperId);
        return questions.stream()
                .map(this::convertToQuestionDto)
                .collect(Collectors.toList());
    }

    // 创建试卷
    @Transactional
    public ExamPaperDto createExamPaper(ExamPaperDto examPaperDto) {
        ExamPaper examPaper = new ExamPaper();
        examPaper.setTitle(examPaperDto.getTitle());
        examPaper.setDescription(examPaperDto.getDescription());
        examPaper.setTotalScore(examPaperDto.getTotalScore());
        examPaper.setPassScore(examPaperDto.getPassScore());
        examPaper.setDuration(examPaperDto.getDuration());
        examPaper.setIsOnline(examPaperDto.getIsOnline());
        examPaper.setIsRandom(examPaperDto.getIsRandom());

        ExamPaper savedExamPaper = examPaperRepository.save(examPaper);

        // 添加可见角色
        if (examPaperDto.getVisibleRoles() != null) {
            for (String role : examPaperDto.getVisibleRoles()) {
                savedExamPaper.getVisibleRoles().add(role);
            }
        }
        return convertToDto(savedExamPaper);
    }

    // 更新试卷
    @Transactional
    public ExamPaperDto updateExamPaper(Long id, ExamPaperDto examPaperDto) {
        ExamPaper examPaper = examPaperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("试卷不存在"));

        examPaper.setTitle(examPaperDto.getTitle());
        examPaper.setDescription(examPaperDto.getDescription());
        examPaper.setTotalScore(examPaperDto.getTotalScore());
        examPaper.setPassScore(examPaperDto.getPassScore());
        examPaper.setDuration(examPaperDto.getDuration());
        examPaper.setIsOnline(examPaperDto.getIsOnline());
        examPaper.setIsRandom(examPaperDto.getIsRandom());

        ExamPaper savedExamPaper = examPaperRepository.save(examPaper);
        return convertToDto(savedExamPaper);
    }

    // 删除试卷
    @Transactional
    public void deleteExamPaper(Long id) {
        examPaperRepository.deleteById(id);
    }

    // 添加题目到试卷
    @Transactional
    public void addQuestionToExamPaper(Long examPaperId, Long questionId, Integer score, Integer order) {
        // 检查是否已存在
        if (examPaperQuestionRepository.existsByExamPaperIdAndQuestionId(examPaperId, questionId)) {
            throw new RuntimeException("题目已存在于试卷中");
        }

        ExamPaperQuestion examPaperQuestion = new ExamPaperQuestion();
        examPaperQuestion.setExamPaper(examPaperRepository.findById(examPaperId).orElseThrow());
        examPaperQuestion.setQuestion(questionRepository.findById(questionId).orElseThrow());
        examPaperQuestion.setScore(score);
        examPaperQuestion.setQuestionOrder(order);

        examPaperQuestionRepository.save(examPaperQuestion);

        // 更新试卷题目总数
        updateExamPaperTotalQuestions(examPaperId);
    }

    // 批量添加题目到试卷
    @Transactional
    public void addQuestionsToExamPaper(Long examPaperId, List<Long> questionIds, Integer defaultScore) {
        int currentOrder = examPaperQuestionRepository.countByExamPaperId(examPaperId) + 1;

        for (Long questionId : questionIds) {
            if (!examPaperQuestionRepository.existsByExamPaperIdAndQuestionId(examPaperId, questionId)) {
                ExamPaperQuestion examPaperQuestion = new ExamPaperQuestion();
                examPaperQuestion.setExamPaper(examPaperRepository.findById(examPaperId).orElseThrow());
                examPaperQuestion.setQuestion(questionRepository.findById(questionId).orElseThrow());
                examPaperQuestion.setScore(defaultScore);
                examPaperQuestion.setQuestionOrder(currentOrder++);

                examPaperQuestionRepository.save(examPaperQuestion);
            }
        }

        // 更新试卷题目总数
        updateExamPaperTotalQuestions(examPaperId);
    }

    // 从试卷移除题目
    @Transactional
    public void removeQuestionFromExamPaper(Long examPaperId, Long questionId) {
        examPaperQuestionRepository.deleteByExamPaperIdAndQuestionId(examPaperId, questionId);

        // 更新试卷题目总数
        updateExamPaperTotalQuestions(examPaperId);
    }

    // 更新题目分值
    @Transactional
    public void updateQuestionScore(Long examPaperId, Long questionId, Integer score) {
        ExamPaperQuestion examPaperQuestion = examPaperQuestionRepository
                .findByExamPaperIdAndQuestionId(examPaperId, questionId)
                .orElseThrow(() -> new RuntimeException("题目不存在于试卷中"));

        examPaperQuestion.setScore(score);
        examPaperQuestionRepository.save(examPaperQuestion);
    }

    // 更新题目顺序
    @Transactional
    public void updateQuestionOrder(Long examPaperId, List<Long> questionIds) {
        for (int i = 0; i < questionIds.size(); i++) {
            ExamPaperQuestion examPaperQuestion = examPaperQuestionRepository
                    .findByExamPaperIdAndQuestionId(examPaperId, questionIds.get(i))
                    .orElseThrow(() -> new RuntimeException("题目不存在于试卷中"));

            examPaperQuestion.setQuestionOrder(i + 1);
            examPaperQuestionRepository.save(examPaperQuestion);
        }
    }

    // 用户购买试卷
    @Transactional
    public void purchaseExamPaper(Long userId, Long examPaperId) {
        if (userExamPaperRepository.existsByUserIdAndExamPaperId(userId, examPaperId)) {
            throw new RuntimeException("已购买该试卷");
        }
        UserExamPaper userExamPaper = new UserExamPaper();
        userExamPaper.setUser(new User());
        userExamPaper.getUser().setId(userId);
        userExamPaper.setExamPaper(new ExamPaper());
        userExamPaper.getExamPaper().setId(examPaperId);
        userExamPaperRepository.save(userExamPaper);
    }

    // 检查用户购买状态
    public PurchaseStatusDto checkPurchaseStatus(Long userId, Long examPaperId) {
        PurchaseStatusDto statusDto = new PurchaseStatusDto();
        statusDto.setHasPurchased(userExamPaperRepository.existsByUserIdAndExamPaperId(userId, examPaperId));

        if (statusDto.isHasPurchased()) {
            // 获取考试记录
            List<ExamPaperResult> results = examPaperResultRepository.findByUserIdAndExamPaperId(userId, examPaperId);
            statusDto.setExamResults(results.stream()
                    .map(this::convertToResultDto)
                    .collect(Collectors.toList()));
        }

        return statusDto;
    }

    // 转换考试结果到DTO
    private ExamPaperResultDto convertToResultDto(ExamPaperResult result) {
        ExamPaperResultDto dto = new ExamPaperResultDto();
        BeanUtils.copyProperties(result, dto);
        return dto;
    }

    // 更新试卷题目总数
    private void updateExamPaperTotalQuestions(Long examPaperId) {
        ExamPaper examPaper = examPaperRepository.findById(examPaperId).orElseThrow();
        int totalQuestions = examPaperQuestionRepository.countByExamPaperId(examPaperId);
        examPaper.setTotalQuestions(totalQuestions);
        examPaperRepository.save(examPaper);
    }

    // 转换实体到DTO
    private ExamPaperDto convertToDto(ExamPaper examPaper) {
        ExamPaperDto dto = new ExamPaperDto();
        BeanUtils.copyProperties(examPaper, dto);
        if (examPaper.getQuestions() != null) {
            dto.setQuestions(examPaper.getQuestions().stream()
                    .map(this::convertToQuestionDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    // 转换题目实体到DTO
    private ExamPaperQuestionDto convertToQuestionDto(ExamPaperQuestion examPaperQuestion) {
        ExamPaperQuestionDto dto = new ExamPaperQuestionDto();
        dto.setId(examPaperQuestion.getId());
        dto.setQuestionId(examPaperQuestion.getQuestion().getId());
        dto.setQuestionContent(examPaperQuestion.getQuestion().getContent());
        dto.setQuestionType(examPaperQuestion.getQuestion().getType());
        dto.setQuestionOrder(examPaperQuestion.getQuestionOrder());
        dto.setScore(examPaperQuestion.getScore());
        dto.setExplanation(examPaperQuestion.getQuestion().getExplanation());

        // 获取题目选项和答案
        if (examPaperQuestion.getQuestion().getOptions() != null) {
            dto.setOptions(examPaperQuestion.getQuestion().getOptions());
        }
        if (examPaperQuestion.getQuestion().getAnswers() != null) {
            dto.setAnswers(examPaperQuestion.getQuestion().getAnswers());
        }

        return dto;
    }
}
