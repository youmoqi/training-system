package com.training.service;

import com.training.dto.UserRegistrationDto;
import com.training.dto.UserPermissionsUpdateDto;
import com.training.entity.JobCategory;
import com.training.entity.User;
import com.training.entity.VisibilityCategory;
import com.training.repository.JobCategoryRepository;
import com.training.repository.UserRepository;
import com.training.repository.VisibilityCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VisibilityCategoryRepository visibilityCategoryRepository;

    @Autowired
    private JobCategoryRepository jobCategoryRepository;

    // 分页查询用户
    public Page<User> findUsersWithPagination(Pageable pageable, String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return userRepository.findByUsernameContainingOrRealNameContainingOrPhoneContaining(
                    keyword, keyword, keyword, pageable);
        } else {
            return userRepository.findAll(pageable);
        }
    }

    public User registerUser(UserRegistrationDto registrationDto, String facePhotoUrl) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查身份证是否已存在
        if (userRepository.existsByIdCard(registrationDto.getIdCard())) {
            throw new RuntimeException("身份证号码已存在");
        }

        // 检查手机号是否已存在
        if (userRepository.existsByPhone(registrationDto.getPhone())) {
            throw new RuntimeException("手机号已存在");
        }

        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRealName(registrationDto.getRealName());
        user.setGender(registrationDto.getGender());
        user.setIdCard(registrationDto.getIdCard());
        user.setPhone(registrationDto.getPhone());
        user.setWorkUnit(registrationDto.getWorkUnit());
        user.setTrainingType(registrationDto.getTrainingType());

        // 设置作业类别
        JobCategory jobCategory = jobCategoryRepository.findById(registrationDto.getJobCategoryId())
                .orElseThrow(() -> new RuntimeException("找不到对应的作业类别"));
        user.setJobCategory(jobCategory);

        user.setFacePhotoUrl(facePhotoUrl);
        user.setPaymentAmount(registrationDto.getPaymentAmount());

        // 根据培训类型设置角色
        if ("易制爆".equals(registrationDto.getTrainingType())) {
            // 设置用户可见性分类为易制爆人员
            VisibilityCategory explosiveCategory = visibilityCategoryRepository.findByCode("EXPLOSIVE_FIRST")
                    .orElseThrow(() -> new RuntimeException("找不到易制爆人员角色分类"));
            user.setRole(explosiveCategory);
        } else {
            // 设置用户可见性分类为爆破三大员
            VisibilityCategory blastCategory = visibilityCategoryRepository.findByCode("BLAST_THREE_FIRST")
                    .orElseThrow(() -> new RuntimeException("找不到爆破三大员角色分类"));
            user.setRole(blastCategory);
        }

        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public User updateUserPermissions(Long id, UserPermissionsUpdateDto dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("用户不存在"));
        if (dto.getVisibilityCategoryId() != null) {
            VisibilityCategory category = visibilityCategoryRepository.findById(dto.getVisibilityCategoryId())
                    .orElseThrow(() -> new RuntimeException("找不到对应的角色分类"));
            user.setRole(category);
        }
        if (dto.getCanLearn() != null) {
            user.setCanLearn(dto.getCanLearn());
        }
        if (dto.getCanExam() != null) {
            user.setCanExam(dto.getCanExam());
        }
        if (dto.getJobCategoryId() != null) {
            JobCategory jobCategory = jobCategoryRepository.findById(dto.getJobCategoryId())
                    .orElseThrow(() -> new RuntimeException("找不到对应的作业类别"));
            user.setJobCategory(jobCategory);
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
