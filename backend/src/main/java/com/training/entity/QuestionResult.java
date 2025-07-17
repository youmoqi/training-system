package com.training.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "question_results")
@Data
public class QuestionResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "exam_result_id", nullable = false)
    private ExamResult examResult;
    
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    @ElementCollection
    @CollectionTable(name = "question_result_user_answers", 
                     joinColumns = @JoinColumn(name = "question_result_id"))
    @Column(name = "user_answer")
    private List<String> userAnswers;
    
    @ElementCollection
    @CollectionTable(name = "question_result_correct_answers", 
                     joinColumns = @JoinColumn(name = "question_result_id"))
    @Column(name = "correct_answer")
    private List<String> correctAnswers;
    
    @Column(nullable = false)
    private Boolean isCorrect;
    
    @Column(columnDefinition = "TEXT")
    private String explanation;
} 