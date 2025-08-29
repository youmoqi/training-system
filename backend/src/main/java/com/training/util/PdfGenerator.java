package com.training.util;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.BorderCollapsePropertyValue;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.training.dto.TrainingCertificateDto;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PdfGenerator {

    // ======= 字体路径（类路径） =======
    private static final String FONT_PATH = "/fonts/simhei.ttf";

    public byte[] generateCertificatePdf(TrainingCertificateDto certificate) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(60, 60, 60, 60);
            // 加载中文字体
            PdfFont font = loadFont();
            document.setFont(font);
            addCertificateBorder(pdf);

            document.add(new Paragraph("培训证明")
                    .setFontSize(28)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(new DeviceRgb(25, 25, 112)));

            document.add(new Paragraph("TRAINING CERTIFICATE")
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(new DeviceRgb(70, 130, 180))
                    .setMarginBottom(30));

            document.add(new Paragraph("证书编号：" + safe(certificate.getCertificateNumber()))
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setMarginBottom(30));

            addCertificateContent(document, certificate);
            addSignatureSection(document);
            addWatermark(pdf);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("生成PDF失败", e);
        }
    }

    /* ================= 私有方法 ================= */

    private PdfFont loadFont() throws IOException {
        InputStream fontStream = getClass().getResourceAsStream(FONT_PATH);
        if (fontStream == null) {
            System.out.println("字体文件未找到：" + FONT_PATH);
            throw new RuntimeException("字体文件未找到：" + FONT_PATH);
        }
        return PdfFontFactory.createFont(
                fontStream.readAllBytes(),
                PdfEncodings.IDENTITY_H,
                PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
    }

    private void addCertificateBorder(PdfDocument pdf) {
        PdfPage page = pdf.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        Rectangle rect = new Rectangle(40, 40, PageSize.A4.getWidth() - 80, PageSize.A4.getHeight() - 80);
        canvas.setStrokeColor(new DeviceRgb(178, 34, 34));
        canvas.setLineWidth(1.5f);
        canvas.rectangle(rect);
        canvas.stroke();
    }

    private void addCertificateContent(Document document, TrainingCertificateDto certificate) {
        document.add(new Paragraph("兹证明 " + safe(certificate.getRealName()) +
                " 同志（用户名：" + safe(certificate.getUsername()) +
                "）已完成相关培训课程的学习，成绩合格，特此颁发此培训证明。")
                .setFontSize(13)
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setMarginBottom(30));

        Table table = new Table(UnitValue.createPercentArray(new float[]{30f, 70f}))
                .useAllAvailableWidth()
                .setMarginBottom(30)
                .setBorderCollapse(BorderCollapsePropertyValue.COLLAPSE);

        DeviceRgb border = (DeviceRgb) ColorConstants.GRAY;
        DeviceRgb labelBg = new DeviceRgb(245, 245, 245);
        DeviceRgb labelText = (DeviceRgb) ColorConstants.BLACK;
        DeviceRgb valueText = (DeviceRgb) ColorConstants.DARK_GRAY;

        addTableRow(table, "学员姓名", safe(certificate.getRealName()), labelBg, labelText, valueText, border);
        addTableRow(table, "用户类型", getUserRoleDisplayName(certificate.getUserRole()), labelBg, labelText, valueText, border);
        addTableRow(table, "培训课程", safe(certificate.getCourseTitle()), labelBg, labelText, valueText, border);
        addTableRow(table, "完成时间", formatDate(certificate.getCompleteDate()), labelBg, labelText, valueText, border);
        addTableRow(table, "颁发时间", formatDate(certificate.getIssueDate()), labelBg, labelText, valueText, border);
        addTableRow(table, "证书类型", getCertificateTypeDisplayName(certificate.getCertificateType()), labelBg, labelText, valueText, border);
        addTableRow(table, "培训状态", Boolean.TRUE.equals(certificate.getIsPaid()) ? "收费培训" : "免费培训", labelBg, labelText, valueText, border);

        document.add(table);

        document.add(new Paragraph("本证书证明该学员已按要求完成相关培训课程的学习，具备相应的专业知识和技能。")
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(70, 130, 180))
                .setMarginTop(20)
                .setMarginBottom(20));
    }

    private void addTableRow(Table table, String label, String value,
                             DeviceRgb labelBg, DeviceRgb labelText, DeviceRgb valueText,
                             DeviceRgb border) {
        table.addCell(new Cell()
                .add(new Paragraph(label).setFontSize(11).setBold())
                .setBackgroundColor(labelBg)
                .setFontColor(labelText)
                .setBorder(new SolidBorder(border, 0.5f))
                .setPadding(6));

        table.addCell(new Cell()
                .add(new Paragraph(value).setFontSize(11))
                .setFontColor(valueText)
                .setBorder(new SolidBorder(border, 0.5f))
                .setPadding(6));
    }

    private void addSignatureSection(Document document) {
        Table signatureTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                .useAllAvailableWidth()
                .setMarginTop(40);

        Cell left = new Cell()
                .add(new Paragraph("颁发单位：培训管理系统").setFontSize(11))
                .add(new Paragraph("负责人：").setFontSize(11))
                .add(new Paragraph("日期：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))).setFontSize(11))
                .setBorder(null)
                .setTextAlignment(TextAlignment.LEFT);

        Cell right = new Cell()
                .add(new Paragraph("\n"))
                .add(new Paragraph("（盖章）").setFontSize(11).setTextAlignment(TextAlignment.CENTER))
                .setBorder(null)
                .setTextAlignment(TextAlignment.CENTER);

        signatureTable.addCell(left);
        signatureTable.addCell(right);
        document.add(signatureTable);
    }

    private void addWatermark(PdfDocument pdfDoc) {
        String watermarkText = "培训专用";
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new IEventHandler() {
            @Override
            public void handleEvent(Event event) {
                PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
                PdfPage page = docEvent.getPage();
                Rectangle pageSize = page.getPageSize();
                PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);
                try {
                    // 字体
                    PdfFont font = loadFont();
                    // 透明度和颜色
                    PdfExtGState gs = new PdfExtGState().setFillOpacity(0.15f);
                    canvas.saveState().setExtGState(gs)
                            .setFillColor(ColorConstants.LIGHT_GRAY)
                            .setFontAndSize(font, 46)
                            .beginText()
                            .setTextMatrix(AffineTransform.getRotateInstance(
                                    Math.toRadians(45),
                                    pageSize.getWidth() / 2 - 660,
                                    pageSize.getHeight() / 2 + 280))
                            .showText(watermarkText)
                            .endText()
                            .restoreState();
                } catch (IOException e) {
                    throw new RuntimeException("水印字体加载失败", e);
                }
            }
        });
    }

    public byte[] generateBatchCertificatesPdf(List<TrainingCertificateDto> dtos) {
        try (ByteArrayOutputStream finalBaos = new ByteArrayOutputStream()) {
            PdfDocument finalPdf = new PdfDocument(new PdfWriter(finalBaos));
            PdfMerger merger = new PdfMerger(finalPdf);

            for (TrainingCertificateDto dto : dtos) {
                byte[] singleBytes = generateCertificatePdf(dto);
                PdfDocument singlePdf = new PdfDocument(new PdfReader(new ByteArrayInputStream(singleBytes)));
                merger.merge(singlePdf, 1, singlePdf.getNumberOfPages());
                singlePdf.close();
            }

            finalPdf.close();
            return finalBaos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("批量生成PDF失败", e);
        }
    }

    /* ================= 工具方法 ================= */

    private String formatDate(LocalDateTime date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")) : "未知";
    }

    private String getUserRoleDisplayName(String role) {
        switch (role) {
            case "EXPLOSIVE_FIRST":
                return "易制爆用户-首次培训";
            case "EXPLOSIVE_CONTINUE":
                return "易制爆用户-继续教育";
            case "BLAST_THREE_FIRST":
                return "爆破三大员-首次培训";
            case "BLAST_THREE_CONTINUE":
                return "爆破三大员-继续教育";
            case "BLAST_TECH_FIRST":
                return "爆破工程技术人员-首次培训";
            case "BLAST_TECH_CONTINUE":
                return "爆破工程技术人员-继续教育";
            case "SUPER_ADMIN":
                return "超级管理员";
            default:
                return "未知";
        }
    }

    private String getCertificateTypeDisplayName(String type) {
        switch (type) {
            case "EXPLOSIVE_USER":
                return "易制爆人员培训证明";
            case "BLAST_USER":
                return "爆破三大员培训证明";
            default:
                return "未知";
        }
    }

    private String safe(String value) {
        return (value != null && !value.trim().isEmpty()) ? value : "未知";
    }
}
