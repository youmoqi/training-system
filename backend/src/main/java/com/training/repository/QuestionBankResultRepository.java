package com.training.repository;

import com.training.entity.QuestionBankResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionBankResultRepository extends JpaRepository<QuestionBankResult, Long> {
    // 根据题库ID查询题库练习结果
    List<QuestionBankResult> findByQuestionBankIdOrderBySubmitTimeDesc(Long questionBankId);

    // 根据用户ID分页查询题库练习历史
    @Query("SELECT qbr FROM QuestionBankResult qbr WHERE qbr.user.id = :userId ORDER BY qbr.submitTime DESC")
    Page<QuestionBankResult> findByUserIdOrderBySubmitTimeDesc(@Param("userId") Long userId, Pageable pageable);

    // 根据用户ID和通过状态分页查询题库练习历史
    @Query("SELECT qbr FROM QuestionBankResult qbr WHERE qbr.user.id = :userId AND qbr.isPassed = :isPassed ORDER BY qbr.submitTime DESC")
    Page<QuestionBankResult> findByUserIdAndIsPassedOrderBySubmitTimeDesc(@Param("userId") Long userId, @Param("isPassed") Boolean isPassed, Pageable pageable);

    // 根据用户ID和题库名称关键词分页查询题库练习历史
    @Query("SELECT qbr FROM QuestionBankResult qbr WHERE qbr.user.id = :userId AND qbr.questionBank.title LIKE %:keyword% ORDER BY qbr.submitTime DESC")
    Page<QuestionBankResult> findByUserIdAndQuestionBankTitleContainingOrderBySubmitTimeDesc(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);

    // 根据用户ID、题库名称关键词和通过状态分页查询题库练习历史
    @Query("SELECT qbr FROM QuestionBankResult qbr WHERE qbr.user.id = :userId AND qbr.questionBank.title LIKE %:keyword% AND qbr.isPassed = :isPassed ORDER BY qbr.submitTime DESC")
    Page<QuestionBankResult> findByUserIdAndQuestionBankTitleContainingAndIsPassedOrderBySubmitTimeDesc(@Param("userId") Long userId, @Param("keyword") String keyword, @Param("isPassed") Boolean isPassed, Pageable pageable);

    // 根据用户ID和题库ID按提交时间升序查询题库练习结果
    @Query("SELECT qbr FROM QuestionBankResult qbr WHERE qbr.user.id = :userId AND qbr.questionBank.id = :questionBankId ORDER BY qbr.submitTime ASC")
    List<QuestionBankResult> findByUserIdAndQuestionBankIdOrderBySubmitTimeAsc(@Param("userId") Long userId, @Param("questionBankId") Long questionBankId);

    // 根据用户ID和题库ID按提交时间降序查询题库练习结果
    @Query("SELECT qbr FROM QuestionBankResult qbr WHERE qbr.user.id = :userId AND qbr.questionBank.id = :questionBankId ORDER BY qbr.submitTime DESC")
    List<QuestionBankResult> findByUserIdAndQuestionBankIdOrderBySubmitTimeDesc(@Param("userId") Long userId, @Param("questionBankId") Long questionBankId);
}
