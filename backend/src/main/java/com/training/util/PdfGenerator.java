package com.training.util;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.training.dto.TrainingCertificateDto;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PdfGenerator {

    public byte[] generateCertificatePdf(TrainingCertificateDto certificate) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // 添加标题
            Paragraph title = new Paragraph("培训证明")
                    .setFontSize(24)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold();
            document.add(title);

            // 添加证书编号
            Paragraph certNumber = new Paragraph("证书编号：" + certificate.getCertificateNumber())
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT);
            document.add(certNumber);

            // 创建信息表格
            Table table = new Table(2).useAllAvailableWidth();

            table.addCell("姓名");
            table.addCell(certificate.getRealName());

            table.addCell("用户名");
            table.addCell(certificate.getUsername());

            table.addCell("用户类型");
            table.addCell(getUserRoleDisplayName(certificate.getUserRole()));

            table.addCell("课程名称");
            table.addCell(certificate.getCourseTitle());

            table.addCell("完成时间");
            table.addCell(certificate.getCompleteDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            table.addCell("颁发时间");
            table.addCell(certificate.getIssueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            table.addCell("证书类型");
            table.addCell(getCertificateTypeDisplayName(certificate.getCertificateType()));

            table.addCell("是否收费");
            table.addCell(certificate.getIsPaid() ? "是" : "否");

            document.add(table);

            // 添加说明文字
            Paragraph description = new Paragraph(
                    "兹证明该学员已完成相关培训课程，成绩合格，特此颁发此培训证明。"
            ).setFontSize(12);
            document.add(description);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("生成PDF失败", e);
        }
    }

    public byte[] generateBatchCertificatesPdf(List<TrainingCertificateDto> certificates) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // 添加标题
            Paragraph title = new Paragraph("培训证明汇总")
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold();
            document.add(title);

            // 创建汇总表格
            Table table = new Table(6).useAllAvailableWidth();

            // 表头
            table.addCell("姓名");
            table.addCell("课程名称");
            table.addCell("证书编号");
            table.addCell("完成时间");
            table.addCell("证书类型");
            table.addCell("是否收费");

            // 数据行
            for (TrainingCertificateDto cert : certificates) {
                table.addCell(cert.getRealName());
                table.addCell(cert.getCourseTitle());
                table.addCell(cert.getCertificateNumber());
                table.addCell(cert.getCompleteDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                table.addCell(getCertificateTypeDisplayName(cert.getCertificateType()));
                table.addCell(cert.getIsPaid() ? "是" : "否");
            }

            document.add(table);
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("生成批量PDF失败", e);
        }
    }

    private String getUserRoleDisplayName(String role) {
        switch (role) {
            case "EXPLOSIVE_USER":
                return "易制爆人员";
            case "BLAST_USER":
                return "爆破三大员";
            case "ADMIN":
                return "管理员";
            case "SUPER_ADMIN":
                return "超级管理员";
            default:
                return role;
        }
    }

    private String getCertificateTypeDisplayName(String certificateType) {
        switch (certificateType) {
            case "EXPLOSIVE_USER":
                return "易制爆人员培训证明";
            case "BLAST_USER":
                return "爆破三大员培训证明";
            default:
                return certificateType;
        }
    }
}
