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
@ApiModel("用户的提交调查问卷参数对象")
@NoArgsConstructor
@AllArgsConstructor
public class InsterAnswerProblemQO implements Serializable {
    private static final long serialVersionUID = 1L;
    //问题id
    private Long id;
    //问题的类型（0单选,1多选,2简答）
    private Integer type;
    //答案(单选id,多选用;隔开,简答（字符文本）)
    private String  answer;
}
