package com.training.service;

import com.training.entity.QuestionBank;
import com.training.entity.User;
import com.training.entity.UserQuestionBank;
import com.training.dto.UserQuestionBankDto;
import com.training.repository.QuestionBankRepository;
import com.training.repository.UserQuestionBankRepository;
import com.training.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuestionBankService {
    
    @Autowired
    private QuestionBankRepository questionBankRepository;
    
    @Autowired
    private UserQuestionBankRepository userQuestionBankRepository;
    
    @Autowired
    private UserRepository userRepository;

    // 分页查询题库
    public Page<QuestionBank> findQuestionBanksWithPagination(Pageable pageable, String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return questionBankRepository.findByTitleContainingOrDescriptionContaining(
                keyword, keyword, pageable);
        } else {
            return questionBankRepository.findAll(pageable);
        }
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
        return questionBankRepository.save(questionBank);
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

    public List<UserQuestionBank> getUserQuestionBanks(User user) {
        return userQuestionBankRepository.findByUser(user);
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
        dto.setIsCompleted(userQuestionBank.getIsCompleted());
        dto.setCompleteTime(userQuestionBank.getCompleteTime());
        dto.setScore(userQuestionBank.getScore());
        return dto;
    }

    public void updateQuestionBankScore(User user, QuestionBank questionBank, Integer score) {
        Optional<UserQuestionBank> userQuestionBankOpt = userQuestionBankRepository.findByUserAndQuestionBank(user, questionBank);
        if (userQuestionBankOpt.isPresent()) {
            UserQuestionBank userQuestionBank = userQuestionBankOpt.get();
            userQuestionBank.setScore(score);
            userQuestionBank.setIsCompleted(true);
            userQuestionBankRepository.save(userQuestionBank);
        }
    }
} 