package com.training.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "registration_config")
public class RegistrationConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // JSON string storing config per field, e.g. {"username":{"required":true,"editable":false}, ...}
    @Lob
    @Column(nullable = false)
    private String fieldsConfigJson;
} 