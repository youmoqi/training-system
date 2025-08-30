package com.training.controller;

import com.training.dto.ApiResponse;
import com.training.dto.UserPermissionsUpdateDto;
import com.training.entity.User;
import com.training.service.UserExportService;
import com.training.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author YIZ
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserExportService userExportService;

    // 分页获取用户列表
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<User>>> getUsersWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> users = userService.findUsersWithPagination(pageable, keyword);
            return ResponseEntity.ok(ApiResponse.success("获取用户列表成功", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取用户列表失败: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(ApiResponse.success("获取用户列表成功", users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(ApiResponse.success("获取用户成功", user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            user.setId(id);
            User updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(ApiResponse.success("更新用户成功", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse<User>> updatePermissions(@PathVariable Long id, @RequestBody UserPermissionsUpdateDto dto) {
        try {
            User updated = userService.updateUserPermissions(id, dto);
            return ResponseEntity.ok(ApiResponse.success("更新权限成功", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("删除用户成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 导出用户数据
     *
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @param roleId         角色ID
     * @param learningStatus 学习状态 (2 completed, 1 inProgress, 0 notStarted)
     * @return Excel文件
     */
    @GetMapping("/export")
    public ResponseEntity<?> exportUserData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false) Integer learningStatus) {
        try {
            byte[] excelData = userExportService.exportUserData(startTime, endTime, roleId, learningStatus);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

            // 使用英文文件名避免编码问题
            String filename = "user_data_" + System.currentTimeMillis() + ".xlsx";
            headers.setContentDispositionFormData("attachment", filename);

            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("导出用户数据失败: " + e.getMessage()));
        }
    }
}
