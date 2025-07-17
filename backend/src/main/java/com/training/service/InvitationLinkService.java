package com.training.service;

import com.training.dto.InvitationLinkDto;
import com.training.entity.Course;
import com.training.entity.InvitationLink;
import com.training.entity.User;
import com.training.repository.CourseRepository;
import com.training.repository.InvitationLinkRepository;
import com.training.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class InvitationLinkService {

    @Autowired
    private InvitationLinkRepository invitationLinkRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseService courseService;

    public List<InvitationLink> getAllInvitationLinks() {
        return invitationLinkRepository.findAll();
    }

    public void deleteInvitationLink(Long id) {
        invitationLinkRepository.deleteById(id);
    }

    public InvitationLink createInvitationLink(InvitationLinkDto dto) {
        InvitationLink link = new InvitationLink();
        link.setLinkCode("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        link.setPassword(dto.getPassword());
        link.setTitle(dto.getTitle());
        link.setDescription(dto.getDescription());
        link.setExpireTime(dto.getExpireTime());
        link.setIsActive(true);

        if (dto.getCourseIds() != null && !dto.getCourseIds().isEmpty()) {
            List<Course> courses = courseRepository.findAllById(dto.getCourseIds());
            link.setCourses(courses);
        }

        return invitationLinkRepository.save(link);
    }

    public void joinCoursesByInvitation(String linkCode, String password, String username) {
        InvitationLink link = invitationLinkRepository.findByLinkCode(linkCode)
                .orElseThrow(() -> new RuntimeException("邀请码不正确"));

        if (link.getIsActive() == null || !link.getIsActive()) {
            throw new RuntimeException("邀请链接已失效");
        }

        if (link.getExpireTime() == null || link.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("邀请链接已过期");
        }

        if (!link.getPassword().equals(password)) {
            throw new RuntimeException("邀请密码不正确");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (user.getRole() != User.UserRole.BLAST_USER) {
            throw new RuntimeException("只有'爆破三大员'角色的用户才能使用邀请链接");
        }

        if (link.getCourses() == null || link.getCourses().isEmpty()) {
            throw new RuntimeException("此邀请链接未关联任何课程");
        }
        
        for (Course course : link.getCourses()) {
            courseService.enrollCourseWithInvitation(user, course);
        }
    }
} 