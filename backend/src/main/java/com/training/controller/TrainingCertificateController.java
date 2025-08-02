package com.training.controller;

import com.training.dto.ApiResponse;
import com.training.dto.TrainingCertificateDto;
import com.training.service.TrainingCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/certificates")
@CrossOrigin(origins = "*")
public class TrainingCertificateController {

    @Autowired
    private TrainingCertificateService certificateService;

    // 生成培训证明
    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TrainingCertificateDto>> generateCertificate(
            @RequestParam Long userId,
            @RequestParam Long courseId,
            @RequestParam Boolean isPaid) {
        try {
            var certificate = certificateService.generateCertificate(userId, courseId, isPaid);
            TrainingCertificateDto dto = convertToDto(certificate);
            return ResponseEntity.ok(ApiResponse.success(dto, "证书生成成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 获取所有证书（分页）
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllCertificates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String certificateType,
            @RequestParam(required = false) Boolean isPaid,
            @RequestParam(required = false) String searchKeyword) {
        try {
            Map<String, Object> result = certificateService.getCertificatesWithPagination(page, size, certificateType, isPaid, searchKeyword);
            return ResponseEntity.ok(ApiResponse.success(result, "获取证书列表成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 根据用户ID获取证书
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<TrainingCertificateDto>>> getCertificatesByUserId(@PathVariable Long userId) {
        try {
            List<TrainingCertificateDto> certificates = certificateService.getCertificatesByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(certificates, "获取用户证书成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 根据证书类型获取证书
    @GetMapping("/type/{certificateType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<TrainingCertificateDto>>> getCertificatesByType(@PathVariable String certificateType) {
        try {
            List<TrainingCertificateDto> certificates = certificateService.getCertificatesByType(certificateType);
            return ResponseEntity.ok(ApiResponse.success(certificates, "获取证书列表成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 根据是否收费获取证书
    @GetMapping("/payment/{isPaid}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<TrainingCertificateDto>>> getCertificatesByPayment(@PathVariable Boolean isPaid) {
        try {
            List<TrainingCertificateDto> certificates = certificateService.getCertificatesByPayment(isPaid);
            return ResponseEntity.ok(ApiResponse.success(certificates, "获取证书列表成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 根据用户角色和是否收费获取证书
    @GetMapping("/role/{role}/payment/{isPaid}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<TrainingCertificateDto>>> getCertificatesByRoleAndPayment(
            @PathVariable String role,
            @PathVariable Boolean isPaid) {
        try {
            List<TrainingCertificateDto> certificates = certificateService.getCertificatesByRoleAndPayment(role, isPaid);
            return ResponseEntity.ok(ApiResponse.success(certificates, "获取证书列表成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 根据证书编号获取证书
    @GetMapping("/number/{certificateNumber}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TrainingCertificateDto>> getCertificateByNumber(@PathVariable String certificateNumber) {
        try {
            TrainingCertificateDto certificate = certificateService.getCertificateByNumber(certificateNumber);
            if (certificate != null) {
                return ResponseEntity.ok(ApiResponse.success(certificate, "获取证书成功"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 删除证书
    @DeleteMapping("/{certificateId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteCertificate(@PathVariable Long certificateId) {
        try {
            certificateService.deleteCertificate(certificateId);
            return ResponseEntity.ok(ApiResponse.success("证书删除成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 下载证书PDF
    @GetMapping("/download/{certificateId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ByteArrayResource> downloadCertificate(@PathVariable Long certificateId) {
        try {
            byte[] pdfBytes = certificateService.generateCertificatePdf(certificateId);
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"certificate.pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 批量下载证书
    @PostMapping("/download/batch")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ByteArrayResource> downloadBatchCertificates(@RequestBody List<Long> certificateIds) {
        try {
            byte[] pdfBytes = certificateService.generateBatchCertificatesPdf(certificateIds);
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"certificates.pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 转换为DTO的辅助方法
    private TrainingCertificateDto convertToDto(com.training.entity.TrainingCertificate certificate) {
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
