package com.training.repository;

import com.training.entity.Exam.QuestionBankResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * @author YIZ
 */
@Repository
public interface QuestionBankResultRepository extends JpaRepository<QuestionBankResult, Long> {
    // 根据题库ID查询题库练习结果
    List<QuestionBankResult> findByQuestionBankIdOrderBySubmitTimeDesc(Long questionBankId);

    // 根据用户ID分页查询题库练习历史
    Page<QuestionBankResult> findByUserIdOrderBySubmitTimeDesc(Long userId, Pageable pageable);

    // 根据用户ID和通过状态分页查询题库练习历史
    Page<QuestionBankResult> findByUserIdAndIsPassedOrderBySubmitTimeDesc(Long userId,Boolean isPassed, Pageable pageable);

    // 根据用户ID和题库名称关键词分页查询题库练习历史
    Page<QuestionBankResult> findByUserIdAndQuestionBankTitleContainingOrderBySubmitTimeDesc(Long userId, String questionBankTitle, Pageable pageable);

    // 根据用户ID、题库名称关键词和通过状态分页查询题库练习历史
    Page<QuestionBankResult> findByUserIdAndQuestionBankTitleContainingAndIsPassedOrderBySubmitTimeDesc(Long userId, String keyword,Boolean isPassed, Pageable pageable);

    // 根据用户ID和题库ID按提交时间升序查询题库练习结果
    List<QuestionBankResult> findByUserIdAndQuestionBankIdOrderBySubmitTimeAsc(Long userId, Long questionBankId);

    // 根据用户ID和题库ID按提交时间降序查询题库练习结果
    List<QuestionBankResult> findByUserIdAndQuestionBankIdOrderBySubmitTimeDesc(Long userId, Long questionBankId);
}
