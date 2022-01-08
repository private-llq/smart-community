package com.jsy.community.dto.advert;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author xrq
 * @version 1.0
 * @Description:
 * @date 2022/1/8 15:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUrlDto {
    /**
     * 文件访问路径
     */
    private String fileUrl;
}
