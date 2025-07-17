package com.training.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvitationLinkDto {
    private Long id;
    private String password;
    private String title;
    private String description;
    private LocalDateTime expireTime;
    private List<Long> courseIds;
} 