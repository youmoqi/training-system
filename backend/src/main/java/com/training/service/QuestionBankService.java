package com.training.service;

import com.training.entity.QuestionBank;
import com.training.entity.User;
import com.training.entity.UserQuestionBank;
import com.training.dto.UserQuestionBankDto;
import com.training.repository.QuestionBankRepository;
import com.training.repository.UserQuestionBankRepository;
import com.training.repository.UserRepository;
import com.training.repository.QuestionBankResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.training.entity.QuestionBankResult;
import java.util.ArrayList;

@Service
@Transactional
public class QuestionBankService {

    @Autowired
    private QuestionBankRepository questionBankRepository;

    @Autowired
    private UserQuestionBankRepository userQuestionBankRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionBankResultRepository questionBankResultRepository;

    // 获取用户已购题库
    private List<QuestionBank> getUserPurchasedQuestionBanks(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ArrayList<>();
        }
        return userQuestionBankRepository.findByUser(user).stream()
            .map(UserQuestionBank::getQuestionBank)
            .collect(Collectors.toList());
    }

    // 管理员分页查询题库（不包含过滤逻辑）
    public Page<QuestionBank> findQuestionBanksForAdmin(Pageable pageable, String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return questionBankRepository.findByTitleContainingOrDescriptionContaining(
                keyword, keyword, pageable);
        } else {
            return questionBankRepository.findAll(pageable);
        }
    }

    // 学员获取我的题库（已购题库）
    public Page<UserQuestionBankDto> findMyQuestionBanks(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Page<UserQuestionBank> userQuestionBanks = userQuestionBankRepository.findByUser(user, pageable);

        // 转换为DTO
        List<UserQuestionBankDto> dtoList = userQuestionBanks.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, userQuestionBanks.getTotalElements());
    }

    // 学员获取可购买的题库（未购题库）
    public Page<QuestionBank> findAvailableQuestionBanks(Long userId, String userRole, Pageable pageable) {
        // 获取用户已购题库
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        List<QuestionBank> userPurchasedQuestionBanks = getUserPurchasedQuestionBanks(userId);

        // 根据角色获取在线题库
        Page<QuestionBank> onlineQuestionBanks;
        if (userRole != null && !userRole.isEmpty()) {
            onlineQuestionBanks = questionBankRepository.findByIsOnlineTrueAndVisibleRolesContaining(userRole, pageable);
        } else {
            onlineQuestionBanks = questionBankRepository.findByIsOnlineTrue(pageable);
        }

        // 过滤掉已购题库
        List<QuestionBank> filteredContent = onlineQuestionBanks.getContent().stream()
            .filter(questionBank -> !userPurchasedQuestionBanks.contains(questionBank))
            .collect(Collectors.toList());

        return new PageImpl<>(filteredContent, pageable, onlineQuestionBanks.getTotalElements() - userPurchasedQuestionBanks.size());
    }

    public QuestionBank createQuestionBank(QuestionBank questionBank) {
        return questionBankRepository.save(questionBank);
    }

    public Optional<QuestionBank> findById(Long id) {
        return questionBankRepository.findById(id);
    }

    public List<QuestionBank> findAllQuestionBanks() {
        return questionBankRepository.findAll();
    }

    public List<QuestionBank> findOnlineQuestionBanks() {
        return questionBankRepository.findByIsOnlineTrue();
    }

    public List<QuestionBank> findQuestionBanksByRole(String role) {
        return questionBankRepository.findByIsOnlineTrueAndVisibleRolesContaining(role);
    }

    public QuestionBank updateQuestionBank(QuestionBank questionBank) {
        // 获取现有的题库数据
        QuestionBank existingQuestionBank = questionBankRepository.findById(questionBank.getId())
                .orElseThrow(() -> new RuntimeException("题库不存在"));

        // 更新字段，但保留 createTime
        existingQuestionBank.setTitle(questionBank.getTitle());
        existingQuestionBank.setDescription(questionBank.getDescription());
        existingQuestionBank.setPrice(questionBank.getPrice());
        existingQuestionBank.setIsOnline(questionBank.getIsOnline());
        existingQuestionBank.setVisibleRoles(questionBank.getVisibleRoles());

        // updateTime 会通过 @PreUpdate 自动设置

        return questionBankRepository.save(existingQuestionBank);
    }

    public void deleteQuestionBank(Long id) {
        questionBankRepository.deleteById(id);
    }

    public boolean purchaseQuestionBank(User user, QuestionBank questionBank) {
        // 检查用户是否已经购买过这个题库
        if (userQuestionBankRepository.existsByUserAndQuestionBank(user, questionBank)) {
            throw new RuntimeException("您已经购买过这个题库");
        }

        // 检查用户缴费额度是否足够
        if (user.getPaymentAmount() < questionBank.getPrice()) {
            throw new RuntimeException("缴费额度不足，无法购买题库");
        }

        // 扣除缴费额度并保存用户信息
        user.setPaymentAmount(user.getPaymentAmount() - questionBank.getPrice());
        userRepository.save(user);

        // 创建用户题库关联
        UserQuestionBank userQuestionBank = new UserQuestionBank();
        userQuestionBank.setUser(user);
        userQuestionBank.setQuestionBank(questionBank);
        userQuestionBankRepository.save(userQuestionBank);

        return true;
    }

    public List<UserQuestionBankDto> getUserQuestionBankDtos(User user) {
        List<UserQuestionBank> userQuestionBanks = userQuestionBankRepository.findByUser(user);
        return userQuestionBanks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<QuestionBank> getPurchasedQuestionBanks(User user) {
        List<UserQuestionBank> userQuestionBanks = userQuestionBankRepository.findByUser(user);
        return userQuestionBanks.stream()
                .map(UserQuestionBank::getQuestionBank)
                .collect(Collectors.toList());
    }

    private UserQuestionBankDto convertToDto(UserQuestionBank userQuestionBank) {
        UserQuestionBankDto dto = new UserQuestionBankDto();
        dto.setId(userQuestionBank.getId());
        dto.setUserId(userQuestionBank.getUser().getId());
        dto.setQuestionBankId(userQuestionBank.getQuestionBank().getId());
        dto.setQuestionBankTitle(userQuestionBank.getQuestionBank().getTitle());
        dto.setQuestionBankDescription(userQuestionBank.getQuestionBank().getDescription());
        dto.setQuestionBankPrice(userQuestionBank.getQuestionBank().getPrice());
        dto.setPurchaseTime(userQuestionBank.getPurchaseTime());

        // 根据练习记录判断完成状态和最高得分
        List<QuestionBankResult> results = questionBankResultRepository.findByUserIdAndQuestionBankIdOrderBySubmitTimeDesc(
                userQuestionBank.getUser().getId(), userQuestionBank.getQuestionBank().getId());

        if (!results.isEmpty()) {
            // 找到最高得分
            Integer maxScore = results.stream()
                    .mapToInt(QuestionBankResult::getScore)
                    .max()
                    .orElse(0);
            dto.setScore(maxScore);

            // 判断是否完成（只要有练习记录就认为完成）
            boolean isCompleted = true;
            dto.setIsCompleted(isCompleted);

            // 设置完成时间（第一次练习的时间）
            if (isCompleted) {
                QuestionBankResult firstResult = results.get(results.size() - 1); // 获取最早的记录
                dto.setCompleteTime(firstResult.getSubmitTime());
            }
        } else {
            dto.setScore(null);
            dto.setIsCompleted(false);
            dto.setCompleteTime(null);
        }

        return dto;
    }
}
