package com.training.controller;

import com.training.dto.ApiResponse;
import com.training.dto.ExamCategoryDto;
import com.training.service.ExamCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam-categories")
@CrossOrigin(origins = "*")
public class ExamCategoryController {

    @Autowired
    private ExamCategoryService examCategoryService;

    @GetMapping
    public ApiResponse<List<ExamCategoryDto>> getAllActiveCategories() {
        return examCategoryService.getAllActiveCategories();
    }

    @GetMapping("/parents")
    public ApiResponse<List<ExamCategoryDto>> getParentCategories() {
        return examCategoryService.getParentCategories();
    }

    @GetMapping("/subs/{parentCode}")
    public ApiResponse<List<ExamCategoryDto>> getSubCategories(@PathVariable String parentCode) {
        return examCategoryService.getSubCategories(parentCode);
    }

    @GetMapping("/{code}")
    public ApiResponse<ExamCategoryDto> getCategoryByCode(@PathVariable String code) {
        return examCategoryService.getCategoryByCode(code);
    }
} 