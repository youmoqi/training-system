package com.training.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.dto.ApiResponse;
import com.training.dto.LoginDto;
import com.training.dto.UserRegistrationDto;
import com.training.entity.RegistrationConfig;
import com.training.entity.User;
import com.training.service.RegistrationConfigService;
import com.training.service.UserService;
import com.training.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RegistrationConfigService registrationConfigService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(@RequestHeader("Authorization") String authorization) {
        try {
            Long userId = jwtUtil.getUserIdFromHeader(authorization);
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            return ResponseEntity.ok(ApiResponse.success("获取用户信息成功", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(
            @RequestParam(value = "facePhoto", required = false) MultipartFile facePhoto,
            @RequestParam("userData") String userData) {
        try {
            // 解析 userData 到 DTO
            UserRegistrationDto registrationDto = objectMapper.readValue(userData, UserRegistrationDto.class);

            // 读取注册配置并校验必填项
            RegistrationConfig cfg = registrationConfigService.getOrCreate();
            List<String> missingFields = validateRequiredFields(cfg.getFieldsConfigJson(), registrationDto, facePhoto);
            if (!missingFields.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("以下字段为必填: " + String.join(", ", missingFields)));
            }

            // 处理人脸照片上传（如有）
            String facePhotoUrl = null;
            if (facePhoto != null && !facePhoto.isEmpty()) {
                facePhotoUrl = uploadFile(facePhoto, "faces");
            }

            User user = userService.registerUser(registrationDto, facePhotoUrl);
            return ResponseEntity.ok(ApiResponse.success("注册成功", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody LoginDto loginDto) {
        try {
            User user = userService.findByUsername(loginDto.getUsername())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            String token = jwtUtil.generateToken(user.getUsername(), user.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);
            return ResponseEntity.ok(ApiResponse.success("登录成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    private String uploadFile(MultipartFile file, String directory) throws IOException {
        String uploadDir = "uploads/" + directory + "/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);
        Files.write(filePath, file.getBytes());

        return uploadDir + fileName;
    }

    private List<String> validateRequiredFields(String fieldsConfigJson, UserRegistrationDto dto, MultipartFile facePhoto) {
        List<String> missing = new ArrayList<>();
        try {
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Object>> cfg = objectMapper.readValue(fieldsConfigJson, Map.class);
            if (isRequired(cfg, "username") && isBlank(dto.getUsername())) missing.add("用户名");
            if (isRequired(cfg, "password") && isBlank(dto.getPassword())) missing.add("密码");
            if (isRequired(cfg, "realName") && isBlank(dto.getRealName())) missing.add("真实姓名");
            if (isRequired(cfg, "gender") && isBlank(dto.getGender())) missing.add("性别");
            if (isRequired(cfg, "idCard") && isBlank(dto.getIdCard())) missing.add("身份证");
            if (isRequired(cfg, "phone") && isBlank(dto.getPhone())) missing.add("手机号");
            if (isRequired(cfg, "workUnit") && isBlank(dto.getWorkUnit())) missing.add("工作单位");
            if (isRequired(cfg, "trainingType") && isBlank(dto.getTrainingType())) missing.add("培训类型");
            if (isRequired(cfg, "jobCategory") && dto.getJobCategoryId() == null) missing.add("岗位类别");
            if (isRequired(cfg, "paymentAmount") && dto.getPaymentAmount() == null) missing.add("缴费金额");
            if (isRequired(cfg, "facePhotoUrl") && (facePhoto == null || facePhoto.isEmpty())) missing.add("人脸照片");
        } catch (Exception ignored) {
        }
        return missing;
    }

    private boolean isRequired(Map<String, Map<String, Object>> cfg, String key) {
        return cfg != null && cfg.containsKey(key) && Boolean.TRUE.equals(cfg.get(key).get("required"));
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
