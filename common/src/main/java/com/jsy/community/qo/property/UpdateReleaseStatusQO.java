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
@ApiModel("修改发布状态")
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReleaseStatusQO implements Serializable {
    private static final long serialVersionUID = 1L;
    private  Long   questionnaireId;
    private Integer releaseStatus;//0未发布，1已发布

}
