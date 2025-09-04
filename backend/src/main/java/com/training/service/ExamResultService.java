package com.training.service;

import com.training.dto.ExamQuestionResultDto;
import com.training.dto.ExamResultDto;
import com.training.entity.Exam.ExamQuestionResult;
import com.training.entity.Exam.ExamResult;
import com.training.entity.Exam.Question;
import com.training.entity.Exam.QuestionOption;
import com.training.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.training.dto.ApiResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.io.ByteArrayInputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.persistence.PersistenceContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Transactional
public class ExamResultService {

    @Autowired
    private ExamResultRepository examResultRepository;

    @Autowired
    private ExamQuestionResultRepository examQuestionResultRepository;
    // 根据结果ID获取考试结果
    public ExamResultDto getExamResultById(Long resultId) {
        ExamResult examResult = examResultRepository.findById(resultId)
                .orElseThrow(() -> new RuntimeException("未找到考试结果"));
        return convertToDto(examResult);
    }

    private ExamResultDto convertToDto(ExamResult examResult) {
        ExamResultDto dto = new ExamResultDto();
        BeanUtils.copyProperties(examResult, dto);

        // 设置试卷标题
        if (examResult.getExam() != null) {
            dto.setExamTitle(examResult.getExam().getTitle());
        }

        // 设置用户信息
        if (examResult.getUser() != null) {
            dto.setUserName(examResult.getUser().getRealName());
        }

        // 设置考试时间
        dto.setExamTime(examResult.getExamTime());

        // 获取题目答题详情
        List<ExamQuestionResult> questionResults = examQuestionResultRepository
                .findByExamResultOrderById(examResult);

        List<ExamQuestionResultDto> questionResultDtos = questionResults.stream()
                .map(this::convertToQuestionResultDto)
                .collect(Collectors.toList());

        dto.setQuestionResults(questionResultDtos);

        return dto;
    }

    private ExamQuestionResultDto convertToQuestionResultDto(ExamQuestionResult questionResult) {
        ExamQuestionResultDto dto = new ExamQuestionResultDto();
        BeanUtils.copyProperties(questionResult, dto);

        // 设置题目信息
        if (questionResult.getQuestion() != null) {
            Question question = questionResult.getQuestion();
            dto.setQuestionId(question.getId());
            dto.setQuestionContent(question.getContent());
            dto.setQuestionType(question.getType());
            dto.setExplanation(question.getExplanation());

            // 设置选项
            if (question.getOptions() != null && !question.getOptions().isEmpty()) {
                List<String> options = question.getOptions().stream()
                        .map(QuestionOption::getOptionContent)
                        .collect(Collectors.toList());
                dto.setOptions(options);
            }

            // 用户答案和正确答案保持选项标签格式，不转换为选项内容
            // 这样前端显示时只显示选项标签（A、B、C、D）
        }

        return dto;
    }

    // 获取用户考试历史
    public ApiResponse<Page<ExamResultDto>> getUserExamHistory(Long userId, Pageable pageable, String keyword, Boolean isPassed) {
        try {
            Page<ExamResult> results;

            if (keyword != null && !keyword.trim().isEmpty()) {
                // 按关键词搜索
                if (isPassed != null) {
                    // 按关键词和通过状态搜索
                    results = examResultRepository.findByUserIdAndExamTitleContainingAndIsPassedOrderByExamTimeDesc(userId, keyword.trim(), isPassed, pageable);
                } else {
                    // 只按关键词搜索
                    results = examResultRepository.findByUserIdAndExamTitleContainingOrderByExamTimeDesc(userId, keyword.trim(), pageable);
                }
            } else {
                // 不按关键词搜索
                if (isPassed != null) {
                    // 只按通过状态搜索
                    results = examResultRepository.findByUserIdAndIsPassedOrderByExamTimeDesc(userId, isPassed, pageable);
                } else {
                    // 不按任何条件搜索
                    results = examResultRepository.findByUserIdOrderByExamTimeDesc(userId, pageable);
                }
            }

            Page<ExamResultDto> historyDtos = results.map(this::convertToHistoryDto);
            return ApiResponse.success(historyDtos);
        } catch (Exception e) {
            return ApiResponse.error("获取考试历史失败: " + e.getMessage());
        }
    }

