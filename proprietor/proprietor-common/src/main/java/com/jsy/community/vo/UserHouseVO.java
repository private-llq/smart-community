package com.jsy.community.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-08-17 14:58
 **/
@Data
public class UserHouseVO implements Serializable {

    /**
     * 业主名称
     */
    private String name;

    /**
     * @Description: 关系
     * @author: Hu
     * @since: 2021/8/17 15:02
     * @Param:
     * @return:
     */
    private String relationText;

    /**
     * @Description: 关系
     * @author: Hu
     * @since: 2021/8/17 15:02
     * @Param:
     * @return:
     */
    private Integer relation;

    /**
     * @Description: 房间id
     * @author: Hu
     * @since: 2021/8/17 15:02
     * @Param:
     * @return:
     */
    private Long houseId;


    /**
     * 房屋地址
     */
    private String houseSite;

    /**
     * @Description: 房间成员集合
     * @author: Hu
     * @since: 2021/8/17 15:08
     * @Param:
     * @return:
     */
    private List<MembersVO> members;
}
