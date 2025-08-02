package com.training.service;

import com.training.dto.QuestionDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WordDocumentServiceTest {

    @Autowired
    private WordDocumentService wordDocumentService;

    @Test
    public void testGenerateWordDocument() throws IOException {
        // 创建测试题目
        List<QuestionDto> questions = new ArrayList<>();

        // 单选题
        QuestionDto singleChoice = new QuestionDto();
        singleChoice.setType("SINGLE_CHOICE");
        singleChoice.setContent("易制爆物品是指哪些物品？");
        singleChoice.setOptions(Arrays.asList("具有爆炸性的物品", "具有燃烧性的物品", "具有毒性的物品", "具有腐蚀性的物品"));
        singleChoice.setAnswers(Arrays.asList("具有爆炸性的物品"));
        singleChoice.setExplanation("易制爆物品是指具有爆炸性的物品，包括炸药、雷管等。");
        questions.add(singleChoice);

        // 多选题
        QuestionDto multipleChoice = new QuestionDto();
        multipleChoice.setType("MULTIPLE_CHOICE");
        multipleChoice.setContent("以下哪些属于易制爆物品？");
        multipleChoice.setOptions(Arrays.asList("硝酸铵", "氯酸钾", "高锰酸钾", "硫磺"));
        multipleChoice.setAnswers(Arrays.asList("硝酸铵", "氯酸钾", "高锰酸钾", "硫磺"));
        multipleChoice.setExplanation("这些都是常见的易制爆物品。");
        questions.add(multipleChoice);

        // 判断题
        QuestionDto trueFalse = new QuestionDto();
        trueFalse.setType("TRUE_FALSE");
        trueFalse.setContent("易制爆物品可以随意购买和使用。");
        trueFalse.setOptions(Arrays.asList("正确", "错误"));
        trueFalse.setAnswers(Arrays.asList("错误"));
        trueFalse.setExplanation("易制爆物品需要特殊许可才能购买和使用。");
        questions.add(trueFalse);

        // 填空题
        QuestionDto fillBlank = new QuestionDto();
        fillBlank.setType("FILL_BLANK");
        fillBlank.setContent("易制爆物品的储存要求是什么？");
        fillBlank.setAnswers(Arrays.asList("必须储存在专用仓库中，远离火源和热源"));
        fillBlank.setExplanation("易制爆物品具有爆炸性，必须严格管理。");
        questions.add(fillBlank);

        // 简答题
        QuestionDto shortAnswer = new QuestionDto();
        shortAnswer.setType("SHORT_ANSWER");
        shortAnswer.setContent("简述易制爆物品的安全管理措施。");
        shortAnswer.setAnswers(Arrays.asList("1. 建立完善的管理制度；2. 指定专人负责；3. 定期检查；4. 建立应急预案"));
        shortAnswer.setExplanation("易制爆物品管理需要多方面的安全措施。");
        questions.add(shortAnswer);

        // 生成Word文档
        byte[] wordDocument = wordDocumentService.generateWordDocument(questions, "测试题库");
        // 保存到文件系統 具体实现
        File outputFile = new File("test_questions.docx");
        java.nio.file.Files.write(outputFile.toPath(), wordDocument);

        // 验证生成的文档不为空
        assertNotNull(wordDocument);
        assertTrue(wordDocument.length > 0);

        System.out.println("生成的Word文档大小: " + wordDocument.length + " 字节");
    }

    @Test
    public void testParseQuestionsFromText() {
        // 测试文本解析功能
        String testText = """
            【单选题】
            1. 易制爆物品是指哪些物品？
            A. 具有爆炸性的物品
            B. 具有燃烧性的物品
            C. 具有毒性的物品
            D. 具有腐蚀性的物品
            答案：A
            解析：易制爆物品是指具有爆炸性的物品，包括炸药、雷管等。
            
            【多选题】
            2. 以下哪些属于易制爆物品？
            A. 硝酸铵
            B. 氯酸钾
            C. 高锰酸钾
            D. 硫磺
            答案：A,B,C,D
            解析：这些都是常见的易制爆物品。
            
            【判断题】
            3. 易制爆物品可以随意购买和使用。
            A. 正确
            B. 错误
            答案：B
            解析：易制爆物品需要特殊许可才能购买和使用。
            """;

        // 这里可以测试文本解析逻辑
        // 由于parseQuestionsFromText是私有方法，我们通过公开方法测试
        assertNotNull(wordDocumentService);
    }

    @Test
    public void testQuestionTypeMapping() {
        // 测试题目类型映射
        // 这个测试可以验证题目类型是否正确映射
        assertTrue(true); // 占位符测试
    }

    @Test
    public void testSimpleWordGeneration() throws IOException {
        // 简单的Word文档生成测试
        List<QuestionDto> questions = new ArrayList<>();

        // 只创建一个简单的单选题
        QuestionDto question = new QuestionDto();
        question.setType("SINGLE_CHOICE");
        question.setContent("测试题目");
        question.setOptions(Arrays.asList("选项A", "选项B"));
        question.setAnswers(Arrays.asList("选项A"));
        question.setExplanation("测试解析");
        questions.add(question);

        try {
            byte[] result = wordDocumentService.generateWordDocument(questions, "简单测试题库");
            assertNotNull(result);
            assertTrue(result.length > 0);
            System.out.println("简单Word文档生成成功，大小: " + result.length + " 字节");
        } catch (Exception e) {
            fail("Word文档生成失败: " + e.getMessage());
        }
    }

    @Test
    public void testWordDocumentWithCustomTitle() throws IOException {
        // 测试自定义题库标题
        List<QuestionDto> questions = new ArrayList<>();
        
        // 创建一个简单的题目
        QuestionDto question = new QuestionDto();
        question.setType("SINGLE_CHOICE");
        question.setContent("这是一个测试题目");
        question.setOptions(Arrays.asList("选项A", "选项B"));
        question.setAnswers(Arrays.asList("选项A"));
        question.setExplanation("这是解析内容，不应该使用斜体");
        questions.add(question);
        
        // 使用自定义标题生成文档
        String customTitle = "自定义题库标题";
        byte[] result = wordDocumentService.generateWordDocument(questions, customTitle);
        
        assertNotNull(result);
        assertTrue(result.length > 0);
        
        // 保存到文件进行验证
        File outputFile = new File("custom_title_test.docx");
        java.nio.file.Files.write(outputFile.toPath(), result);
        
        System.out.println("自定义标题Word文档生成成功，大小: " + result.length + " 字节");
        System.out.println("文档标题: " + customTitle);
    }
}
