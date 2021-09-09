package com.jsy.community.qo.proprietor;

import com.baomidou.mybatisplus.annotation.TableField;
import com.jsy.community.entity.BaseEntity;
import com.jsy.community.entity.proprietor.ProprietorMarketEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.RegexUtils;
import com.jsy.community.vo.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.apache.poi.ss.formula.functions.T;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("商品发布接参数")
/*@EqualsAndHashCode(callSuper = false)*/
public class ProprietorMarketQO extends BaseQO {

    @ApiModelProperty(value = "发布人uid")
    private String uid;


    @ApiModelProperty(value = "社区id")
    private  Long id;

    @ApiModelProperty(value = "社区id")
    private  Long communityId;

    @NotBlank(groups = {ProprietorMarketQO.proprietorMarketValidated.class},message = "商品名不能为空")
    @ApiModelProperty(value = "商品名")
    private String goodsName;


    @Range(min = 0,message = "交易金额错误")
    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @NotBlank(message = "商品说明不能为空")
    @ApiModelProperty(value = "商品说明")
    private String goodsExplain;

    @ApiModelProperty(value = "是否面议（0不面议 1面议  默认1）")
    private Integer negotiable;

    @NotBlank(groups = {ProprietorMarketQO.proprietorMarketValidated.class},message = "商品标签不能为空")
    @ApiModelProperty(value = "标签id")
    private String labelId;

    @NotBlank(groups = {ProprietorMarketQO.proprietorMarketValidated.class},message = "商品类别不能为空")
    @ApiModelProperty(value = "商品类别id")
    private String categoryId;

    @Pattern(groups = {ProprietorMarketQO.proprietorMarketValidated.class},regexp = RegexUtils.REGEX_MOBILE)
    @NotBlank(message = "手机号不能为空")
    @ApiModelProperty(value = "手机号")
    private String phone;

    @NotBlank(groups = {ProprietorMarketQO.proprietorMarketValidated.class},message = "图片不能为空")
    @ApiModelProperty(value = "图片")
    private String images;

    @TableField(exist = false)
    @ApiModelProperty("标签名")
    private String labelName;

    @ApiModelProperty("类别名")
    @TableField(exist = false)
    private String categoryName;

    @ApiModelProperty(value = "点击率")
    private Integer click;

    @ApiModelProperty("分页查询当前页")
    @TableField(exist = false)
    private Long page;

    @ApiModelProperty("分页查询每页数据条数")
    @TableField(exist = false)
    private Long size;

    @TableField(exist = false)
    private T query;

    @ApiModelProperty("业主名")
    @TableField(exist = false)
    private String realName;


    public interface proprietorMarketValidated{}
}
