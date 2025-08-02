package com.training.repository;

import com.training.entity.TrainingCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingCertificateRepository extends JpaRepository<TrainingCertificate, Long>, JpaSpecificationExecutor<TrainingCertificate> {
    
    // 根据用户ID查询所有证书
    List<TrainingCertificate> findByUserId(Long userId);
    
    // 根据用户ID和课程ID查询证书
    Optional<TrainingCertificate> findByUserIdAndCourseId(Long userId, Long courseId);
    
    // 根据证书类型查询
    List<TrainingCertificate> findByCertificateType(String certificateType);
    
    // 根据是否收费查询
    List<TrainingCertificate> findByIsPaid(Boolean isPaid);
    
    // 根据证书编号查询
    Optional<TrainingCertificate> findByCertificateNumber(String certificateNumber);
    
    // 根据用户角色查询证书
    @Query("SELECT tc FROM TrainingCertificate tc WHERE tc.user.role = :role")
    List<TrainingCertificate> findByUserRole(@Param("role") String role);
    
    // 根据用户角色和是否收费查询
    @Query("SELECT tc FROM TrainingCertificate tc WHERE tc.user.role = :role AND tc.isPaid = :isPaid")
    List<TrainingCertificate> findByUserRoleAndIsPaid(@Param("role") String role, @Param("isPaid") Boolean isPaid);
    
    // 根据课程ID查询所有证书
    List<TrainingCertificate> findByCourseId(Long courseId);
} 