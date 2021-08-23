package com.jsy.community.qo.property;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = false)
@Data
@Accessors(chain = true)
@ApiModel("调查问卷连带问题")
@AllArgsConstructor
@NoArgsConstructor
public class WProblemQO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 题目
     */
    private String problem;

    /**
     * 题目类型（0单选,1多选,2简答）
     */
    private Integer problemType;
    /**
     * 选项集合，简答为空
     */
   private ArrayList<WOptionsQO> optionsList;



}
