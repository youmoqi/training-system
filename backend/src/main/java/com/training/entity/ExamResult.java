package com.training.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "exam_results")
@Data
public class ExamResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "question_bank_id", nullable = false)
    private QuestionBank questionBank;
    
    @Column(nullable = false)
    private Integer totalQuestions;
    
    @Column(nullable = false)
    private Integer correctAnswers;
    
    @Column(nullable = false)
    private Integer score;
    
    @Column(name = "exam_time", nullable = false)
    private LocalDateTime examTime;
    
    @Column(name = "time_taken")
    private Integer timeTaken; // 考试用时（秒）
    
    @OneToMany(mappedBy = "examResult", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<QuestionResult> questionResults = new ArrayList<>();
} 