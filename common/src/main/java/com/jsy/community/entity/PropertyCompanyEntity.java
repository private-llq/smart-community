package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @program: com.jsy.community
 * @description: 物业公司
 * @author: Hu
 * @create: 2021-08-20 14:58
 **/
@Data
@TableName("t_property_company")
public class PropertyCompanyEntity extends BaseEntity {
    /**
     * 公司名称
     */
    private String name;
    /**
     * 公司简介
     */
    private String describe;
    /**
     * 公司图片   以逗号分割
     */
    private String picture;

}
