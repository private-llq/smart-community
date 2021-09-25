package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserIMEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author chq459799974
 * @description im用户
 * @since 2021-01-15 09:25
 **/
public interface UserIMMapper extends BaseMapper<UserIMEntity> {
    /**
     * @Description: 查询imid
     * @author: Hu
     * @since: 2021/9/24 14:17
     * @Param:
     * @return:
     */
    List<String> selectByUid(@Param("set") Set<String> total);
}
