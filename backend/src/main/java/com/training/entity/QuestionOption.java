package com.training.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.persistence.*;

/**
 * @author YIZ
 */
@Data
@Entity
@Table(name = "question_options")
public class QuestionOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private Question question;

    @Column(name = "option_label", nullable = false)
    // A, B, C, D...
    private String optionLabel;

    @Column(name = "option_content", nullable = false)
    private String optionContent;

    @Column(name = "option_order", nullable = false)
    private Integer optionOrder;
}
