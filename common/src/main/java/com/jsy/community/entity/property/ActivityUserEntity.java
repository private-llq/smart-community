package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.utils.RegexUtils;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description: APP活动报名人员
 * @author: Hu
 * @create: 2021-08-13 14:32
 **/
@Data
@TableName("t_activity_user")
public class ActivityUserEntity implements Serializable {

    /**
     * ID
     */
    private Long id;
    /**
     * 用户uid
     */
    private String uid;
    /**
     * 活动id
     */
    private Long activityId;
    /**
     * 报名人员电话
     */
    @NotBlank(message = "电话不能为空！",groups ={ActivityUserVerification.class})
    @Pattern(regexp = RegexUtils.REGEX_MOBILE,message = "请输入正确的手机号！",groups ={ActivityUserVerification.class})
    private String mobile;
    /**
     * 报名人员名称
     */
    @NotBlank(message = "姓名不能为空！",groups ={ActivityUserVerification.class})
    private String name;

    public interface ActivityUserVerification{}

}
