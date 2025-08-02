package com.training.controller;

import com.training.dto.ApiResponse;
import com.training.entity.Course;
import com.training.entity.User;
import com.training.entity.UserCourse;
import com.training.service.CourseService;
import com.training.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    // 管理员分页获取课程列表
    @GetMapping("/admin/page")
    public ResponseEntity<ApiResponse<Page<Course>>> getCoursesForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Course> courses = courseService.findCoursesForAdmin(pageable, keyword);
            return ResponseEntity.ok(ApiResponse.success("获取课程列表成功", courses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取课程列表失败: " + e.getMessage()));
        }
    }

    // 学员获取我的课程（已选课程）
    @GetMapping("/student/my-courses")
    public ResponseEntity<ApiResponse<Page<UserCourse>>> getMyCourses(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<UserCourse> userCourses = courseService.findMyCourses(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success("获取我的课程成功", userCourses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取我的课程失败: " + e.getMessage()));
        }
    }

    // 学员获取可购买的课程（未选课程）
    @GetMapping("/student/available-courses")
    public ResponseEntity<ApiResponse<Page<Course>>> getAvailableCourses(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String userRole) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Course> courses = courseService.findAvailableCourses(userId, userRole, pageable);
            return ResponseEntity.ok(ApiResponse.success("获取可购买课程成功", courses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取可购买课程失败: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Course>>> getAllCourses() {
        List<Course> courses = courseService.findAllCourses();
        return ResponseEntity.ok(ApiResponse.success("获取课程列表成功", courses));
    }

    @GetMapping("/online")
    public ResponseEntity<ApiResponse<List<Course>>> getOnlineCourses() {
        List<Course> courses = courseService.findOnlineCourses();
        return ResponseEntity.ok(ApiResponse.success("获取在线课程成功", courses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Course>> getCourseById(@PathVariable Long id) {
        return courseService.findById(id)
                .map(course -> ResponseEntity.ok(ApiResponse.success("获取课程成功", course)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Course>> createCourse(@RequestBody Course course) {
        try {
            Course savedCourse = courseService.createCourse(course);
            return ResponseEntity.ok(ApiResponse.success("创建课程成功", savedCourse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Course>> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        try {
            course.setId(id);
            Course updatedCourse = courseService.updateCourse(course);
            return ResponseEntity.ok(ApiResponse.success("更新课程成功", updatedCourse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        try {
            courseService.deleteCourse(id);
            return ResponseEntity.ok(ApiResponse.success("删除课程成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/enroll")
    public ResponseEntity<ApiResponse<Void>> enrollCourse(@PathVariable Long id, @RequestParam Long userId) {
        try {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            Course course = courseService.findById(id)
                    .orElseThrow(() -> new RuntimeException("课程不存在"));

            courseService.enrollCourse(user, course);
            return ResponseEntity.ok(ApiResponse.success("选课成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<UserCourse>>> getUserCourses(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            List<UserCourse> userCourses = courseService.getUserCourses(user);
            return ResponseEntity.ok(ApiResponse.success("获取用户课程成功", userCourses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/unenroll/{courseId}")
    public ResponseEntity<ApiResponse<Void>> unenrollCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("用户未登录"));
            }
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            Course course = courseService.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("课程不存在"));

            courseService.unenrollCourse(user, course);
            return ResponseEntity.ok(ApiResponse.success("成功退出课程"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/progress")
    public ResponseEntity<ApiResponse<Void>> updateProgress(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam Integer progress) {
        try {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            Course course = courseService.findById(id)
                    .orElseThrow(() -> new RuntimeException("课程不存在"));

            courseService.updateCourseProgress(user, course, progress);
            return ResponseEntity.ok(ApiResponse.success("更新进度成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
