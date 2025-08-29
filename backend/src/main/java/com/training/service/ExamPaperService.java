package com.training.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.dto.*;
import com.training.entity.*;
import com.training.repository.*;
import com.training.entity.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Arrays;

@Service
public class ExamPaperService {

    @Autowired
    private ExamPaperRepository examPaperRepository;

    @Autowired
    private ExamPaperQuestionRepository examPaperQuestionRepository;

    @Autowired
    private ExamPaperAutoRuleRepository examPaperAutoRuleRepository;

    @Autowired
    private UserExamPaperRepository userExamPaperRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ExamPaperResultRepository examPaperResultRepository;

    @Autowired
    private ExamPaperQuestionResultRepository examPaperQuestionResultRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExamPaperVisibleRoleRepository examPaperVisibleRoleRepository;

    @Autowired
    private VisibilityCategoryRepository visibilityCategoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

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
        // 获取用户信息以确定用户角色
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        String userRole = user.getRole().getCode();
        Page<UserExamPaper> userExamPapers;
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 按关键词搜索用户已购买的试卷，并过滤可见角色
            userExamPapers = userExamPaperRepository.findByUserIdAndExamPaperTitleContainingAndUserRole(userId, keyword, userRole, pageable);
        } else {
            // 不按关键词搜索，获取用户所有已购买的试卷，并过滤可见角色
            userExamPapers = userExamPaperRepository.findByUserIdAndUserRole(userId, userRole, pageable);
        }
        return userExamPapers.map(uep -> convertToDto(uep.getExamPaper()));
    }

    // 分页查询用户可购买的试卷（未购买的试卷）
    public Page<ExamPaperDto> getPurchasableExamPapersWithPagination(Pageable pageable, String keyword, Long userId) {
        // 获取用户信息以确定用户角色
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        String userRole = user.getRole().getCode();
        Page<ExamPaper> examPapers;
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 按关键词搜索可购买的试卷，并过滤可见角色
            examPapers = examPaperRepository.findPurchasableByKeywordAndUserRole(keyword, userRole, userId, pageable);
        } else {
            // 不按关键词搜索，获取所有可购买的试卷，并过滤可见角色
            examPapers = examPaperRepository.findPurchasableByUserRole(userRole, userId, pageable);
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

    // 删除试卷
    @Transactional
    public void deleteExamPaper(Long id) {
        examPaperRepository.deleteById(id);
    }

    // ==================== 题目管理功能 ====================

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
        int currentOrder = (int) (examPaperQuestionRepository.countByExamPaperId(examPaperId) + 1);
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

    // ==================== 用户购买功能 ====================

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

    // 创建试卷
    @Transactional
    public ApiResponse<ExamPaperDto> createExamPaper(ExamPaperDto examPaperDto) {
        try {
            ExamPaper examPaper = new ExamPaper();
            BeanUtils.copyProperties(examPaperDto, examPaper);
            // 设置默认值
            if (examPaper.getExamCategory() == null) {
                examPaper.setExamCategory("GENERAL");
            }
            if (examPaper.getAllowRetake() == null) {
                examPaper.setAllowRetake(true);
            }
            if (examPaper.getMaxAttempts() == null) {
                examPaper.setMaxAttempts(3);
            }
            examPaper = examPaperRepository.save(examPaper);

            // 重建可见分类映射
            if (examPaperDto.getVisibleCategoryIds() != null && !examPaperDto.getVisibleCategoryIds().isEmpty()) {
                List<VisibilityCategory> cats = visibilityCategoryRepository.findAllById(examPaperDto.getVisibleCategoryIds());
                List<ExamPaperVisibleRole> mappings = new ArrayList<>();
                for (VisibilityCategory cat : cats) {
                    ExamPaperVisibleRole m = new ExamPaperVisibleRole();
                    m.setExamPaper(examPaper);
                    m.setVisibilityCategory(cat);
                    mappings.add(m);
                }
                examPaperVisibleRoleRepository.saveAll(mappings);
            }

            return ApiResponse.success(convertToDto(examPaperRepository.findById(examPaper.getId()).orElse(examPaper)));
        } catch (Exception e) {
            return ApiResponse.error("创建试卷失败: " + e.getMessage());
        }
    }

    // 更新试卷
    @Transactional
    public ApiResponse<ExamPaperDto> updateExamPaper(Long id, ExamPaperDto examPaperDto) {
        try {
            ExamPaper examPaper = examPaperRepository.findById(id)
                    .orElse(null);
            if (examPaper == null) {
                return ApiResponse.error("试卷不存在");
            }
            // 保存原有的totalQuestions值
            BeanUtils.copyProperties(examPaperDto, examPaper);
            examPaper = examPaperRepository.save(examPaper);

            // 重建可见分类映射
            examPaperVisibleRoleRepository.deleteAllByExamPaperId(examPaper.getId());
            if (examPaperDto.getVisibleCategoryIds() != null && !examPaperDto.getVisibleCategoryIds().isEmpty()) {
                List<VisibilityCategory> cats = visibilityCategoryRepository.findAllById(examPaperDto.getVisibleCategoryIds());
                List<ExamPaperVisibleRole> mappings = new ArrayList<>();
                for (VisibilityCategory cat : cats) {
                    ExamPaperVisibleRole m = new ExamPaperVisibleRole();
                    m.setExamPaper(examPaper);
                    m.setVisibilityCategory(cat);
                    mappings.add(m);
                }
                examPaperVisibleRoleRepository.saveAll(mappings);
            }

            return ApiResponse.success(convertToDto(examPaperRepository.findById(examPaper.getId()).orElse(examPaper)));
        } catch (Exception e) {
            return ApiResponse.error("更新试卷失败: " + e.getMessage());
        }
    }

    // 自动组卷
    @Transactional
    public ApiResponse<String> autoGenerateExamPaper(Long examPaperId) {
        try {
            ExamPaper examPaper = examPaperRepository.findById(examPaperId)
                    .orElse(null);

            if (examPaper == null) {
                return ApiResponse.error("试卷不存在");
            }

            List<ExamPaperAutoRule> rules = examPaperAutoRuleRepository.findByExamPaperId(examPaperId);
            if (rules.isEmpty()) {
                return ApiResponse.error("未找到自动组卷规则");
            }

            // 清除现有题目
            examPaperQuestionRepository.deleteByExamPaperId(examPaperId);

            for (ExamPaperAutoRule rule : rules) {
                List<Long> questionBankIds = new ArrayList<>();
                if (rule.getQuestionBankIds() != null) {
                    try {
                        questionBankIds = objectMapper.readValue(rule.getQuestionBankIds(),
                                new TypeReference<List<Long>>() {
                                });
                    } catch (Exception e) {
                        return ApiResponse.error("解析题库ID失败: " + e.getMessage());
                    }
                }

                // 根据规则抽取题目
                List<Question> questions;
                if (questionBankIds.isEmpty()) {
                    // 如果题库ID列表为空，从所有题库中抽取
                    questions = questionRepository.findRandomQuestionsByType(
                            rule.getQuestionType(),
                            rule.getQuestionCount());
                } else {
                    // 从指定题库中抽取
                    questions = questionRepository.findRandomQuestionsByTypeAndBanks(
                            rule.getQuestionType(),
                            questionBankIds,
                            rule.getQuestionCount());
                }

                if (questions.size() < rule.getQuestionCount()) {
                    return ApiResponse.error("题库中题目数量不足，需要" + rule.getQuestionCount() +
                            "道" + rule.getQuestionType() + "题，但只有" + questions.size() + "道");
                }

                // 添加题目到试卷
                int order = 1;
                for (Question question : questions) {
                    ExamPaperQuestion examPaperQuestion = new ExamPaperQuestion();
                    examPaperQuestion.setExamPaper(examPaper);
                    examPaperQuestion.setQuestion(question);
                    examPaperQuestion.setQuestionOrder(order++);

                    // 根据题型设置分值
                    int score = getScoreByQuestionType(question.getType(), examPaper);
                    examPaperQuestion.setScore(score);

                    examPaperQuestionRepository.save(examPaperQuestion);
                }
            }

            // 更新试卷题目总数
            updateExamPaperTotalQuestions(examPaperId);

            return ApiResponse.success("自动组卷成功");
        } catch (Exception e) {
            return ApiResponse.error("自动组卷失败: " + e.getMessage());
        }
    }

    // 检查用户是否可以重复考试
    public ApiResponse<Boolean> canUserRetakeExam(Long userId, Long examPaperId) {
        try {
            ExamPaper examPaper = examPaperRepository.findById(examPaperId)
                    .orElse(null);

            if (examPaper == null) {
                return ApiResponse.error("试卷不存在");
            }

            // 检查试卷是否允许重复考试
            if (!examPaper.getAllowRetake()) {
                return ApiResponse.success(false);
            }

            // 检查用户是否已购买试卷
            if (!userExamPaperRepository.existsByUserIdAndExamPaperId(userId, examPaperId)) {
                return ApiResponse.error("您尚未购买此试卷");
            }

            // 获取用户对该试卷的考试次数
            Long attemptCount = examPaperResultRepository.countByUserIdAndExamPaperId(userId, examPaperId);

            // 检查是否达到最大考试次数
            if (attemptCount >= examPaper.getMaxAttempts()) {
                return ApiResponse.success(false);
            }

            return ApiResponse.success(true);
        } catch (Exception e) {
            return ApiResponse.error("检查重复考试权限失败: " + e.getMessage());
        }
    }

    // 获取用户下次考试次数
    public ApiResponse<Integer> getUserNextAttemptNumber(Long userId, Long examPaperId) {
        try {
            Long attemptCount = examPaperResultRepository.countByUserIdAndExamPaperId(userId, examPaperId);
            return ApiResponse.success(attemptCount.intValue() + 1);
        } catch (Exception e) {
            return ApiResponse.error("获取考试次数失败: " + e.getMessage());
        }
    }

    // 根据考试分类获取试卷
    public ApiResponse<List<ExamPaperDto>> getExamPapersByCategory(String category) {
        try {
            List<ExamPaper> examPapers = examPaperRepository.findByExamCategoryAndIsOnlineTrue(category);
            List<ExamPaperDto> dtos = examPapers.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ApiResponse.success(dtos);
        } catch (Exception e) {
            return ApiResponse.error("获取试卷失败: " + e.getMessage());
        }
    }

    // ==================== 自动组卷规则管理 ====================

    // 获取自动组卷规则
    public ApiResponse<List<ExamPaperAutoRuleDto>> getAutoRules(Long examPaperId) {
        try {
            List<ExamPaperAutoRule> rules = examPaperAutoRuleRepository.findByExamPaperId(examPaperId);
            List<ExamPaperAutoRuleDto> ruleDtos = rules.stream()
                    .map(this::convertToAutoRuleDto)
                    .collect(Collectors.toList());
            return ApiResponse.success(ruleDtos);
        } catch (Exception e) {
            return ApiResponse.error("获取自动组卷规则失败: " + e.getMessage());
        }
    }

    // 创建自动组卷规则
    public ApiResponse<ExamPaperAutoRuleDto> createAutoRule(Long examPaperId, ExamPaperAutoRuleDto ruleDto) {
        try {
            ExamPaperAutoRule rule = new ExamPaperAutoRule();
            rule.setExamPaperId(examPaperId);
            rule.setQuestionType(ruleDto.getQuestionType());
            rule.setQuestionCount(ruleDto.getQuestionCount());

            if (ruleDto.getQuestionBankIds() != null) {
                rule.setQuestionBankIds(objectMapper.writeValueAsString(ruleDto.getQuestionBankIds()));
            }

            rule = examPaperAutoRuleRepository.save(rule);
            return ApiResponse.success(convertToAutoRuleDto(rule));
        } catch (Exception e) {
            return ApiResponse.error("创建自动组卷规则失败: " + e.getMessage());
        }
    }

    // 更新自动组卷规则
    public ApiResponse<ExamPaperAutoRuleDto> updateAutoRule(Long examPaperId, Long ruleId, ExamPaperAutoRuleDto ruleDto) {
        try {
            ExamPaperAutoRule rule = examPaperAutoRuleRepository.findById(ruleId)
                    .orElse(null);

            if (rule == null || !rule.getExamPaperId().equals(examPaperId)) {
                return ApiResponse.error("规则不存在");
            }
            rule.setQuestionType(ruleDto.getQuestionType());
            rule.setQuestionCount(ruleDto.getQuestionCount());
            if (ruleDto.getQuestionBankIds() != null) {
                rule.setQuestionBankIds(objectMapper.writeValueAsString(ruleDto.getQuestionBankIds()));
            }
            rule = examPaperAutoRuleRepository.save(rule);
            return ApiResponse.success(convertToAutoRuleDto(rule));
        } catch (Exception e) {
            return ApiResponse.error("更新自动组卷规则失败: " + e.getMessage());
        }
    }

    // 删除自动组卷规则
    public ApiResponse<String> deleteAutoRule(Long examPaperId, Long ruleId) {
        try {
            ExamPaperAutoRule rule = examPaperAutoRuleRepository.findById(ruleId)
                    .orElse(null);

            if (rule == null || !rule.getExamPaperId().equals(examPaperId)) {
                return ApiResponse.error("规则不存在");
            }

            examPaperAutoRuleRepository.delete(rule);
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            return ApiResponse.error("删除自动组卷规则失败: " + e.getMessage());
        }
    }

    // 根据题型获取分值
    private int getScoreByQuestionType(String questionType, ExamPaper examPaper) {
        switch (questionType) {
            case "SINGLE_CHOICE":
                return examPaper.getSingleChoiceScore();
            case "MULTIPLE_CHOICE":
                return examPaper.getMultipleChoiceScore();
            case "TRUE_FALSE":
                return examPaper.getTrueFalseScore();
            case "FILL_BLANK":
                return examPaper.getFillBlankScore();
            case "SHORT_ANSWER":
                return examPaper.getShortAnswerScore();
            default:
                return 2;
        }
    }

    // 更新试卷题目总数
    private void updateExamPaperTotalQuestions(Long examPaperId) {
        Long totalQuestions = examPaperQuestionRepository.countByExamPaperId(examPaperId);
        examPaperRepository.updateTotalQuestions(examPaperId, totalQuestions.intValue());
    }

    // 转换DTO
    private ExamPaperDto convertToDto(ExamPaper examPaper) {
        ExamPaperDto dto = new ExamPaperDto();
        BeanUtils.copyProperties(examPaper, dto);
        // 获取试卷题目
        List<ExamPaperQuestion> questions = examPaperQuestionRepository.findQuestionsByExamPaperId(examPaper.getId());
        List<ExamPaperQuestionDto> questionDtos = questions.stream()
                .map(this::convertToQuestionDto)
                .collect(Collectors.toList());
        dto.setQuestions(questionDtos);
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

        // 获取题目选项 - 转换为字符串列表
        if (examPaperQuestion.getQuestion().getOptions() != null) {
            dto.setOptions(examPaperQuestion.getQuestion().getOptions().stream()
                    .map(QuestionOption::getOptionContent)
                    .collect(Collectors.toList()));
        }

        // 获取题目答案 - 从字符串转换为列表
        if (examPaperQuestion.getQuestion().getAnswer() != null && !examPaperQuestion.getQuestion().getAnswer().trim().isEmpty()) {
            String answerStr = examPaperQuestion.getQuestion().getAnswer().trim();
            if (answerStr.contains(",")) {
                // 多选题：A,B 格式
                dto.setAnswers(Arrays.asList(answerStr.split(",")));
            } else {
                // 单选题、判断题、填空题、简答题：单个答案
                dto.setAnswers(Arrays.asList(answerStr));
            }
        }
        return dto;
    }

    // 转换自动组卷规则DTO
    private ExamPaperAutoRuleDto convertToAutoRuleDto(ExamPaperAutoRule rule) {
        ExamPaperAutoRuleDto dto = new ExamPaperAutoRuleDto();
        BeanUtils.copyProperties(rule, dto);

        if (rule.getQuestionBankIds() != null) {
            try {
                List<Long> questionBankIds = objectMapper.readValue(rule.getQuestionBankIds(),
                        new TypeReference<List<Long>>() {
                        });
                dto.setQuestionBankIds(questionBankIds);
            } catch (Exception e) {
                dto.setQuestionBankIds(new ArrayList<>());
            }
        }

        return dto;
    }


    // 转换考试结果到DTO
    private ExamPaperResultDto convertToResultDto(ExamPaperResult result) {
        ExamPaperResultDto dto = new ExamPaperResultDto();
        BeanUtils.copyProperties(result, dto);

        // 设置试卷标题
        if (result.getExamPaper() != null) {
            dto.setExamPaperTitle(result.getExamPaper().getTitle());
        }

        // 设置用户信息
        if (result.getUser() != null) {
            dto.setUserName(result.getUser().getRealName());
        }

        // 计算当前这次考试是第几次
        // 获取该用户对该试卷的所有考试记录，按时间排序
        List<ExamPaperResult> allResults = examPaperResultRepository.findByUserIdAndExamPaperIdOrderByExamTimeAsc(
                result.getUser().getId(), result.getExamPaper().getId());

        // 找到当前结果在列表中的位置（从1开始）
        int attemptNumber = 1;
        for (ExamPaperResult examResult : allResults) {
            if (examResult.getId().equals(result.getId())) {
                break;
            }
            attemptNumber++;
        }
        dto.setAttemptNumber(attemptNumber);

        return dto;
    }

    // 转换题目结果到DTO
    private ExamPaperQuestionResultDto convertToQuestionResultDto(ExamPaperQuestionResult questionResult) {
        ExamPaperQuestionResultDto dto = new ExamPaperQuestionResultDto();
        dto.setId(questionResult.getId());
        dto.setQuestionId(questionResult.getQuestion().getId());
        dto.setQuestionContent(questionResult.getQuestion().getContent());
        dto.setQuestionType(questionResult.getQuestion().getType());
        dto.setUserAnswer(questionResult.getUserAnswer());
        dto.setCorrectAnswer(questionResult.getCorrectAnswer());
        dto.setIsCorrect(questionResult.getIsCorrect());
        dto.setScore(questionResult.getScore());
        dto.setMaxScore(questionResult.getMaxScore());
        dto.setExplanation(questionResult.getExplanation());

        // 获取题目选项
        if (questionResult.getQuestion().getOptions() != null && !questionResult.getQuestion().getOptions().isEmpty()) {
            List<String> optionsList = questionResult.getQuestion().getOptions().stream()
                    .map(QuestionOption::getOptionContent)
                    .collect(Collectors.toList());
            dto.setOptions(optionsList);
        }

        return dto;
    }

    // ==================== 试卷考试功能 ====================

    // 提交试卷考试
    @Transactional
    public ApiResponse<ExamPaperResultDto> submitExamPaper(Long userId, Long examPaperId, List<QuestionAnswerDto> answers, Integer timeTaken) {
        try {
            // 检查用户是否已购买试卷
            if (!userExamPaperRepository.existsByUserIdAndExamPaperId(userId, examPaperId)) {
                return ApiResponse.error("您尚未购买此试卷");
            }

            // 获取试卷信息
            ExamPaper examPaper = examPaperRepository.findById(examPaperId)
                    .orElseThrow(() -> new RuntimeException("试卷不存在"));

            // 获取试卷题目
            List<ExamPaperQuestion> examQuestions = examPaperQuestionRepository.findQuestionsByExamPaperId(examPaperId);

            // 创建考试结果
            ExamPaperResult examResult = new ExamPaperResult();
            examResult.setUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在")));
            examResult.setExamPaper(examPaper);
            examResult.setTotalQuestions(examQuestions.size());
            examResult.setTotalScore(examPaper.getTotalScore());
            examResult.setTimeTaken(timeTaken);
            examResult.setExamTime(LocalDateTime.now());

            // 计算得分和正确题数
            int totalScore = 0;
            int correctAnswers = 0;
            List<ExamPaperQuestionResult> questionResults = new ArrayList<>();

            for (ExamPaperQuestion examQuestion : examQuestions) {
                Question question = examQuestion.getQuestion();

                // 查找用户答案
                QuestionAnswerDto userAnswer = answers.stream()
                        .filter(a -> a.getQuestionId().equals(question.getId()))
                        .findFirst()
                        .orElse(null);

                ExamPaperQuestionResult questionResult = new ExamPaperQuestionResult();
                questionResult.setExamPaperResult(examResult);
                questionResult.setQuestion(question);
                questionResult.setMaxScore(examQuestion.getScore());
                questionResult.setCorrectAnswer(question.getAnswer());
                questionResult.setExplanation(question.getExplanation());

                if (userAnswer != null && userAnswer.getUserAnswer() != null && !userAnswer.getUserAnswer().trim().isEmpty()) {
                    questionResult.setUserAnswer(userAnswer.getUserAnswer());

                    // 判断答案是否正确
                    boolean isCorrect = checkAnswer(question, userAnswer.getUserAnswer());
                    questionResult.setIsCorrect(isCorrect);

                    if (isCorrect) {
                        questionResult.setScore(examQuestion.getScore());
                        totalScore += examQuestion.getScore();
                        correctAnswers++;
                    } else {
                        questionResult.setScore(0);
                    }
                } else {
                    questionResult.setUserAnswer("");
                    questionResult.setIsCorrect(false);
                    questionResult.setScore(0);
                }

                questionResults.add(questionResult);
            }

            examResult.setScore(totalScore);
            examResult.setCorrectAnswers(correctAnswers);
            examResult.setIsPassed(totalScore >= examPaper.getPassScore());

            // 保存考试结果
            examResult = examPaperResultRepository.save(examResult);

            // 保存题目结果
            for (ExamPaperQuestionResult questionResult : questionResults) {
                questionResult.setExamPaperResult(examResult);
            }
            examPaperQuestionResultRepository.saveAll(questionResults);

            return ApiResponse.success(convertToResultDto(examResult));
        } catch (Exception e) {
            return ApiResponse.error("提交考试失败: " + e.getMessage());
        }
    }

    // 检查答案是否正确
    private boolean checkAnswer(Question question, String userAnswer) {
        String correctAnswer = question.getAnswer();
        if (correctAnswer == null || correctAnswer.trim().isEmpty()) {
            return false;
        }

        String userAnswerTrim = userAnswer.trim();
        String correctAnswerTrim = correctAnswer.trim();

        switch (question.getType()) {
            case "SINGLE_CHOICE":
            case "TRUE_FALSE":
            case "FILL_BLANK":
            case "SHORT_ANSWER":
                return userAnswerTrim.equalsIgnoreCase(correctAnswerTrim);
            case "MULTIPLE_CHOICE":
                // 多选题答案格式：A,B,C
                String[] userAnswers = userAnswerTrim.split(",");
                String[] correctAnswers = correctAnswerTrim.split(",");

                if (userAnswers.length != correctAnswers.length) {
                    return false;
                }

                // 排序后比较
                Arrays.sort(userAnswers);
                Arrays.sort(correctAnswers);

                return Arrays.equals(userAnswers, correctAnswers);
            default:
                return false;
        }
    }
}
