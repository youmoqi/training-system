package com.training.controller;

import com.training.dto.ApiResponse;
import com.training.entity.JobCategory;
import com.training.entity.VisibilityCategory;
import com.training.repository.JobCategoryRepository;
import com.training.repository.VisibilityCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private JobCategoryRepository jobCategoryRepository;

    @Autowired
    private VisibilityCategoryRepository visibilityCategoryRepository;

    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse<List<JobCategory>>> listJobCategories() {
        List<JobCategory> all = jobCategoryRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("获取作业类别成功", all));
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<VisibilityCategory>>> listRoleCategories() {
        List<VisibilityCategory> all = visibilityCategoryRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("获取角色类别成功", all));
    }
} 