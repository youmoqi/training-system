package com.training.service;

import com.training.dto.UserCourseDto;
import com.training.entity.UserCourse;
import com.training.repository.UserCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author YIZ
 */
@Service
public class UserCourseService {

    @Autowired
    private UserCourseRepository userCourseRepository;

    /**
     * 分页查询用户课程数据
     */
    public Page<UserCourseDto> findUserCourses(Pageable pageable, String searchKeyword,
                                               Long courseId, String completionStatus) {
        Page<UserCourse> userCourses;

        // 将completionStatus转换为Boolean
        Boolean isCompleted = null;
        if (completionStatus != null) {
            if ("COMPLETED".equals(completionStatus)) {
                isCompleted = true;
            } else if ("INCOMPLETE".equals(completionStatus)) {
                isCompleted = false;
            }
        }

        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            // 按关键词搜索
            if (courseId != null) {
                if (isCompleted != null) {
                    userCourses = userCourseRepository.findByUserUsernameContainingOrUserRealNameContainingAndCourseIdAndIsCompleted(searchKeyword.trim(), searchKeyword.trim(), courseId, isCompleted, pageable);
                } else {
                    userCourses = userCourseRepository.findByUserUsernameContainingOrUserRealNameContainingAndCourseId(searchKeyword.trim(), searchKeyword.trim(), courseId, pageable);
                }
            } else {
                if (isCompleted != null) {
                    userCourses = userCourseRepository.findByUserUsernameContainingOrUserRealNameContainingAndIsCompleted(searchKeyword.trim(), searchKeyword.trim(),isCompleted, pageable);
                } else {
                    userCourses = userCourseRepository.findByUserUsernameContainingOrUserRealNameContaining(searchKeyword.trim(), searchKeyword.trim(), pageable);
                }
            }
        } else {
            // 不按关键词搜索
            if (courseId != null) {
                if (isCompleted != null) {
                    userCourses = userCourseRepository.findByCourseIdAndIsCompleted(courseId, isCompleted, pageable);
                } else {
                    userCourses = userCourseRepository.findByCourseId(courseId, pageable);
                }
            } else {
                if (isCompleted != null) {
                    userCourses = userCourseRepository.findByIsCompleted(isCompleted, pageable);
                } else {
                    userCourses = userCourseRepository.findAll(pageable);
                }
            }
        }

        return userCourses.map(this::convertToDto);
    }

    /**
     * 转换为DTO
     */
    private UserCourseDto convertToDto(UserCourse userCourse) {
        UserCourseDto dto = new UserCourseDto();
        dto.setUserId(userCourse.getUser().getId());
        dto.setUsername(userCourse.getUser().getUsername());
        dto.setRealName(userCourse.getUser().getRealName());
        dto.setCourseId(userCourse.getCourse().getId());
        dto.setCourseTitle(userCourse.getCourse().getTitle());
        dto.setIsCompleted(userCourse.getIsCompleted());
        dto.setCompletedTime(userCourse.getCompleteTime());
        return dto;
    }
}
