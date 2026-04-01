package com.abswitch.weblog.admin.event;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-31 11:21
 * @Description：
 */

@Getter
public class PublishArticleEvent extends ApplicationEvent {

    /**
     * 文章 ID
     */
    private Long articleId;

    public PublishArticleEvent(Object source, Long articleId) {
        super(source);
        this.articleId = articleId;
    }
}
