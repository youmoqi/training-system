package com.training.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.dto.*;
import com.training.entity.*;
import com.training.entity.Exam.*;
import com.training.repository.*;
import com.training.entity.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Arrays;

/**
 * @author 14798
 */
@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamQuestionRepository examQuestionRepository;

    @Autowired
    private ExamAutoRuleRepository examAutoRuleRepository;

    @Autowired
    private UserExamRepository userExamRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ExamResultRepository examResultRepository;

    @Autowired
    private ExamQuestionResultRepository examQuestionResultRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // 分页查询试卷
    public Page<ExamDto> getExamsWithPagination(Pageable pageable, String keyword, Boolean isOnline) {
        Page<Exam> exams;

        if (keyword != null && !keyword.trim().isEmpty()) {
            exams = isOnline != null ?
                    examRepository.findByTitleContainingAndIsOnline(keyword, isOnline, pageable) :
                    examRepository.findByTitleContaining(keyword, pageable);
        } else {
            exams = isOnline != null ?
                    examRepository.findByIsOnline(isOnline, pageable) :
                    examRepository.findAll(pageable);
        }

        return exams.map(this::convertToDto);
    }

    // 分页查询用户已购买的试卷
    public Page<ExamDto> getAvailableExamsWithPagination(Pageable pageable, String keyword, Long userId) {
        // 获取用户信息以确定用户角色
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Long userRole = user.getRole().getId();
        Page<UserExam> userExams;
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 按关键词搜索用户已购买的试卷，并过滤可见角色
            userExams = userExamRepository.findByUserIdAndExamTitleContainingAndExamVisibleRolesId(userId, keyword, userRole, pageable);
        } else {
            // 不按关键词搜索，获取用户所有已购买的试卷，并过滤可见角色
            userExams = userExamRepository.findByUserIdAndExamVisibleRolesId(userId, userRole, pageable);
        }
        return userExams.map(uep -> convertToDto(uep.getExam()));
    }

    // 分页查询用户可购买的试卷（未购买的试卷）
    public Page<ExamDto> getPurchasableExamsWithPagination(Pageable pageable, String keyword, Long userId) {
        // 获取用户信息以确定用户角色
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Long roleId = user.getRole().getId();
        Page<Exam> exams;
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 按关键词搜索可购买的试卷，并过滤可见角色
            exams = examRepository.findPurchasableByKeywordAndRoleId(keyword, roleId, userId, pageable);
        } else {
            // 不按关键词搜索，获取所有可购买的试卷，并过滤可见角色
            exams = examRepository.findPurchasableByRoleId(roleId, userId, pageable);
        }
        return exams.map(this::convertToDto);
    }

    // 获取所有试卷
    public List<ExamDto> getAllExams() {
        return examRepository.findByIsOnlineTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 获取试卷详情
    public ExamDto getExamById(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("试卷不存在"));
        return convertToDto(exam);
    }

    // 获取试卷题目列表
    public List<ExamQuestionDto> getExamQuestions(Long examId) {
        List<ExamQuestion> questions = examQuestionRepository.findQuestionsByExamId(examId);
        return questions.stream()
                .map(this::convertToQuestionDto)
                .collect(Collectors.toList());
    }

    // 删除试卷
    @Transactional
    public void deleteExam(Long id) {
        examRepository.deleteById(id);
    }

    // ==================== 题目管理功能 ====================

    // 添加题目到试卷
    @Transactional
    public void addQuestionToExam(Long examId, Long questionId, Integer score, Integer order) {
        // 检查是否已存在
        if (examQuestionRepository.existsByExamIdAndQuestionId(examId, questionId)) {
            throw new RuntimeException("题目已存在于试卷中");
        }

        ExamQuestion examQuestion = new ExamQuestion();
        examQuestion.setExam(examRepository.findById(examId).orElseThrow());
        examQuestion.setQuestion(questionRepository.findById(questionId).orElseThrow());
        examQuestion.setScore(score);
        examQuestion.setQuestionOrder(order);

        examQuestionRepository.save(examQuestion);

        // 更新试卷题目总数
        updateExamTotalQuestions(examId);
    }

    // 批量添加题目到试卷
    @Transactional
    public void addQuestionsToExam(Long examId, List<Long> questionIds, Integer defaultScore) {
        int currentOrder = (int) (examQuestionRepository.countByExamId(examId) + 1);
        for (Long questionId : questionIds) {
            if (!examQuestionRepository.existsByExamIdAndQuestionId(examId, questionId)) {
                ExamQuestion examQuestion = new ExamQuestion();
                examQuestion.setExam(examRepository.findById(examId).orElseThrow());
                examQuestion.setQuestion(questionRepository.findById(questionId).orElseThrow());
                examQuestion.setScore(defaultScore);
                examQuestion.setQuestionOrder(currentOrder++);

                examQuestionRepository.save(examQuestion);
            }
        }

        // 更新试卷题目总数
        updateExamTotalQuestions(examId);
    }

    // 从试卷移除题目
    @Transactional
    public void removeQuestionFromExam(Long examId, Long questionId) {
        examQuestionRepository.deleteByExamIdAndQuestionId(examId, questionId);

        // 更新试卷题目总数
        updateExamTotalQuestions(examId);
    }

    // 更新题目分值
    @Transactional
    public void updateQuestionScore(Long examId, Long questionId, Integer score) {
        ExamQuestion examQuestion = examQuestionRepository
                .findByExamIdAndQuestionId(examId, questionId)
                .orElseThrow(() -> new RuntimeException("题目不存在于试卷中"));

        examQuestion.setScore(score);
        examQuestionRepository.save(examQuestion);
    }

    // 更新题目顺序
    @Transactional
    public void updateQuestionOrder(Long examId, List<Long> questionIds) {
        for (int i = 0; i < questionIds.size(); i++) {
            ExamQuestion examQuestion = examQuestionRepository
                    .findByExamIdAndQuestionId(examId, questionIds.get(i))
                    .orElseThrow(() -> new RuntimeException("题目不存在于试卷中"));

            examQuestion.setQuestionOrder(i + 1);
            examQuestionRepository.save(examQuestion);
        }
    }

    // ==================== 用户购买功能 ====================

    // 用户购买试卷
    @Transactional
    public void purchaseExam(Long userId, Long examId) {
        if (userExamRepository.existsByUserIdAndExamId(userId, examId)) {
            throw new RuntimeException("已购买该试卷");
        }
        UserExam userExam = new UserExam();
        userExam.setUser(new User());
        userExam.getUser().setId(userId);
        userExam.setExam(new Exam());
        userExam.getExam().setId(examId);
        userExamRepository.save(userExam);
    }

    // 检查用户购买状态
    public PurchaseStatusDto checkPurchaseStatus(Long userId, Long examId) {
        PurchaseStatusDto statusDto = new PurchaseStatusDto();
        statusDto.setHasPurchased(userExamRepository.existsByUserIdAndExamId(userId, examId));
        if (statusDto.isHasPurchased()) {
            // 获取考试记录
            List<ExamResult> results = examResultRepository.findByUserIdAndExamIdOrderByExamTimeDesc(userId, examId);
            statusDto.setExamResults(results.stream()
                    .map(this::convertToResultDto)
                    .collect(Collectors.toList()));
        }
        return statusDto;
    }

    // 创建试卷
    @Transactional
    public ApiResponse<ExamDto> createExam(ExamDto examDto) {
        try {
            Exam exam = new Exam();
            BeanUtils.copyProperties(examDto, exam);
            // 设置默认值
            if (exam.getExamCategory() == null) {
                exam.setExamCategory("GENERAL");
            }
            if (exam.getAllowRetake() == null) {
                exam.setAllowRetake(true);
            }
            if (exam.getMaxAttempts() == null) {
                exam.setMaxAttempts(3);
            }
            exam = examRepository.save(exam);

            // 重建可见分类映射
            if (examDto.getVisibleRoleIds() != null && !examDto.getVisibleRoleIds().isEmpty()) {
                List<Role> cats = roleRepository.findAllById(examDto.getVisibleRoleIds());
                exam.setVisibleRoles(cats);
            } else {
                exam.setVisibleRoles(new ArrayList<>());
            }

            return ApiResponse.success(convertToDto(examRepository.findById(exam.getId()).orElse(exam)));
        } catch (Exception e) {
            return ApiResponse.error("创建试卷失败: " + e.getMessage());
        }
    }

    // 更新试卷
    @Transactional
    public ApiResponse<ExamDto> updateExam(Long id, ExamDto examDto) {
        try {
            Exam exam = examRepository.findById(id)
                    .orElse(null);
            if (exam == null) {
                return ApiResponse.error("试卷不存在");
            }
            // 保存原有的totalQuestions值
            BeanUtils.copyProperties(examDto, exam);
            exam = examRepository.save(exam);

            // 可见分类映射
            if (examDto.getVisibleRoleIds() != null && !examDto.getVisibleRoleIds().isEmpty()) {
                List<Role> cats = roleRepository.findAllById(examDto.getVisibleRoleIds());
                exam.setVisibleRoles(cats);
            } else {
                exam.setVisibleRoles(new ArrayList<>());
            }

            return ApiResponse.success(convertToDto(examRepository.findById(exam.getId()).orElse(exam)));
        } catch (Exception e) {
            return ApiResponse.error("更新试卷失败: " + e.getMessage());
        }
    }

    // 自动组卷
    @Transactional
    public ApiResponse<String> autoGenerateExam(Long examId) {
        try {
            Exam exam = examRepository.findById(examId)
                    .orElse(null);

            if (exam == null) {
                return ApiResponse.error("试卷不存在");
            }

            List<ExamAutoRule> rules = examAutoRuleRepository.findByExamId(examId);
            if (rules.isEmpty()) {
                return ApiResponse.error("未找到自动组卷规则");
            }

            // 清除现有题目
            examQuestionRepository.deleteByExamId(examId);

            for (ExamAutoRule rule : rules) {
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
                    ExamQuestion examQuestion = new ExamQuestion();
                    examQuestion.setExam(exam);
                    examQuestion.setQuestion(question);
                    examQuestion.setQuestionOrder(order++);

                    // 根据题型设置分值
                    int score = getScoreByQuestionType(question.getType(), exam);
                    examQuestion.setScore(score);

                    examQuestionRepository.save(examQuestion);
                }
            }

            // 更新试卷题目总数
            updateExamTotalQuestions(examId);

            return ApiResponse.success("自动组卷成功");
        } catch (Exception e) {
            return ApiResponse.error("自动组卷失败: " + e.getMessage());
        }
    }

    // 检查用户是否可以重复考试
    public ApiResponse<Boolean> canUserRetakeExam(Long userId, Long examId) {
        try {
            Exam exam = examRepository.findById(examId)
                    .orElse(null);

            if (exam == null) {
                return ApiResponse.error("试卷不存在");
            }

            // 检查试卷是否允许重复考试
            if (!exam.getAllowRetake()) {
                return ApiResponse.success(false);
            }

            // 检查用户是否已购买试卷
            if (!userExamRepository.existsByUserIdAndExamId(userId, examId)) {
                return ApiResponse.error("您尚未购买此试卷");
            }

            // 获取用户对该试卷的考试次数
            Long attemptCount = examResultRepository.countByUserIdAndExamId(userId, examId);

            // 检查是否达到最大考试次数
            if (attemptCount >= exam.getMaxAttempts()) {
                return ApiResponse.success(false);
            }

            return ApiResponse.success(true);
        } catch (Exception e) {
            return ApiResponse.error("检查重复考试权限失败: " + e.getMessage());
        }
    }

    // 获取用户下次考试次数
    public ApiResponse<Integer> getUserNextAttemptNumber(Long userId, Long examId) {
        try {
            Long attemptCount = examResultRepository.countByUserIdAndExamId(userId, examId);
            return ApiResponse.success(attemptCount.intValue() + 1);
        } catch (Exception e) {
            return ApiResponse.error("获取考试次数失败: " + e.getMessage());
        }
    }

    // 根据考试分类获取试卷
    public ApiResponse<List<ExamDto>> getExamsByCategory(String category) {
        try {
            List<Exam> exams = examRepository.findByExamCategoryAndIsOnlineTrue(category);
            List<ExamDto> dtos = exams.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ApiResponse.success(dtos);
        } catch (Exception e) {
            return ApiResponse.error("获取试卷失败: " + e.getMessage());
        }
    }

    // ==================== 自动组卷规则管理 ====================

    // 获取自动组卷规则
    public ApiResponse<List<ExamAutoRuleDto>> getAutoRules(Long examId) {
        try {
            List<ExamAutoRule> rules = examAutoRuleRepository.findByExamId(examId);
            List<ExamAutoRuleDto> ruleDtos = rules.stream()
                    .map(this::convertToAutoRuleDto)
                    .collect(Collectors.toList());
            return ApiResponse.success(ruleDtos);
        } catch (Exception e) {
            return ApiResponse.error("获取自动组卷规则失败: " + e.getMessage());
        }
    }

    // 创建自动组卷规则
    public ApiResponse<ExamAutoRuleDto> createAutoRule(Long examId, ExamAutoRuleDto ruleDto) {
        try {
            ExamAutoRule rule = new ExamAutoRule();
            rule.setExamId(examId);
            rule.setQuestionType(ruleDto.getQuestionType());
            rule.setQuestionCount(ruleDto.getQuestionCount());

            if (ruleDto.getQuestionBankIds() != null) {
                rule.setQuestionBankIds(objectMapper.writeValueAsString(ruleDto.getQuestionBankIds()));
            }

            rule = examAutoRuleRepository.save(rule);
            return ApiResponse.success(convertToAutoRuleDto(rule));
        } catch (Exception e) {
            return ApiResponse.error("创建自动组卷规则失败: " + e.getMessage());
        }
    }

    // 更新自动组卷规则
    public ApiResponse<ExamAutoRuleDto> updateAutoRule(Long examId, Long ruleId, ExamAutoRuleDto ruleDto) {
        try {
            ExamAutoRule rule = examAutoRuleRepository.findById(ruleId)
                    .orElse(null);

            if (rule == null || !rule.getExamId().equals(examId)) {
                return ApiResponse.error("规则不存在");
            }
            rule.setQuestionType(ruleDto.getQuestionType());
            rule.setQuestionCount(ruleDto.getQuestionCount());
            if (ruleDto.getQuestionBankIds() != null) {
                rule.setQuestionBankIds(objectMapper.writeValueAsString(ruleDto.getQuestionBankIds()));
            }
            rule = examAutoRuleRepository.save(rule);
            return ApiResponse.success(convertToAutoRuleDto(rule));
        } catch (Exception e) {
            return ApiResponse.error("更新自动组卷规则失败: " + e.getMessage());
        }
    }

    // 删除自动组卷规则
    public ApiResponse<String> deleteAutoRule(Long examId, Long ruleId) {
        try {
            ExamAutoRule rule = examAutoRuleRepository.findById(ruleId)
                    .orElse(null);

            if (rule == null || !rule.getExamId().equals(examId)) {
                return ApiResponse.error("规则不存在");
            }

            examAutoRuleRepository.delete(rule);
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            return ApiResponse.error("删除自动组卷规则失败: " + e.getMessage());
        }
    }

    // 根据题型获取分值
    private int getScoreByQuestionType(String questionType, Exam exam) {
        switch (questionType) {
            case "SINGLE_CHOICE":
                return exam.getSingleChoiceScore();
            case "MULTIPLE_CHOICE":
                return exam.getMultipleChoiceScore();
            case "TRUE_FALSE":
                return exam.getTrueFalseScore();
            case "FILL_BLANK":
                return exam.getFillBlankScore();
            case "SHORT_ANSWER":
                return exam.getShortAnswerScore();
            default:
                return 2;
        }
    }

    // 更新试卷题目总数
    private void updateExamTotalQuestions(Long examId) {
        Long totalQuestions = examQuestionRepository.countByExamId(examId);
        examRepository.updateTotalQuestions(examId, totalQuestions.intValue());
    }

    // 转换DTO
    private ExamDto convertToDto(Exam exam) {
        ExamDto dto = new ExamDto();
        BeanUtils.copyProperties(exam, dto);
        // 获取试卷题目
        List<ExamQuestion> questions = examQuestionRepository.findQuestionsByExamId(exam.getId());
        List<ExamQuestionDto> questionDtos = questions.stream()
                .map(this::convertToQuestionDto)
                .collect(Collectors.toList());
        dto.setQuestions(questionDtos);
        return dto;
    }

    // 转换题目实体到DTO
    private ExamQuestionDto convertToQuestionDto(ExamQuestion examQuestion) {
        ExamQuestionDto dto = new ExamQuestionDto();
        dto.setId(examQuestion.getId());
        dto.setQuestionId(examQuestion.getQuestion().getId());
        dto.setQuestionContent(examQuestion.getQuestion().getContent());
        dto.setQuestionType(examQuestion.getQuestion().getType());
        dto.setQuestionOrder(examQuestion.getQuestionOrder());
        dto.setScore(examQuestion.getScore());
        dto.setExplanation(examQuestion.getQuestion().getExplanation());

        // 获取题目选项 - 转换为字符串列表
        if (examQuestion.getQuestion().getOptions() != null) {
            dto.setOptions(examQuestion.getQuestion().getOptions().stream()
                    .map(QuestionOption::getOptionContent)
                    .collect(Collectors.toList()));
        }

        // 获取题目答案 - 从字符串转换为列表
        if (examQuestion.getQuestion().getAnswer() != null && !examQuestion.getQuestion().getAnswer().trim().isEmpty()) {
            String answerStr = examQuestion.getQuestion().getAnswer().trim();
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
    private ExamAutoRuleDto convertToAutoRuleDto(ExamAutoRule rule) {
        ExamAutoRuleDto dto = new ExamAutoRuleDto();
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
    private ExamResultDto convertToResultDto(ExamResult result) {
        ExamResultDto dto = new ExamResultDto();
        BeanUtils.copyProperties(result, dto);

        // 设置试卷标题
        if (result.getExam() != null) {
            dto.setExamTitle(result.getExam().getTitle());
        }

        // 设置用户信息
        if (result.getUser() != null) {
            dto.setUserName(result.getUser().getRealName());
        }

        // 计算当前这次考试是第几次
        // 获取该用户对该试卷的所有考试记录，按时间排序
        List<ExamResult> allResults = examResultRepository.findByUserIdAndExamIdOrderByExamTimeAsc(
                result.getUser().getId(), result.getExam().getId());

        // 找到当前结果在列表中的位置（从1开始）
        int attemptNumber = 1;
        for (ExamResult examResult : allResults) {
            if (examResult.getId().equals(result.getId())) {
                break;
            }
            attemptNumber++;
        }
        dto.setAttemptNumber(attemptNumber);

        return dto;
    }

    // 转换题目结果到DTO
    private ExamQuestionResultDto convertToQuestionResultDto(ExamQuestionResult questionResult) {
        ExamQuestionResultDto dto = new ExamQuestionResultDto();
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
    public ApiResponse<ExamResultDto> submitExam(Long userId, Long examId, List<QuestionAnswerDto> answers, Integer timeTaken) {
        try {
            // 检查用户是否已购买试卷
            if (!userExamRepository.existsByUserIdAndExamId(userId, examId)) {
                return ApiResponse.error("您尚未购买此试卷");
            }

            // 获取试卷信息
            Exam exam = examRepository.findById(examId)
                    .orElseThrow(() -> new RuntimeException("试卷不存在"));

            // 获取试卷题目
            List<ExamQuestion> examQuestions = examQuestionRepository.findQuestionsByExamId(examId);

            // 创建考试结果
            ExamResult examResult = new ExamResult();
            examResult.setUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在")));
            examResult.setExam(exam);
            examResult.setTotalQuestions(examQuestions.size());
            examResult.setTotalScore(exam.getTotalScore());
            examResult.setTimeTaken(timeTaken);
            examResult.setExamTime(LocalDateTime.now());

            // 计算得分和正确题数
            int totalScore = 0;
            int correctAnswers = 0;
            List<ExamQuestionResult> questionResults = new ArrayList<>();

            for (ExamQuestion examQuestion : examQuestions) {
                Question question = examQuestion.getQuestion();

                // 查找用户答案
                QuestionAnswerDto userAnswer = answers.stream()
                        .filter(a -> a.getQuestionId().equals(question.getId()))
                        .findFirst()
                        .orElse(null);

                ExamQuestionResult questionResult = new ExamQuestionResult();
                questionResult.setExamResult(examResult);
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
            examResult.setIsPassed(totalScore >= exam.getPassScore());

            // 保存考试结果
            examResult = examResultRepository.save(examResult);

            // 保存题目结果
            for (ExamQuestionResult questionResult : questionResults) {
                questionResult.setExamResult(examResult);
            }
            examQuestionResultRepository.saveAll(questionResults);

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
