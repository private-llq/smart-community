package com.jsy.community.qo.property;

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
@ApiModel("用户的提交调查问卷参数对象")
@NoArgsConstructor
@AllArgsConstructor
public class InsterAnswerQO implements Serializable {
    private static final long serialVersionUID = 1L;
    /*问卷id*/
    private Long id;

    private List<InsterAnswerProblemQO> list;


}
