package com.jsy.community.vo.property;

import com.jsy.community.vo.property.WOptionsStatisticsVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;

@EqualsAndHashCode(callSuper = false)
@Data
@Accessors(chain = true)
@ApiModel("问题统计vo")
@NoArgsConstructor
@AllArgsConstructor
public class ProblemStatisticsVO  implements Serializable {
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
     * 题目回答各个选项数量和
     */
    private Integer problemAmount;

    /**
     * 选项集合，简答为空
     */
    private ArrayList<WOptionsStatisticsVO> wOptionsStatisticsVOArrayList;

    private ArrayList<String>  ShortAnswerList;
}
