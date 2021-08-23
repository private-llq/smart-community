package com.jsy.community.vo.property;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
@Accessors(chain = true)
@ApiModel("查询调查问卷的统计情况vo")
@AllArgsConstructor
@NoArgsConstructor
public class SelectQuestionnaireStatisticsVO implements Serializable {
    private static final long serialVersionUID = 1L;
    //统计人数
    private Integer sumALL;
    //问题集合
   private List<ProblemStatisticsVO>   problemStatisticsVOList;


}
