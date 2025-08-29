package com.training.controller;

import com.training.dto.ApiResponse;
import com.training.entity.RegistrationConfig;
import com.training.service.RegistrationConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registration-config")
@CrossOrigin(origins = "*")
public class RegistrationConfigController {

    @Autowired
    private RegistrationConfigService service;

    @GetMapping
    public ResponseEntity<ApiResponse<RegistrationConfig>> get() {
        RegistrationConfig cfg = service.getOrCreate();
        return ResponseEntity.ok(ApiResponse.success("获取成功", cfg));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<RegistrationConfig>> update(@RequestBody RegistrationConfig body) {
        RegistrationConfig updated = service.update(body.getFieldsConfigJson());
        return ResponseEntity.ok(ApiResponse.success("更新成功", updated));
    }
} 