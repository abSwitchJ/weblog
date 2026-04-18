package com.abswitch.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-23 19:37
 * @Description：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName(value = "t_blog_settings")
public class BlogSettingsDO {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String logo;

    private String name;

    private String author;

    private String introduction;

    private String avatar;

    private String githubHomepage;

    private String csdnHomepage;

    private String giteeHomepage;

    private String zhihuHomepage;

    private String twitterHomepage;

    private String about;
}
