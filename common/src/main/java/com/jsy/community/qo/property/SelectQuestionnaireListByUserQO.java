package com.jsy.community.qo.property;

import com.jsy.community.qo.BaseQO;
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
@ApiModel("分页查询调查问卷（用户可见）")

public class SelectQuestionnaireListByUserQO extends BaseQO implements Serializable {


}
