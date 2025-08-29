package com.training.controller;

import com.training.dto.ApiResponse;
import com.training.entity.JobCategory;
import com.training.entity.Role;
import com.training.repository.JobCategoryRepository;
import com.training.repository.RoleRepository;
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
    private RoleRepository roleRepository;

    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse<List<JobCategory>>> listJobCategories() {
        List<JobCategory> all = jobCategoryRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("获取作业类别成功", all));
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<Role>>> listRoleCategories() {
        List<Role> all = roleRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("获取角色类别成功", all));
    }
}
