package com.training.util;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Word模板生成工具
 * 用于生成正确格式的Word文档模板
 */
public class WordTemplateGenerator {

    /**
     * 生成题目导入模板
     */
    public static byte[] generateTemplate() throws IOException {
        XWPFDocument document = new XWPFDocument();

        // 添加标题
        XWPFParagraph titleParagraph = document.createParagraph();
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText("易制爆与爆破作业人员培训题库");
        titleRun.setBold(true);
        titleRun.setFontSize(16);

        // 添加说明
        XWPFParagraph descParagraph = document.createParagraph();
        XWPFRun descRun = descParagraph.createRun();
        descRun.setText("请按照以下格式编写题目，注意：");
        descRun.setBold(true);
        descRun.setFontSize(12);

        XWPFParagraph rule1Paragraph = document.createParagraph();
        XWPFRun rule1Run = rule1Paragraph.createRun();
        rule1Run.setText("1. 题目类型必须用【】包围");
        rule1Run.setFontSize(11);

        XWPFParagraph rule2Paragraph = document.createParagraph();
        XWPFRun rule2Run = rule2Paragraph.createRun();
        rule2Run.setText("2. 选项必须用A、B、C、D等字母标识");
        rule2Run.setFontSize(11);

        XWPFParagraph rule3Paragraph = document.createParagraph();
        XWPFRun rule3Run = rule3Paragraph.createRun();
        rule3Run.setText("3. 答案格式：单选题和判断题用单个字母，多选题用逗号分隔");
        rule3Run.setFontSize(11);

        XWPFParagraph rule4Paragraph = document.createParagraph();
        XWPFRun rule4Run = rule4Paragraph.createRun();
        rule4Run.setText("4. 解析部分可选，以解析：开头");
        rule4Run.setFontSize(11);

        // 添加空行
        document.createParagraph();

        // 添加示例题目
        addExampleQuestion(document, "单选题",
            "易制爆物品是指哪些物品？",
            new String[]{"具有爆炸性的物品", "具有燃烧性的物品", "具有毒性的物品", "具有腐蚀性的物品"},
            "A",
            "易制爆物品是指具有爆炸性的物品，包括炸药、雷管等。");

        addExampleQuestion(document, "多选题",
            "以下哪些属于易制爆物品？",
            new String[]{"硝酸铵", "氯酸钾", "高锰酸钾", "硫磺"},
            "A,B,C,D",
            "这些都是常见的易制爆物品。");

        addExampleQuestion(document, "判断题",
            "易制爆物品可以随意购买和使用。",
            new String[]{"正确", "错误"},
            "B",
            "易制爆物品需要特殊许可才能购买和使用。");

        addExampleQuestion(document, "填空题",
            "易制爆物品的储存要求是什么？",
            null,
            "必须储存在专用仓库中，远离火源和热源",
            "易制爆物品具有爆炸性，必须严格管理。");

        addExampleQuestion(document, "简答题",
            "简述易制爆物品的安全管理措施。",
            null,
            "1. 建立完善的管理制度；2. 指定专人负责；3. 定期检查；4. 建立应急预案",
            "易制爆物品管理需要多方面的安全措施。");

                // 将文档转换为字节数组
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            document.write(outputStream);
            return outputStream.toByteArray();
        } finally {
            document.close();
            outputStream.close();
        }
    }

    /**
     * 添加示例题目
     */
    private static void addExampleQuestion(XWPFDocument document, String type, String content,
                                         String[] options, String answer, String explanation) {
        // 添加题目类型
        XWPFParagraph typeParagraph = document.createParagraph();
        XWPFRun typeRun = typeParagraph.createRun();
        typeRun.setText("【" + type + "】");
        typeRun.setBold(true);
        typeRun.setFontSize(14);

        // 添加题目内容
        XWPFParagraph contentParagraph = document.createParagraph();
        XWPFRun contentRun = contentParagraph.createRun();
        contentRun.setText(content);
        contentRun.setFontSize(12);

        // 添加选项（如果有）
        if (options != null) {
            for (int i = 0; i < options.length; i++) {
                XWPFParagraph optionParagraph = document.createParagraph();
                XWPFRun optionRun = optionParagraph.createRun();
                String optionLabel = String.valueOf((char) ('A' + i));
                optionRun.setText(optionLabel + ". " + options[i]);
                optionRun.setFontSize(12);
            }
        }

        // 添加答案
        XWPFParagraph answerParagraph = document.createParagraph();
        XWPFRun answerRun = answerParagraph.createRun();
        answerRun.setText("答案：" + answer);
        answerRun.setBold(true);
        answerRun.setFontSize(12);

        // 添加解析
        if (explanation != null && !explanation.trim().isEmpty()) {
            XWPFParagraph explanationParagraph = document.createParagraph();
            XWPFRun explanationRun = explanationParagraph.createRun();
            explanationRun.setText("解析：" + explanation);
            explanationRun.setFontSize(12);
        }

        // 添加空行
        document.createParagraph();
    }
}