    // 获取所有用户的考试历史记录（管理端使用）
    public ApiResponse<Page<ExamResultDto>> getAllUsersExamHistory(Pageable pageable, String examKeyword, String userKeyword, Boolean isPassed) {
        try {
            // 首先创建一个自定义查询
            StringBuilder queryBuilder = new StringBuilder("SELECT er FROM ExamResult er JOIN er.user u JOIN er.exam e WHERE 1=1");
            StringBuilder countQueryBuilder = new StringBuilder("SELECT COUNT(er) FROM ExamResult er JOIN er.user u JOIN er.exam e WHERE 1=1");
            List<Object> params = new ArrayList<>();
            int paramIndex = 1;

            // 添加试卷关键词筛选
            if (examKeyword != null && !examKeyword.trim().isEmpty()) {
                queryBuilder.append(" AND e.title LIKE :examKeyword");
                countQueryBuilder.append(" AND e.title LIKE :examKeyword");
                params.add("%" + examKeyword.trim() + "%");
            }

            // 添加用户关键词筛选（用户名或真实姓名）
            if (userKeyword != null && !userKeyword.trim().isEmpty()) {
                queryBuilder.append(" AND (u.username LIKE :userKeyword OR u.realName LIKE :userKeyword)");
                countQueryBuilder.append(" AND (u.username LIKE :userKeyword OR u.realName LIKE :userKeyword)");
                params.add("%" + userKeyword.trim() + "%");
            }

            // 添加通过状态筛选
            if (isPassed != null) {
                queryBuilder.append(" AND er.isPassed = :isPassed");
                countQueryBuilder.append(" AND er.isPassed = :isPassed");
                params.add(isPassed);
            }

            // 添加排序
            queryBuilder.append(" ORDER BY er.examTime DESC");

            // 执行查询
            javax.persistence.Query query = entityManager.createQuery(queryBuilder.toString(), ExamResult.class);
            javax.persistence.Query countQuery = entityManager.createQuery(countQueryBuilder.toString(), Long.class);

            // 设置参数
            int paramPosition = 0;
            if (examKeyword != null && !examKeyword.trim().isEmpty()) {
                query.setParameter("examKeyword", params.get(paramPosition));
                countQuery.setParameter("examKeyword", params.get(paramPosition));
                paramPosition++;
            }
            if (userKeyword != null && !userKeyword.trim().isEmpty()) {
                query.setParameter("userKeyword", params.get(paramPosition));
                countQuery.setParameter("userKeyword", params.get(paramPosition));
                paramPosition++;
            }
            if (isPassed != null) {
                query.setParameter("isPassed", params.get(paramPosition));
                countQuery.setParameter("isPassed", params.get(paramPosition));
            }

            // 设置分页
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());

            List<ExamResult> results = query.getResultList();
            long total = (long) countQuery.getSingleResult();

            // 转换为DTO
            List<ExamResultDto> dtos = results.stream()
                    .map(this::convertToHistoryDto)
                    .collect(Collectors.toList());

            // 创建分页结果
            Page<ExamResultDto> pageResult = new org.springframework.data.domain.PageImpl<>(dtos, pageable, total);

            return ApiResponse.success(pageResult);
        } catch (Exception e) {
            return ApiResponse.error("获取所有用户考试历史失败: " + e.getMessage());
        }
    }

    // 为了支持自定义查询，需要注入EntityManager
    @PersistenceContext
    private javax.persistence.EntityManager entityManager;

    // 转换历史记录DTO
    private ExamResultDto convertToHistoryDto(ExamResult result) {
        ExamResultDto dto = new ExamResultDto();
        BeanUtils.copyProperties(result, dto);

        // 计算考试次数 - 获取该用户对该试卷的所有考试记录，按时间排序，找到当前考试的位置
        List<ExamResult> allResults = examResultRepository.findByUserIdAndExamIdOrderByExamTimeAsc(
                result.getUser().getId(), result.getExam().getId());
        int attemptNumber = 1;
        for (ExamResult examResult : allResults) {
            if (examResult.getId().equals(result.getId())) {
                break;
            }
            attemptNumber++;
        }
        dto.setAttemptNumber(attemptNumber);

        // 获取关联信息
        if (result.getExam() != null) {
            dto.setExamTitle(result.getExam().getTitle());
        }
        if (result.getUser() != null) {
            dto.setUserName(result.getUser().getRealName());
        }
        if (result.getExam() != null) {
            dto.setExamId(result.getExam().getId());
        }
        dto.setId(result.getId());
        return dto;
    }

