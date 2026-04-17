package com.abswitch.weblog.web.model.vo.archive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindArchiveListReqVO {

    /**
     * 语言（前台中英文切换）：zh/en，默认 zh
     */
    private String lang = "zh";
}
