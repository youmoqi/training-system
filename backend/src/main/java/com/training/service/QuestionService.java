package com.training.service;

import com.training.dto.QuestionDto;
import com.training.dto.QuestionImportDto;
import com.training.dto.QuestionBankResultDto;
import com.training.dto.QuestionAnswerDto;
import com.training.entity.*;
import com.training.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
@Transactional
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionBankRepository questionBankRepository;

    @Autowired
    private QuestionOptionRepository questionOptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionBankResultRepository questionBankResultRepository;

    @Autowired
    private QuestionBankQuestionResultRepository questionBankQuestionResultRepository;

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

        return questions.map(this::convertToDtoWithAnswers);
    }

    // 题目管理功能
    @Transactional
    public Question createQuestion(QuestionDto questionDto) {
        QuestionBank questionBank = questionBankRepository.findById(questionDto.getQuestionBankId())
                .orElseThrow(() -> new RuntimeException("题库不存在"));

        Question question = new Question();
        question.setQuestionBank(questionBank);
        question.setContent(questionDto.getContent());
        question.setType(questionDto.getType());
        question.setExplanation(questionDto.getExplanation());

        // 处理选项
        if (questionDto.getOptions() != null && !questionDto.getOptions().isEmpty()) {
            List<QuestionOption> options = processOptions(question, questionDto.getOptions());
            question.setOptions(options);
        }

        // 处理答案 - 将答案列表转换为字符串
        if (questionDto.getAnswers() != null && !questionDto.getAnswers().isEmpty()) {
            if (questionDto.getAnswers().size() == 1) {
                // 单选题、判断题、填空题、简答题：单个答案
                question.setAnswer(questionDto.getAnswers().get(0));
            } else {
                // 多选题：多个答案用逗号分隔
                String answerStr = String.join(",", questionDto.getAnswers());
                question.setAnswer(answerStr);
            }
        }

        // 保存题目
        return questionRepository.save(question);
    }

    public Optional<Question> findById(Long id) {
        return questionRepository.findById(id);
    }

    public List<Question> findByQuestionBank(Long questionBankId) {
        return questionRepository.findByQuestionBankId(questionBankId);
    }

    @Transactional
    public Question updateQuestion(Long id, QuestionDto questionDto) {
        // 获取题目
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("题目不存在"));

        QuestionBank questionBank = questionBankRepository.findById(questionDto.getQuestionBankId())
                .orElseThrow(() -> new RuntimeException("题库不存在"));

        question.setQuestionBank(questionBank);
        question.setContent(questionDto.getContent());
        question.setType(questionDto.getType());
        question.setExplanation(questionDto.getExplanation());
        // 处理选项
        questionOptionRepository.deleteAllByQuestionId(question.getId());
        question.getOptions().clear();
        if (questionDto.getOptions() != null && !questionDto.getOptions().isEmpty()) {
            List<QuestionOption> newOptions = new ArrayList<>();
            for (int i = 0; i < questionDto.getOptions().size(); i++) {
                QuestionOption option = new QuestionOption();
                option.setQuestion(question);
                option.setOptionLabel(String.valueOf((char) ('A' + i)));
                option.setOptionContent(questionDto.getOptions().get(i).trim());
                option.setOptionOrder(i);
                newOptions.add(option);
            }
            question.getOptions().addAll(newOptions);
        }

        // 处理答案 - 将答案列表转换为字符串
        if (questionDto.getAnswers() != null && !questionDto.getAnswers().isEmpty()) {
            if (questionDto.getAnswers().size() == 1) {
                // 单选题、判断题、填空题、简答题：单个答案
                question.setAnswer(questionDto.getAnswers().get(0));
            } else {
                // 多选题：多个答案用逗号分隔
                String answerStr = String.join(",", questionDto.getAnswers());
                question.setAnswer(answerStr);
            }
        }

        return questionRepository.save(question);
    }

    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    // 处理选项
    private List<QuestionOption> processOptions(Question question, List<String> optionContents) {
        List<QuestionOption> options = new ArrayList<>();
        for (int i = 0; i < optionContents.size(); i++) {
            QuestionOption option = new QuestionOption();
            option.setQuestion(question);
            String optionLabel = String.valueOf((char) ('A' + i)); // A, B, C, D...
            option.setOptionLabel(optionLabel);
            option.setOptionContent(optionContents.get(i).trim());
            option.setOptionOrder(i);
            options.add(option);
        }
        return options;
    }

    // 答题功能
    public List<QuestionDto> getQuestionsForExam(Long questionBankId) {
        List<Question> questions = findByQuestionBank(questionBankId);
        return questions.stream()
                .map(this::convertToDtoWithAnswers)
                .collect(Collectors.toList());
    }

    private boolean checkAnswer(Question question, List<String> userAnswers, List<String> correctAnswers) {
        if (userAnswers == null || userAnswers.isEmpty() || correctAnswers == null || correctAnswers.isEmpty()) {
            return false;
        }

        // 单选题和多选题的判分逻辑
        if ("SINGLE_CHOICE".equals(question.getType()) || "MULTIPLE_CHOICE".equals(question.getType()) || "TRUE_FALSE".equals(question.getType())) {
            // 将用户答案和正确答案都转换为选项标签进行比较
            List<String> userAnswerLabels = userAnswers.stream()
                    .map(answer -> {
                        // 如果是选项内容，转换为选项标签
                        if (question.getOptions() != null) {
                            for (QuestionOption option : question.getOptions()) {
                                if (option.getOptionContent().equals(answer)) {
                                    return option.getOptionLabel();
                                }
                            }
                        }
                        return answer;
                    })
                    .collect(Collectors.toList());

            return userAnswerLabels.size() == correctAnswers.size() &&
                    new HashSet<>(userAnswerLabels).containsAll(correctAnswers) &&
                    new HashSet<>(correctAnswers).containsAll(userAnswerLabels);
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
        questionBankRepository.findById(importDto.getQuestionBankId())
                .orElseThrow(() -> new RuntimeException("题库不存在"));

        for (QuestionDto questionDto : importDto.getQuestions()) {
            createQuestion(questionDto);
        }
    }

    // 提交题库练习
    public QuestionBankResultDto submitQuestionBankExam(Long userId, Long questionBankId, List<QuestionAnswerDto> answers, Integer timeTaken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        QuestionBank questionBank = questionBankRepository.findById(questionBankId)
                .orElseThrow(() -> new RuntimeException("题库不存在"));

        // 获取题库中的所有题目
        List<Question> questions = questionRepository.findByQuestionBankId(questionBankId);
        if (questions.isEmpty()) {
            throw new RuntimeException("题库中没有题目");
        }

        // 计算得分
        int totalScore = 0;
        int correctAnswers = 0;
        List<QuestionBankQuestionResult> questionResults = new ArrayList<>();

        for (Question question : questions) {
            // 查找用户答案
            QuestionAnswerDto userAnswer = answers.stream()
                    .filter(a -> a.getQuestionId().equals(question.getId()))
                    .findFirst()
                    .orElse(null);

            boolean isCorrect = false;
            List<String> userAnswers = new ArrayList<>();
            List<String> correctAnswersList = new ArrayList<>();

            // 获取正确答案
            if ("MULTIPLE_CHOICE".equals(question.getType())) {
                correctAnswersList = Arrays.asList(question.getAnswer().split(","));
            } else {
                correctAnswersList = Collections.singletonList(question.getAnswer());
            }

            if (userAnswer != null && userAnswer.getUserAnswer() != null && !userAnswer.getUserAnswer().trim().isEmpty()) {
                String userAnswerStr = userAnswer.getUserAnswer().trim();

                // 解析用户答案
                if ("MULTIPLE_CHOICE".equals(question.getType())) {
                    // 多选题：用户答案可能是 "A,B,C" 格式
                    userAnswers = Arrays.asList(userAnswerStr.split(","));
                } else {
                    // 单选题、判断题、填空题、简答题：单个答案
                    userAnswers = Collections.singletonList(userAnswerStr);
                }

                // 检查答案是否正确
                isCorrect = checkAnswer(question, userAnswers, correctAnswersList);
            }

            // 计算题目得分
            int questionMaxScore = 5;
            int questionScore = isCorrect ? questionMaxScore : 0;
            totalScore += questionMaxScore;

            if (isCorrect) {
                correctAnswers++;
            }

            // 创建题目结果
            QuestionBankQuestionResult questionResult = new QuestionBankQuestionResult();
            questionResult.setQuestion(question);
            questionResult.setIsCorrect(isCorrect);
            questionResult.setUserAnswer(String.join(",", userAnswers));
            questionResult.setCorrectAnswer(String.join(",", correctAnswersList));
            questionResult.setScore(questionScore);
            questionResult.setMaxScore(questionMaxScore);
            questionResult.setExplanation(question.getExplanation());

            questionResults.add(questionResult);
        }

        // 计算总分
        int score = questionResults.stream()
                .mapToInt(QuestionBankQuestionResult::getScore)
                .sum();

        // 判断是否通过（降低及格线到50%，或者根据题目数量动态调整）
        boolean isPassed;
        if (questions.size() <= 5) {
            // 题目较少时，只要答对一半以上就算通过
            isPassed = correctAnswers >= (questions.size() / 2);
        } else {
            // 题目较多时，使用50%的及格线
            isPassed = score >= (totalScore * 0.5);
        }

        // 保存题库练习结果
        QuestionBankResult questionBankResult = new QuestionBankResult();
        questionBankResult.setUser(user);
        questionBankResult.setQuestionBank(questionBank);
        questionBankResult.setTotalQuestions(questions.size());
        questionBankResult.setCorrectAnswers(correctAnswers);
        questionBankResult.setScore(score);
        questionBankResult.setTotalScore(totalScore);
        questionBankResult.setTimeTaken(timeTaken);
        questionBankResult.setIsPassed(isPassed);
        questionBankResult.setSubmitTime(LocalDateTime.now());

        questionBankResult = questionBankResultRepository.save(questionBankResult);

        // 保存题目结果
        for (QuestionBankQuestionResult questionResult : questionResults) {
            questionResult.setQuestionBankResult(questionBankResult);
        }
        questionBankQuestionResultRepository.saveAll(questionResults);

        // 创建返回结果
        QuestionBankResultDto result = new QuestionBankResultDto();
        result.setId(questionBankResult.getId());
        result.setUserId(userId);
        result.setUserName(user.getRealName());
        result.setQuestionBankId(questionBankId);
        result.setQuestionBankTitle(questionBank.getTitle());
        result.setTotalQuestions(questions.size());
        result.setCorrectAnswers(correctAnswers);
        result.setScore(score);
        result.setTotalScore(totalScore);
        result.setTimeTaken(questionBankResult.getTimeTaken());
        result.setIsPassed(isPassed);
        result.setSubmitTime(questionBankResult.getSubmitTime());
        result.setCreateTime(questionBankResult.getCreateTime());

        // 计算练习次数
        List<QuestionBankResult> allResults = questionBankResultRepository.findByUserIdAndQuestionBankIdOrderBySubmitTimeAsc(userId, questionBankId);
        int attemptNumber = 1;
        for (QuestionBankResult res : allResults) {
            if (res.getId().equals(questionBankResult.getId())) {
                break;
            }
            attemptNumber++;
        }
        result.setAttemptNumber(attemptNumber);

        // 转换题目结果
        List<QuestionBankResultDto.QuestionResult> questionResultDtos = questionResults.stream()
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
        result.setQuestionResults(questionResultDtos);

        return result;
    }

    private QuestionDto convertToDtoWithAnswers(Question question) {
        QuestionDto dto = new QuestionDto();
        BeanUtils.copyProperties(question, dto);
        dto.setQuestionBankId(question.getQuestionBank().getId());
        dto.setQuestionBankTitle(question.getQuestionBank().getTitle());

        // 转换选项
        if (question.getOptions() != null) {
            dto.setOptions(question.getOptions().stream()
                    .map(QuestionOption::getOptionContent)
                    .collect(Collectors.toList()));
        }

        // 转换答案 - 从字符串转换为列表
        if (question.getAnswer() != null && !question.getAnswer().trim().isEmpty()) {
            String answerStr = question.getAnswer().trim();
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

    private QuestionDto convertToDto(Question question) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setQuestionBankId(question.getQuestionBank().getId());
        dto.setQuestionBankTitle(question.getQuestionBank().getTitle());
        dto.setContent(question.getContent());
        dto.setType(question.getType());
        dto.setExplanation(question.getExplanation());
        // 转换选项
        if (question.getOptions() != null) {
            dto.setOptions(question.getOptions().stream()
                    .map(QuestionOption::getOptionContent)
                    .collect(Collectors.toList()));
        }
        // 答题时不返回答案
        dto.setAnswers(null);
        return dto;
    }
}
