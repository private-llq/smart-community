package com.jsy.community.qo.property;

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
@ApiModel("调查问卷连带问题的选项")
@AllArgsConstructor
@NoArgsConstructor
public class WOptionsQO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 选项
     */
    private String choice;
    /**
     * 选项内容
     */
    private String optionContent;
}
