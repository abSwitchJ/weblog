package com.abswitch.weblog.web.model.vo.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TocItemVO {

    private String id;

    private String text;

    private Integer level;
}
