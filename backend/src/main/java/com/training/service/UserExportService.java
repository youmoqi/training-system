package com.training.service;

import com.training.dto.UserExportDto;
import com.training.entity.Exam.ExamResult;
import com.training.entity.User;
import com.training.entity.UserCourse;
import com.training.repository.ExamResultRepository;
import com.training.repository.UserCourseRepository;
import com.training.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserExportService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCourseRepository userCourseRepository;

    @Autowired
    private ExamResultRepository examResultRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 根据条件导出用户数据
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param roleId 角色ID
     * @param learningStatus 学习状态
     * @return 导出的Excel文件字节数组
     */
    public byte[] exportUserData(LocalDateTime startTime, LocalDateTime endTime, Long roleId, Integer learningStatus) throws IOException {
        List<User> users = findUsersByConditions(startTime, endTime, roleId, learningStatus);
        List<UserExportDto> exportData = convertToExportDto(users);
        return generateExcel(exportData);
    }

    /**
     * 根据条件查询用户
     */
    private List<User> findUsersByConditions(LocalDateTime startTime, LocalDateTime endTime, Long roleId, Integer learningStatus) {
        List<User> users;

        // 基础查询
        if (roleId != null) {
            users = userRepository.findByRoleId(roleId);
        } else {
            users = userRepository.findAll();
        }

        // 时间过滤
        if (startTime != null && endTime != null) {
            users = users.stream()
                    .filter(user -> user.getCreateTime().isAfter(startTime) && user.getCreateTime().isBefore(endTime))
                    .collect(Collectors.toList());
        }

        // 学习状态过滤
        if (learningStatus != null) {
            switch (learningStatus) {
                case 2:
                    users = users.stream()
                            .filter(user -> hasCompletedCourses(user.getId()))
                            .collect(Collectors.toList());
                    break;
                case 1:
                    users = users.stream()
                            .filter(user -> hasInProgressCourses(user.getId()))
                            .collect(Collectors.toList());
                    break;
                case 0:
                    users = users.stream()
                            .filter(user -> !hasAnyCourses(user.getId()))
                            .collect(Collectors.toList());
                    break;
                default:
                    break;
            }
        }

        return users;
    }

    /**
     * 检查用户是否有已完成的课程
     */
    private boolean hasCompletedCourses(Long userId) {
        return userCourseRepository.countByUserIdAndIsCompleted(userId, true) > 0;
    }

    /**
     * 检查用户是否有进行中的课程
     */
    private boolean hasInProgressCourses(Long userId) {
        return userCourseRepository.countByUserIdAndIsCompleted(userId, false) > 0;
    }

    /**
     * 检查用户是否有任何课程
     */
    private boolean hasAnyCourses(Long userId) {
        return userCourseRepository.countByUserId(userId) > 0;
    }

    /**
     * 将用户实体转换为导出DTO
     */
    private List<UserExportDto> convertToExportDto(List<User> users) {
        List<UserExportDto> exportDtos = new ArrayList<>();

        for (User user : users) {
            UserExportDto dto = new UserExportDto();

            // 基本信息
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setRealName(user.getRealName());
            dto.setGender(user.getGender());
            dto.setIdCard(user.getIdCard());
            dto.setPhone(user.getPhone());
            dto.setWorkUnit(user.getWorkUnit());
            dto.setTrainingType(user.getTrainingType());
            dto.setJobCategory(user.getJobCategory() != null ? user.getJobCategory().getName() : "");
            dto.setRole(user.getRole() != null ? user.getRole().getName() : "");
            dto.setCreateTime(user.getCreateTime());

            // 学习记录
            List<UserCourse> userCourses = userCourseRepository.findByUserId(user.getId());
            dto.setTotalCourses(userCourses.size());
            dto.setCompletedCourses((int) userCourses.stream().filter(UserCourse::getIsCompleted).count());
            dto.setAverageProgress(calculateAverageProgress(userCourses));

            // 考试成绩
            List<ExamResult> examResults = examResultRepository.findByUserId(user.getId());
            dto.setTotalExams(examResults.size());
            dto.setPassedExams((int) examResults.stream().filter(ExamResult::getIsPassed).count());
            dto.setAverageScore(calculateAverageScore(examResults));

            exportDtos.add(dto);
        }

        return exportDtos;
    }

    /**
     * 计算平均学习进度
     */
    private int calculateAverageProgress(List<UserCourse> userCourses) {
        if (userCourses.isEmpty()) {
            return 0;
        }

        int totalProgress = userCourses.stream()
                .mapToInt(course -> course.getWatchProgress() != null ? course.getWatchProgress() : 0)
                .sum();

        return totalProgress / userCourses.size();
    }

    /**
     * 计算平均考试分数
     */
    private int calculateAverageScore(List<ExamResult> examResults) {
        if (examResults.isEmpty()) {
            return 0;
        }

        int totalScore = examResults.stream()
                .mapToInt(ExamResult::getScore)
                .sum();

        return totalScore / examResults.size();
    }

    /**
     * 生成Excel文件
     */
    private byte[] generateExcel(List<UserExportDto> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("用户数据");

            // 创建表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "用户ID", "用户名", "真实姓名", "性别", "身份证号", "手机号", "工作单位",
                "培训类型", "岗位类别", "角色", "注册时间", "总课程数", "已完成课程数",
                "平均学习进度", "考试次数", "通过考试次数", "平均考试分数"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }

            // 填充数据
            int rowNum = 1;
            for (UserExportDto user : data) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getUsername());
                row.createCell(2).setCellValue(user.getRealName());
                row.createCell(3).setCellValue(user.getGender());
                row.createCell(4).setCellValue(user.getIdCard());
                row.createCell(5).setCellValue(user.getPhone());
                row.createCell(6).setCellValue(user.getWorkUnit());
                row.createCell(7).setCellValue(user.getTrainingType());
                row.createCell(8).setCellValue(user.getJobCategory());
                row.createCell(9).setCellValue(user.getRole());
                row.createCell(10).setCellValue(user.getCreateTime() != null ?
                        user.getCreateTime().format(DATE_FORMATTER) : "");
                row.createCell(11).setCellValue(user.getTotalCourses());
                row.createCell(12).setCellValue(user.getCompletedCourses());
                row.createCell(13).setCellValue(user.getAverageProgress() + "%");
                row.createCell(14).setCellValue(user.getTotalExams());
                row.createCell(15).setCellValue(user.getPassedExams());
                row.createCell(16).setCellValue(user.getAverageScore());
            }

            // 调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 写入输出流
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
}
