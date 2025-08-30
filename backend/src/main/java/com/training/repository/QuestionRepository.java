package com.training.repository;

import com.training.entity.Exam.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author YIZ
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // 分页查询方法
    Page<Question> findByContentContaining(String content, Pageable pageable);

    Page<Question> findByType(String type, Pageable pageable);

    Page<Question> findByContentContainingAndType(String content, String type, Pageable pageable);

    // 按题库ID查询
    Page<Question> findByQuestionBankId(Long questionBankId, Pageable pageable);

    Page<Question> findByContentContainingAndQuestionBankId(String content, Long questionBankId, Pageable pageable);

    Page<Question> findByTypeAndQuestionBankId(String type, Long questionBankId, Pageable pageable);

    Page<Question> findByContentContainingAndTypeAndQuestionBankId(String content, String type, Long questionBankId, Pageable pageable);

    // 自定义查询方法
    List<Question> findByQuestionBankId(@Param("questionBankId") Long questionBankId);

    // 随机抽取题目方法
    @Query(value = "SELECT * FROM questions q WHERE q.type = :type AND q.question_bank_id IN :questionBankIds ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestionsByTypeAndBanks(String type, List<Long> questionBankIds, int limit);

    // 按类型随机抽取题目（不限制题库）
    @Query(value = "SELECT * FROM questions q WHERE q.type = :type ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestionsByType(String type, int limit);
}
