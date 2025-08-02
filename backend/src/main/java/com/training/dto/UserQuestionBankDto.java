package com.training.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * @author YIZ
 */
@Data
public class UserQuestionBankDto {
    private Long id;
    private Long userId;
    private Long questionBankId;
    private String questionBankTitle;
    private String questionBankDescription;
    private Double questionBankPrice;
    private LocalDateTime purchaseTime;
    private Boolean isCompleted;
    private LocalDateTime completeTime;
    private Integer score;
}
