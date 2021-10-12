package com.jsy.community.qo;

import com.jsy.community.utils.RegexUtils;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description: 房屋成员入参
 * @author: Hu
 * @create: 2021-08-18 09:14
 **/
@Data
public class MembersQO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 登录用户uid
     */
    private String uid;

    /**
     * id
     */
    @NotNull(groups = {MembersVerify.class},message = "房间id不能为空！" )
    private Long houseId;

    /**
     * id
     */
    @NotNull(groups = {MembersVerify.class},message = "社区id不能为空！" )
    private Long communityId;

    /**
     * name
     */
    @NotBlank(groups = {MembersVerify.class},message = "姓名不能为空！" )
    private String name;

    /**
     * 电话
     */
    @NotBlank(groups = {MembersVerify.class},message = "电话不能为空！" )
    @Pattern(groups = {MembersVerify.class},message = "电话号码不合法",regexp = RegexUtils.REGEX_MOBILE)
    private String mobile;

    /**
     * 关系
     */
    @NotNull(groups = {MembersVerify.class},message = "成员关系不能为空！" )
    private Integer relation;


    public interface MembersVerify{}
}
