package com.training.service;

import com.training.dto.CourseDto;
import com.training.dto.MyCourseDto;
import com.training.dto.UserCourseListDto;
import com.training.entity.*;
import com.training.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 14798
 */
@Service
@Transactional
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserCourseRepository userCourseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    // 获取用户已选课程
    private List<Course> getUserEnrolledCourses(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ArrayList<>();
        }
        return userCourseRepository.findByUserWithJoins(user).stream()
                .map(UserCourse::getCourse)
                .collect(Collectors.toList());
    }

    // 管理员分页查询课程（不包含过滤逻辑）
    public Page<Course> findCoursesForAdmin(Pageable pageable, String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return courseRepository.findByTitleContainingOrDescriptionContaining(
                    keyword, keyword, pageable);
        } else {
            return courseRepository.findAll(pageable);
        }
    }

    // 学员获取我的课程（已选课程）- 返回DTO
    public Page<MyCourseDto> findMyCoursesDto(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Page<UserCourse> userCourses = userCourseRepository.findByUserWithJoins(user, pageable);
        return userCourses.map(this::convertToMyCourseDto);
    }

    // 学员获取可购买的课程（未选课程）
    public Page<Course> findAvailableCourses(Long userId, Long roleId, Pageable pageable) {
        // 获取用户已选课程
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        List<Course> userEnrolledCourses = getUserEnrolledCourses(userId);

        // 根据角色获取在线课程
        Page<Course> onlineCourses;
        if (roleId != null) {
            onlineCourses = courseRepository.findByIsOnlineTrueAndVisibleRolesId(roleId, pageable);
        } else {
            onlineCourses = courseRepository.findByIsOnlineTrue(pageable);
        }

        // 过滤掉已选课程
        List<Course> filteredContent = onlineCourses.getContent().stream()
                .filter(course -> !userEnrolledCourses.contains(course))
                .collect(Collectors.toList());

        return new PageImpl<>(filteredContent, pageable, onlineCourses.getTotalElements() - userEnrolledCourses.size());
    }

    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    public List<Course> findAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> findOnlineCourses() {
        return courseRepository.findByIsOnlineTrue();
    }

    public void deleteCourse(Long id) {
        // 验证课程是否存在
        courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
        // 检查是否有用户已选这门课程
        List<UserCourse> userCourses = userCourseRepository.findByCourseId(id);
        if (!userCourses.isEmpty()) {
            throw new RuntimeException("该课程已有用户选择，无法删除");
        }
        courseRepository.deleteById(id);
    }

    public void enrollCourse(User user, Course course) {
        // 检查用户是否已经选过这门课
        if (userCourseRepository.existsByUserAndCourse(user, course)) {
            throw new RuntimeException("您已经选过这门课程");
        }

        // 检查用户缴费额度是否足够
        if (user.getPaymentAmount() < course.getPrice()) {
            throw new RuntimeException("缴费额度不足，无法选课");
        }

        // 扣除缴费额度
        user.setPaymentAmount(user.getPaymentAmount() - course.getPrice());

        // 创建用户课程关联
        UserCourse userCourse = new UserCourse();
        userCourse.setUser(user);
        userCourse.setCourse(course);
        userCourseRepository.save(userCourse);
    }

    public void enrollCourseWithInvitation(User user, Course course) {
        // 检查用户是否已经选过这门课
        if (userCourseRepository.existsByUserAndCourse(user, course)) {
            // 如果已经选过，则不进行任何操作
            return;
        }
        // 创建用户课程关联
        UserCourse userCourse = new UserCourse();
        userCourse.setUser(user);
        userCourse.setCourse(course);
        userCourseRepository.save(userCourse);
    }

    public List<UserCourseListDto> getUserCoursesDto(User user) {
        List<UserCourse> userCourses = userCourseRepository.findByUserWithJoins(user);
        return userCourses.stream()
                .map(this::convertToUserCourseListDto)
                .collect(Collectors.toList());
    }

    /**
     * 转换为MyCourseDto
     */
    private MyCourseDto convertToMyCourseDto(UserCourse userCourse) {
        MyCourseDto dto = new MyCourseDto();
        dto.setId(userCourse.getId());
        dto.setCourseId(userCourse.getCourse().getId());
        dto.setCourseTitle(userCourse.getCourse().getTitle());
        dto.setCourseDescription(userCourse.getCourse().getDescription());
        dto.setCoverImageUrl(userCourse.getCourse().getCoverImageUrl());
        dto.setVideoUrl(userCourse.getCourse().getVideoUrl());
        dto.setPrice(userCourse.getCourse().getPrice());
        dto.setIsOnline(userCourse.getCourse().getIsOnline());
        dto.setEnrollTime(userCourse.getEnrollTime());
        dto.setIsCompleted(userCourse.getIsCompleted());
        dto.setCompleteTime(userCourse.getCompleteTime());
        dto.setWatchProgress(userCourse.getWatchProgress());
        return dto;
    }

    /**
     * 转换为UserCourseListDto
     */
    private UserCourseListDto convertToUserCourseListDto(UserCourse userCourse) {
        UserCourseListDto dto = new UserCourseListDto();
        dto.setId(userCourse.getId());
        dto.setUserId(userCourse.getUser().getId());
        dto.setUsername(userCourse.getUser().getUsername());
        dto.setRealName(userCourse.getUser().getRealName());
        dto.setCourseId(userCourse.getCourse().getId());
        dto.setCourseTitle(userCourse.getCourse().getTitle());
        dto.setEnrollTime(userCourse.getEnrollTime());
        dto.setIsCompleted(userCourse.getIsCompleted());
        dto.setCompleteTime(userCourse.getCompleteTime());
        dto.setWatchProgress(userCourse.getWatchProgress());
        return dto;
    }

    public void updateCourseProgress(User user, Course course, Integer progress) {
        Optional<UserCourse> userCourseOpt = userCourseRepository.findByUserAndCourse(user, course);
        if (userCourseOpt.isPresent()) {
            UserCourse userCourse = userCourseOpt.get();
            userCourse.setWatchProgress(progress);
            if (progress >= 100) {
                userCourse.setIsCompleted(true);
            }
            userCourseRepository.save(userCourse);
        }
    }

    public void unrollCourse(User user, Course course) {
        UserCourse userCourse = userCourseRepository.findByUserAndCourse(user, course)
                .orElseThrow(() -> new RuntimeException("您没有选择这门课程"));
        userCourseRepository.delete(userCourse);
    }

    public Course createOrUpdateCourse(Long id, CourseDto dto) {
        Course course = id == null
                ? new Course()
                : courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setVideoUrl(dto.getVideoUrl());
        course.setPrice(dto.getPrice());
        course.setIsOnline(Boolean.TRUE.equals(dto.getIsOnline()));
        course.setCoverImageUrl(dto.getCoverImageUrl());

        // 设置可见分类
        if (dto.getVisibleRoleIds() != null && !dto.getVisibleRoleIds().isEmpty()) {
            List<Role> cats = roleRepository.findAllById(dto.getVisibleRoleIds());
            course.setVisibleRoles(cats);
        } else {
            course.setVisibleRoles(new ArrayList<>());
        }

        return courseRepository.save(course);
    }

}
