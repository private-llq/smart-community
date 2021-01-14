package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 *
 * @author YuLF
 * @since  2021/1/13 17:51
 * 房屋租售常量实体对象
 * 数据访问对象：这个类主要用于对应数据库表t_house_const的数据字段的映射关系，
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_house_const")
public class HouseLeaseConstEntity extends BaseEntity {

    private Long houseConstCode;

    /**
     * 常量名称
     */
    private String houseConstName;

    /**
     * 常量值
     */
    private String houseConstValue;

    /**
     * 常量类型
     */
    private String houseConstType;

    /**
     * 常量注释
     */
    private String annotation;

    public HouseLeaseConstEntity(Long id, Long houseConstCode, String houseConstName, String houseConstType, String annotation){
        super.setId(id);
        this.houseConstCode = houseConstCode;
        this.houseConstName = houseConstName;
        this.houseConstType = houseConstType;
        this.annotation = annotation;
    }
    public HouseLeaseConstEntity(){}
}
