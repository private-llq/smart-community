package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-04-16 16:44
 **/
@Data
@ApiModel("业主实体类")
@TableName("t_user_search")
public class UserSearchEntity extends BaseEntity {
    //用户id
    private String uid;
    //个人搜索的词汇集合
    private String searchRecord;

}
