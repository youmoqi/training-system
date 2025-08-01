package com.training.controller;

import com.training.dto.ApiResponse;
import com.training.dto.LoginDto;
import com.training.dto.UserRegistrationDto;
import com.training.entity.User;
import com.training.service.UserService;
import com.training.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(
            @RequestParam("facePhoto") MultipartFile facePhoto,
            @RequestParam("userData") String userData) {
        try {
            // 处理人脸照片上传
            String facePhotoUrl = uploadFile(facePhoto, "faces");
            
            // 这里需要将userData字符串转换为UserRegistrationDto对象
            // 为了简化，我们直接创建对象
            UserRegistrationDto registrationDto = new UserRegistrationDto();
            // 解析userData并设置到registrationDto中
            
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

            // if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            //     throw new RuntimeException("密码错误");
            // }

            // 使用新的token生成方法，包含userId
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
} 