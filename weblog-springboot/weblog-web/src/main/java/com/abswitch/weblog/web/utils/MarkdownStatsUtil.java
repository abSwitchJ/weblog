package com.abswitch.weblog.web.utils;

import lombok.extern.slf4j.Slf4j;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;

import java.util.regex.Pattern;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-31 14:20
 * @Description：
 */
@Slf4j
public class MarkdownStatsUtil {

    // 每分钟阅读字数（保留，可扩展阅读时间估算）
    private static final int WORDS_READ_PRE_MINUTE = 300;

    // 匹配所有汉字字符（支持扩展区、日韩汉字等）
    private static final Pattern HAN_CHAR_PATTERN = Pattern.compile("\\p{IsHan}");

    // 匹配空白字符（用于分割单词）
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    // 匹配纯标点符号（一个或多个标点）
    private static final Pattern PURE_PUNCTUATION_PATTERN = Pattern.compile("^\\p{Punct}+$");

    // 匹配纯数字（一个或多个数字）
    private static final Pattern PURE_DIGIT_PATTERN = Pattern.compile("^\\d+$");

    /**
     * 计算 Markdown 文本的真实阅读字数（中文字符 + 有效非中文单词）
     * - 中文按字计数
     * - 非中文单词按空格分隔，但纯标点或纯数字不计入
     * - 自动去除 Markdown 标记
     *
     * @param markdown Markdown 格式的源文本
     * @return 估算字数
     */
    public static int calculateWordCount(String markdown) {
        // 1. 将 Markdown 转换为纯文本
        String plainText = markdownToPlainText(markdown);

        // 2. 统计中文字符数（使用 \p{IsHan}）
        int chineseCount = countChineseCharacters(plainText);
        // 3. 移除中文字符，得到非中文文本
        String textWithoutChinese = HAN_CHAR_PATTERN.matcher(plainText).replaceAll(" ");

        // 4. 如果剩余文本为空，直接返回中文字数
        if (textWithoutChinese.isBlank()) {
            return chineseCount;
        }

        // 5. 按空白分割得到候选单词
        String[] candidates = WHITESPACE_PATTERN.split(textWithoutChinese.trim());

        // 6. 统计有效单词（非纯标点、非纯数字）
        int validWordCount = 0;
        for (String word : candidates) {
            if (isValidWord(word)) {
                validWordCount++;
            }
        }

        return chineseCount + validWordCount;
    }

    /**
     * 计算阅读时长
     * @param wordCount
     * @return
     */
    public static String calculateReadingTime(int wordCount) {
        // 判断总字数是否大于一分钟阅读字数
        if (wordCount >= WORDS_READ_PRE_MINUTE) {
            // 计算花费分钟数
            int minutes = wordCount / WORDS_READ_PRE_MINUTE;
            return "约 " + minutes + " 分钟";
        }

        // 若总字数小于一分钟阅读字数，则按秒来计算
        int seconds = (wordCount % WORDS_READ_PRE_MINUTE) * 60 / WORDS_READ_PRE_MINUTE;

        // 最小阅读时长为 1s
        if (seconds == 0) seconds = 1;

        return "约 " + seconds + " 秒";
    }

    /**
     * 使用 CommonMark 将 Markdown 转为纯文本（去除所有标记、链接、代码块等）
     */
    private static String markdownToPlainText(String markdown) {
        Parser parser = Parser.builder().build();
        TextContentRenderer renderer = TextContentRenderer.builder().build();
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }

    /**
     * 统计纯文本中的中文字符数（支持 Unicode 所有汉字）
     */
    private static int countChineseCharacters(String text) {
        int count = 0;
        var matcher = HAN_CHAR_PATTERN.matcher(text);
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * 判断一个单词是否为有效单词（不被过滤）
     * 过滤规则：纯标点符号 或 纯数字 视为无效
     */
    private static boolean isValidWord(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }
        // 纯标点符号 -> 无效
        if (PURE_PUNCTUATION_PATTERN.matcher(word).matches()) {
            return false;
        }
        // 纯数字 -> 无效
        return !PURE_DIGIT_PATTERN.matcher(word).matches();
    }
}

