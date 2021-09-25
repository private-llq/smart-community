package com.jsy.community.qo;

import com.jsy.community.vo.MembersVO;
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
public class UserHouseQO implements Serializable {


    /**
     * 房间id
     */
    private Long houseId;
    /**
     * 社区id
     */
    private Long communityId;

    /**
     * 社区id
     */
    private String name;
    /**
     * 社区id
     */
    private String mobile;

    /**
     * 成员集合
     */
    private List<MembersVO> members;
}
