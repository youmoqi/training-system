package com.training.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.persistence.*;
import java.util.List;

/**
 * @author 14798
 */
@Data
@Entity
@Table(name = "question_results")
public class QuestionResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_result_id", nullable = false)
    @JsonIgnore
    private ExamResult examResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private Question question;

    @Column(nullable = false)
    private Boolean correct;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @ElementCollection
    @CollectionTable(name = "question_result_answers",
                    joinColumns = @JoinColumn(name = "question_result_id"))
    @Column(name = "answer")
    private List<String> userAnswers;

    @ElementCollection
    @CollectionTable(name = "question_result_correct_answers",
                    joinColumns = @JoinColumn(name = "question_result_id"))
    @Column(name = "answer")
    private List<String> correctAnswers;
}
