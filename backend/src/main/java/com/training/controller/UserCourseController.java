package com.training.controller;

import com.training.dto.ApiResponse;
import com.training.dto.UserCourseDto;
import com.training.service.UserCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-courses")
@CrossOrigin(origins = "*")
public class UserCourseController {

    @Autowired
    private UserCourseService userCourseService;

    /**
     * 分页查询用户课程数据
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserCourseDto>>> getUserCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String completionStatus) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<UserCourseDto> userCourses = userCourseService.findUserCourses(
                pageable, searchKeyword, courseId, completionStatus);
            
            return ResponseEntity.ok(ApiResponse.success("获取用户课程数据成功", userCourses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 