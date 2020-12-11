package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.utils.RegexUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * 房屋租售常量实体对象
 * YuLF
 * 数据访问对象：这个类主要用于对应数据库表t_house_const的数据字段的映射关系，
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_house_const")
public class HouseLeaseConstEntity extends BaseEntity {

    //常量名称
    private String houseConstName;

    //常量值
    private String houseConstValue;

    //常量类型
    private String houseConstType;

    //常量注释
    private String annotation;

    public HouseLeaseConstEntity(Long id, String houseConstName, String houseConstType, String annotation){
        super.setId(id);
        this.houseConstName = houseConstName;
        this.houseConstType = houseConstType;
        this.annotation = annotation;
    }
    public HouseLeaseConstEntity(){}
}