public ByteArrayInputStream exportExamDataToExcel(LocalDateTime startDate, LocalDateTime endDate, List<Long> roleIds) {
    List<ExamResult> results = examResultRepository.findExamResultsByDateRangeAndRoleIds(startDate, endDate, roleIds);

    // 创建工作簿
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Exam Results");

    // 创建标题字体样式
    Font headerFont = workbook.createFont();
    headerFont.setBold(true);               // 设置加粗
    headerFont.setFontHeightInPoints((short) 12);  // 设置标题字体大小

    // 创建单元格样式
    CellStyle headerStyle = workbook.createCellStyle();
    headerStyle.setFont(headerFont);
    headerStyle.setBorderBottom(BorderStyle.THIN);
    headerStyle.setBorderTop(BorderStyle.THIN);
    headerStyle.setBorderLeft(BorderStyle.THIN);
    headerStyle.setBorderRight(BorderStyle.THIN);
    headerStyle.setAlignment(HorizontalAlignment.CENTER);  // 水平居中
    headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);  // 垂直居中

    // 创建数据单元格样式
    CellStyle dataStyle = workbook.createCellStyle();
    dataStyle.setBorderBottom(BorderStyle.THIN);
    dataStyle.setBorderTop(BorderStyle.THIN);
    dataStyle.setBorderLeft(BorderStyle.THIN);
    dataStyle.setBorderRight(BorderStyle.THIN);
    dataStyle.setAlignment(HorizontalAlignment.CENTER);  // 水平居中
    dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);  // 垂直居中

    // 创建表头
    Row headerRow = sheet.createRow(0);
    headerRow.createCell(0).setCellValue("学员用户名");
    headerRow.createCell(1).setCellValue("试卷标题");
    headerRow.createCell(2).setCellValue("得分");
    headerRow.createCell(3).setCellValue("正确题数");
    headerRow.createCell(4).setCellValue("总题数");
    headerRow.createCell(5).setCellValue("考试时间");

    // 设置表头样式
    for (int i = 0; i < 6; i++) {
        headerRow.getCell(i).setCellStyle(headerStyle);
    }

    // 填充数据
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    int rowNum = 1;
    for (ExamResult r : results) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(r.getUser().getUsername());
        row.createCell(1).setCellValue(r.getExam().getTitle());
        row.createCell(2).setCellValue(r.getScore());
        row.createCell(3).setCellValue(r.getCorrectAnswers());
        row.createCell(4).setCellValue(r.getTotalQuestions());
        String examTimeStr = r.getExamTime() != null ? r.getExamTime().format(dateFormatter) : "";
        row.createCell(5).setCellValue(examTimeStr);

        // 设置数据单元格样式
        for (int i = 0; i < 6; i++) {
            row.getCell(i).setCellStyle(dataStyle);
        }
    }

    // 根据每列的最长内容计算列宽（包括标题和数据）
    for (int i = 0; i < 6; i++) {
        int maxLength = 0;

        // 计算标题的长度
        String header = sheet.getRow(0).getCell(i).toString();
        maxLength = Math.max(maxLength, getStringWidth(header));

        // 查找每列最长的字符串长度
        for (int rowIndex = 1; rowIndex < results.size() + 1; rowIndex++) {  // 包括表头
            Row row = sheet.getRow(rowIndex);

            if (row != null && row.getCell(i) != null) {
                String cellValue = row.getCell(i).toString();
                maxLength = Math.max(maxLength, getStringWidth(cellValue));
            }
        }

        // 设置列宽：根据最大长度来设置列宽，适当增加一些宽度
        sheet.setColumnWidth(i, (maxLength + 2) * 256); // 256是字符宽度单位，可以根据实际数据调整
    }

    // 将 Excel 数据写入 ByteArrayOutputStream
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    } catch (IOException e) {
        e.printStackTrace();
        throw new RuntimeException("Error writing Excel data", e);
    }
}

    // 计算字符串的宽度（考虑中文字符）
    private int getStringWidth(String str) {
        int length = 0;
        if (str != null) {
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (Character.toString(c).getBytes().length > 1) {
                    length += 2;  // 中文字符宽度
                } else {
                    length += 1;  // 英文字符宽度
                }
            }
        }
        return length;
    }
}
