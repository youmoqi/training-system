package com.training.service;

import com.training.entity.Course;
import com.training.entity.User;
import com.training.entity.UserCourse;
import com.training.repository.CourseRepository;
import com.training.repository.UserCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private UserCourseRepository userCourseRepository;

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