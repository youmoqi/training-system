package com.training.service;

import com.training.dto.TrainingCertificateDto;
import com.training.entity.Course;
import com.training.entity.TrainingCertificate;
import com.training.entity.User;
import com.training.entity.UserCourse;
import com.training.repository.CourseRepository;
import com.training.repository.TrainingCertificateRepository;
import com.training.repository.UserCourseRepository;
import com.training.repository.UserRepository;
import com.training.util.PdfGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author YIZ
 */
@Service
public class TrainingCertificateService {

    @Autowired
    private TrainingCertificateRepository certificateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserCourseRepository userCourseRepository;

    @Autowired
    private PdfGenerator pdfGenerator;

    // 生成培训证明
    public TrainingCertificate generateCertificate(Long userId, Long courseId, Boolean isPaid) {
        // 检查用户和课程是否存在
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Course> courseOpt = courseRepository.findById(courseId);

        if (!userOpt.isPresent() || !courseOpt.isPresent()) {
            throw new RuntimeException("用户或课程不存在");
        }

        User user = userOpt.get();
        Course course = courseOpt.get();

        // 检查用户是否已完成该课程
        Optional<UserCourse> userCourseOpt = userCourseRepository.findByUserIdAndCourseId(userId, courseId);
        if (!userCourseOpt.isPresent() || !userCourseOpt.get().getIsCompleted()) {
            throw new RuntimeException("用户未完成该课程，无法生成证书");
        }

        // 检查是否已存在证书
        Optional<TrainingCertificate> existingCert = certificateRepository.findByUserIdAndCourseId(userId, courseId);
        if (existingCert.isPresent()) {
            throw new RuntimeException("该用户已完成该课程的证书");
        }

        // 生成证书编号
        String certificateNumber = generateCertificateNumber(user, course);

        // 创建证书
        TrainingCertificate certificate = new TrainingCertificate();
        certificate.setUser(user);
        certificate.setCourse(course);
        certificate.setCertificateNumber(certificateNumber);
        certificate.setIssueDate(LocalDateTime.now());
        certificate.setCompleteDate(userCourseOpt.get().getCompleteTime());
        certificate.setIsPaid(isPaid);
        certificate.setCertificateType(user.getRole().name());

        return certificateRepository.save(certificate);
    }

    // 生成证书编号
    private String generateCertificateNumber(User user, Course course) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateStr = LocalDateTime.now().format(formatter);
        String userType = user.getRole().name().substring(0, 2);
        return String.format("CERT-%s-%s-%s-%d", dateStr, userType, course.getId(), user.getId());
    }

    // 根据用户ID查询证书
    public List<TrainingCertificateDto> getCertificatesByUserId(Long userId) {
        List<TrainingCertificate> certificates = certificateRepository.findByUserId(userId);
        return certificates.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // 根据证书类型查询
    public List<TrainingCertificateDto> getCertificatesByType(String certificateType) {
        List<TrainingCertificate> certificates = certificateRepository.findByCertificateType(certificateType);
        return certificates.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // 根据是否收费查询
    public List<TrainingCertificateDto> getCertificatesByPayment(Boolean isPaid) {
        List<TrainingCertificate> certificates = certificateRepository.findByIsPaid(isPaid);
        return certificates.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // 根据用户角色和是否收费查询
    public List<TrainingCertificateDto> getCertificatesByRoleAndPayment(String role, Boolean isPaid) {
        List<TrainingCertificate> certificates = certificateRepository.findByUserRoleAndIsPaid(role, isPaid);
        return certificates.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // 查询所有证书
    public List<TrainingCertificateDto> getAllCertificates() {
        List<TrainingCertificate> certificates = certificateRepository.findAll();
        return certificates.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // 分页查询证书
    public Map<String, Object> getCertificatesWithPagination(int page, int size, String certificateType, Boolean isPaid, String searchKeyword) {
        Pageable pageable = PageRequest.of(page, size);
        
        // 构建查询条件
        Specification<TrainingCertificate> spec = Specification.where(null);
        
        if (certificateType != null && !certificateType.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("certificateType"), certificateType));
        }
        
        if (isPaid != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isPaid"), isPaid));
        }
        
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.or(
                    cb.like(root.get("certificateNumber"), "%" + searchKeyword + "%"),
                    cb.like(root.get("user").get("username"), "%" + searchKeyword + "%"),
                    cb.like(root.get("user").get("realName"), "%" + searchKeyword + "%")
                )
            );
        }
        
        Page<TrainingCertificate> certificatePage = certificateRepository.findAll(spec, pageable);
        
        List<TrainingCertificateDto> certificates = certificatePage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("certificates", certificates);
        result.put("total", certificatePage.getTotalElements());
        result.put("totalPages", certificatePage.getTotalPages());
        result.put("currentPage", page);
        result.put("size", size);
        
        return result;
    }

    // 根据证书编号查询
    public TrainingCertificateDto getCertificateByNumber(String certificateNumber) {
        Optional<TrainingCertificate> certificate = certificateRepository.findByCertificateNumber(certificateNumber);
        return certificate.map(this::convertToDto).orElse(null);
    }

    // 删除证书
    public void deleteCertificate(Long certificateId) {
        certificateRepository.deleteById(certificateId);
    }

    // 生成单个证书PDF
    public byte[] generateCertificatePdf(Long certificateId) {
        Optional<TrainingCertificate> certificateOpt = certificateRepository.findById(certificateId);
        if (!certificateOpt.isPresent()) {
            throw new RuntimeException("证书不存在");
        }
        TrainingCertificateDto dto = convertToDto(certificateOpt.get());
        return pdfGenerator.generateCertificatePdf(dto);
    }

    // 生成批量证书PDF
    public byte[] generateBatchCertificatesPdf(List<Long> certificateIds) {
        List<TrainingCertificate> certificates = certificateRepository.findAllById(certificateIds);
        List<TrainingCertificateDto> dtos = certificates.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return pdfGenerator.generateBatchCertificatesPdf(dtos);
    }

    // 转换为DTO
    private TrainingCertificateDto convertToDto(TrainingCertificate certificate) {
        TrainingCertificateDto dto = new TrainingCertificateDto();
        dto.setId(certificate.getId());
        dto.setUserId(certificate.getUser().getId());
        dto.setUsername(certificate.getUser().getUsername());
        dto.setRealName(certificate.getUser().getRealName());
        dto.setUserRole(certificate.getUser().getRole().name());
        dto.setCourseId(certificate.getCourse().getId());
        dto.setCourseTitle(certificate.getCourse().getTitle());
        dto.setCertificateNumber(certificate.getCertificateNumber());
        dto.setIssueDate(certificate.getIssueDate());
        dto.setCompleteDate(certificate.getCompleteDate());
        dto.setIsPaid(certificate.getIsPaid());
        dto.setCertificateType(certificate.getCertificateType());
        dto.setCreateTime(certificate.getCreateTime());
        dto.setUpdateTime(certificate.getUpdateTime());
        return dto;
    }
}
