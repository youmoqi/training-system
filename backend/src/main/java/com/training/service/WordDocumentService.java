package com.training.service;

import com.training.dto.QuestionDto;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class WordDocumentService {

    /**
     * 解析Word文档中的题目
     */
    public List<QuestionDto> parseQuestionsFromWord(MultipartFile file) throws IOException {
        List<QuestionDto> questions = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            String content = extractTextFromWord(inputStream, file.getOriginalFilename());
            questions = parseQuestionsFromText(content);
        }

        return questions;
    }

        /**
     * 从Word文档中提取文本
     */
    private String extractTextFromWord(InputStream inputStream, String fileName) throws IOException {
        try {
            if (fileName.toLowerCase().endsWith(".docx")) {
                // 处理.docx文件
                XWPFDocument document = new XWPFDocument(inputStream);
                StringBuilder text = new StringBuilder();
                
                for (XWPFParagraph paragraph : document.getParagraphs()) {
                    String paragraphText = paragraph.getText();
                    if (paragraphText != null && !paragraphText.trim().isEmpty()) {
                        text.append(paragraphText).append("\n");
                    }
                }
                
                document.close();
                return text.toString();
            } else if (fileName.toLowerCase().endsWith(".doc")) {
                // 处理.doc文件
                HWPFDocument document = new HWPFDocument(inputStream);
                WordExtractor extractor = new WordExtractor(document);
                String text = extractor.getText();
                document.close();
                return text;
            } else {
                throw new IllegalArgumentException("不支持的文件格式，请上传.doc或.docx文件");
            }
        } catch (Exception e) {
            throw new IOException("文件解析失败: " + e.getMessage(), e);
        }
    }

        /**
     * 从文本中解析题目
     */
    private List<QuestionDto> parseQuestionsFromText(String text) throws IOException {
        List<QuestionDto> questions = new ArrayList<>();
        
        if (text == null || text.trim().isEmpty()) {
            throw new IOException("文档内容为空");
        }
        
        // 按题目类型分割文本
        String[] sections = text.split("【");
        
        for (String section : sections) {
            if (section.trim().isEmpty()) {
                continue;
            }
            
            try {
                // 找到题目类型
                int typeEndIndex = section.indexOf("】");
                if (typeEndIndex == -1) {
                    continue;
                }
                
                String questionType = section.substring(0, typeEndIndex).trim();
                String questionContent = section.substring(typeEndIndex + 1).trim();
                
                // 解析题目
                QuestionDto question = parseQuestion(questionType, questionContent);
                if (question != null && question.getContent() != null && !question.getContent().trim().isEmpty()) {
                    questions.add(question);
                }
            } catch (Exception e) {
                // 记录错误但继续处理其他题目
                System.err.println("解析题目时出错: " + e.getMessage());
            }
        }
        
        if (questions.isEmpty()) {
            throw new IOException("未找到有效的题目，请检查文档格式是否正确");
        }
        
        return questions;
    }

        /**
     * 解析单个题目
     */
    private QuestionDto parseQuestion(String questionType, String content) throws IOException {
        if (content == null || content.trim().isEmpty()) {
            return null;
        }
        
        QuestionDto question = new QuestionDto();
        
        try {
            // 设置题目类型
            question.setType(getQuestionType(questionType));
            
            // 解析题目内容、选项、答案和解析
            QuestionParseResult result = parseQuestionContent(content);
            
            // 验证题目内容
            if (result.getContent() == null || result.getContent().trim().isEmpty()) {
                return null;
            }
            
            question.setContent(result.getContent());
            question.setOptions(result.getOptions());
            question.setAnswers(result.getAnswers());
            question.setExplanation(result.getExplanation());
            
            return question;
        } catch (Exception e) {
            throw new IOException("解析题目失败: " + e.getMessage(), e);
        }
    }

        /**
     * 解析题目内容
     */
    private QuestionParseResult parseQuestionContent(String content) throws IOException {
        QuestionParseResult result = new QuestionParseResult();
        
        if (content == null || content.trim().isEmpty()) {
            throw new IOException("题目内容为空");
        }
        
        String[] lines = content.split("\n");
        StringBuilder contentBuilder = new StringBuilder();
        List<String> options = new ArrayList<>();
        List<String> answers = new ArrayList<>();
        String explanation = "";
        
        boolean inOptions = false;
        boolean foundAnswer = false;
        boolean foundExplanation = false;
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            
            try {
                // 检查是否是答案行
                if (line.startsWith("答案：")) {
                    foundAnswer = true;
                    String answerText = line.substring(3).trim();
                    if (answerText.contains(",")) {
                        // 多选题答案
                        String[] answerArray = answerText.split(",");
                        for (String answer : answerArray) {
                            if (!answer.trim().isEmpty()) {
                                answers.add(answer.trim());
                            }
                        }
                    } else {
                        // 单选题或判断题答案
                        if (!answerText.isEmpty()) {
                            answers.add(answerText);
                        }
                    }
                    continue;
                }
                
                // 检查是否是解析行
                if (line.startsWith("解析：")) {
                    foundExplanation = true;
                    explanation = line.substring(3).trim();
                    continue;
                }
                
                // 检查是否是选项行
                if (line.matches("^[A-Z]\\..*")) {
                    inOptions = true;
                    String optionContent = line.substring(2).trim();
                    if (!optionContent.isEmpty()) {
                        options.add(optionContent);
                    }
                    continue;
                }
                
                // 如果还没有找到答案，且不是选项，则为题目内容
                if (!foundAnswer && !inOptions) {
                    // 移除题目编号
                    String cleanLine = line.replaceAll("^\\d+\\.\\s*", "");
                    if (!cleanLine.isEmpty()) {
                        contentBuilder.append(cleanLine).append(" ");
                    }
                }
            } catch (Exception e) {
                // 记录错误但继续处理
                System.err.println("解析行时出错: " + line + ", 错误: " + e.getMessage());
            }
        }

        String finalContent = contentBuilder.toString().trim();
        if (finalContent.isEmpty()) {
            throw new IOException("题目内容解析失败");
        }

        result.setContent(finalContent);
        result.setOptions(options);
        result.setAnswers(answers);
        result.setExplanation(explanation);

        return result;
    }

    /**
     * 获取题目类型
     */
    private String getQuestionType(String typeText) {
        switch (typeText) {
            case "单选题":
                return "SINGLE_CHOICE";
            case "多选题":
                return "MULTIPLE_CHOICE";
            case "判断题":
                return "TRUE_FALSE";
            case "填空题":
                return "FILL_BLANK";
            case "简答题":
                return "SHORT_ANSWER";
            default:
                throw new IllegalArgumentException("不支持的题目类型: " + typeText);
        }
    }

    /**
     * 获取题目类型文本
     */
    private String getQuestionTypeText(String type) {
        switch (type) {
            case "SINGLE_CHOICE":
                return "单选题";
            case "MULTIPLE_CHOICE":
                return "多选题";
            case "TRUE_FALSE":
                return "判断题";
            case "FILL_BLANK":
                return "填空题";
            case "SHORT_ANSWER":
                return "简答题";
            default:
                return "未知类型";
        }
    }

    public byte[] generateWordDocument(List<QuestionDto> questions, String questionBankTitle) throws IOException {
        XWPFDocument document = new XWPFDocument();

        // 添加标题
        XWPFParagraph titleParagraph = document.createParagraph();
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText(questionBankTitle != null ? questionBankTitle : "题目库");
        titleRun.setBold(true);
        titleRun.setFontSize(16);

        // 添加空行
        document.createParagraph();

        int questionNumber = 1;
        for (QuestionDto question : questions) {
            // 添加题目类型
            XWPFParagraph typeParagraph = document.createParagraph();
            XWPFRun typeRun = typeParagraph.createRun();
            typeRun.setText("【" + getQuestionTypeText(question.getType()) + "】");
            typeRun.setBold(true);
            typeRun.setFontSize(14);

            // 添加题目内容
            XWPFParagraph contentParagraph = document.createParagraph();
            XWPFRun contentRun = contentParagraph.createRun();
            contentRun.setText(questionNumber + ". " + question.getContent());
            contentRun.setFontSize(12);

            // 添加选项（如果是选择题）
            if (question.getOptions() != null && !question.getOptions().isEmpty()) {
                for (int i = 0; i < question.getOptions().size(); i++) {
                    XWPFParagraph optionParagraph = document.createParagraph();
                    XWPFRun optionRun = optionParagraph.createRun();
                    String optionLabel = String.valueOf((char) ('A' + i));
                    optionRun.setText(optionLabel + ". " + question.getOptions().get(i));
                    optionRun.setFontSize(12);
                }
            }

            // 添加答案
            XWPFParagraph answerParagraph = document.createParagraph();
            XWPFRun answerRun = answerParagraph.createRun();
            String answerText = "答案：";
            if (question.getAnswers() != null && !question.getAnswers().isEmpty()) {
                if ("MULTIPLE_CHOICE".equals(question.getType())) {
                    // 多选题：将答案内容转换为选项标签
                    List<String> answerLabels = new ArrayList<>();
                    for (String answer : question.getAnswers()) {
                        if (question.getOptions() != null) {
                            int index = question.getOptions().indexOf(answer);
                            if (index >= 0) {
                                answerLabels.add(String.valueOf((char) ('A' + index)));
                            } else {
                                answerLabels.add(answer);
                            }
                        } else {
                            answerLabels.add(answer);
                        }
                    }
                    answerText += String.join(",", answerLabels);
                } else if ("SINGLE_CHOICE".equals(question.getType()) || "TRUE_FALSE".equals(question.getType())) {
                    // 单选题和判断题：将答案内容转换为选项标签
                    String answer = question.getAnswers().get(0);
                    if (question.getOptions() != null) {
                        int index = question.getOptions().indexOf(answer);
                        if (index >= 0) {
                            answerText += String.valueOf((char) ('A' + index));
                        } else {
                            answerText += answer;
                        }
                    } else {
                        answerText += answer;
                    }
                } else {
                    // 填空题和简答题：直接使用答案内容
                    answerText += String.join(",", question.getAnswers());
                }
            }
            answerRun.setText(answerText);
            answerRun.setBold(true);
            answerRun.setFontSize(12);

            // 添加解析（如果有）
            if (question.getExplanation() != null && !question.getExplanation().trim().isEmpty()) {
                XWPFParagraph explanationParagraph = document.createParagraph();
                XWPFRun explanationRun = explanationParagraph.createRun();
                explanationRun.setText("解析：" + question.getExplanation());
                explanationRun.setFontSize(12);
            }

            // 添加空行
            document.createParagraph();

            questionNumber++;
        }

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
     * 题目解析结果内部类
     */
    private static class QuestionParseResult {
        private String content;
        private List<String> options;
        private List<String> answers;
        private String explanation;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<String> getOptions() {
            return options;
        }

        public void setOptions(List<String> options) {
            this.options = options;
        }

        public List<String> getAnswers() {
            return answers;
        }

        public void setAnswers(List<String> answers) {
            this.answers = answers;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }
    }
}
