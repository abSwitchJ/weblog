package com.abswitch.weblog.web.markdown.renderer;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlWriter;

import java.util.Objects;
import java.util.Set;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-31 16:58
 * @Description：
 */
public class LinkNodeRenderer implements NodeRenderer {

    private final HtmlWriter html;

    /**
     * 网站域名（上线后需要改成自己的域名）
     */
    private final static String DOMAIN = "www.abswitchj.com";


    public LinkNodeRenderer(HtmlNodeRendererContext context) {
        this.html = context.getWriter();
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        // 指定想要自定义渲染的节点，这里指定为超链接 Link
        return Sets.newHashSet(Link.class);
    }

    @Override
    public void render(Node node) {
        if (node instanceof Link) {
            Link link = (Link) node;

            // 链接的内容
            String linkDescription = null;
            if (Objects.nonNull(link.getFirstChild())) {
                Text text = (Text) link.getFirstChild();
                linkDescription = text.getLiteral();
            }

            // 链接 url
            String linkUrl = link.getDestination();
            // 链接标题
            String linkTitle = link.getTitle();

            // 拼接 HTML 结构
            StringBuilder sb = new StringBuilder("<a href=\"");
            sb.append(linkUrl);
            sb.append("\"");

            // 添加 title="链接标题" 属性
            if (StringUtils.isNotBlank(linkTitle)) {
                sb.append(String.format(" title=\"%s\"", linkTitle));
            }

            // 如果链接不是自己域名，则添加 rel="nofollow" 属性
            if (!linkUrl.contains(DOMAIN)) {
                sb.append(" ref=\"nofollow\"");
            }

            // 添加 target="_blank" 属性
            sb.append(" target=\"_blank\">");
            // 超链接展示内容
            sb.append(StringUtils.isNotBlank(linkDescription) ? linkDescription : linkUrl);
            sb.append("</a>");

            // 设置 HTML 内容
            html.raw(sb.toString());
        }
    }
}


