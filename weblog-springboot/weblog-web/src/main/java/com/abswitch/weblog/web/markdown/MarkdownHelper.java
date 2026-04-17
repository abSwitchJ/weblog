package com.abswitch.weblog.web.markdown;

import com.abswitch.weblog.web.markdown.renderer.ImageNodeRenderer;
import com.abswitch.weblog.web.markdown.renderer.LinkNodeRenderer;
import com.abswitch.weblog.web.model.vo.article.TocItemVO;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.ext.image.attributes.ImageAttributesExtension;
import org.commonmark.ext.task.list.items.TaskListItemsExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-26 16:42
 * @Description：
 */
public class MarkdownHelper {

    /**
     * Markdown 解析器
     */
    private final static Parser PARSER;
    /**
     * HTML 渲染器
     */
    private final static HtmlRenderer HTML_RENDERER;

    /**
     * 初始化
     */
    static {
        // Markdown 拓展
        List<Extension> extensions = Arrays.asList(
                TablesExtension.create(), // 表格拓展
                HeadingAnchorExtension.create(), // 标题锚定项
                ImageAttributesExtension.create(), // 图片宽高
                TaskListItemsExtension.create() // 任务列表
        );

        PARSER = Parser.builder().extensions(extensions).build();
        HTML_RENDERER = HtmlRenderer.builder()
                .extensions(extensions)
                .nodeRendererFactory(context -> new ImageNodeRenderer(context)) // 自定义图片解析
                .nodeRendererFactory(context -> new LinkNodeRenderer(context)) // 自定义超链接解析
                .build();
    }



    /**
     * 将 Markdown 转换成 HTML
     * @param markdown
     * @return
     */
    public static String convertMarkdown2Html(String markdown) {
        Node document = PARSER.parse(markdown);
        return HTML_RENDERER.render(document);
    }

    /**
     * 匹配 <h[1-6] ... id="..." ...>...</hN>。DOTALL 允许标题内容跨行（极少见但保险）。
     */
    private static final Pattern HEADING_PATTERN = Pattern.compile(
            "<h([1-6])\\s+[^>]*?\\bid=\"([^\"]+)\"[^>]*>(.*?)</h\\1>",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    /**
     * 从渲染后的 HTML 中提取目录。只保留 h1-h6 中级别最高（level 最小）的所有同级标题。
     */
    public static List<TocItemVO> extractToc(String html) {
        if (html == null || html.isEmpty()) {
            return Collections.emptyList();
        }
        Matcher m = HEADING_PATTERN.matcher(html);
        List<TocItemVO> all = new ArrayList<>();
        while (m.find()) {
            int level = Integer.parseInt(m.group(1));
            String id = m.group(2);
            String text = stripHtmlTags(m.group(3)).trim();
            if (id == null || id.isEmpty() || text.isEmpty()) {
                continue;
            }
            all.add(TocItemVO.builder().level(level).id(id).text(text).build());
        }
        if (all.isEmpty()) {
            return Collections.emptyList();
        }
        int topLevel = all.stream().mapToInt(TocItemVO::getLevel).min().getAsInt();
        return all.stream()
                .filter(i -> i.getLevel() == topLevel)
                .collect(Collectors.toList());
    }

    /**
     * 去除字符串中的 HTML 标签，保留纯文本。
     */
    private static String stripHtmlTags(String input) {
        return input.replaceAll("<[^>]+>", "");
    }

    /**
     * 百度等翻译 API 常把 "# xxx" 的井号与文本间的空格吞掉（变成 "#xxx"），
     * 导致 CommonMark 不再识别为标题。这里按行修复：行首（最多 3 空格缩进）出现
     * 1-6 个 #、紧跟非空白非 # 字符时，在 # 后补一个空格。Fenced code block 内不处理。
     */
    public static String normalizeHeadings(String markdown) {
        if (markdown == null || markdown.isEmpty()) return markdown;
        String[] lines = markdown.split("\n", -1);
        boolean inFence = false;
        String fenceMarker = null;
        for (int i = 0; i < lines.length; i++) {
            String stripped = lines[i].stripLeading();
            if (!inFence) {
                if (stripped.startsWith("```") || stripped.startsWith("~~~")) {
                    inFence = true;
                    fenceMarker = stripped.startsWith("```") ? "```" : "~~~";
                    continue;
                }
                lines[i] = lines[i].replaceFirst("^(\\s{0,3}#{1,6})(?=[^\\s#])", "$1 ");
            } else {
                if (stripped.startsWith(fenceMarker)) {
                    inFence = false;
                    fenceMarker = null;
                }
            }
        }
        return String.join("\n", lines);
    }

    public static void main(String[] args) {
        String markdown = "# 一级标题\n\n## 二级A\n\n正文\n\n## 二级B\n\n### 三级\n\n## 二级C";
        String html = MarkdownHelper.convertMarkdown2Html(markdown);
        System.out.println(html);
        System.out.println("---- TOC ----");
        MarkdownHelper.extractToc(html).forEach(System.out::println);
    }
}

