package com.training.service;

import com.training.dto.ApiResponse;
import com.training.dto.ExamCategoryDto;
import com.training.entity.ExamCategory;
import com.training.repository.ExamCategoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamCategoryService {

    @Autowired
    private ExamCategoryRepository examCategoryRepository;

    public ApiResponse<List<ExamCategoryDto>> getAllActiveCategories() {
        try {
            List<ExamCategory> categories = examCategoryRepository.findByIsActiveTrueOrderBySortOrderAsc();
            List<ExamCategoryDto> categoryDtos = categories.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            
            return ApiResponse.success(categoryDtos);
        } catch (Exception e) {
            return ApiResponse.error("获取考试分类失败: " + e.getMessage());
        }
    }

    public ApiResponse<List<ExamCategoryDto>> getParentCategories() {
        try {
            List<ExamCategory> categories = examCategoryRepository.findActiveParentCategories();
            List<ExamCategoryDto> categoryDtos = categories.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            
            return ApiResponse.success(categoryDtos);
        } catch (Exception e) {
            return ApiResponse.error("获取父级考试分类失败: " + e.getMessage());
        }
    }

    public ApiResponse<List<ExamCategoryDto>> getSubCategories(String parentCode) {
        try {
            List<ExamCategory> categories = examCategoryRepository.findActiveSubCategories(parentCode);
            List<ExamCategoryDto> categoryDtos = categories.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            
            return ApiResponse.success(categoryDtos);
        } catch (Exception e) {
            return ApiResponse.error("获取子级考试分类失败: " + e.getMessage());
        }
    }

    public ApiResponse<ExamCategoryDto> getCategoryByCode(String code) {
        try {
            ExamCategory category = examCategoryRepository.findByCode(code)
                    .orElse(null);
            
            if (category == null) {
                return ApiResponse.error("考试分类不存在");
            }
            
            return ApiResponse.success(convertToDto(category));
        } catch (Exception e) {
            return ApiResponse.error("获取考试分类失败: " + e.getMessage());
        }
    }

    private ExamCategoryDto convertToDto(ExamCategory category) {
        ExamCategoryDto dto = new ExamCategoryDto();
        BeanUtils.copyProperties(category, dto);
        return dto;
    }
} 