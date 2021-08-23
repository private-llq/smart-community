package com.jsy.community.vo.property;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDateTime;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author Arli
 * @since 2021-08-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WOptionsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 选项
     */
    private String choice;

    /**
     * 选项内容
     */
    private String optionContent;

    /**
     * 问题id
     */
    private Long problemId;

    /**
     * 问卷id
     */
    private Long questionnaireId;



}
