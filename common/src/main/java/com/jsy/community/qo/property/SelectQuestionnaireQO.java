package com.jsy.community.qo.property;

import com.jsy.community.qo.BaseQO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Page;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = false)
@Data
@Accessors(chain = true)
@ApiModel("分页查询调查问卷")
@AllArgsConstructor
@NoArgsConstructor
public class SelectQuestionnaireQO extends BaseQO implements Serializable {

    private Integer status;//0未开始1进行中2已经就结束
    private Integer releaseStatus;//0未发布，1已发布
}
