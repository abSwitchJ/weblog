package com.abswitch.weblog.common.utils;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import java.util.function.Function;
import java.util.regex.Pattern;

@Slf4j
public class SlugUtil {

    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]");
    private static final int MAX_SLUG_LENGTH = 80;

    private static final HanyuPinyinOutputFormat PINYIN_FORMAT = new HanyuPinyinOutputFormat();

    static {
        PINYIN_FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        PINYIN_FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        PINYIN_FORMAT.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    /**
     * 根据标题生成 slug
     * <p>
     * 英文标题：直接转小写 + 空格用 "-" 代替
     * 中文标题：通过百度翻译 API 翻译为英文后再转 slug
     *
     * @param title     文章标题
     * @param appId     百度翻译 appId
     * @param secretKey 百度翻译 secretKey
     * @return slug 字符串
     */
    public static String generateSlug(String title, String appId, String secretKey) {
        if (title == null || title.isBlank()) {
            return "";
        }

        String text = title.trim();

        // 如果包含中文，先翻译为英文
        if (containsChinese(text)) {
            String translated = BaiduTranslateUtil.translateToEnglish(text, appId, secretKey);
            if (translated != null && !translated.isBlank()) {
                text = translated;
            } else {
                log.warn("翻译失败，使用拼音生成 slug: {}", title);
                text = toPinyin(text);
            }
        }

        // 转小写
        String slug = text.toLowerCase();
        // 将非字母数字字符替换为连字符
        slug = slug.replaceAll("[^a-z0-9]+", "-");
        // 去除首尾连字符
        slug = slug.replaceAll("^-+|-+$", "");

        // 截断到最大长度，在连字符处截断
        if (slug.length() > MAX_SLUG_LENGTH) {
            slug = slug.substring(0, MAX_SLUG_LENGTH);
            int lastHyphen = slug.lastIndexOf('-');
            if (lastHyphen > 0) {
                slug = slug.substring(0, lastHyphen);
            }
        }

        return slug;
    }

    /**
     * 确保 slug 唯一，重复时追加数字后缀
     *
     * @param baseSlug      基础 slug
     * @param existsChecker 检查 slug 是否已存在的函数
     * @return 唯一的 slug
     */
    public static String ensureUnique(String baseSlug, Function<String, Boolean> existsChecker) {
        if (!existsChecker.apply(baseSlug)) {
            return baseSlug;
        }

        int suffix = 2;
        while (true) {
            String candidate = baseSlug + "-" + suffix;
            if (!existsChecker.apply(candidate)) {
                return candidate;
            }
            suffix++;
        }
    }

    private static boolean containsChinese(String text) {
        return CHINESE_PATTERN.matcher(text).find();
    }

    private static String toPinyin(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 0x4e00 && c <= 0x9fa5) {
                try {
                    String[] pinyinArr = PinyinHelper.toHanyuPinyinStringArray(c, PINYIN_FORMAT);
                    if (pinyinArr != null && pinyinArr.length > 0) {
                        sb.append(' ').append(pinyinArr[0]).append(' ');
                        continue;
                    }
                } catch (Exception e) {
                    log.warn("字符 [{}] 转拼音失败: {}", c, e.getMessage());
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
