package com.training.service;

import com.training.entity.Course;
import com.training.entity.User;
import com.training.entity.UserCourse;
import com.training.repository.CourseRepository;
import com.training.repository.UserCourseRepository;
import com.training.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserCourseRepository userCourseRepository;

    @Autowired
    private UserRepository userRepository;

    // 获取用户已选课程
    private List<Course> getUserEnrolledCourses(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ArrayList<>();
        }
        return userCourseRepository.findByUser(user).stream()
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

    // 学员获取我的课程（已选课程）
    public Page<UserCourse> findMyCourses(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return userCourseRepository.findByUser(user, pageable);
    }

    // 学员获取可购买的课程（未选课程）
    public Page<Course> findAvailableCourses(Long userId, String userRole, Pageable pageable) {
        // 获取用户已选课程
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        List<Course> userEnrolledCourses = getUserEnrolledCourses(userId);

        // 根据角色获取在线课程
        Page<Course> onlineCourses;
        if (userRole != null && !userRole.isEmpty()) {
            onlineCourses = courseRepository.findByIsOnlineTrueAndVisibleRolesContaining(userRole, pageable);
        } else {
            onlineCourses = courseRepository.findByIsOnlineTrue(pageable);
        }

        // 过滤掉已选课程
        List<Course> filteredContent = onlineCourses.getContent().stream()
                .filter(course -> !userEnrolledCourses.contains(course))
                .collect(Collectors.toList());

        return new PageImpl<>(filteredContent, pageable, onlineCourses.getTotalElements() - userEnrolledCourses.size());
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
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

    public List<Course> findCoursesByRole(String role) {
        return courseRepository.findByIsOnlineTrueAndVisibleRolesContaining(role);
    }

    public Course updateCourse(Course course) {
        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public boolean enrollCourse(User user, Course course) {
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

        return true;
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

    public List<UserCourse> getUserCourses(User user) {
        return userCourseRepository.findByUser(user);
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

    public void unenrollCourse(User user, Course course) {
        UserCourse userCourse = userCourseRepository.findByUserAndCourse(user, course)
                .orElseThrow(() -> new RuntimeException("您没有选择这门课程"));
        userCourseRepository.delete(userCourse);
    }
}
