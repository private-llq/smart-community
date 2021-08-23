package com.jsy.community.vo.property;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = false)
@Data
@Accessors(chain = true)
@ApiModel("选项统计vo")
@AllArgsConstructor
@NoArgsConstructor
public class WOptionsStatisticsVO  implements Serializable {

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
     * 数量
     */
    private Integer amount;




}
